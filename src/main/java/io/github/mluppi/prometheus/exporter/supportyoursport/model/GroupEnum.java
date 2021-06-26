package io.github.mluppi.prometheus.exporter.supportyoursport.model;

public enum GroupEnum {

    A("small"),
    B("medium"),
    C("big");

    private final String size;

    GroupEnum(final String size) {
        this.size = size;
    }

    public String getSize() {
        return size;
    }
}
