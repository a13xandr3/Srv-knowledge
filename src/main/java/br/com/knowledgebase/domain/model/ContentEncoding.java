package br.com.knowledgebase.domain.model;

public enum ContentEncoding {

    GZIP("gzip"),
    IDENTITY("identity");

    private final String value;

    ContentEncoding(String v){
        this.value = v;
    }

    public String value(){
        return value;
    }

    public static ContentEncoding from(String raw) {
        if (raw == null) throw new IllegalArgumentException("contentEncoding is null");
        String s = raw.trim().toLowerCase();
        return switch (s) {
            case "gzip" -> GZIP;
            case "identity" -> IDENTITY;
            default -> throw new IllegalArgumentException("Unsupported contentEncoding: " + raw);
        };
    }
}