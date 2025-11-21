package br.com.knowledgebase.domain.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class CompressionService {
    private CompressionService(){}

    public static byte[] gzip(byte[] data){
        try (var out = new ByteArrayOutputStream(); var gzip = new GZIPOutputStream(out)) {
            gzip.write(data);
            gzip.finish();
            return out.toByteArray();
        } catch (Exception e){ throw new IllegalStateException("gzip failed", e); }
    }

    public static byte[] gunzip(byte[] gz){
        try (var in = new GZIPInputStream(new ByteArrayInputStream(gz)); var out = new ByteArrayOutputStream()) {
            in.transferTo(out);
            return out.toByteArray();
        } catch (Exception e){ throw new IllegalStateException("gunzip failed", e); }
    }
}