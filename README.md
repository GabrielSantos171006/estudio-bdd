# Estúdio BDD — Gestão de Histórias de Usuário e Cenários BDD



Aplicação web full stack desenvolvida para a disciplina de **Programação de Sistemas II (ENEC50882)** da Universidade Presbiteriana Mackenzie. O sistema gerencia projetos, histórias de usuário e cenários BDD com validação de sintaxe Gherkin, curadoria humana auditável de sugestões assistidas por IA, exportação em formato `.feature`, documentação interativa via Swagger e testes automatizados de regras e arquitetura.



---



## Integrantes do Grupo



- **Gabriel Gomes Dos Santos** — RA: 10748012

- **Kauê Trigolô Damaceno** — RA: 10748319

- **Henrique Orlando da Silva Rosa** — RA: 10753510



---



## Arquitetura do Sistema



O backend foi desenvolvido em **Java 17/21** utilizando o framework **Spring Boot** estruturado no padrão em 5 camadas com fluxo unidirecional:



1. **Controller (`br.com.mackenzie.estudiobdd.controller`):** Camada de entrada que disponibiliza os endpoints REST, aciona validações perimetrais (`@Valid`) e recebe as requisições HTTP.

2. **DTO (`br.com.mackenzie.estudiobdd.dto`):** Objetos de transferência de dados imutáveis (Java Records) com validações via Bean Validation, isolando entidades JPA e prevenindo loops de serialização JSON.

3. **Service (`br.com.mackenzie.estudiobdd.service`):** Núcleo de regras de negócio, controle transacional (`@Transactional`), validações de autoria, integridade da sintaxe dos passos BDD, curadoria de IA e exportação Gherkin.

4. **Repository (`br.com.mackenzie.estudiobdd.repository`):** Interfaces Spring Data JPA com consultas derivadas e JPQL explícito (`@Query`) para comunicação com o banco de dados.

5. **Model (`br.com.mackenzie.estudiobdd.model`):** Entidades JPA mapeadas para PostgreSQL (`Usuario`, `Projeto`, `HistoriaUsuario`, `Cenario`, `Passo`, `Sugestao`) com gatilhos de auditoria (`@PrePersist`) e Enums de domínio (`Prioridade`, `OrigemCenario`, `TipoPasso`, `DecisaoSugestao`, `MotivoRejeicao`).



### Componentes Complementares



- **Config (`br.com.mackenzie.estudiobdd.config`):** Configuração global de CORS (`CorsConfig`) para comunicação com o navegador e carga inicial idempotente de dados de demonstração (`DataSeeder` via `CommandLineRunner`).

- **Exception (`br.com.mackenzie.estudiobdd.exception`):** Tratamento centralizado de exceções de domínio e validação (`@RestControllerAdvice`).

- **Frontend SPA (`src/main/resources/static/index.html`):** Single Page Application responsiva construída em HTML5, CSS3 e JavaScript puro (Vanilla JS com `fetch` assíncrono), servida diretamente pelo Tomcat embutido do Spring Boot sem necessidade de build externo.



---



## Regras de Negócio Implementadas



- **Validação Estrutural Gherkin:** Exige obrigatoriamente ao menos um passo `QUANDO` e um `ENTAO`, respeitando a sequência `DADO -> QUANDO -> ENTAO`, bloqueando conectores (`E` e `MAS`) no início do cenário e impedindo títulos duplicados na mesma história.

- **Curadoria Humana de Sugestões IA:** Sugestões nascem com status `PENDENTE` e só podem ser avaliadas pelo autor da história. A rejeição exige obrigatoriamente um `MotivoRejeicao` (`INCORRETA`, `GENERICA`, `DUPLICADA`, `FORA_DO_ESCOPO`, `OUTRO`) e uma justificativa textual; o aceite cria automaticamente um cenário real (`OrigemCenario.IA_ACEITA`) sob transação atômica (`@Transactional`).

- **Governança de Exportação `.feature`:** Bloqueia a geração do arquivo Gherkin (`HTTP 400`) caso a história não possua cenários ou enquanto houver sugestões de IA com status `PENDENTE`.



---



## Banco de Dados



- **SGBD:** PostgreSQL hospedado via **Supabase**.

- **Conexão:** Gerenciada através do *Transaction Pooler* (porta 6543) com SSL obrigatório.

- **ORM:** Hibernate / Spring Data JPA com estratégia `ddl-auto=update` para geração e sincronização automática de esquemas e `DataSeeder` automático no startup quando a base está vazia.



---



## Como Executar a Aplicação



### Pré-requisitos

- Java JDK 17 ou 21 instalado.

- Maven (ou utilizar o Maven Wrapper `./mvnw` incluso no repositório).



## Como Executar a Aplicação



Siga o passo a passo abaixo para preparar o ambiente, validar os testes automatizados e subir o servidor web.



### Pré-requisitos

* **Java JDK 17 ou 21** instalado (ou ambiente **GitHub Codespaces** já configurado).

* **Maven** (o projeto já inclui o *Maven Wrapper* `./mvnw`, então você não precisa instalar o Maven manualmente se não quiser).

* **Acesso à Internet** para conexão com o banco PostgreSQL hospedado no Supabase e download das dependências na primeira execução.



---



### Passo 1: Clonar o Repositório

Abra o terminal do seu computador (ou use o terminal integrado do GitHub Codespaces) e execute:



```bash

git clone https://github.com/GabrielSantos171006/estudio-bdd.git

cd estudio-bdd

```



---



### Passo 2: Verificar a Configuração do Banco de Dados

Confira se o arquivo de configuração abaixo contém a URL JDBC, o usuário e a senha do PostgreSQL (Supabase):



```text

src/main/resources/application.properties

```



*(Nota: Na primeira inicialização, o Hibernate criará/atualizará as tabelas automaticamente e o `DataSeeder` preencherá um cenário inicial de demonstração caso o banco esteja vazio).*



---



### Passo 3: Executar os Testes Automatizados (Opcional, mas Recomendado)

Antes de subir o servidor, você pode rodar a suíte de testes unitários (JUnit 5 + Mockito) e de arquitetura (ArchUnit) para garantir que todas as regras estão íntegras:



```bash

mvn test

```

*(No Windows, utilize `mvnw.cmd test` — ou apenas `mvn test` caso tenha o Maven instalado globalmente).*



---



### Passo 4: Iniciar o Servidor Spring Boot

No terminal, dentro da pasta raiz do projeto, execute o comando para iniciar a aplicação na porta `8080`:



```bash

mvn spring-boot:run

```

*(Aguarde até aparecer no console a mensagem `Started EstudioBddApplication` e `DataSeeder: Cenário de demonstração populado com sucesso!`).*



---



### Passo 5: Acessar o Sistema no Navegador

Com o servidor rodando, abra o navegador de sua preferência e acesse os endereços abaixo:



* **Interface Web Principal (SPA):**

  ```text

  http://localhost:8080

  ```

  *(Dica: Na tela inicial, clique em **"Atualizar Contexto"** no topo para carregar automaticamente o Usuário #1, o Projeto #1 e as Histórias de Demonstração).*



* **Documentação Interativa da API (Swagger UI):**

  ```text

  http://localhost:8080/swagger-ui.html

  ```



> **Observação para quem usa GitHub Codespaces:** Ao iniciar o servidor, o VS Code exibirá um aviso no canto inferior direito com o botão **"Open in Browser"** (ou abra a aba **Ports** e clique no ícone do globo ao lado da porta `8080`).


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
