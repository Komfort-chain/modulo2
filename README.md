# Módulo 2 — Login Service & API Gateway (Komfort Chain)

O **Módulo 2** da suíte **Komfort Chain** fornece a camada de **autenticação, autorização e roteamento seguro** das requisições.  
Ele implementa dois microserviços principais:

- **Login Service** — responsável pela autenticação e emissão de tokens JWT.  
- **API Gateway** — responsável pelo roteamento, filtragem e validação centralizada dos tokens.

Ambos seguem princípios de **Clean Architecture**, **SOLID** e contam com **observabilidade via Graylog** e segurança aprimorada via **SonarCloud** e **OWASP Dependency Check**.

---

## Status do Projeto

[![Full CI/CD](https://github.com/Komfort-chain/modulo2/actions/workflows/full-ci.yml/badge.svg)](https://github.com/Komfort-chain/modulo2/actions/workflows/full-ci.yml)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=Komfort-chain_modulo2&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Komfort-chain_modulo2)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Komfort-chain_modulo2&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Komfort-chain_modulo2)
[![Maintainability](https://sonarcloud.io/api/project_badges/measure?project=Komfort-chain_modulo2&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=Komfort-chain_modulo2)

---

## Tecnologias Utilizadas

| Categoria          | Ferramenta / Tecnologia                 |
| ------------------ | -------------------------------------- |
| Linguagem          | Java 21                                |
| Frameworks         | Spring Boot 3.5.7 / Spring Cloud 2024.0 |
| Banco de Dados     | PostgreSQL 16                          |
| Segurança          | Spring Security + JWT (Bearer Token)   |
| Logs               | Logback GELF → Graylog 5.1             |
| Build              | Maven Wrapper (mvnw)                   |
| Testes             | JUnit 5 + Spring Boot Test + JaCoCo    |
| Análise Estática   | SonarCloud + OWASP Dependency Check + CodeQL |
| Containerização    | Docker e Docker Compose                |
| Arquitetura        | Clean Architecture / SOLID / RESTful   |

---

## Estrutura do Projeto

```bash
modulo2/
├── docker-compose.yml
├── .github/workflows/
│   ├── full-ci.yml        # CI/CD completo: build, testes, análise e imagens Docker
│   └── codeql.yml         # Análise semântica de segurança
│
├── api-gateway/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/cabos/api_gateway/
│       └── ApiGatewayApplication.java
│
└── login-service/
    ├── Dockerfile
    ├── pom.xml
    └── src/main/java/com/cabos/login_service/
        ├── application/
        ├── domain/
        ├── infrastructure/
        │   └── security/JwtUtil.java
        └── presentation/
````

### Fluxo Arquitetural

```
Cliente → API Gateway → Login Service → PostgreSQL
```

---

## Execução Local

### 1. Clonar o repositório

```bash
git clone https://github.com/Komfort-chain/modulo2.git
cd modulo2
```

### 2. Gerar os artefatos

```bash
cd login-service && ./mvnw clean package -DskipTests
cd ../api-gateway && ./mvnw clean package -DskipTests
cd ..
```

### 3. Subir a stack completa

```bash
docker compose up --build -d
```

### 4. Verificar containers ativos

```bash
docker ps
```

**Serviços esperados:**

| Serviço       | Porta | Função                               |
| ------------- | ----- | ------------------------------------ |
| API Gateway   | 8080  | Entrada e roteamento HTTP            |
| Login Service | 8081  | Autenticação JWT                     |
| PostgreSQL    | 5432  | Armazenamento de usuários            |
| Graylog       | 9009  | Observabilidade e logs centralizados |
| SonarQube (*) | 9000  | Análise estática de código           |

> (*) O SonarQube é opcional, usado apenas durante análise local ou em pipeline.

---

## Endpoints Principais

### Registro de Usuário

```http
POST http://localhost:8080/login/register
Content-Type: application/json
```

**Body:**

```json
{
  "username": "admin",
  "password": "123456",
  "role": "ADMIN"
}
```

### Autenticação (Login)

```http
POST http://localhost:8080/login
Content-Type: application/json
```

**Body:**

```json
{
  "username": "admin",
  "password": "123456"
}
```

**Resposta:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIs..."
}
```

---

## Pipeline CI/CD

### Full CI/CD — `full-ci.yml`

Executa automaticamente:

1. Build e testes unitários dos módulos `login-service` e `api-gateway`
2. Análise estática com **SonarCloud**
3. Varredura de vulnerabilidades com **OWASP Dependency Check**
4. Upload de relatórios de testes e cobertura
5. Build e push das imagens Docker para o **Docker Hub**

[![Full CI](https://github.com/Komfort-chain/modulo2/actions/workflows/full-ci.yml/badge.svg)](https://github.com/Komfort-chain/modulo2/actions/workflows/full-ci.yml)

### CodeQL — Segurança Semântica

Workflow: `.github/workflows/codeql.yml`
Executa a varredura semântica de vulnerabilidades em tempo de build, conforme as práticas de **DevSecOps**.

[![CodeQL](https://github.com/Komfort-chain/modulo2/actions/workflows/codeql.yml/badge.svg)](https://github.com/Komfort-chain/modulo2/actions/workflows/codeql.yml)

---

## Imagens Docker

As imagens geradas pelo pipeline estão disponíveis no **repositório oficial da organização Komfort Chain** e são publicadas automaticamente a cada *push* na branch `main`.

| Serviço        | Repositório Docker Hub                                                                 |
|----------------|------------------------------------------------------------------------------------------|
| API Gateway    | [magyodev/api-gateway](https://hub.docker.com/repository/docker/magyodev/api-gateway)   |
| Login Service  | [magyodev/login-service](https://hub.docker.com/repository/docker/magyodev/login-service) |


Cada módulo contém seu próprio `Dockerfile` e é construído e versionado de forma independente dentro do pipeline CI/CD (`.github/workflows/full-ci.yml`).

---

## Logs e Monitoramento

A observabilidade é implementada com **Logback GELF**, enviando logs estruturados em formato JSON para o Graylog.
Cada evento inclui timestamp, nível, logger e contexto da requisição.

Visualizar logs em tempo real:

```bash
docker logs -f login-service
```

---

## Diagrama Simplificado

```
┌────────────┐      ┌───────────────┐      ┌──────────────────┐
│  Cliente   │ ───▶ │ API Gateway   │ ───▶ │ Login Service    │
└────────────┘      └───────────────┘      └──────────────────┘
                                     │
                                     ▼
                               ┌────────────┐
                               │ PostgreSQL │
                               └────────────┘
```

---

## Contribuição

1. Faça um fork do projeto.
2. Crie uma branch: `feature/nova-funcionalidade`.
3. Realize as alterações e commits semânticos.
4. Envie um Pull Request para a branch `main`.

---

## Autor

**Alan de Lima Silva (MagyoDev)**
[GitHub](https://github.com/MagyoDev) • [Docker Hub](https://hub.docker.com/u/magyodev) • [E-mail](mailto:magyodev@gmail.com)

