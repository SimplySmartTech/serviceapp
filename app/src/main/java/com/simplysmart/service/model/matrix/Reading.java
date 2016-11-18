package com.simplysmart.service.model.matrix;

/**
 * Created by shailendrapsp on 4/11/16.
 */

public class Reading {
    private String tare_weight;
    private String value;
    private String photographic_evidence_url;
    private long timestamp;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPhotographic_evidence_url() {
        return photographic_evidence_url;
    }

    public void setPhotographic_evidence_url(String photographic_evidence_url) {
        this.photographic_evidence_url = photographic_evidence_url;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
