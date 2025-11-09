# Módulo 2 — API de Login e API Gateway (Komfort Chain)

O **Módulo 2** é responsável pela autenticação e controle de acesso da suíte **Komfort Chain**, garantindo segurança e roteamento centralizado das requisições.
Ele implementa uma **API REST de Login** integrada a um **API Gateway**, utilizando autenticação via **JWT (Bearer Token)** e observabilidade por meio do **Graylog**.

---

## Tecnologias Utilizadas

| Categoria         | Tecnologia                     |
| ----------------- | ------------------------------ |
| Linguagem         | Java 21                        |
| Framework         | Spring Boot 3.5.7              |
| Banco de Dados    | PostgreSQL 16                  |
| Gateway           | Spring Cloud Gateway           |
| Autenticação      | JWT + Spring Security          |
| Observabilidade   | Graylog 5.1 (via Logback GELF) |
| Build             | Maven                          |
| Containerização   | Docker e Docker Compose        |
| Testes            | JUnit + Spring Boot Test       |
| Análise de Código | SonarQube 25.11                |
| Arquitetura       | Clean Architecture + SOLID     |

---

## Estrutura do Projeto

```
modulo2/
├── docker-compose.yml
├── api-gateway/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/com/cabos/api_gateway/
│           │   └── ApiGatewayApplication.java
│           └── resources/
│               ├── application.yml
│               └── logback-spring.xml
└── login-service/
    ├── Dockerfile
    ├── pom.xml
    └── src/
        └── main/
            ├── java/com/cabos/login_service/
            │   ├── application/
            │   ├── domain/
            │   ├── infrastructure/
            │   └── presentation/
            └── resources/
                ├── application.yml
                └── logback-spring.xml
```

Fluxo arquitetural:

```
Controller → Service → Repository → Domain
```

---

## Como Executar

### 1. Entrar na raiz do projeto

```bash
cd E:\Komfort-chain\modulo2
```

### 2. Build dos serviços

```bash
cd .\login-service\
mvn clean package -DskipTests
cd ..\api-gateway\
mvn clean package -DskipTests
cd ..
```

Esses comandos compilam e empacotam ambos os serviços Spring Boot, gerando os arquivos JAR necessários.

### 3. Subir a stack completa

```bash
docker-compose up --build -d
```

Esse comando inicia todos os containers:
PostgreSQL, OpenSearch, MongoDB, Graylog, SonarQube, Login Service e API Gateway.

### 4. Verificar os serviços

```bash
docker ps
```

Exemplo de saída esperada:

```
api-gateway     Up   0.0.0.0:8080->8080/tcp
login-service   Up   8080-8081/tcp
graylog         Up   0.0.0.0:9009->9000/tcp
sonarqube       Up   0.0.0.0:9000->9000/tcp
postgres        Up   0.0.0.0:5432->5432/tcp
```

---

## Endpoints Principais

### Registro de Usuário

```bash
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

### Login

```bash
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

## Serviços

| Serviço       | Porta          | Descrição                       |
| ------------- | -------------- | ------------------------------- |
| API Gateway   | 8080           | Entrada de todas as requisições |
| Login Service | 8081 (interno) | Autenticação via JWT            |
| Graylog       | 9009           | Central de logs                 |
| SonarQube     | 9000           | Análise estática de código      |
| PostgreSQL    | 5432           | Banco de dados                  |
| MongoDB       | 27017          | Base do Graylog                 |
| OpenSearch    | 9200           | Engine de busca do Graylog      |

---

## Descrição do Projeto

O Módulo 2 provê autenticação de usuários e roteamento de requisições através de um API Gateway.
Os tokens JWT são gerados e validados pelo serviço de Login, enquanto o Gateway controla o acesso aos demais módulos do Komfort Chain.
A aplicação segue princípios de arquitetura limpa e mantém logs centralizados no Graylog.

---

**Autor:** Alan de Lima Silva (MagyoDev)
- **GitHub:** [https://github.com/MagyoDev](https://github.com/MagyoDev)
- **E-mail:** [magyodev@gmail.com](mailto:magyodev@gmail.com)

