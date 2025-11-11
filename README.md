# Módulo 2 — API de Login e API Gateway (Komfort Chain)

O **Módulo 2** integra a suíte **Komfort Chain** e fornece a camada de **autenticação, autorização e roteamento seguro** das requisições.  
Ele implementa uma **API REST de Login** e um **API Gateway** centralizado, com autenticação via **JWT (Bearer Token)**, arquitetura limpa e observabilidade através do **Graylog**.

---

## Badges de Status

[![Multi-Module CI](https://github.com/Komfort-chain/modulo2/actions/workflows/ci-multimodule.yml/badge.svg)](https://github.com/Komfort-chain/modulo2/actions/workflows/ci-multimodule.yml)
[![CodeQL Analysis](https://github.com/Komfort-chain/modulo2/actions/workflows/codeql.yml/badge.svg)](https://github.com/Komfort-chain/modulo2/actions/workflows/codeql.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Komfort-chain_modulo2&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Komfort-chain_modulo2)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Komfort-chain_modulo2&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Komfort-chain_modulo2)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Komfort-chain_modulo2&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=Komfort-chain_modulo2)

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
| Análise Estática | CodeQL + SonarCloud + OWASP Check    |
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

````

---

## Execução Local

### 1. Clonar o repositório

```bash
git clone https://github.com/Komfort-chain/modulo2.git
cd modulo2
````

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

O repositório contém dois workflows principais.

### CI Multi-Module — Build, Testes e Segurança

Workflow: `.github/workflows/ci-multimodule.yml`

Executa automaticamente:

1. Build dos módulos `login-service` e `api-gateway`
2. Execução dos testes (JUnit + Spring Boot Test)
3. Geração e merge de relatórios de cobertura (JaCoCo)
4. Análise estática no **SonarCloud**
5. Verificação de vulnerabilidades via **OWASP Dependency Check**
6. Upload de artefatos (testes e relatórios)

[![Multi-Module CI](https://github.com/Komfort-chain/modulo2/actions/workflows/ci-multimodule.yml/badge.svg)](https://github.com/Komfort-chain/modulo2/actions/workflows/ci-multimodule.yml)

---

### CodeQL — Análise de Segurança Semântica

Workflow: `.github/workflows/codeql.yml`

Executa:

1. Inicialização do **CodeQL** para Java 21
2. Build dos módulos
3. Escaneamento de vulnerabilidades semânticas
4. Publicação dos resultados na aba **Security → Code Scanning Alerts**

[![CodeQL Analysis](https://github.com/Komfort-chain/modulo2/actions/workflows/codeql.yml/badge.svg)](https://github.com/Komfort-chain/modulo2/actions/workflows/codeql.yml)

---

## Logs e Monitoramento

A aplicação utiliza **Logback GELF** para enviar logs estruturados ao Graylog.
Cada evento registrado inclui informações de contexto como timestamp, thread, logger e nível de severidade.

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
[GitHub](https://github.com/MagyoDev) • [Docker Hub](https://hub.docker.com/u/magyodev) • [E-mail](mailto:magyodev@gmail.com)

