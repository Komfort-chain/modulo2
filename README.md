# **Módulo 2 — Login Service & API Gateway (Komfort Chain)**

O **Módulo 2** da suíte **Komfort Chain** fornece a camada de **autenticação, autorização e roteamento seguro** de requisições.
Ele é composto por dois microserviços independentes:

* **Login Service** — responsável pela autenticação de usuários, geração de tokens **JWT (Bearer Token)** e acesso ao banco **PostgreSQL**.
* **API Gateway** — atua como ponto central de entrada, executando **roteamento inteligente**, **validação de tokens**, filtragem e composição de chamadas.

O módulo adota **Clean Architecture**, **SOLID**, **RESTful APIs**, observabilidade via **Graylog**, pipelines de segurança automatizados e análise contínua via **SonarCloud** e **CodeQL**.

---

## **Status do Projeto**

[![Full CI/CD](https://github.com/Komfort-chain/modulo2/actions/workflows/full-ci.yml/badge.svg)](https://github.com/Komfort-chain/modulo2/actions/workflows/full-ci.yml)
[![CodeQL](https://github.com/Komfort-chain/modulo2/actions/workflows/codeql.yml/badge.svg)](https://github.com/Komfort-chain/modulo2/actions/workflows/codeql.yml)
[![Release](https://github.com/Komfort-chain/modulo2/actions/workflows/release.yml/badge.svg)](https://github.com/Komfort-chain/modulo2/actions/workflows/release.yml)
[![Docker Hub](https://img.shields.io/badge/DockerHub-magyodev/login--service-blue)](https://hub.docker.com/repository/docker/magyodev/login-service)
[![Docker Hub](https://img.shields.io/badge/DockerHub-magyodev/api--gateway-blue)](https://hub.docker.com/repository/docker/magyodev/api-gateway)
[![SonarCloud](https://sonarcloud.io/api/project_badges/measure?project=Komfort-chain_modulo2\&metric=alert_status)](https://sonarcloud.io/summary/overall?id=Komfort-chain_modulo2)
![Java 21](https://img.shields.io/badge/Java-21-red)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.7-brightgreen)

---

## **Tecnologias Utilizadas**

| Categoria            | Tecnologias / Ferramentas                          |
| -------------------- | -------------------------------------------------- |
| **Linguagem**        | Java 21                                            |
| **Frameworks**       | Spring Boot 3.5.7 • Spring Cloud • Spring Security |
| **Banco de Dados**   | PostgreSQL 16                                      |
| **Autenticação**     | JWT (Bearer Token)                                 |
| **Logs**             | Logback GELF → Graylog 5.1                         |
| **Testes**           | JUnit 5 • Spring Boot Test • JaCoCo                |
| **Análise Estática** | SonarCloud • CodeQL • OWASP Dependency-Check       |
| **Build**            | Maven Wrapper                                      |
| **Containerização**  | Docker • Docker Compose                            |
| **Arquitetura**      | Clean Architecture • SOLID • RESTful APIs          |

---

## **Arquitetura**

O Módulo 2 é composto por dois microserviços independentes executando de forma colaborativa:

### Fluxo Arquitetural

```
Cliente
   │
   ▼
┌──────────────┐
│  API Gateway │  ← Valida JWT, roteia, aplica filtros
└──────┬───────┘
       │
       ▼
┌──────────────┐
│ Login Service│  ← Autentica usuário e gera JWT
└──────┬───────┘
       │
       ▼
┌──────────────┐
│ PostgreSQL   │  ← Armazena usuários e credenciais
└──────────────┘
```

---

## **Estrutura do Projeto**

```
modulo2/
├── docker-compose.yml
├── .github/workflows/
│   ├── full-ci.yml          # Build • Testes • SonarCloud • OWASP • Docker Hub
│   ├── codeql.yml           # Análise semântica de segurança
│   └── release.yml          # Release + Docker Hub (tags semver)
│
├── api-gateway/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/cabos/api_gateway/...
│
└── login-service/
    ├── Dockerfile
    ├── pom.xml
    └── src/main/java/com/cabos/login_service/...
```

---

## **Execução Local**

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

### 3. Subir toda a stack

```bash
docker compose up --build -d
```

### 4. Verificar serviços

```bash
docker ps
```

| Serviço       | Porta | Descrição                        |
| ------------- | ----- | -------------------------------- |
| API Gateway   | 8080  | Roteamento e validação JWT       |
| Login Service | 8081  | Autenticação e geração de tokens |
| PostgreSQL    | 5432  | Banco de dados                   |
| Graylog       | 9009  | Logs centralizados               |

---

## **Endpoints Principais**

### **Registrar usuário**

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

---

### **Autenticação**

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

**Resposta (JWT):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIs..."
}
```

---

## **Workflows CI/CD**

O projeto possui **três pipelines oficiais**:

---

### **1. full-ci.yml — Pipeline Completo**

Executa automaticamente:

* Build & Test (Login + Gateway)
* SonarCloud (cobertura, duplicações, bugs)
* OWASP Dependency-Check
* Upload de relatórios (JaCoCo, Surefire, OWASP)
* Build & Push das imagens Docker:

```
magyodev/login-service
magyodev/api-gateway
```

---

### **2. codeql.yml — Análise de Segurança Avançada**

Pipeline DevSecOps utilizando **CodeQL**:

* Análise de vulnerabilidades semânticas
* Dataflow analysis
* Segurança de API e validação de token

Ideal para ambientes corporativos.

---

### **3. release.yml — Automação de Release**

Executado ao criar uma tag ou release:

* Build completo
* Geração de imagem Docker com tag SemVer (`v1.0.0`)
* Publicação automática no Docker Hub

---

## **Imagens Docker Oficiais**

| Serviço       | Docker Hub                                                                                                                         |
| ------------- | ---------------------------------------------------------------------------------------------------------------------------------- |
| API Gateway   | [https://hub.docker.com/repository/docker/magyodev/api-gateway](https://hub.docker.com/repository/docker/magyodev/api-gateway)     |
| Login Service | [https://hub.docker.com/repository/docker/magyodev/login-service](https://hub.docker.com/repository/docker/magyodev/login-service) |

Tags:

* `latest`
* `${run_number}`
* `vX.Y.Z` (release semver)

---

## **Observabilidade**

Logs centralizados via **Graylog**, enviados com Logback GELF.

Visualizar logs:

```bash
docker logs -f login-service
docker logs -f api-gateway
```

Cada entrada inclui:

* Timestamp
* Logger
* Server
* Nível de severidade
* Stacktrace (se houver erro)

---

## **Contribuição**

1. Faça um fork do projeto
2. Crie uma branch: `feature/nova-funcionalidade`
3. Utilize **commits semânticos profissionais**
4. Abra um Pull Request para `main`

---

## **Autor**

**Alan de Lima Silva (MagyoDev)**

* GitHub: [https://github.com/MagyoDev](https://github.com/MagyoDev)
* Docker Hub: [https://hub.docker.com/u/magyodev](https://hub.docker.com/u/magyodev)
* E-mail: [magyodev@gmail.com](mailto:magyodev@gmail.com)
