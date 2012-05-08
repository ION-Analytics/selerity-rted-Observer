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

import org.junit.Before;
import org.junit.Test;

import static com.seleritycorp.cs.standalone.commons.Utilities.consoleBanner;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class TickerLookupIntegrationTest {
    private TickerLookup instance;

    @Before
    public void setUp() throws Exception {
        instance = new TickerLookup();
    }

    @Test
    public void testGet() throws Exception {
        consoleBanner(this, "APPLE lookup");
        assertNotNull(instance);
        instance.periodicTask();
        assertEquals("AAPL", instance.get("c888a357-b56e-7bf4-0934-2dd780028b5f"));
    }
}
