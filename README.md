# Estúdio BDD — Gestão de Histórias de Usuário e Cenários BDD



Aplicação web desenvolvida para a disciplina de **Programação de Sistemas II** da Universidade Presbiteriana Mackenzie. O sistema gerencia histórias de usuário e cenários BDD com validação de sintaxe Gherkin, curadoria humana auditável de sugestões assistidas por IA, exportação em formato `.feature`, documentação interativa via Swagger e testes automatizados de regras e arquitetura.



---



## Integrantes do Grupo



- **Gabriel Gomes Dos Santos** — RA: 10748012

- **Kauê Trigolô Damaceno** — RA: 10748319

- **Henrique Orlando da Silva Rosa** — RA: 10753510



---



## Arquitetura do Sistema



O backend foi desenvolvido em **Java 17/21** utilizando o framework **Spring Boot** estruturado no padrão em 5 camadas com fluxo unidirecional:



1. **Controller (`br.com.mackenzie.estudiobdd.controller`):** Camada de entrada que disponibiliza os endpoints REST e recebe as requisições HTTP.

2. **DTO (`br.com.mackenzie.estudiobdd.dto`):** Objetos de transferência de dados imutáveis (Java Records) com validações via Bean Validation (`@Valid`).

3. **Service (`br.com.mackenzie.estudiobdd.service`):** Regras de negócio, validações de autoria, integridade da sintaxe dos passos BDD, curadoria de IA e exportação Gherkin.

4. **Repository (`br.com.mackenzie.estudiobdd.repository`):** Interfaces Spring Data JPA com consultas derivadas e JPQL explícito (`@Query`) para persistência no banco de dados.

5. **Model (`br.com.mackenzie.estudiobdd.model`):** Entidades JPA mapeadas para PostgreSQL (`Usuario`, `Projeto`, `HistoriaUsuario`, `Cenario`, `Passo`, `Sugestao`) e Enums de domínio (`Prioridade`, `OrigemCenario`, `TipoPasso`, `DecisaoSugestao`, `MotivoRejeicao`).



Além das 5 camadas principais, o projeto conta com:

- **Config (`br.com.mackenzie.estudiobdd.config`):** Configuração global de CORS (`CorsConfig`) para comunicação com o frontend.

- **Exception (`br.com.mackenzie.estudiobdd.exception`):** Tratamento centralizado de exceções de domínio e validação (`@RestControllerAdvice`).

- **Frontend SPA (`src/main/resources/static/index.html`):** Interface web responsiva em HTML5, CSS3 e JavaScript (`fetch` assíncrono) integrada à API REST.



---



## Regras de Negócio Implementadas



- **Validação Estrutural Gherkin:** Exige obrigatoriamente ao menos um passo `QUANDO` e um `ENTAO`, respeitando a sequência `DADO -> QUANDO -> ENTAO`, bloqueando conectores (`E` e `MAS`) no início e impedindo títulos duplicados na mesma história.

- **Curadoria Humana de Sugestões IA:** Sugestões nascem como `PENDENTE` e só podem ser avaliadas pelo autor da história. A rejeição exige obrigatoriamente um `MotivoRejeicao` e uma justificativa textual; o aceite cria automaticamente um cenário real (`OrigemCenario.IA_ACEITA`) sob transação atômica (`@Transactional`).

- **Governança de Exportação `.feature`:** Bloqueia a geração do arquivo Gherkin (`HTTP 400`) caso a história não possua cenários ou enquanto houver sugestões de IA com status `PENDENTE`.



---



## Banco de Dados



- **SGBD:** PostgreSQL hospedado via **Supabase**.

- **Conexão:** Gerenciada através do *Transaction Pooler* (porta 6543) com SSL obrigatório.

- **ORM:** Hibernate / Spring Data JPA com estratégia `ddl-auto=update` para geração e sincronização automática de esquemas.



---



## Como Executar a Aplicação



### Pré-requisitos

- Java JDK 17 ou 21 instalado.

- Maven (ou utilizar o Maven Wrapper `./mvnw` incluso no repositório).



### Passos de Execução



1. Clone o repositório:

   ```bash

   git clone https://github.com/GabrielSantos171006/estudio-bdd.git

   cd estudio-bdd

   ```



2. Verifique as credenciais do banco no arquivo:

   ```text

   src/main/resources/application.properties

   ```



3. Execute a suíte de testes automatizados (Regras de Negócio com JUnit 5/Mockito e Auditoria Arquitetural com ArchUnit):

   ```bash

   ./mvnw test

   ```



4. Inicie a aplicação via Maven Wrapper:

   ```bash

   ./mvnw spring-boot:run

   ```



5. Acesse os ambientes no navegador:

   - **Interface Web (SPA):** `http://localhost:8080`

   - **Documentação Interativa (Swagger UI):** `http://localhost:8080/swagger-ui.html`



---



## Principais Endpoints da API



| Método | Rota | Descrição |

| :--- | :--- | :--- |

| `POST` | `/api/usuarios` | Cadastra um novo usuário |

| `POST` | `/api/projetos` | Cria um novo projeto |

| `POST` | `/api/historias` | Cadastra uma História de Usuário |

| `GET` | `/api/historias?projetoId={id}` | Lista as histórias de um projeto |

| `GET` | `/api/historias/{id}/exportar-feature` | Exporta os cenários aprovados no formato `.feature` |

| `POST` | `/api/cenarios` | Cadastra um cenário manual validando a ordem Gherkin |

| `GET` | `/api/cenarios?historiaId={id}` | Lista os cenários e passos ordenados de uma história |

| `POST` | `/api/sugestoes` | Registra uma sugestão de cenário gerada por IA (`PENDENTE`) |

| `PATCH` | `/api/sugestoes/{id}/curadoria` | Aprova (converte em cenário) ou rejeita (exige motivo e justificativa) |

| `GET` | `/api/sugestoes?historiaId={id}` | Lista as sugestões de IA vinculadas à história |



---



## Documentos do Projeto



- `AGENTS.md`: Registro de regras arquiteturais, comandos e histórico de interações com IA.

- `DiagramaClassesBDD.pdf`: Diagrama conceitual de classes do domínio.

- `HistoriasCenarios.pdf`: Especificação da história e cenários BDD iniciais.
