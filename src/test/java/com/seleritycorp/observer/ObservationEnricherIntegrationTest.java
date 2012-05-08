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
import com.seleritycorp.observer.processors.Processor;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.seleritycorp.cs.standalone.commons.Utilities.consoleBanner;
import static junit.framework.Assert.assertTrue;

public class ObservationEnricherIntegrationTest extends DoesLoggingImpl {
    @Test
    public void enricherTest() throws Exception {
        consoleBanner(this, "Enricher");
        final ResponseListener responseListener = new ResponseListener();
        final ObservationListener observationListener = new ObservationListener();
        final ObservationEnricher observationEnricher = new ObservationEnricher(responseListener.getQueue(), observationListener);

        // Start the enricher
        Thread thread = new Thread(observationEnricher);
        thread.start();

        // Start the producer
        responseListener.start();
        
        while (observationListener.getCount() < 1) {
            TimeUnit.MILLISECONDS.sleep(50);
        }
        assertTrue(observationListener.getCount() > 0);
    }
    
    
    private class ObservationListener extends Processor {
        private final AtomicInteger count = new AtomicInteger(0);
        
        public int getCount() {
            return count.get();
        } 
        
        @Override
        public void receive(EnrichedObservation observation) {
            getLogger().info("Received: " + observation);
            count.incrementAndGet();
        }
    }
}
