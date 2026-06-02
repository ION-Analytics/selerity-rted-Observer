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

/**
 * Event Sub's representation of an Observation.
 */
public class EnrichedObservation {
    private long specId;
    private String observationTimestamp;
    private ObservationStatus observationStatus;
    private String measure;
    private String measurement;
    private String entity;
    private String ticker;
    private String period;

    public String getObservationTimestamp() {
        return observationTimestamp;
    }

    public void setObservationTimestamp(String observationTimestamp) {
        this.observationTimestamp = observationTimestamp;
    }

    public ObservationStatus getObservationStatus() {
        return observationStatus;
    }

    public void setObservationStatus(ObservationStatus observationStatus) {
        this.observationStatus = observationStatus;
    }

    public long getSpecId() {
        return specId;
    }

    public void setSpecId(long specId) {
        this.specId = specId;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("EnrichedObservation");
        sb.append("{specId=").append(specId);
        sb.append(", observationTimestamp='").append(observationTimestamp).append('\'');
        sb.append(", observationStatus=").append(observationStatus);
        sb.append(", measure='").append(measure).append('\'');
        sb.append(", measurement='").append(measurement).append('\'');
        sb.append(", entity='").append(entity).append('\'');
        sb.append(", ticker='").append(ticker).append('\'');
        sb.append(", period='").append(period).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
