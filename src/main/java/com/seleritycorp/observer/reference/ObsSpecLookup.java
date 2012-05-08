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

package com.seleritycorp.observer.reference;

import com.seleritycorp.cs.standalone.commons.DateUtils;
import com.seleritycorp.cs.standalone.commons.Lookup;
import com.seleritycorp.cs.standalone.commons.LruCache;
import com.seleritycorp.cs.standalone.commons.PeriodicTask;
import com.seleritycorp.datatypes.ObservationSpec;
import com.seleritycorp.datatypes.SearchOption;
import com.seleritycorp.narwhal.client.*;
import com.seleritycorp.narwhal.client.methods.BDS;
import com.seleritycorp.narwhal.client.methods.CS;
import com.seleritycorp.observer.Config;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Lookup Obs Spec by legacy Id.
 */
public class ObsSpecLookup extends PeriodicTask implements Lookup<Long, ObservationSpec> {
    private static final int RELOAD_MINUTES = 30;
    private final CSAuthSession session;
    private final Map<Long, ObservationSpec> specCache;
    private final ObservableLookup observableLookup;

    public ObsSpecLookup(ObservableLookup observableLookup) throws MalformedURLException {
        super(TimeUnit.MINUTES, RELOAD_MINUTES);
        session = new CSAuthSession(Config.SERVER_BDS.getProperty(), Config.USER.getProperty(),
                Config.CLIENT.getProperty(), Config.SERVER_CS.getProperty());
        session.setPassword(Config.PASSWORD.getProperty());
        session.setDebug(Boolean.parseBoolean(Config.RPC_DEBUG.getProperty()));
        specCache = Collections.synchronizedMap(new LruCache<Long, ObservationSpec>(Integer.parseInt(Config.SPEC_CACHE_SIZE.getProperty())));
        this.observableLookup = observableLookup;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ObservationSpec get(Long specId) {
        final ObservationSpec cached = specCache.get(specId);
        if (cached != null) {
            return cached;
        }
        getLogger().info("Cache miss " + specId);
        final Request request = new Request(session, BDS.GET_OBS_SPEC, specId);
        final Response response;
        try {
            response = session.dispatch(request);
        } catch (DispatchException e) {
            getLogger().warning("Failed to dispatch " + request + ": " + e);
            return null;
        }

        if (response.hasError()) {
            getLogger().warning("Could not find obs spec " + specId + ": " + response.getError());
            return null;
        }

        if (!(response.getResult() instanceof Map)) {
            getLogger().warning("Spec lookup returned bad type.");
            return null;
        }

        Map<String, Object> obsSpec = (Map<String, Object>) response.getResult();
        final ObservationSpec observationSpec = new ObservationSpec(obsSpec);
        specCache.put(observationSpec.getLegacyId(), observationSpec);
        return observationSpec;
    }

    /**
     * Load the ObsSpec and Observable Cache
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void periodicTask() throws DispatchException, RemoteException, MalformedURLException, ParseException {
        final AuthenticatedSession periodicSession = new AuthenticatedSession(Config.SERVER_CS.getProperty(), Config.USER.getProperty(),
                Config.CLIENT.getProperty(), true);
        periodicSession.setPassword(Config.PASSWORD.getProperty());
        periodicSession.setDebug(Boolean.parseBoolean(Config.RPC_DEBUG.getProperty()));

        // Get server time UTF
        Request request = new Request(periodicSession, CS.SERVER_TIME, "UTC");
        Response response = periodicSession.dispatch(request);
        if (response.hasError()) {
            throw new RemoteException(response.getError());
        }
        final String timestamp = (String) response.getResult();
        getLogger().info("Server time: " + timestamp);

        // Ask the server for the entitlements
        request = new Request(periodicSession, "ContentSetHandler.getContentSets");
        response = periodicSession.dispatch(request);
        if(response.hasError()) {
            throw new RemoteException(response.getError());
        }
        final List<Map<String, Object>> allContentSets = (List<Map<String, Object>>) response.getResult();
        ArrayList<String> contentSetIds = new ArrayList<>(allContentSets.size());
        for(Map<String, Object> contentSet: allContentSets)
        {
            contentSetIds.add((String)contentSet.get("contentSetId"));
        }
        getLogger().info("ContentSet IDs: " + contentSetIds.toString());

        // Create a window for upcoming events
        final Date serverNow = DateUtils.parse(timestamp);
        final Date windowEnd = new Date(serverNow.getTime() + TimeUnit.MINUTES.toMillis(Integer.parseInt(Config.SPEC_PREFETCH_WINDOW.getProperty())));
        final SearchOption searchOption = new SearchOption(contentSetIds, DateUtils.format(serverNow), DateUtils.format(windowEnd));
        getLogger().info("Loading: " + searchOption);

        // Grab events in that window
        request = new Request(periodicSession, "EventHandler.search", "AND()", searchOption);
        response = periodicSession.dispatch(request);
        if (response.hasError()) {
            throw new RemoteException(response.getError());
        }
        final List<Map<String, Object>> events = (List<Map<String, Object>>) response.getResult();

        // Get the events Obs Specs
        for (Map<String, Object> event : events) {
            final String eventId = (String) event.get("eventId");
            getLogger().info("Processing event: " + eventId);
            request = new Request(periodicSession, "ObservationSpecHandler.getCurrentObservationSpecForEvent", eventId);
            response = periodicSession.dispatch(request);
            if (response.hasError()) {
                throw new RemoteException(response.getError());
            }
            final List<Map<String, Object>> observationSpecs = (List<Map<String, Object>>) response.getResult();
            for (Map<String, Object> map : observationSpecs) {
                final ObservationSpec observationSpec = new ObservationSpec(map);

                getLogger().info("Processing observationSpec: " + observationSpec);


                // Cache the spec
                specCache.put(observationSpec.getLegacyId(), observationSpec);
                // Load the observable
                observableLookup.get(observationSpec.getObservableId());
            }
        }

        getLogger().info("Specs cached: " + specCache.size());
    }

}
