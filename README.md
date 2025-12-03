````markdown
# Módulo 2 – API de Autenticação com Gateway (Komfort Chain)

Este repositório implementa o **Módulo 2** do projeto **Komfort Chain**: uma solução de autenticação baseada em **Spring Boot**, **MongoDB**, um esquema de token **“JWT-like”** e um **API Gateway** utilizando Spring Cloud Gateway.

A arquitetura segue **Clean Architecture** e princípios **SOLID**, separando claramente:

- **Regras de negócio (Domínio)**
- **Camada de aplicação / infraestrutura (Spring)**
- **Borda de entrada do sistema (Gateway)**

O objetivo deste módulo é:

- Centralizar o **login** e o **registro** de usuários.
- Expor uma **borda única** via **Gateway**, isolando clientes dos detalhes internos.
- Garantir baixo acoplamento e alta coesão entre domínio, infraestrutura e borda.

---

## 1. Visão Geral da Arquitetura

### 1.1 Componentes principais

- `domain/`  
  Núcleo de domínio:
  - Entidades (`User`, `Role`)
  - Exceções de domínio
  - Ports (interfaces)
  - Casos de uso (use cases)

- `spring/`  
  Aplicação de autenticação:
  - Controllers REST (`/api/v1/register`, `/api/v1/login`)
  - Configurações de segurança e beans
  - Implementações de ports (adapters)
  - Persistência com MongoDB (`UserEntity`, `SpringDataUserRepository`)
  - Implementação de um token “JWT-like” (`JwtTokenProvider`)

- `gateway/`  
  API Gateway:
  - Rotas para encaminhar requisições de `/api/v1/*` para o `login-service`.

- `docker-compose.yml`  
  Orquestração dos serviços:
  - `mongo` (banco de dados)
  - `login-service` (módulo `spring`)
  - `gateway` (módulo `gateway`)

---

## 2. Limites de Contexto e Fluxo de Requisição

### 2.1 Fluxo de autenticação

1. O cliente chama o **Gateway** em:
   - `POST http://localhost:8080/api/v1/register`
   - `POST http://localhost:8080/api/v1/login`

2. O Gateway encaminha a requisição para o **login-service** (`spring`):
   - `http://login-service:8081/api/v1/...`

3. O `login-service`:
   - Usa os **casos de uso do domínio** para registrar ou autenticar o usuário.
   - Persiste/consulta dados no **MongoDB**.
   - Gera um **token** usando `JwtTokenProvider` (implementação didática de um token “JWT-like” baseado em Base64).

4. O cliente recebe:
   - No registro: dados básicos do usuário.
   - No login: `accessToken`, tipo do token e timestamp de expiração.

---

## 3. Endpoints Disponíveis

### 3.1 Registro de usuário

**URL via Gateway**  
`POST http://localhost:8080/api/v1/register`

**Body (JSON)**

```json
{
  "name": "Usuário de Teste",
  "email": "user@example.com",
  "password": "minha-senha"
}
````

**Resposta (201 Created)**

```json
{
  "id": "generated-uuid",
  "name": "Usuário de Teste",
  "email": "user@example.com"
}
```

**Possíveis erros**

* `409 Conflict` – `UserAlreadyExistsException` (usuário já cadastrado com o mesmo e-mail).

---

### 3.2 Login

**URL via Gateway**
`POST http://localhost:8080/api/v1/login`

**Body (JSON)**

```json
{
  "email": "user@example.com",
  "password": "minha-senha"
}
```

**Resposta (200 OK)**

```json
{
  "accessToken": "base64-token",
  "tokenType": "Bearer",
  "expiresAt": 1735689600
}
```

**Possíveis erros**

* `401 Unauthorized` – `InvalidCredentialsException`
  E-mail ou senha inválidos, ou usuário inativo.

---

## 4. Como Executar o Projeto

### 4.1 Pré-requisitos

* Docker e Docker Compose instalados.
* Opcional (execução local): **JDK 21** + **Maven Wrapper** (`mvnw`).

### 4.2 Subir tudo com Docker Compose

Na raiz do projeto (onde está o `docker-compose.yml`):

```bash
docker compose up -d --build
```

Serviços disponíveis:

* MongoDB: `mongodb://localhost:27017/login-db`
* Login Service: `http://localhost:8081`
* Gateway: `http://localhost:8080`

### 4.3 Testes rápidos com `curl`

#### Registro

```bash
curl -X POST http://localhost:8080/api/v1/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "password": "123456"
  }'
```

#### Login

```bash
curl -X POST http://localhost:8080/api/v1/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "123456"
  }'
```

---

## 5. Clean Architecture no Módulo 2

A organização dos módulos respeita os princípios de **Clean Architecture**.

### 5.1 `domain/` – Regras de Negócio

**Modelos de domínio**

* `User`
* `Role`

**Exceções de domínio**

* `InvalidCredentialsException`
* `InvalidTokenException`
* `UserAlreadyExistsException`

**Ports (interfaces)**

* `UserRepositoryPort`
* `PasswordEncoderPort`
* `TokenProviderPort`

**Use Cases**

* `AuthenticateUserUseCase`
* `RegisterUserUseCase`
* `ValidateTokenUseCase`

O domínio **não depende de Spring** nem de outros frameworks. Ele é focado em **regras de negócio**, facilitando testes e reutilização.

### 5.2 `spring/` – Aplicação, Adapters e Infraestrutura

Camada responsável por “ligar” o domínio ao mundo externo.

**Configuração / Inversão de Controle**

* `BeanConfig`:

  * Constrói os use cases do domínio.
  * Implementa `PasswordEncoderPort` usando `BCryptPasswordEncoder`.
  * Usa `JwtTokenProvider` como implementação de `TokenProviderPort`.
* `JwtProperties`: configurações externas de segredo e expiração do token.

**Segurança e Filtros**

* `SecurityConfig`:

  * Configura autenticação stateless com token.
  * Libera `/api/v1/login` e `/api/v1/register`.
  * Exige autenticação nas demais rotas.
* `JwtAuthenticationFilter`:

  * Lê o header `Authorization: Bearer <token>`.
  * Valida o token via `TokenProviderPort`.
  * Popula o `SecurityContext`.
* `JwtAuthenticationEntryPoint`:

  * Retorna `401` para acessos não autorizados.

**Controller REST**

* `LoginController`:

  * `POST /api/v1/register`
  * `POST /api/v1/login`
  * Mapeia exceções de domínio para códigos HTTP adequados.

**Persistência (MongoDB)**

* `UserEntity`: documento Mongo da coleção `users`.
* `SpringDataUserRepository`: `MongoRepository<UserEntity, String>`.
* `UserRepositoryAdapter`:

  * Implementa `UserRepositoryPort`.
  * Converte entre `User` (domínio) e `UserEntity` (persistência) usando `UserMapper`.

**DTOs e Mapper**

* DTOs usados na API:

  * `RegisterRequestDTO`
  * `LoginRequestDTO`
  * `LoginResponseDTO`
  * `UserResponseDTO`
* `UserMapper`:

  * `User <-> UserEntity`
  * `User -> UserResponseDTO`

### 5.3 `gateway/` – API Gateway

* `GatewayApplication`: aplicação Spring Boot do gateway.
* `application.yml`:

  * Define rotas:

    * `/api/v1/register` → `http://login-service:8081`
    * `/api/v1/login` → `http://login-service:8081`
  * Remove cabeçalhos sensíveis (`RemoveResponseHeader=Server`).

Dessa forma, o **cliente** fala sempre com o Gateway, que funciona como **borda única** do sistema.

---

## 6. SOLID na Implementação

Exemplos de aplicação dos princípios **SOLID** neste módulo:

**S – Single Responsibility Principle**

* `AuthenticateUserUseCase`: autentica usuários.
* `RegisterUserUseCase`: registra usuários.
* `JwtAuthenticationFilter`: trata extração e validação do token a cada requisição.
* `UserRepositoryAdapter`: converte e delega persistência.

**O – Open/Closed Principle**

* Novos tipos de token ou mecanismos de persistência podem ser adicionados **sem alterar** o domínio, apenas criando novas implementações de ports.
* Rotas do gateway podem ser estendidas via `application.yml`, mantendo as classes Java estáveis.

**L – Liskov Substitution Principle**

* Interfaces de ports (`UserRepositoryPort`, `TokenProviderPort`, `PasswordEncoderPort`) podem receber diferentes implementações sem quebrar o fluxo dos use cases.

**I – Interface Segregation Principle**

* Interfaces específicas e focadas:

  * `UserRepositoryPort` lida somente com persistência de usuários.
  * `PasswordEncoderPort` cuida apenas de codificação/validação de senha.

**D – Dependency Inversion Principle**

* O domínio depende de **abstrações** (ports), não de detalhes de framework.
* Implementações concretas (`UserRepositoryAdapter`, `JwtTokenProvider`) vivem na camada `spring` e são plugadas via `BeanConfig`.

---

## 7. Arquivos de Configuração (`application.yml`)

### 7.1 `spring/src/main/resources/application.yml`

```yaml
spring:
  application:
    name: login-service
  data:
    mongodb:
      uri: mongodb://mongo:27017/login-db

server:
  port: 8081

jwt:
  secret: change-me-in-prod
  expiration-seconds: 3600
```

* Configura a URL do MongoDB (integrada ao `docker-compose`).
* Define a porta do serviço de login (`8081`).
* Define propriedades do token (segredo e tempo de expiração).

### 7.2 `gateway/src/main/resources/application.yml`

```yaml
spring:
  application:
    name: gateway
  cloud:
    gateway:
      routes:
        - id: login-register
          uri: http://login-service:8081
          predicates:
            - Path=/api/v1/register
        - id: login-login
          uri: http://login-service:8081
          predicates:
            - Path=/api/v1/login
      default-filters:
        - RemoveResponseHeader=Server

server:
  port: 8080
```

* Define o gateway na porta `8080`.
* Cria rotas apontando para o `login-service`.
* Remove cabeçalhos sensíveis da resposta.
