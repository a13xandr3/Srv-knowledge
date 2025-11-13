package br.com.knowledgebase.application.usecases;

public interface LoginUseCase {
    enum Decision { OK, TWO_FA_REQUIRED }
    record Result(Decision decision, String token) {}
    Result login(String username, String password);
}
