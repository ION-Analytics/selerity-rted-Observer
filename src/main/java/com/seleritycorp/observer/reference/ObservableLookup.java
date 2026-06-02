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
package com.seleritycorp.observer.reference;

import com.seleritycorp.cs.standalone.commons.caching.Lookup;
import com.seleritycorp.cs.standalone.commons.caching.SimpleCache;
import com.seleritycorp.cs.standalone.commons.logging.DoesLoggingImpl;
import com.seleritycorp.datatypes.Observable;
import com.seleritycorp.narwhal.client.AuthenticatedSession;
import com.seleritycorp.narwhal.client.DispatchException;
import com.seleritycorp.narwhal.client.Request;
import com.seleritycorp.narwhal.client.Response;
import com.seleritycorp.narwhal.client.methods.CS;
import com.seleritycorp.observer.Config;

import java.net.MalformedURLException;
import java.util.Map;

/**
 * Lookup an event by an Observable ID
 */
public class ObservableLookup extends DoesLoggingImpl implements Lookup<String, Observable> {
    private final AuthenticatedSession session;
    private final SimpleCache<String, Observable> observableCache;

    public ObservableLookup() throws MalformedURLException {
        session = new AuthenticatedSession(Config.SERVER_CS.getProperty(), Config.USER.getProperty(), Config.CLIENT.getProperty(), true);
        session.setDebug(Boolean.parseBoolean(Config.RPC_DEBUG.getProperty()));
        session.setPassword(Config.PASSWORD.getProperty());
        observableCache = new SimpleCache<>(null, new OnCacheMiss(), 2);
        observableCache.setMaximumSize(Integer.parseInt(Config.SPEC_CACHE_SIZE.getProperty()));
    }

    @Override
    public Observable get(String observableId) {
        return observableCache.get(observableId);
    }

    private class OnCacheMiss implements Lookup<String, Observable> {
        @SuppressWarnings("unchecked")
        @Override
        public Observable get(String observableId) {
            getLogger().info("Cache miss " + observableId);
            if (observableId == null || observableId.trim().length() == 0) {
                return null;
            }

            final Request request = new Request(session, CS.GET_OBSERVABLE, observableId);
            final Response response;

            try {
                response = session.dispatch(request);
            } catch (DispatchException exception) {
                getLogger().warning("Failed dispatching " + request + ": " + exception);
                return null;
            }

            if (response.hasError()) {
                getLogger().warning("Server rejected request " + request + ": " + response.getError());
                return null;
            }

            if (response.getResult() == null || !(response.getResult() instanceof Map)) {
                return null;
            }

            return new Observable((Map<String, Object>) response.getResult());
        }
    }

}
