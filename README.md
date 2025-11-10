# Módulo 2 — API de Login e API Gateway (Komfort Chain)

O **Módulo 2** integra a suíte **Komfort Chain** e fornece a camada de **autenticação, autorização e roteamento seguro** das requisições.
Ele implementa uma **API REST de Login** e um **API Gateway** centralizado, com autenticação via **JWT (Bearer Token)**, arquitetura limpa e observabilidade através do **Graylog**.

---

## Badges de Status

[![CI Security](https://github.com/Komfort-chain/modulo2/actions/workflows/ci-login.yml/badge.svg)](https://github.com/Komfort-chain/modulo2/actions/workflows/ci-login.yml)
[![CodeQL Analysis](https://github.com/Komfort-chain/modulo2/actions/workflows/codeql.yml/badge.svg)](https://github.com/Komfort-chain/modulo2/actions/workflows/codeql.yml)
[![Java](https://img.shields.io/badge/Java-21-red)]()
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.7-brightgreen)]()
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)]()

---

## Tecnologias Utilizadas

| Categoria        | Tecnologia                           |
| ---------------- | ------------------------------------ |
| Linguagem        | Java 21                              |
| Framework        | Spring Boot 3.5.7                    |
| Banco de Dados   | PostgreSQL 16                        |
| Gateway          | Spring Cloud Gateway                 |
| Autenticação     | JWT + Spring Security                |
| Observabilidade  | Graylog 5.1 (via Logback GELF)       |
| Build            | Maven (Wrapper)                      |
| Testes           | JUnit 5 + Spring Boot Test           |
| Análise Estática | CodeQL + OWASP Dependency Check      |
| Containerização  | Docker e Docker Compose              |
| Arquitetura      | Clean Architecture + SOLID + RESTful |

---

## Estrutura do Projeto

```
modulo2/
├── docker-compose.yml
├── api-gateway/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/cabos/api_gateway/
│       └── ApiGatewayApplication.java
└── login-service/
    ├── Dockerfile
    ├── pom.xml
    └── src/main/java/com/cabos/login_service/
        ├── application/
        ├── domain/
        ├── infrastructure/
        │   └── security/
        │       ├── JwtUtil.java
        │       └── JwtUtilTest.java
        └── presentation/
```

Fluxo Arquitetural:

```
Cliente → API Gateway → Login Service → Banco de Dados
```

---

## Execução Local

### 1. Clonar o repositório

```bash
git clone https://github.com/Komfort-chain/modulo2.git
cd modulo2
```

### 2. Build dos serviços

```bash
cd login-service
./mvnw clean package -DskipTests
cd ../api-gateway
./mvnw clean package -DskipTests
cd ..
```

### 3. Subir a stack completa

```bash
docker compose up --build -d
```

### 4. Verificar serviços

```bash
docker ps
```

**Serviços esperados:**

```
api-gateway     Up   0.0.0.0:8080->8080/tcp
login-service   Up   8081/tcp
graylog         Up   0.0.0.0:9009->9000/tcp
sonarqube       Up   0.0.0.0:9000->9000/tcp
postgres        Up   0.0.0.0:5432->5432/tcp
```

---

## Endpoints Principais

### Cadastro de Usuário

```
POST http://localhost:8080/login/register
Content-Type: application/json
```

**Body**

```json
{
  "username": "admin",
  "password": "123456",
  "role": "ADMIN"
}
```

### Autenticação (Login)

```
POST http://localhost:8080/login
Content-Type: application/json
```

**Body**

```json
{
  "username": "admin",
  "password": "123456"
}
```

**Resposta**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIs..."
}
```

---

## Serviços da Stack

| Serviço       | Porta | Descrição                  |
| ------------- | ----- | -------------------------- |
| API Gateway   | 8080  | Entrada das requisições    |
| Login Service | 8081  | Autenticação JWT           |
| Graylog       | 9009  | Central de logs            |
| SonarQube     | 9000  | Análise estática de código |
| PostgreSQL    | 5432  | Banco de dados de usuários |
| MongoDB       | 27017 | Base do Graylog            |
| OpenSearch    | 9200  | Engine de busca para logs  |

---

## Pipeline Automatizado (CI/CD)

O repositório contém dois workflows de integração contínua:

### 🔹 **CI Security – Testes e OWASP**

Workflow: `.github/workflows/ci-login.yml`

Executa automaticamente:

1. Build do serviço `login-service`
2. Execução dos testes unitários (JUnit + Spring)
3. Geração de relatórios de teste HTML
4. Análise de vulnerabilidades (OWASP Dependency Check)
5. Upload dos relatórios como artefatos no GitHub Actions

Badge de status:
[![CI Security](https://github.com/Komfort-chain/modulo2/actions/workflows/ci-login.yml/badge.svg)](https://github.com/Komfort-chain/modulo2/actions/workflows/ci-login.yml)

---

### 🔹 **CodeQL – Análise Estática de Segurança**

Workflow: `.github/workflows/codeql.yml`

Executa:

1. Inicialização do CodeQL para Java 21
2. Build dos módulos `api-gateway` e `login-service`
3. Escaneamento de vulnerabilidades no código fonte
4. Publicação dos resultados na aba **Security → Code Scanning Alerts**

Badge de status:
[![CodeQL Analysis](https://github.com/Komfort-chain/modulo2/actions/workflows/codeql.yml/badge.svg)](https://github.com/Komfort-chain/modulo2/actions/workflows/codeql.yml)

---

## Logs e Monitoramento

A aplicação utiliza **Logback GELF** para enviar logs estruturados ao Graylog.
Cada evento registrado é enriquecido com campos como timestamp, thread, logger e nível de severidade.

Visualizar logs em tempo real:

```bash
docker logs -f login-service
```

---

## Diagrama Simplificado

```
┌───────────────┐
│ Cliente       │
└──────┬────────┘
       │  Requisição HTTP (c/ Bearer Token)
       ▼
┌───────────────┐
│ API Gateway   │
└──────┬────────┘
       │  Roteia e valida token JWT
       ▼
┌────────────────────┐
│ Login Service      │
│ Gera e valida JWT  │
└────────────────────┘
       │
       ▼
┌───────────────┐
│ PostgreSQL    │
└───────────────┘
```

---

## Contribuição

1. Faça um fork do projeto
2. Crie uma branch: `feature/nova-funcionalidade`
3. Realize as alterações e commits semânticos
4. Envie um Pull Request para a branch `main`

---

## Autor

**Alan de Lima Silva (MagyoDev)**
[GitHub](https://github.com/MagyoDev) | [Docker Hub](https://hub.docker.com/u/magyodev) | [[magyodev@gmail.com](mailto:magyodev@gmail.com)](mailto:magyodev@g
