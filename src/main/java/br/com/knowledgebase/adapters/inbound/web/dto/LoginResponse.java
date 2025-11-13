package br.com.knowledgebase.adapters.inbound.web.dto;

public record LoginResponse(LoginStatus status, String token) {
    public enum LoginStatus { OK, TWO_FA_REQUIRED }

    public static LoginResponse twoFaRequired() {
        return new LoginResponse(LoginStatus.TWO_FA_REQUIRED, null);
    }
    public static LoginResponse ok(String jwt) {
        return new LoginResponse(LoginStatus.OK, jwt);
    }
}