/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.milight;

import java.util.Set;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

import com.google.common.collect.Sets;

/**
 * The {@link MilightBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author David Graeff - Initial contribution
 */
public class MilightBindingConstants {

    public static final String BINDING_ID = "milight";

    // List of all Thing Type UIDs
    public final static ThingTypeUID BRIDGEV3_THING_TYPE = new ThingTypeUID(BINDING_ID, "bridgeV3");
    public final static ThingTypeUID BRIDGEV6_THING_TYPE = new ThingTypeUID(BINDING_ID, "bridgeV6");

    public final static ThingTypeUID RGB_THING_TYPE = new ThingTypeUID(BINDING_ID, "rgbLed");
    public final static ThingTypeUID RGB_V2_THING_TYPE = new ThingTypeUID(BINDING_ID, "rgbv2Led");
    public final static ThingTypeUID RGB_IBOX_THING_TYPE = new ThingTypeUID(BINDING_ID, "rgbiboxLed");
    public final static ThingTypeUID RGB_CW_WW_THING_TYPE = new ThingTypeUID(BINDING_ID, "rgbwwLed");
    public final static ThingTypeUID RGB_W_THING_TYPE = new ThingTypeUID(BINDING_ID, "rgbwLed");
    public final static ThingTypeUID WHITE_THING_TYPE = new ThingTypeUID(BINDING_ID, "whiteLed");

    public final static Set<ThingTypeUID> BRIDGE_THING_TYPES_UIDS = Sets.newHashSet(BRIDGEV3_THING_TYPE,
            BRIDGEV6_THING_TYPE);
    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Sets.newHashSet(RGB_V2_THING_TYPE,
            RGB_THING_TYPE, WHITE_THING_TYPE, RGB_W_THING_TYPE, RGB_CW_WW_THING_TYPE, RGB_IBOX_THING_TYPE);

    // List of all Channel ids
    public final static String CHANNEL_COLOR = "ledcolor";
    public final static String CHANNEL_NIGHTMODE = "lednightmode";
    public final static String CHANNEL_WHITEMODE = "ledwhitemode";
    public final static String CHANNEL_BRIGHTNESS = "ledbrightness";
    public final static String CHANNEL_SATURATION = "ledsaturation";
    public final static String CHANNEL_TEMP = "ledtemperature";
    public final static String CHANNEL_SPEED_REL = "animation_speed_relative";
    public final static String CHANNEL_ANIMATION_MODE = "animation_mode";
    public final static String CHANNEL_ANIMATION_MODE_REL = "animation_mode_relative";
    public final static String CHANNEL_LINKLED = "ledlink";
    public final static String CHANNEL_UNLINKLED = "ledunlink";

    public static final int PORT_DISCOVER = 48899;
    public static final int PORT_VER3 = 8899;
    public static final int PORT_VER6 = 5987;

    public static final String CONFIG_HOST_NAME = "ADDR";
    public static final String CONFIG_CUSTOM_PORT = "CUSTOM_PORT";
    public static final String CONFIG_PROTOCOL_VERSION = "CONFIG_PROTOCOL_VERSION";
    public static final String CONFIG_ID = "ID";
    public static final String CONFIG_REFRESH_SEC = "REFRESH_IN_SEC";
    public static final String CONFIG_PASSWORD_BYTE_1 = "PASSWORD_BYTE_1";
    public static final String CONFIG_PASSWORD_BYTE_2 = "PASSWORD_BYTE_2";
    public static final String CONFIG_REPEAT = "REPEAT";
    public static final String CONFIG_WAIT_BETWEEN_COMMANDS = "WAIT_BETWEEN_COMMANDS";

    public static final String PROPERTY_SESSIONID = "sessionid";
    public static final String PROPERTY_SESSIONCONFIRMED = "sessionid_last_refresh";
}
