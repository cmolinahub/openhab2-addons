/**
 * Copyright (c) 2014-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.yamahareceiver.internal.protocol;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.smarthome.config.core.ConfigConstants;
import org.openhab.binding.yamahareceiver.handler.YamahaZoneThingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * All other protocol classes in this directory use this class for communication. An object
 * of HttpXMLSendReceive is always bound to a specific host.
 *
 * @author David Graeff <david.graeff@web.de>
 *
 */
public class HttpXMLSendReceive {
    private String host;
    // We need a lot of xml parsing. Create a document builder beforehand.
    private final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    private Logger logger = LoggerFactory.getLogger(YamahaZoneThingHandler.class);

    public final static String XML_GET = "<?xml version=\"1.0\" encoding=\"utf-8\"?><YAMAHA_AV cmd=\"GET\">";
    public final static String XML_PUT = "<?xml version=\"1.0\" encoding=\"utf-8\"?><YAMAHA_AV cmd=\"PUT\">";
    public final static String XML_END = "</YAMAHA_AV>";

    /**
     * Set this flag to write out all send and received traffic
     */
    private boolean protocol_sniffer_enabled = false;
    private FileOutputStream debug_out_stream;

    public enum ReadWriteEnum {
        GET,
        PUT
    }

    public HttpXMLSendReceive(String host) {
        this.host = host;
        // Enable sniffing of the communication to the AVR if the logger level is set to trace
        // when the addon is being loaded.
        setProtocolSnifferEnable(logger.isTraceEnabled());
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setProtocolSnifferEnable(boolean enable) {
        if (enable) {
            File path_without_file = new File(ConfigConstants.getUserDataFolder());
            path_without_file.mkdirs();
            File file = new File(path_without_file, "yamaha_trace.log");
            if (file.exists()) {
                file.delete();
            }
            logger.warn("Protocol sniffing for Yamaha Receiver Addon is enabled. Performance may suffer! Writing to "
                    + file.getAbsolutePath());
            try {
                debug_out_stream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                debug_out_stream = null;
                logger.error(e.getLocalizedMessage());
            }
        } else if (protocol_sniffer_enabled) {
            // Close stream if protocol sniffing will be disabled
            try {
                debug_out_stream.close();
            } catch (Exception e) {
            }
            debug_out_stream = null;
        }
        this.protocol_sniffer_enabled = enable;
    }

    static Node getNode(Node root, String nodePath) {
        String[] nodePathArr = nodePath.split("/");
        return getNode(root, nodePathArr, 0);
    }

    private static Node getNode(Node parent, String[] nodePath, int offset) {
        if (parent == null) {
            return null;
        }
        if (offset < nodePath.length - 1) {
            return getNode(((Element) parent).getElementsByTagName(nodePath[offset]).item(0), nodePath, offset + 1);
        } else {
            return ((Element) parent).getElementsByTagName(nodePath[offset]).item(0);
        }
    }

    public static boolean childNodeExists(Node parent, String childNodeName) {
        return ((Element) parent).getElementsByTagName(childNodeName).item(0) != null;
    }

    /**
     * Post the given xml message and return the response as an xml document node.
     *
     * @param message XML formatted message excluding <?xml> or <YAMAHA_AV> tags.
     * @return Return the response as xml node or throws an exception if response is not xml.
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    Document xml(String message) throws IOException, ParserConfigurationException, SAXException {
        String response = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + message;
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new InputSource(new StringReader(response)));
        return doc;
    }

    /**
     * Post the given xml message and assume ReadWriteEnum.PUT.
     *
     * @param message XML formatted message excluding <?xml> or <YAMAHA_AV> tags.
     * @throws Exception
     */
    void postPut(String message) throws IOException {
        HttpURLConnection connection = null;
        if (message.startsWith("<?xml")) {
            throw new IOException("No preformatted xml allowed!");
        }

        message = XML_PUT + message + XML_END;

        write_trace_file(message);

        try {
            URL url = new URL("http://" + host + "/YamahaRemoteControl/ctrl");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Length", "" + Integer.toString(message.length()));

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(message);
            wr.flush();
            wr.close();

            if (connection.getResponseCode() != 200) {
                throw new IOException("Changing a value on the Yamaha AVR failed: " + message);
            }

        } catch (IOException e) {
            throw e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void write_trace_file(String message) {
        if (protocol_sniffer_enabled && debug_out_stream != null) {
            try {
                debug_out_stream.write(message.replace('\n', ' ').getBytes());
                debug_out_stream.write('\n');
                debug_out_stream.write('\n');
                debug_out_stream.flush();
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage());
            }
        }
    }

    /**
     * Post the given xml message and return the response as string.
     *
     * @param message XML formatted message excluding <?xml> or <YAMAHA_AV> tags.
     * @return Return the response as text or throws an exception if the connection failed.
     * @throws IOException
     */
    public String post(String message) throws IOException {
        HttpURLConnection connection = null;
        if (message.startsWith("<?xml")) {
            throw new IOException("No preformatted xml allowed!");
        }

        message = XML_GET + message + XML_END;

        write_trace_file(message);

        try {
            URL url = new URL("http://" + host + "/YamahaRemoteControl/ctrl");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Length", "" + Integer.toString(message.length()));

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(message);
            wr.flush();
            wr.close();

            // Read response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer responseBuffer = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                responseBuffer.append(line);
                responseBuffer.append('\r');
            }
            rd.close();
            String response = responseBuffer.toString();

            write_trace_file(message);

            return response;
        } catch (Exception e) {
            logger.error("post failed on: " + message);
            throw e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public String getHost() {
        return host;
    }
}
