# Knowledgeback Hexagonal (MySQL, JWT, 2FA TOTP)

Projeto refatorado no estilo Arquitetura Hexagonal (Ports & Adapters) com foco em SOLID e Clean Architecture. 
Inclui autenticação com JWT, suporte a 2FA TOTP (sem dependência externa, implementado via HMAC-SHA1) e persistência em MySQL.

## Estrutura

domain/               # Entidades e portas (interfaces)
application/usecases  # Casos de uso (orquestração)
adapters/inbound/web  # Controllers REST
adapters/outbound/*   # JPA, JWT, Crypto
config/               # Spring Security

## Endpoints

- POST /api/auth/login -> body { "username": "...", "password": "..." }
  - Retorna { "token": "..." } ou 202 Accepted { "status": "2FA_REQUIRED" }

- POST /api/2fa/setup -> body { "username": "..." }
  - Gera segredo, habilita 2FA e retorna { "otpauth": "otpauth://totp/..." }

- POST /api/2fa/verify -> body { "username": "...", "code": "123456" }
  - Valida TOTP e retorna { "token": "..." }

## MySQL

Atualize src/main/resources/application.yml (user/password/url).
Naming Strategy: PhysicalNamingStrategyStandardImpl.

## Inicialização

mvn clean package
java -jar target/knowledgeback-hexagonal-1.0.0.jar
# ou
mvn spring-boot:run

## Usuário de teste

INSERT INTO usr(username, password_hash, two_fa_enabled) 
VALUES ('esteves', '$2a$12$CwTycUXWue0Thq9StjUM0uJ8Y5Q0pV/4Y5C6sPH9wQmM3m7j8QjOS', 0);
# senha = "password"

## Atividades (CRUD)

- `GET /api/atividade?page=0&size=20&q=termo` — lista (filtro opcional em título/categoria/subCategoria)
- `GET /api/atividade/{id}` — detalhe
- `POST /api/atividade` — cria (JSON Activity)
- `PUT /api/atividade/{id}` — atualiza
- `DELETE /api/atividade/{id}` — remove

### Modelo Activity
```json
{
  "titulo": "Revisão de arquitetura",
  "descricao": "Refatorar para hexagonal",
  "categoria": "Engenharia",
  "subCategoria": "Arquitetura",
  "tags": ["hexagonal","solid"],
  "uris": ["https://exemplo.com/doc"]
}
```

## Files (Upload/Download)

- `POST /api/files` (multipart/form-data: file, hashMode?) — salva e retorna metadados
- `GET /api/files?page=0&size=20` — lista
- `GET /api/files/{id}` — metadados
- `GET /api/files/{id}/raw` — conteúdo binário

Armazenamento local em `${app.storage.dir:storage}` com sharding por hash.
