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
package com.seleritycorp.observer;

import com.seleritycorp.cs.standalone.commons.DateUtils;

import java.util.*;

/**
 * A simple time log. Not thread safe. Only as accurate as java.util.Date.
 */
public class DateHistory {
    private final static String NL = System.getProperty("line.separator");
    private final String name;
    private final List<Map.Entry<Date, String>> events = new ArrayList<>(15);

    public DateHistory(String name) {
        this(name, "Start");
    }

    public DateHistory(String name, String msg) {
        this.name = name;
        add(msg);
    }

    public void add(String msg) {
        events.add(new AbstractMap.SimpleEntry<>(new Date(), msg));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(NL);

        Map.Entry<Date, String> last = null;
        for (Map.Entry<Date, String> event : events) {
            sb.append(DateUtils.format(event.getKey()));
            sb.append(' ');
            sb.append(event.getValue());
            if (last != null) {
                sb.append(" (");
                sb.append(event.getKey().getTime() - last.getKey().getTime());
                sb.append(" ms)");
            }
            last = event;
            sb.append(NL);
        }

        if (last != null) {
            sb.append("Total ");
            sb.append(last.getKey().getTime() - events.get(0).getKey().getTime());
            sb.append(" ms");
            sb.append(NL);
        }
        return sb.toString();
    }

}
