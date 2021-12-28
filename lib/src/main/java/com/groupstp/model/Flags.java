package com.groupstp.model;

public class Flags {
    protected String prefix = "00";
    protected String route = "0";
    protected String encryptionAlg = "00";
    protected String compression = "0";
    protected String priority = "11";

    public Flags() {
    }

    public Flags(String prefix, String route, String encryptionAlg, String compression, String priority) {
        this.prefix = prefix;
        this.route = route;
        this.encryptionAlg = encryptionAlg;
        this.compression = compression;
        this.priority = priority;
    }

    public int encode() {
        String flags = this.prefix + this.route + this.encryptionAlg + this.compression + this.priority;
        return Integer.parseInt(flags, 2);
    }
}
