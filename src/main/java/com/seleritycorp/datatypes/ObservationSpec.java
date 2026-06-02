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
package com.seleritycorp.datatypes;

import java.util.Map;

/**
 * As much of the Obs Spec as wee need here.
 */
public class ObservationSpec {
    final private String observableId;
    final private Long legacyId;

    public ObservationSpec(Map<String, Object> map) {
        observableId = (String) map.get("observableId");
        legacyId = ((Number) map.get("legacyId")).longValue();
    }

    public String getObservableId() {
        return observableId;
    }

    public Long getLegacyId() {
        return legacyId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ObservationSpec");
        sb.append("{observableId='").append(observableId).append('\'');
        sb.append(", legacyId=").append(legacyId);
        sb.append('}');
        return sb.toString();
    }
}
