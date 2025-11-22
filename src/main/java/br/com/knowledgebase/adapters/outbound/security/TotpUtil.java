package br.com.knowledgebase.adapters.outbound.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Instant;

public class TotpUtil {
    private static final int TIME_STEP = 30;
    private static final int DIGITS = 6;
    private static final String BASE32_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    public static String randomBase32() {
        byte[] random = new byte[20];
        new java.security.SecureRandom().nextBytes(random);
        return base32Encode(random);
    }

    public static boolean verify(String base32Secret, String code) {
        long timeIndex = Instant.now().getEpochSecond() / TIME_STEP;
        for (int i = -1; i <= 1; i++) {
            String candidate = generateCode(base32Secret, timeIndex + i);
            if (candidate.equals(code)) return true;
        }
        return false;
    }

    public static String otpauthUri(String issuer, String account, String base32Secret) {
        String label = url(issuer) + ":" + url(account);
        String params = "secret=" + url(base32Secret) + "&issuer=" + url(issuer) + "&algorithm=SHA1&digits=" + DIGITS + "&period=" + TIME_STEP;
        return "otpauth://totp/" + label + "?" + params;
    }

    private static String generateCode(String base32Secret, long timeIndex) {
        byte[] key = base32Decode(base32Secret);
        byte[] data = ByteBuffer.allocate(8).putLong(timeIndex).array();
        byte[] hash = hmacSha1(key, data);
        int offset = hash[hash.length - 1] & 0x0F;
        int binary = ((hash[offset] & 0x7f) << 24) |
                ((hash[offset + 1] & 0xff) << 16) |
                ((hash[offset + 2] & 0xff) << 8) |
                (hash[offset + 3] & 0xff);
        int otp = binary % (int) Math.pow(10, DIGITS);
        return String.format("%0" + DIGITS + "d", otp);
    }

    private static byte[] hmacSha1(byte[] key, byte[] data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            return mac.doFinal(data);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private static String url(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static String base32Encode(byte[] bytes) {
        StringBuilder out = new StringBuilder();
        int i = 0, index = 0, digit = 0, currByte, nextByte;
        while (i < bytes.length) {
            currByte = bytes[i] >= 0 ? bytes[i] : (bytes[i] + 256);
            if (index > 3) {
                if ((i + 1) < bytes.length) {
                    nextByte = bytes[i + 1] >= 0 ? bytes[i + 1] : (bytes[i + 1] + 256);
                } else {
                    nextByte = 0;
                }
                digit = currByte & (0xFF >> index);
                index = (index + 5) % 8;
                digit = (digit << index) | (nextByte >> (8 - index));
                i++;
            } else {
                digit = (currByte >> (8 - (index + 5))) & 0x1F;
                index = (index + 5) % 8;
                if (index == 0) i++;
            }
            out.append(BASE32_CHARS.charAt(digit));
        }
        return out.toString();
    }

    private static byte[] base32Decode(String base32) {
        base32 = base32.replace("=", "").toUpperCase();
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        int buffer = 0, bitsLeft = 0;
        for (char c : base32.toCharArray()) {
            int val;
            if ('A' <= c && c <= 'Z') val = c - 'A';
            else if ('2' <= c && c <= '7') val = 26 + (c - '2');
            else continue;
            buffer = (buffer << 5) | val;
            bitsLeft += 5;
            if (bitsLeft >= 8) {
                out.write((buffer >> (bitsLeft - 8)) & 0xFF);
                bitsLeft -= 8;
            }
        }
        return out.toByteArray();
    }
}
