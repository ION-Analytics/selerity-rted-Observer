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

import com.seleritycorp.cs.standalone.commons.logging.DoesLoggingImpl;
import com.seleritycorp.datatypes.Observable;
import org.junit.Before;
import org.junit.Test;

import static com.seleritycorp.cs.standalone.commons.Utilities.consoleBanner;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

public class ObservableLookupIntegrationTest extends DoesLoggingImpl {
    private ObservableLookup instance;

    @Before
    public void setUp() throws Exception {
        instance = new ObservableLookup();
    }

    @Test
    public void testGet() throws Exception {
        consoleBanner(this, "Valid get");
        Observable observable= instance.get(TestData.OBSERVABLE_ID);
        assertNotNull(observable);
        getLogger().info(observable.toString());
    }

    @Test
    public void testFailedGet() throws Exception {
        consoleBanner(this, "Invalid get");
        Observable observable = instance.get(TestData.BAD_OBSERVABLE_ID);
        assertNull(observable);
    }

}
