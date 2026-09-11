/*
 * (c) Copyright Selerity, Inc. 2009-2019. All rights reserved. This source code is confidential 
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

import com.seleritycorp.cs.standalone.commons.DataListener;
import com.seleritycorp.cs.standalone.commons.DateUtils;
import com.seleritycorp.cs.standalone.commons.StartStop;
import com.seleritycorp.cs.standalone.commons.logging.DoesLoggingImpl;
import com.seleritycorp.narwhal.client.*;
import com.seleritycorp.narwhal.client.methods.OBS;

import java.util.AbstractMap;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Listens for Observations.
 */
public class ResponseListener extends DoesLoggingImpl implements StartStop, com.seleritycorp.narwhal.client.DataListener<Response> {
    private final AtomicInteger alive = new AtomicInteger(0);
    private final BlockingQueue<Map.Entry<DateHistory, Object>> queue = new LinkedBlockingQueue<>();
    private com.seleritycorp.narwhal.client.StartStop observations = null;

    @Override
    public void start() throws Exception {
        alive.set(0);
        final AuthenticatedSession session = new AuthenticatedSession(Config.SERVER_CS.getProperty(), Config.USER.getProperty(), Config.CLIENT.getProperty());
        session.setDebug(Boolean.parseBoolean(Config.RPC_DEBUG.getProperty()));
        session.setPassword(Config.PASSWORD.getProperty());
        session.connect();
        final Session authenticatedSession = new Session(Config.SERVER_OBS.getProperty(), Config.USER.getProperty(), Config.CLIENT.getProperty());
        authenticatedSession.setReadTimeout(TimeUnit.SECONDS, 90);
        authenticatedSession.setToken(session.getToken());
        authenticatedSession.setDebug(Boolean.parseBoolean(Config.RPC_DEBUG.getProperty()));
        final Request request = new Request(authenticatedSession, OBS.SUBSCRIBE, DateUtils.format(new Date()), "SPEC_MEASUREMENT_STATUS");
        observations = authenticatedSession.dispatch(request, (com.seleritycorp.narwhal.client.DataListener<Response>) this);
        observations.start();
        while (!observations.isAlive()) {
            TimeUnit.MILLISECONDS.sleep(20);
        }
        alive.set(1);
    }

    @Override
    public void stop() throws Exception {
        if (observations != null) {
            observations.stop();
        }
        alive.set(2);
    }

    @Override
    public boolean isAlive() {
        return alive.get() == 1;
    }

    @Override
    public void receive(Response response) {
        if (!isAlive()) {
            getLogger().info("Received response while I'm not alive.");
        }
        if (response.hasError()) {
            getLogger().warning(response.getError().getMessage());
            return;
        }
        try {
            getLogger().info("Response received: "+response.getResult());
            final DateHistory timeHistory = new DateHistory("Message Processing", "Response received");
            queue.put(new AbstractMap.SimpleEntry<>(timeHistory, response.getResult()));
        } catch (InterruptedException e) {
            getLogger().warning("Failed to enqueue response: " + e);
        }
    }

    @Override
    public void endOfData() {
        getLogger().info("Received endOfData");
        observations = null;

        while (observations == null || !observations.isAlive()) {
            try {
                TimeUnit.SECONDS.sleep(5);
                start();
            } catch (Throwable t) {
                getLogger().severe("Failed to reconnect observations: " + t);
            }
        }
        getLogger().info("Reconnected");
    }

    public BlockingQueue<Map.Entry<DateHistory, Object>> getQueue() {
        return queue;
    }

}
