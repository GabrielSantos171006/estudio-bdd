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
