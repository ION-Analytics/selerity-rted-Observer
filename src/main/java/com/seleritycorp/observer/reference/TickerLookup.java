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

import com.seleritycorp.cs.standalone.commons.Lookup;
import com.seleritycorp.cs.standalone.commons.PeriodicTask;
import com.seleritycorp.narwhal.client.RemoteException;
import com.seleritycorp.narwhal.client.Request;
import com.seleritycorp.narwhal.client.Response;
import com.seleritycorp.narwhal.client.RpcException;
import com.seleritycorp.narwhal.client.methods.BDS;
import com.seleritycorp.observer.Config;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Lookup the exchange synonym for an entity id.
 */
public class TickerLookup extends PeriodicTask implements Lookup<String, String> {
    private final static int RELOAD_MINUTES = 15;
    private final static String[] RANK = {"none", "SELERITY", "ABBREV", "CIK", "HKSE", "LSE", "TSX", "NYSE", "NASDAQ"};
    private final ConcurrentHashMap<String, String> tickers = new ConcurrentHashMap<>();
    private final SynonymComparator synonymComparator = new SynonymComparator();

    public TickerLookup() {
        super(TimeUnit.MINUTES, RELOAD_MINUTES);
    }

    /**
     * Load the ticker cache
     *
     * @throws MalformedURLException
     * @throws RpcException
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void periodicTask() throws MalformedURLException, RpcException {
        final CSAuthSession session = new CSAuthSession(Config.SERVER_BDS.getProperty(),
                Config.USER.getProperty(), Config.CLIENT.getProperty(), Config.SERVER_CS.getProperty());
        session.setPassword(Config.PASSWORD.getProperty());
        session.setDebug(Boolean.parseBoolean(Config.RPC_DEBUG.getProperty()));
        final Request request = new Request(session, BDS.GET_ALL_TAGS);
        final Response response = session.dispatch(request);

        if (response.hasError()) {
            throw new RemoteException(response.getError());
        }

        final List<Map<String, Object>> tags = (List<Map<String, Object>>) response.getResult();


        if (tags == null) {
            return;
        }

        for (Map<String, Object> tag : tags) {
            if ("Entity".equals(tag.get("name"))) {
                final List<Map<String, Object>> synonyms = (List<Map<String, Object>>) tag.get("synonyms");
                if (synonyms == null || synonyms.size() == 0) {
                    continue;
                }
                final Map<String, Object> best = Collections.max(synonyms, synonymComparator);
                tickers.put((String) tag.get("tagId"), (String) best.get("synonym"));
            }
        }

        getLogger().info("Tickers loaded: " + tickers.size());
    }

    @Override
    public String get(String s) {
        return tickers.get(s);
    }

    /**
     * Compare two synonyms by the relative ranks of their families.
     */
    private class SynonymComparator implements Comparator<Map<String, Object>> {
        @Override
        public int compare(Map<String, Object> map1, Map<String, Object> map2) {
            final int rank1 = rank((String) map1.get("family"));
            final int rank2 = rank((String) map2.get("family"));

            return rank1 - rank2;
        }

        private int rank(String str) {
            if (str == null) {
                return 0;
            }
            final String upper = str.toUpperCase();
            for (int i = 1; i < RANK.length; i++) {
                if (RANK[i].equals(upper)) {
                    return i;
                }
            }
            return 0;
        }
    }
}
