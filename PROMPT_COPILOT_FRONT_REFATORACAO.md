# Prompt para repassar ao Copilot do Front

Copie o texto abaixo e use como prompt no projeto front.

---

Voce vai refatorar a aplicacao front que consome a API de agendamento.

## Contexto da API
- Base URL: `http://localhost:8080`
- Prefixo: `/api/v1`
- Contratos e fluxos: use como fonte principal o arquivo `DOCUMENTACAO_API_PARA_FRONT.md`.
- Formato de erro da API: `ProblemDetail` com `status`, `title`, `detail`.

## Objetivo
Refatorar a camada de consumo HTTP e os fluxos de agendamento para ficar tipado, previsivel, facil de manter e com UX consistente para sucesso/erro.

## Requisitos obrigatorios
1. Criar/organizar uma camada de API client unica (ex: `apiClient`).
2. Criar modulos por recurso:
   - `agendaApi`
   - `cidadaoApi`
   - `dominiosApi`
   - `equipeApi`
3. Tipar requests/responses por endpoint, seguindo os contratos da documentacao.
4. Implementar parser de erro padrao para `ProblemDetail`.
5. Tratar estados de UI: `idle`, `loading`, `success`, `error`.
6. Implementar fluxo completo de agendamento:
   - buscar cidadao por CPF
   - listar profissionais por equipe
   - obter grade por lotacao/data
   - criar agendamento
   - listar agendamentos
   - cancelar agendamento
7. Garantir invalidacao/atualizacao de dados apos criar/cancelar.
8. Nao quebrar telas existentes (manter comportamento funcional).

## Regras de negocio que devem ser refletidas na UI
- `agendamentos` aceita periodo maximo de 60 dias.
- `agendar` nao aceita data passada/fim de semana (mostrar erro de forma amigavel).
- `cancelar` exige `motivoCancelamento` em `CIDADAO` ou `PROFISSIONAL`.
- Slots so podem ser agendados quando `situacao === "DISPONIVEL"`.

## Entregaveis esperados
1. Arquitetura proposta da camada de dados (breve explicacao).
2. Codigo refatorado dos servicos HTTP.
3. Tipos/interfaces dos DTOs.
4. Adaptacao das telas/containers para usar a nova camada.
5. Tratamento de erro padrao com mensagens amigaveis.
6. Pequenos testes (unitarios de parser/servicos e de fluxo critico).
7. Checklist final do que foi alterado.

## Criterios de aceite
- Build da aplicacao front sem erros.
- Fluxo principal de agendamento funcionando ponta a ponta.
- Erros da API exibidos ao usuario com base em `title/detail`.
- Sem duplicacao de logica de fetch entre telas.
- Codigo com responsabilidade separada (API, dominio de tela, componentes).

## Padrao de resposta esperado do Copilot
- Primeiro: plano de refatoracao em etapas.
- Depois: diff/arquivos alterados por etapa.
- Em cada etapa: como validar localmente.
- Final: riscos residuais e proximos passos.

## Observacoes importantes
- Se encontrar divergencia entre tela atual e contrato da API, priorize o contrato da API e documente o ajuste.
- Evite hardcode de mensagens; normalize erros por status/title.
- Mantenha a solucao simples e evolutiva.

---

Se precisar, voce pode me devolver em 2 fases:
1) refatoracao da camada HTTP + tipos
2) adaptacao das telas e fluxos

