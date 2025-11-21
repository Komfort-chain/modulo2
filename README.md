# **Módulo 2 — Login Service & API Gateway (Komfort Chain)**

O **Módulo 2** da suíte **Komfort Chain** implementa a camada de autenticação, autorização e roteamento seguro.
Ele é composto por dois microserviços independentes, integrados por meio de um fluxo simples e claro:

* **Login Service**: responsável pela autenticação, geração e validação de tokens JWT e persistência de usuários.
* **API Gateway**: ponto único de entrada que executa roteamento, controle de acesso e validação de token.

Os serviços seguem princípios de **Clean Architecture**, **SOLID** e **RESTful APIs**, mantendo observabilidade com **Graylog** e garantindo qualidade contínua com **SonarCloud**, **OWASP Dependency-Check** e automação completa via pipelines GitHub Actions.

---

## **Status do Projeto**

[![Full CI/CD](https://github.com/Komfort-chain/modulo2/actions/workflows/full-ci.yml/badge.svg)](https://github.com/Komfort-chain/modulo2/actions/workflows/full-ci.yml)
[![Release](https://github.com/Komfort-chain/modulo2/actions/workflows/release.yml/badge.svg)](https://github.com/Komfort-chain/modulo2/actions/workflows/release.yml)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=Komfort-chain_modulo2\&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Komfort-chain_modulo2)
[![Maintainability](https://sonarcloud.io/api/project_badges/measure?project=Komfort-chain_modulo2\&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=Komfort-chain_modulo2)
[![Docker Hub](https://img.shields.io/badge/DockerHub-magyodev/login--service-blue)](https://hub.docker.com/repository/docker/magyodev/login-service)
[![Docker Hub](https://img.shields.io/badge/DockerHub-magyodev/api--gateway-blue)](https://hub.docker.com/repository/docker/magyodev/api-gateway)

---

## **Tecnologias Utilizadas**

| Categoria        | Ferramenta / Tecnologia                   |
| ---------------- | ----------------------------------------- |
| Linguagem        | Java 21                                   |
| Framework        | Spring Boot 3.5.7                         |
| Segurança        | Spring Security • JWT                     |
| Banco de Dados   | PostgreSQL 16                             |
| Logs             | Logback GELF → Graylog 5.2                |
| Testes           | JUnit 5 • Spring Boot Test • JaCoCo       |
| Build            | Maven Wrapper (mvnw)                      |
| Análise Estática | SonarCloud • OWASP Dependency-Check       |
| Containerização  | Docker e Docker Compose                   |
| Arquitetura      | Clean Architecture • SOLID • RESTful APIs |

---

## **Arquitetura Geral**

O fluxo entre os microserviços foi projetado para garantir segurança e isolamento:

```
Cliente
   │
   ▼
┌────────────────────┐
│     API Gateway     │  → Valida JWT, roteia requisições
└─────────┬──────────┘
          │
          ▼
┌────────────────────┐
│   Login Service     │  → Autentica e gera tokens JWT
└─────────┬──────────┘
          │
          ▼
┌────────────────────┐
│     PostgreSQL      │  → Persiste usuários e credenciais
└────────────────────┘
```

Essa estrutura garante:

* centralização do controle de acesso;
* desacoplamento entre autenticação e os demais módulos;
* validação de segurança padronizada pelo Gateway.

---

## **Organização da Estrutura de Pastas**

A estrutura foi organizada seguindo o mesmo padrão do Módulo 1, garantindo previsibilidade e clareza.

### **1. Raiz do projeto (`modulo2/`)**

Contém:

* `docker-compose.yml`
* `.github/workflows/`
* `README.md`

Arquivos destinados à infraestrutura do módulo como um todo.

---

### **2. Diretório `login-service/`**

Contém a API responsável por autenticação, JWT e persistência:

```
login-service/
├── application/
│   ├── dto/
│   └── service/
├── domain/
│   ├── model/
│   └── repository/
├── infrastructure/
│   ├── config/
│   ├── persistence/
│   └── security/
└── presentation/
    ├── controller/
    └── advice/
```

**Motivos da divisão:**

* **application/**: regras de autenticação e transporte de dados.
* **domain/**: entidade User e abstração do repositório.
* **infrastructure/**: detalhes de banco, segurança e configurações.
* **presentation/**: endpoints REST e tratadores globais de erro.

---

### **3. Diretório `api-gateway/`**

Responsável por roteamento e controle de entrada:

```
api-gateway/
├── src/main/java/com/cabos/api_gateway/
└── resources/
    ├── application.yml
    └── logback-spring.xml
```

O gateway segue o padrão minimalista utilizado em microserviços: leve, simples e focado apenas em roteamento e segurança.

---

## **Execução Local**

### **1. Clonar o repositório**

```bash
git clone https://github.com/Komfort-chain/modulo2.git
cd modulo2
```

### **2. Gerar os artefatos**

```bash
cd login-service && ./mvnw clean package -DskipTests
cd ../api-gateway && ./mvnw clean package -DskipTests
cd ..
```

### **3. Subir toda a stack**

```bash
docker compose up --build -d
```

### **Serviços Esperados**

| Serviço       | Porta | Descrição                        |
| ------------- | ----- | -------------------------------- |
| API Gateway   | 8080  | Entrada única e validação do JWT |
| Login Service | 8081  | Autenticação e emissão de tokens |
| PostgreSQL    | 5432  | Banco de dados de usuários       |
| Graylog       | 9009  | Centralização de logs            |

---

## **Endpoints Principais**

### **1. Registro de usuário**

```
POST /login/register
```

Body:

```json
{
  "username": "admin",
  "password": "123456",
  "role": "ADMIN"
}
```

---

### **2. Autenticação**

```
POST /login
```

Body:

```json
{
  "username": "admin",
  "password": "123456"
}
```

Retorno:

```json
{
  "token": "jwt_aqui"
}
```

---

## **Testes Automatizados**

Os testes foram organizados para refletir exatamente as camadas internas:

```
login-service/src/test/java/com/cabos/login_service/
    application/service/
    infrastructure/security/
    presentation/controller/
```

### **Objetivos dos testes**

* validar regras de negócio da autenticação;
* verificar geração e validação do JWT;
* garantir o correto funcionamento das rotas REST;
* manter a cobertura exigida pelo SonarCloud;
* detectar inconsistências durante o desenvolvimento.

### **Testes implementados**

* **AuthControllerTest**: cobre login e registro.
* **AuthenticationServiceTest**: valida fluxo interno de autenticação e registro.
* **TokenServiceTest**: garante a validação de tokens.
* **JwtUtilTest**: cobre geração e validação de token JWT.

Essa bateria de testes garante estabilidade, previsibilidade e segurança ao módulo.

---

## **Workflows CI/CD**

### **1. full-ci.yml**

Responsável por:

* compilação e testes;
* análise no SonarCloud;
* verificação de vulnerabilidades (OWASP);
* upload de relatórios;
* build e push das imagens Docker.

### **2. release.yml**

Executado quando uma tag SemVer é criada:

* build completo;
* geração do changelog;
* upload do `.jar`;
* publicação de imagens Docker versionadas.

---

## **Imagens Docker Oficiais**

| Serviço       | Link                                                                                                                               |
| ------------- | ---------------------------------------------------------------------------------------------------------------------------------- |
| Login Service | [https://hub.docker.com/repository/docker/magyodev/login-service](https://hub.docker.com/repository/docker/magyodev/login-service) |
| API Gateway   | [https://hub.docker.com/repository/docker/magyodev/api-gateway](https://hub.docker.com/repository/docker/magyodev/api-gateway)     |

Tags disponíveis:

* `latest`
* `${run_number}`
* `vX.Y.Z` (releases)

---

## **Logs e Monitoramento**

Ambos os serviços utilizam logs estruturados via GELF, encaminhados ao Graylog.
Essa abordagem facilita:

* rastreamento de erros;
* análise de fluxo entre microserviços;
* auditoria de requisições.

---

## **Contribuição**

1. Faça um fork do repositório
2. Crie uma branch: `feature/nova-funcionalidade`
3. Utilize commits semânticos
4. Abra um Pull Request para `main`

---

## **Autor**

**Alan de Lima Silva (MagyoDev)**
* GitHub: [https://github.com/MagyoDev](https://github.com/MagyoDev)
* Docker Hub: [https://hub.docker.com/u/magyodev](https://hub.docker.com/u/magyodev)
* E-mail: [magyodev@gmail.com](mailto:magyodev@gmail.com)
