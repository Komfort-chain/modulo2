# **Módulo 2 — Login Service & API Gateway (Komfort Chain)**

O **Módulo 2** da suíte **Komfort Chain** implementa a camada de **autenticação, autorização e roteamento seguro** da plataforma.
Ele é formado por dois microserviços:

* **Login Service**: cuida de cadastro, autenticação de usuários e geração/validação de tokens JWT.
* **API Gateway**: atua como ponto único de entrada, validando tokens e roteando chamadas para os demais serviços.

A estrutura foi organizada para manter **separação clara de responsabilidades**, alinhada a **Clean Architecture** e **SOLID**, com automação de qualidade via **SonarCloud**, **OWASP Dependency-Check** e pipelines GitHub Actions.

---

## **Status do Projeto**

[![Full CI/CD](https://github.com/Komfort-chain/modulo2/actions/workflows/full-ci.yml/badge.svg)](https://github.com/Komfort-chain/modulo2/actions/workflows/full-ci.yml)
[![Release](https://github.com/Komfort-chain/modulo2/actions/workflows/release.yml/badge.svg)](https://github.com/Komfort-chain/modulo2/actions/workflows/release.yml)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=Komfort-chain_modulo2\&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Komfort-chain_modulo2)
[![Maintainability](https://sonarcloud.io/api/project_badges/measure?project=Komfort-chain_modulo2\&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=Komfort-chain_modulo2)
[![Docker Hub](https://img.shields.io/badge/DockerHub-magyodev%2Flogin--service-blue)](https://hub.docker.com/repository/docker/magyodev/login-service)
[![Docker Hub](https://img.shields.io/badge/DockerHub-magyodev%2Fapi--gateway-blue)](https://hub.docker.com/repository/docker/magyodev/api-gateway)

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

Fluxo simplificado entre os serviços:

```text
Cliente
   │
   ▼
┌────────────────────┐
│     API Gateway     │ → Valida JWT, aplica filtros e roteia
└─────────┬──────────┘
          │
          ▼
┌────────────────────┐
│   Login Service     │ → Autentica e gera tokens JWT
└─────────┬──────────┘
          │
          ▼
┌────────────────────┐
│     PostgreSQL      │ → Persistência de usuários
└────────────────────┘
```

Essa divisão garante:

* centralização do controle de acesso no Gateway;
* o Login Service focado apenas em autenticação e usuários (SRP – Single Responsibility Principle);
* o banco de dados acessado apenas pela camada certa, evitando acoplamento indevido.

---

## **Organização da Estrutura de Pastas**

A organização segue o mesmo padrão do Módulo 1, para manter consistência dentro da suíte **Komfort Chain**.

### **1. Raiz do projeto (`modulo2/`)**

Arquivos ligados ao módulo como um todo:

* `docker-compose.yml`
  Sobe: PostgreSQL, Graylog, Login Service e API Gateway.

* `.github/workflows/`
  Contém:

  * `full-ci.yml`: pipeline completo (build, testes, SonarCloud, OWASP, Docker).
  * `release.yml`: pipeline de release (versões taggeadas).

* `README.md`
  Documentação do módulo, explicando papéis, arquitetura e estrutura.

Essa separação deixa claro o que pertence à **infraestrutura do módulo**, separado do código Java de cada microserviço.

---

### **2. Diretório `login-service/`**

É o serviço responsável por **autenticação** e **emissão de tokens**.

Estrutura base (padrão Clean Architecture):

```text
login-service/
├── src/main/java/com/cabos/login_service/
│   ├── application/
│   │   ├── dto/
│   │   └── service/
│   ├── domain/
│   │   ├── model/
│   │   └── repository/
│   ├── infrastructure/
│   │   ├── config/
│   │   ├── persistence/
│   │   └── security/
│   └── presentation/
│       ├── controller/
│       └── advice/
└── src/main/resources/
    ├── application.yml
    └── logback-spring.xml
```

A seguir, o papel de cada camada e como isso conversa com **Clean Architecture** e **SOLID**.

---

#### **2.1. `application/` – Casos de uso**

Aqui ficam as **regras de aplicação**, que orquestram o fluxo entre domínio e infraestrutura.

* `application/dto/`
  Contém objetos de transporte, como por exemplo:

  * `LoginRequestDTO`
  * `RegisterUserDTO`
  * `AuthResponseDTO`

  Esses DTOs:

  * evitam expor diretamente a entidade de domínio (`User`);
  * definem exatamente o que entra e o que sai nos endpoints;
  * ajudam a aplicar **SRP** (cada classe com uma responsabilidade clara).

* `application/service/`
  Aqui entram classes como:

  * `AuthenticationService`
    Implementa o caso de uso de login, chamando repositório, comparando senhas, gerando token, etc.

  * `UserRegistrationService` (ou similar)
    Lida com cadastro de usuários.

  Como isso se encaixa em **Clean Architecture**?

  * Essas classes representam os **use cases**.
  * Elas conhecem o domínio e dependem de **interfaces**, não de detalhes concretos (seguindo **DIP – Dependency Inversion Principle**).
  * Não têm conhecimento de HTTP, controllers ou detalhes do banco.

---

#### **2.2. `domain/` – Regras centrais do sistema**

* `domain/model/`
  Principalmente:

  * `User`
    Representa o usuário do sistema, com atributos como `id`, `username`, `password`, `role`, `enabled`.

  Ela é:

  * o **modelo de domínio**, que expressa o que o sistema manipula;
  * independente de frameworks (Spring, JPA, etc., quando possível);
  * usada por camadas superiores sem expor detalhes técnicos.

* `domain/repository/`
  Interface(s) que descrevem acesso a dados, por exemplo:

  * `UserRepository` (interface de contrato).

  Em termos de **Clean Architecture**:

  * o domínio descreve **o que precisa ser feito**, não **como**;
  * a implementação concreta do repositório fica na infraestrutura, respeitando o **DIP**.

---

#### **2.3. `infrastructure/` – Detalhes técnicos**

Tudo que é **implementação concreta** fica aqui.

* `infrastructure/config/`
  Arquivos de configuração, por exemplo:

  * configuração de beans,
  * configurações de segurança,
  * integração com outros serviços.

* `infrastructure/persistence/`
  Implementações de repositório, geralmente usando Spring Data JPA.

  Exemplo típico:

  * `UserRepositoryImpl` ou diretamente um `UserJpaRepository` que estende `JpaRepository<User, Long>`.

  Aqui entram detalhes como:

  * anotações `@Repository`,
  * queries específicas,
  * mapeamento JPA.

* `infrastructure/security/`
  Comportamentos ligados a autenticação/autorização, como:

  * `SecurityConfig`
    Configura o Spring Security (rotas públicas, rotas protegidas, filtros, etc.).

  * `JwtAuthenticationFilter`
    Lê o token JWT no header, valida e injeta o usuário autenticado no contexto de segurança.

  * `JwtUtil` ou `TokenService`
    Gera e valida tokens JWT (em alguns projetos, isso fica em `application`, em outros em `infrastructure/security`; aqui você segue o padrão que escolheu).

Isso tudo é **infraestrutura pura**: são os detalhes que “plugam” seus casos de uso na realidade (banco, segurança, etc.).

---

#### **2.4. `presentation/` – Interface HTTP**

Aqui entra tudo que conversa com o “lado de fora” via HTTP.

```text
presentation/
├── controller/
└── advice/
```

##### `controller/`

* Controllers REST, como:

  * `AuthController`
    Expõe endpoints `/login` e `/login/register`, recebendo DTOs, chamando serviços da camada `application` e devolvendo respostas HTTP.

Pontos importantes:

* O controller **não** conhece detalhes de banco.
* Ele chama serviços da `application`, convertendo HTTP → DTO → Serviço → Resposta.

Isso ajuda a aplicar **SRP** e **separação de preocupações**: cada classe cuida apenas do que precisa.

##### `advice/` – O que é isso?

A pasta **`advice`** centraliza tratadores globais de exceção usando `@ControllerAdvice`.

Aqui normalmente você tem uma classe como:

* `GlobalExceptionHandler`

O que ela faz:

* Intercepta exceções lançadas pelos controllers/serviços.
* Converte essas exceções em respostas HTTP padronizadas (status code + corpo de erro).
* Evita espalhar `try/catch` por toda a aplicação.

Por que isso faz sentido em **Clean Architecture**?

* Tratamento de erro de interface HTTP é uma **preocupação da camada de apresentação**, não do domínio.
* Com o `ControllerAdvice`, você agrupa esse comportamento em um ponto único, reduzindo acoplamento e repetição.

Por que isso faz sentido em **SOLID**?

* **SRP**: o controller foca em receber requisições e chamar serviços; o `GlobalExceptionHandler` foca em traduzir erros em respostas HTTP.
* **OCP**: você pode adicionar novos tipos de erro ou mensagens customizadas estendendo o handler, sem mexer em todos os controllers.

Resumindo:

> A pasta `advice` é onde você concentra as classes responsáveis por tratar erros de forma global na camada de apresentação.

---

### **3. Diretório `api-gateway/`**

Este serviço é responsável por:

* ser o ponto único de entrada;
* validar tokens JWT;
* rotear as chamadas para os demais módulos.

Estrutura simplificada:

```text
api-gateway/
├── src/main/java/com/cabos/api_gateway/
│   ├── ApiGatewayApplication.java
│   └── (configs e filtros, se aplicável)
└── src/main/resources/
    ├── application.yml
    └── logback-spring.xml
```

Ele tende a ser mais enxuto, justamente porque:

* não tem lógica de negócio;
* foca em **roteamento** e **segurança**;
* aplica o princípio de manter cada serviço fazendo apenas o que precisa.

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

### 3. Subir a stack

```bash
docker compose up --build -d
```

---

## **Serviços Esperados**

| Serviço       | Porta | Descrição                        |
| ------------- | ----- | -------------------------------- |
| API Gateway   | 8080  | Entrada única e validação do JWT |
| Login Service | 8081  | Autenticação e emissão de tokens |
| PostgreSQL    | 5432  | Banco de dados de usuários       |
| Graylog       | 9009  | Centralização de logs            |

---

## **Endpoints Principais**

### Registro de usuário

```http
POST /login/register
```

Exemplo de corpo:

```json
{
  "username": "admin",
  "password": "123456",
  "role": "ADMIN"
}
```

### Autenticação

```http
POST /login
```

Body:

```json
{
  "username": "admin",
  "password": "123456"
}
```

Resposta:

```json
{
  "token": "jwt_gerado_aqui"
}
```

---

## **Testes Automatizados**

A estrutura de testes acompanha a divisão por camadas:

```text
login-service/src/test/java/com/cabos/login_service/
    application/service/
    infrastructure/security/
    presentation/controller/
```

Eles cobrem:

* fluxo de autenticação;
* registro de usuário;
* validação de token;
* comportamento HTTP dos endpoints;
* integração com Spring Security.

Essa bateria de testes ajuda a:

* manter a qualidade exigida pelo SonarCloud;
* evitar regressões;
* garantir que o módulo se comporte como esperado.

---

## **Workflows CI/CD**

### `full-ci.yml`

Responsável por:

* build e testes;
* análise no SonarCloud;
* execução do OWASP Dependency-Check (com fallback offline);
* upload de relatórios (JaCoCo, Surefire);
* build e push das imagens no Docker Hub.

### `release.yml`

Executado quando é criada uma tag `vX.Y.Z`:

* build dos serviços;
* geração de changelog;
* criação de release no GitHub;
* upload dos artefatos `.jar`;
* publicação de imagens versionadas.

---

## **Imagens Docker Oficiais**

| Serviço       | Link                                                                                                                               |
| ------------- | ---------------------------------------------------------------------------------------------------------------------------------- |
| Login Service | [https://hub.docker.com/repository/docker/magyodev/login-service](https://hub.docker.com/repository/docker/magyodev/login-service) |
| API Gateway   | [https://hub.docker.com/repository/docker/magyodev/api-gateway](https://hub.docker.com/repository/docker/magyodev/api-gateway)     |

Tags:

* `latest`
* `${run_number}`
* `vX.Y.Z`

---

## **Logs e Monitoramento**

Ambos os serviços enviam logs para o Graylog usando GELF.
Isso facilita:

* rastreamento de erros;
* análise de fluxo de autenticação;
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
