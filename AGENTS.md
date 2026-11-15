# AGENTS.md — Estúdio BDD

Este documento orienta o desenvolvimento assistido por ferramentas de Inteligência Artificial e registra o histórico de interações e curadorias do projeto.

---

## 1. Comandos do Projeto (Maven)

- **Compilar o projeto:**
./mvnw clean compile

- **Executar a aplicação localmente:**
./mvnw spring-boot:run

- **Executar a suíte de testes automatizados:**
./mvnw test

## 2. Regras de Arquitetura e Engenharia

Arquitetura em 5 Camadas: A dependência é estritamente unidirecional: Controller -> DTO -> Service -> Repository -> Model. O Controller nunca acessa Repositórios diretamente.

Separação de Modelos: Entidades JPA (model) nunca devem ser expostas diretamente na API REST; usar sempre DTOs específicos de entrada e saída com validação Bean Validation (@Valid).

Regras BDD e Gherkin:

  - Todo cenário deve seguir a ordem estrita de passos: DADO -> QUANDO -> ENTAO.
  
  - É obrigatório conter ao menos um passo QUANDO e um passo ENTAO.
  
  - Passos E e MAS não podem iniciar um cenário.
  
  - O título de um cenário deve ser único dentro do escopo da história de usuário.

Curadoria Humana: A IA atua rascunhando sugestões e código; o analista/desenvolvedor humano avalia, aprova ou rejeita com base no escopo e nas regras de negócio.

Identidade Simulada (`X-Usuario-Id`): A API recebe o usuário atual pelo cabeçalho HTTP `X-Usuario-Id` para validar a autoria (Regra 3). Isso não é autenticação e deve ser documentado como limitação acadêmica.

Tratamento de Erros: Usar `@RestControllerAdvice` retornando `ProblemDetail` (RFC 9457) com códigos HTTP coerentes (400, 403, 404, 409, 422).

## 3. Registro de Interações com IA
### Interação 1 — Ampliação de Cenários BDD (Semana 1)

Data: Outubro/2026

Problema: Ampliar cenários de teste para a história "Cadastrar cenário em história de usuário", cobrindo possíveis regras e brechas de negócio.

Ferramenta: Gemini

Prompt: "Aqui estão minha história e meus 3 cenários sobre cadastro de cenários BDD. Amplie e sugira novos cenários para cobrir possíveis brechas de regras de negócio."

Proposta da IA:

  - Sugestão 1: Rejeição de cenário que inicia com passo "E" ou "Mas" (regra de sintaxe).

  - Sugestão 2: Rejeição de cenário fora da ordem "Dado -> Quando -> Então".

  - Sugestão 3: Disparo de notificação por e-mail com alerta sonoro para a equipe após salvar um cenário.

### Decisão do Grupo:

As sugestões 1 e 2 foram analisadas como regras válidas de sintaxe BDD.

A sugestão 3 foi REJEITADA com o motivo oficial FORA_DO_ESCOPO. O sistema gerencia requisitos e exporta arquivos .feature, não contemplando serviço de mensageria externa de e-mail ou alerta sonoro.

O grupo manteve no documento oficial (HistoriasCenarios.pdf) a cobertura original de integridade com validação de títulos duplicados.

### Interação 2 — Correção de Prioridade, Unicidade e Exceções Padronizadas na API (Semana 4)
- **Problema:** `HistoriaUsuarioService.criarHistoria` não atribuía a prioridade (gerando erro 500 de coluna `NOT NULL`), não validava título duplicado no projeto e os erros retornavam 400 genérico.
- **Ferramenta / Prompt:** Assistente de IA — "Revise os services e o GlobalExceptionHandler para corrigir o erro de prioridade, validar unicidade de título e adotar ProblemDetail com códigos HTTP adequados."
- **Proposta da IA:** Criar exceções específicas (`RecursoNaoEncontradoException`, `RegraNegocioException`, `ConflitoException`, `AcessoNegadoException`) e mapeá-las para `ProblemDetail` (404, 422, 409, 403).
- **Decisão do grupo:** Aceita.
- **Mudanças realizadas:** Atualização de `GlobalExceptionHandler`, `HistoriaUsuarioService` e `ProjetoService`.
- **Verificação:** `./mvnw test` e testes manuais nos endpoints REST.

### Interação 3 — Identidade Simulada (`X-Usuario-Id`) e CRUD Completo na API (Semana 4)
- **Problema:** Faltavam endpoints `PUT`/`DELETE` e a validação de autoria via cabeçalho `X-Usuario-Id` exigida pela Regra 3.
- **Ferramenta / Prompt:** Assistente de IA — "Adicione suporte a X-Usuario-Id nos controllers e implemente PUT/DELETE para projetos, histórias, cenários e passos com checagem de autoria."
- **Proposta da IA:** Centralizar a constante `IdentidadeSimulada.CABECALHO` nos controllers e o helper `RegrasAutoria.exigirAutor` nos services, além de endpoints de consulta e resumo do projeto.
- **Decisão do grupo:** Aceita, mantendo o `DataSeeder` compatível com as novas assinaturas.
- **Mudanças realizadas:** `IdentidadeSimulada`, `RegrasAutoria`, controllers, services e `DataSeeder`.
- **Verificação:** Testes unitários em `RegrasNegocioTest` e execução da API.

### Interação 4 — Ajuste da Curadoria de Sugestões e Testes Unitários (Semana 4)
- **Problema:** O aceite de sugestão criava um cenário automaticamente (contrariando a Regra 4) e faltavam os 3 testes unitários obrigatórios.
- **Ferramenta / Prompt:** Assistente de IA — "Ajuste a curadoria para não alterar cenários automaticamente no aceite e crie testes unitários JUnit 5 + Mockito para as regras obrigatórias."
- **Proposta da IA:** Remover a criação automática de cenário em `SugestaoService.realizarCuradoria` e criar `RegrasNegocioTest` cobrindo cenário sem Então, rejeição sem motivo e exportação bloqueada com pendência.
- **Decisão do grupo:** Aceita.
- **Mudanças realizadas:** `SugestaoService`, `CuradoriaRequestDTO` e `RegrasNegocioTest`.
- **Verificação:** `./mvnw test`.

### Interação 5 — SPA: Seletor de Usuário, Consulta com Filtro e Resumo do Projeto (Semana 4)
- **Problema:** A SPA enviava `proprietarioId` em vez de usar `X-Usuario-Id`, não tinha filtro por prioridade nem tela de resumo do projeto.
- **Ferramenta / Prompt:** Assistente de IA — "Atualize a SPA para carregar usuários e projetos da API, enviar X-Usuario-Id nas requisições, filtrar histórias por prioridade e exibir a aba de Resumo do Projeto."
- **Proposta da IA:** Substituir inputs numéricos por `<select>` populados da API, adicionar tabela/lista com contagens e filtro, e criar a aba Resumo consumindo `/api/projetos/{id}/resumo`.
- **Decisão do grupo:** Aceita com sanitização `escapeHtml` para evitar XSS.
- **Mudanças realizadas:** `src/main/resources/static/index.html`.
- **Verificação:** Navegação completa na SPA em `http://localhost:8080`.

### Interação 6 — SPA: Edição, Reordenação de Passos e Curadoria Lado a Lado (Semana 4)
- **Problema:** Não era possível editar histórias/cenários, reordenar passos ou visualizar a história ao lado das sugestões na curadoria.
- **Ferramenta / Prompt:** Assistente de IA — "Implemente edição e exclusão de histórias e cenários com botões de reordenação de passos e exiba a história selecionada ao lado da fila de curadoria."
- **Proposta da IA:** Adicionar botões `↑` e `↓` nos passos, formulários de edição reutilizando os cards e painel da história na aba de Sugestões.
- **Decisão do grupo:** Aceita.
- **Mudanças realizadas:** `src/main/resources/static/index.html` e restrição de origens em `CorsConfig.java`.
- **Verificação:** Fluxo completo na SPA (criar, editar, reordenar, curar sugestão e exportar `.feature`).
