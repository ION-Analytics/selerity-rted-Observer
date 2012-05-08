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

import com.seleritycorp.observer.processors.Processor;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        LOGGER.info("Setup..");

        LOGGER.info("Setting up processor...");
        final Processor observationProcessor;

        // Find the processor class
        final String processorClass = Config.PROCESSOR_CLASS.getProperty();

        try {
            observationProcessor = (Processor) Class.forName(processorClass).newInstance();
        } catch (Exception e) {
            LOGGER.severe("Could not load processor class (" + processorClass + ") :" + e);
            return;
        }

        LOGGER.info("Setting up listener to receive JSON...");
        final ResponseListener responseListener = new ResponseListener();

        LOGGER.info("Setting up enricher to feed processor observations from listener's JSON...");
        final ObservationEnricher observationEnricher;

        try {
            observationEnricher = new ObservationEnricher(responseListener.getQueue(), observationProcessor);
        } catch (MalformedURLException e) {
            LOGGER.severe("Connection failure: " + e);
            return;
        }

        LOGGER.info("Starting up enricher...");
        Thread thread = new Thread(observationEnricher);
        thread.setDaemon(true);
        thread.start();

        LOGGER.info("Starting up listener...");
        try {
            responseListener.start();
        } catch (Exception e) {
            LOGGER.severe("Failed to start response listener: " + e);
            return;
        }

        try {
            while (true) {
                LOGGER.info("Listening: " + responseListener.isAlive());
                try {
                    TimeUnit.MINUTES.sleep(2);
                } catch (InterruptedException e) {
                    LOGGER.warning("Sleep was disturbed: " + e);
                    return;
                }
            }
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, "Unhandled runtime exception.", t);
        }
    }
}
