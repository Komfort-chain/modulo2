# Módulo 2 — Login Service & API Gateway (Komfort Chain)

O **Módulo 2** da suíte **Komfort Chain** fornece a camada de **autenticação, autorização e roteamento seguro** das requisições.  
Ele é composto por dois microserviços independentes e integrados:

- **Login Service** — responsável pela autenticação de usuários e emissão de tokens **JWT (Bearer Token)**.  
- **API Gateway** — atua como ponto central de entrada, realizando **roteamento inteligente**, **filtro de requisições** e **validação de tokens**.

Ambos os serviços seguem princípios de **Clean Architecture**, **SOLID** e **RESTful APIs**, com observabilidade centralizada via **Graylog** e conformidade de segurança garantida por **SonarCloud**, **CodeQL** e **OWASP Dependency Check**.

---

## Status do Projeto

[![Full CI/CD](https://github.com/Komfort-chain/modulo2/actions/workflows/full-ci.yml/badge.svg)](https://github.com/Komfort-chain/modulo2/actions/workflows/full-ci.yml)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=Komfort-chain_modulo2&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Komfort-chain_modulo2)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Komfort-chain_modulo2&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Komfort-chain_modulo2)
[![Maintainability](https://sonarcloud.io/api/project_badges/measure?project=Komfort-chain_modulo2&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=Komfort-chain_modulo2)

---

## Tecnologias Utilizadas

| Categoria        | Tecnologia / Ferramenta                           |
| ---------------- | ------------------------------------------------- |
| **Linguagem**    | Java 21                                           |
| **Frameworks**   | Spring Boot 3.5.7 / Spring Cloud 2024.0           |
| **Banco de Dados** | PostgreSQL 16                                   |
| **Segurança**    | Spring Security + JWT (Bearer Token)              |
| **Logs**         | Logback GELF → Graylog 5.1                        |
| **Build**        | Maven Wrapper (`mvnw`)                            |
| **Testes**       | JUnit 5 + Spring Boot Test + JaCoCo               |
| **Análise Estática** | SonarCloud + OWASP Dependency Check + CodeQL   |
| **Containerização** | Docker e Docker Compose                        |
| **Arquitetura**  | Clean Architecture / SOLID / RESTful              |

---

## Estrutura do Projeto

```bash
modulo2/
├── docker-compose.yml
├── .github/workflows/
│   ├── full-ci.yml        # CI/CD completo: build, testes, análise e publicação das imagens Docker
│   └── codeql.yml         # Análise semântica de segurança (CodeQL)
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
```

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

| Serviço        | Porta | Descrição                            |
|----------------|-------|--------------------------------------|
| API Gateway    | 8080  | Entrada e roteamento de requisições  |
| Login Service  | 8081  | Autenticação e emissão de tokens JWT |
| PostgreSQL     | 5432  | Armazenamento de usuários            |
| Graylog        | 9009  | Monitoramento e logs centralizados   |
| SonarQube (*)  | 9000  | Análise estática de código           |

> (*) O SonarQube é opcional, utilizado apenas durante execução local ou em pipelines de análise.

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

### Workflow Principal — `full-ci.yml`

Executa automaticamente:

1. Build e testes unitários dos módulos `login-service` e `api-gateway`;  
2. Análise estática com **SonarCloud**;  
3. Varredura de vulnerabilidades com **OWASP Dependency Check**;  
4. Geração e upload de relatórios de cobertura (JaCoCo);  
5. Build e publicação das imagens Docker no **Docker Hub**.

[![Full CI/CD](https://github.com/Komfort-chain/modulo2/actions/workflows/full-ci.yml/badge.svg)](https://github.com/Komfort-chain/modulo2/actions/workflows/full-ci.yml)

### Workflow de Segurança — `codeql.yml`

Executa a análise semântica de vulnerabilidades no código-fonte utilizando o **CodeQL**, seguindo práticas de **DevSecOps**.

[![CodeQL](https://github.com/Komfort-chain/modulo2/actions/workflows/codeql.yml/badge.svg)](https://github.com/Komfort-chain/modulo2/actions/workflows/codeql.yml)

---

## Imagens Docker

As imagens oficiais do módulo são geradas e publicadas automaticamente pelo pipeline CI/CD, disponíveis no **Docker Hub**:

| Serviço        | Repositório Docker Hub                                                                 |
|----------------|------------------------------------------------------------------------------------------|
| API Gateway    | [magyodev/api-gateway](https://hub.docker.com/repository/docker/magyodev/api-gateway)   |
| Login Service  | [magyodev/login-service](https://hub.docker.com/repository/docker/magyodev/login-service) |

Cada módulo possui seu próprio `Dockerfile` e é construído de forma independente dentro do pipeline localizado em `.github/workflows/full-ci.yml`.

---

## Logs e Monitoramento

A observabilidade é implementada com **Logback GELF**, enviando logs estruturados em formato JSON para o **Graylog**.  
Cada evento inclui metadados como timestamp, nível de severidade, logger e contexto de requisição.

Visualizar logs em tempo real:

```bash
docker logs -f login-service
```

---

## Diagrama Simplificado

```
┌────────────┐      ┌───────────────┐      ┌──────────────────┐
│   Cliente  │ ───▶ │  API Gateway  │ ───▶ │  Login Service   │
└────────────┘      └───────────────┘      └──────────────────┘
                                     │
                                     ▼
                               ┌────────────┐
                               │ PostgreSQL │
                               └────────────┘
```

---

## Contribuição

1. Faça um fork do projeto;  
2. Crie uma branch: `feature/nova-funcionalidade`;  
3. Realize as alterações e utilize commits semânticos;  
4. Envie um Pull Request para a branch `main`.

---

## Autor

**Alan de Lima Silva (MagyoDev)**  
[GitHub](https://github.com/MagyoDev) • [Docker Hub](https://hub.docker.com/u/magyodev) • [E-mail](mailto:magyodev@gmail.com)
