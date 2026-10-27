# Estúdio BDD — Gestão de Histórias de Usuário e Cenários BDD

Aplicação web desenvolvida para a disciplina de **Programação de Sistemas II** da Universidade Presbiteriana Mackenzie. O sistema gerencia histórias de usuário e cenários BDD com validação de sintaxe Gherkin, rastreabilidade de sugestões assistidas por IA e exportação em formato `.feature`.

---

## Integrantes do Grupo

- **Gabriel Gomes Dos Santos** — RA: 10748012
- **Kauê Trigolô Damaceno** — RA: 10748319
- **Henrique Orlando da Silva Rosa** — RA: 10753510

---

## Arquitetura do Sistema

O backend foi desenvolvido em **Java 17/21** utilizando o framework **Spring Boot** estruturado no padrão em 5 camadas com fluxo unidirecional:

1. **Controller (`br.com.mackenzie.estudiobdd.controller`):** Camada de entrada que disponibiliza os endpoints REST e recebe as requisições HTTP.
2. **DTO (`br.com.mackenzie.estudiobdd.dto`):** Objetos de transferência de dados com validações via Bean Validation (`@Valid`).
3. **Service (`br.com.mackenzie.estudiobdd.service`):** Regras de negócio, validações de autoria e integridade da sintaxe dos passos BDD.
4. **Repository (`br.com.mackenzie.estudiobdd.repository`):** Interfaces Spring Data JPA para comunicação e persistência no banco de dados.
5. **Model (`br.com.mackenzie.estudiobdd.model`):** Entidades JPA mapeadas para PostgreSQL (`Usuario`, `Projeto`, `HistoriaUsuario`, `Cenario`, `Passo`) e Enums de domínio (`Prioridade`, `OrigemCenario`, `TipoPasso`, `DecisaoSugestao`, `MotivoRejeicao`).

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
   git clone [https://github.com/GabrielSantos171006/estudio-bdd.git](https://github.com/GabrielSantos171006/estudio-bdd.git)
   cd estudio-bdd

2. Verifique as credenciais do banco no arquivo:
   ```bash
   src/main/resources/application.properties

3. Execute a aplicação via Maven Wrapper::
   ```bash
   ./mvnw spring-boot:run

4. Execute a aplicação via Maven Wrapper::
   ```bash
   http://localhost:8080

## Documentos do Projeto

- AGENTS.md: Registro de regras arquiteturais, comandos e histórico de interações com IA.

- DiagramaClassesBDD.pdf: Diagrama conceitual de classes do domínio.

- HistoriasCenarios.pdf: Especificação da história e cenários BDD iniciais.
