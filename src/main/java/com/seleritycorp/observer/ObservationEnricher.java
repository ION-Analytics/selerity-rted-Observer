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

import com.seleritycorp.cs.standalone.commons.logging.DoesLoggingImpl;
import com.seleritycorp.datatypes.EnrichedObservation;
import com.seleritycorp.datatypes.Observable;
import com.seleritycorp.datatypes.ObservationSpec;
import com.seleritycorp.datatypes.ObservationStatus;
import com.seleritycorp.observer.processors.Processor;
import com.seleritycorp.observer.reference.ObsSpecLookup;
import com.seleritycorp.observer.reference.ObservableLookup;
import com.seleritycorp.observer.reference.TickerLookup;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;

/**
 * This class takes object from a blocking queue assumed to be json objects representing
 * observations. It maps the data, with enrichment, to a proper class and then passes it
 * along to a consumer.
 */
public class ObservationEnricher extends DoesLoggingImpl implements Runnable {
    private final BlockingQueue<Map.Entry<DateHistory, Object>> queue;
    private final Processor consumer;
    private final ObsSpecLookup obsSpecLookup;
    private final ObservableLookup observableLookup;
    private final TickerLookup tickerLookup;
    private final boolean nullTickerReject = Boolean.parseBoolean(Config.NULL_TICKER_REJECT.getProperty("true"));

    public ObservationEnricher(BlockingQueue<Map.Entry<DateHistory, Object>> queue, Processor consumer) throws MalformedURLException {
        this.queue = queue;
        this.consumer = consumer;
        observableLookup = new ObservableLookup();
        obsSpecLookup = new ObsSpecLookup(observableLookup);
        tickerLookup = new TickerLookup();
    }

    @Override
    public void run() {
        try {
            while (true) {
                consume(queue.take());
            }
        } catch (InterruptedException ex) {
            getLogger().severe("Interrupted: " + ex);
            consumer.endOfData();
        }
    }

    @SuppressWarnings("unchecked")
    private void consume(final Map.Entry<DateHistory, Object> entry) {
        final DateHistory history = entry.getKey();
        history.add("Received by enricher");
        try {
            EnrichedObservation enhancedObservation = new EnrichedObservation();
            final Map<String, Object> packet = (Map<String, Object>) entry.getValue();
            if (packet == null) {
                throw new IllegalStateException("Could not find initial map.");
            }
            final Map<String, Object> fields = (Map<String, Object>) packet.get("fields");
            if (fields == null) {
                throw new IllegalStateException("Could not find fields.");
            }

            final ObservationStatus observationStatus = ObservationStatus.valueOf((String) fields.get("ObservationStatus"));
            // Discard Not Observed
            if (observationStatus == ObservationStatus.NOT_OBSERVED) {
                return;
            }
            enhancedObservation.setObservationStatus(observationStatus);
            enhancedObservation.setObservationTimestamp((String) fields.get("ObservationTimestamp"));
            enhancedObservation.setMeasurement((String) fields.get("Measurement"));

            final Long obsSpecId = ((Number) packet.get("swordfishObsSpecID")).longValue();

            // Discard Flow Test
            if (obsSpecId == 4) {
                return;
            }
            history.add("Ready for spec");
            final ObservationSpec observationSpec = obsSpecLookup.get(obsSpecId);
            history.add("Got spec");

            if (observationSpec == null) {
                throw new IllegalStateException("Could not find Obs Spec");
            }

            enhancedObservation.setSpecId(obsSpecId);

            final Observable observable = observableLookup.get(observationSpec.getObservableId());
            history.add("Got Observable");
            if (observable == null) {
                throw new IllegalStateException("Could not find observation");
            }

            enhancedObservation.setEntity(observable.getEntity());
            final String ticker = tickerLookup.get(observable.getEntityId());
            if (ticker == null && nullTickerReject) {
                getLogger().warning("Not able to find ticker for: " + observable.getEntity());
                return;
            }
            enhancedObservation.setTicker(ticker);
            enhancedObservation.setPeriod(observable.getPeriod());
            enhancedObservation.setMeasure(observable.getMeasure());
            history.add("Ready to process");
            consumer.receive(enhancedObservation);
            history.add("Processed");
            getLogger().info(history.toString());
        } catch (Exception e) {
            getLogger().log(Level.FINE, "Failed to enrich " + entry.getValue(), e);
        }
    }


}
