package br.com.knowledgebase.domain.ports.in;

public interface LoginUseCase {

    enum Decision {OK, TWO_FA_REQUIRED}

    record Result(Decision decision, String token, String username) {
    }

    Result login(String username, String password);

}