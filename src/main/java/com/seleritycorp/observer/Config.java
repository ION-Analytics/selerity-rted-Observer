/*
 * (c) Copyright Selerity, Inc. 2009-2012. All rights reserved. This source code is confidential
 * and proprietary information of Selerity Inc. and may be used only by a recipient designated
 * by and for the purposes permitted by Selerity Inc. in writing.  Reproduction of, dissemination
 * of, modifications to or creation of derivative works from this source code, whether in source
 * or binary forms, by any means and in any form or manner, is expressly prohibited, except with
 * the prior written permission of Selerity Inc..  THIS CODE AND INFORMATION ARE PROVIDED "AS IS"
 * WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE. This notice may not be
 * removed from the software by any user thereof.
 */

package com.seleritycorp.observer;

import com.seleritycorp.cs.standalone.commons.IOUtilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Configuration file access.
 */
public enum Config {
    USER,
    PASSWORD,
    CLIENT("sample_observer"),
    SERVER_CS,
    SERVER_OBS,
    SERVER_BDS,
    PROCESSOR_CLASS("com.seleritycorp.observer.processors.SomeProcessor"),
    RPC_DEBUG("false"),
    NULL_TICKER_REJECT("true"),
    PROCESSOR_SINGLE_FILE_OUTPUT("./out.csv"),
    SPEC_PREFETCH_WINDOW("120"),
    SPEC_CACHE_SIZE("5000");

    private static final Properties PROPERTIES;
    private final String defaultValue;

    static {
        final String properties = System.getProperty("config.properties", "config.properties");
        PROPERTIES = new Properties();
        try {
            InputStream stream = IOUtilities.getResourceAsStream(properties);
            if (stream == null) {
                Logger.getLogger(Config.class.getName()).warning("Failed finding " + properties);
            } else {
                PROPERTIES.load(IOUtilities.getResourceAsStream(properties));
            }
        } catch (IOException e) {
            Logger.getLogger(Config.class.getName()).warning("Failed loading " + properties + ": " + e);
        }
    }

    private Config() {
        this(null);
    }

    private Config(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getProperty() {
        return PROPERTIES.getProperty(name(), getDefaultValue());
    }

    public String getProperty(String defaultValue) {
        return PROPERTIES.getProperty(name(), defaultValue);
    }

}
