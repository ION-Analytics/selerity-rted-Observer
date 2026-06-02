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

import java.util.List;
import java.util.Map;

/**
 * As much of the observable as is needed here.
 */
public class Observable {
    private final String observableId;
    private final String measure;
    private final String period;
    private String entity = null;
    private String entityId = null;

    @SuppressWarnings("unchecked")
    public Observable(Map<String, Object> map) {
        observableId = (String) map.get("observableId");
        measure = (String) map.get("measure");
        period = (String) map.get("period");
        final List<Map<String, Object>> tags = (List<Map<String, Object>>) map.get("tags");
        if (tags != null) {
            for (Map<String, Object> tag : tags) {
                if ("Entity".equals(tag.get("name"))) {
                    entity = (String) tag.get("value");
                    entityId = (String) tag.get("tagId");
                    break;
                }
            }
        }
    }

    public String getObservableId() {
        return observableId;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getEntity() {
        return entity;
    }

    public String getPeriod() {
        return period;
    }

    public String getMeasure() {
        return measure;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Observable");
        sb.append("{observableId='").append(observableId).append('\'');
        sb.append(", measure='").append(measure).append('\'');
        sb.append(", period='").append(period).append('\'');
        sb.append(", entity='").append(entity).append('\'');
        sb.append(", entityId='").append(entityId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
