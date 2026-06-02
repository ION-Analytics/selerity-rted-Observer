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

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The SearchOptions we need.
 */
public class SearchOption {
    private Boolean timeIntervalOverlappingAlgorithm;
    private String timeIntervalTimeZoneId;
    private Boolean isCompleted;
    private String timeIntervalStart, timeIntervalEnd;
    private ArrayList<String> contentSetIds;

    public SearchOption(ArrayList<String> contentSetIds, String timeIntervalStart, String timeIntervalEnd) {
        timeIntervalTimeZoneId = "UTC";
        timeIntervalOverlappingAlgorithm = true;
        isCompleted = false;
        this.contentSetIds = contentSetIds;
        this.timeIntervalStart = timeIntervalStart;
        this.timeIntervalEnd = timeIntervalEnd;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SearchOption");
        sb.append("{timeIntervalOverlappingAlgorithm=").append(timeIntervalOverlappingAlgorithm);
        sb.append(", timeIntervalTimeZoneId='").append(timeIntervalTimeZoneId).append('\'');
        sb.append(", isCompleted=").append(isCompleted);
        sb.append(", timeIntervalStart='").append(timeIntervalStart).append('\'');
        sb.append(", timeIntervalEnd='").append(timeIntervalEnd).append('\'');
        sb.append(", contentSetIds=").append(contentSetIds == null ? "null" : Arrays.asList(contentSetIds).toString());
        sb.append('}');
        return sb.toString();
    }

}
