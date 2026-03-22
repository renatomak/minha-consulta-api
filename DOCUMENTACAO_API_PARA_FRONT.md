# Documentacao da API para consumo no Front

Este documento descreve os recursos da API, contratos de request/response, regras de negocio e fluxo recomendado para o front.

## 1) Visao geral

- Base URL padrao: `http://localhost:8080`
- Versao de API: `v1`
- Prefixo de rotas: `/api/v1`
- OpenAPI JSON: `GET /v3/api-docs`
- Swagger UI: `GET /swagger-ui.html`

## 2) Formato de erro (padrao)

A API retorna erros no formato `ProblemDetail` do Spring:

```json
{
  "type": "about:blank",
  "title": "Dados invalidos",
  "status": 400,
  "detail": "coLotacao: must not be null",
  "instance": "/api/v1/agenda/agendar"
}
```

Campos principais para o front:
- `status`: codigo HTTP
- `title`: categoria amigavel de erro
- `detail`: mensagem para exibicao/log

## 3) Recursos

## 3.1 Agenda

### 3.1.1 Obter grade de horarios
- **GET** `/api/v1/agenda/{coLotacao}/grade?data=YYYY-MM-DD`
- Uso: montar agenda diaria por profissional/lotacao.

Path params:
- `coLotacao` (Long)

Query params:
- `data` (LocalDate, obrigatorio, formato `YYYY-MM-DD`)

Response 200 (`GradeResponse`):
```json
{
  "coLotacao": 500001,
  "profissional": "Nome Profissional",
  "cbo": "MEDICO CLINICO",
  "data": "2026-03-23",
  "diaSemana": "MONDAY",
  "bloqueado": false,
  "motivoBloqueio": null,
  "mensagem": null,
  "slots": [
    {
      "horaInicio": "08:20",
      "horaFim": "08:40",
      "periodo": "MANHA",
      "situacao": "DISPONIVEL",
      "coAgendado": null,
      "statusAgendamento": null,
      "paciente": null
    }
  ],
  "totalSlots": 20,
  "slotsDisponiveis": 18,
  "slotsOcupados": 2,
  "slotsBloqueados": 0
}
```

Erros comuns:
- `404 Lotacao nao encontrada`
- `422 Agenda nao configurada`
- `400 Dados invalidos` (parametro data invalido)

---

### 3.1.2 Listar agendamentos por periodo
- **GET** `/api/v1/agenda/{coLotacao}/agendamentos?inicio=YYYY-MM-DD&fim=YYYY-MM-DD`
- Uso: historico/lista para tabela no front.

Regras:
- `inicio <= fim`
- intervalo maximo de 60 dias

Response 200 (`AgendamentoResponse[]`):
```json
[
  {
    "coAgendado": 991000,
    "data": "2026-03-23",
    "horaInicio": "08:20",
    "status": "AGENDADO",
    "motivo": "RETORNO",
    "paciente": "Nome Paciente",
    "cpfPaciente": "12345678901",
    "telefonePaciente": "61999999999",
    "observacao": "texto"
  }
]
```

Erros comuns:
- `400 Dados invalidos` (datas invalidas)
- `400 Periodo excede o limite` (>60 dias)

---

### 3.1.3 Criar agendamento
- **POST** `/api/v1/agenda/agendar`
- Uso: confirmar agendamento no slot selecionado.

Request body (`AgendarRequest`):
```json
{
  "coLotacao": 500001,
  "coProntuario": 900001,
  "data": "2026-03-23",
  "horaInicio": "08:20",
  "coOrigem": 1,
  "coMotivoReserva": 1,
  "observacao": "agendamento via front"
}
```

Campos obrigatorios:
- `coLotacao`, `coProntuario`, `data`, `horaInicio`, `coOrigem`

Observacoes:
- `coMotivoReserva` e opcional; se nao enviado, backend usa `1`.

Response 201 (`AgendamentoCriadoResponse`):
```json
{
  "coAgendado": 991000,
  "coLotacao": 500001,
  "coProntuario": 900001,
  "data": "2026-03-23",
  "horaInicio": "08:20",
  "status": "AGENDADO",
  "uuidAgendamento": "a3f2..."
}
```

Regras de negocio importantes:
- lotacao deve existir e estar ativa
- prontuario deve existir
- profissional deve ter agenda configurada
- nao permite fim de semana
- nao permite data passada
- slot deve existir e estar `DISPONIVEL`
- nao permite duplicidade no mesmo dia/profissional para o mesmo prontuario

Erros comuns:
- `404 Lotacao nao encontrada`
- `404 Recurso nao encontrado` (prontuario)
- `422 Agenda nao configurada`
- `409 Slot indisponivel`
- `409 Agendamento duplicado`
- `400 Dados invalidos`

---

### 3.1.4 Cancelar agendamento
- **PATCH** `/api/v1/agenda/{coAgendado}/cancelar`

Request body (`CancelarRequest`):
```json
{
  "motivoCancelamento": "CIDADAO",
  "observacao": "cancelado pelo paciente"
}
```

Validacao:
- `motivoCancelamento` obrigatorio e deve ser `CIDADAO` ou `PROFISSIONAL`.

Response 200 (`CancelamentoResponse`):
```json
{
  "coAgendado": 991000,
  "status": "CANCELADO_CIDADAO",
  "mensagem": "Agendamento cancelado com sucesso."
}
```

Erros comuns:
- `404 Recurso nao encontrado` (agendamento)
- `409 Cancelamento invalido` (status nao e AGENDADO)
- `400 Dados invalidos` (body invalido)

## 3.2 Cidadao

### 3.2.1 Buscar cidadao por CPF
- **GET** `/api/v1/cidadao/{cpf}`
- `cpf` deve ter exatamente 11 digitos numericos.

Response 200 (`CidadaoDetalheResponse`):
```json
{
  "coSeqCidadao": 100,
  "nome": "Maria Silva",
  "cpf": "12345678901",
  "cns": "123456789012345",
  "dataNascimento": "1990-05-10",
  "idade": 35,
  "sexo": "F",
  "telefone": "61999999999",
  "email": "maria@email.com",
  "coProntuario": 900001,
  "unidadeSaude": {
    "coSeq": 10,
    "cnes": "0000000",
    "nome": "UBS Centro",
    "endereco": "Rua X, 10 - Centro",
    "cep": "70000000"
  },
  "equipe": {
    "coSeq": 1,
    "nome": "Equipe 1",
    "ine": "0000000000"
  }
}
```

Erros comuns:
- `404 Cidadao nao encontrado`
- `404 Cidadao sem equipe vinculada`
- `400 Dados invalidos` (CPF fora do padrao)

## 3.3 Dominios

### 3.3.1 Motivos de reserva
- **GET** `/api/v1/dominios/motivos-reserva`
- Response: `DominioResponse[]` (`co`, `descricao`)

### 3.3.2 Origens
- **GET** `/api/v1/dominios/origens`
- Response: `DominioResponse[]` (`co`, `descricao`)

### 3.3.3 Situacoes de agendado
- **GET** `/api/v1/dominios/situacoes-agendado`
- Response: `DominioResponse[]` (`co`, `descricao`)

Uso no front:
- popular selects de filtros
- mapear codigos para labels no UI

## 3.4 Equipe

### 3.4.1 Listar profissionais da equipe
- **GET** `/api/v1/equipe/{coSeqEquipe}/profissionais`

Response 200 (`ProfissionalResponse[]`):
```json
[
  {
    "coLotacao": 500001,
    "coProf": 200,
    "nome": "Dr. Joao",
    "cbo": "MEDICO CLINICO",
    "coCbo": 225125,
    "hasAgenda": true
  }
]
```

Uso no front:
- escolher profissional antes de consultar grade
- `coLotacao` sera usado nas rotas de agenda

## 4) Fluxo recomendado no front

Fluxo principal para agendamento:
1. Buscar cidadao por CPF (`/cidadao/{cpf}`) para obter `coProntuario` e equipe.
2. Listar profissionais da equipe (`/equipe/{coSeqEquipe}/profissionais`).
3. Selecionar profissional e carregar grade (`/agenda/{coLotacao}/grade?data=...`).
4. Selecionar slot `DISPONIVEL`.
5. Carregar dominios (`/dominios/origens` e `/dominios/motivos-reserva`) para montar payload.
6. Enviar POST de agendamento (`/agenda/agendar`).
7. Atualizar lista com `/agenda/{coLotacao}/agendamentos`.
8. Se necessario, cancelar via PATCH (`/agenda/{coAgendado}/cancelar`).

## 5) Recomendacoes de implementacao no front

- Centralizar cliente HTTP (timeout, base URL, interceptors)
- Tipar contratos por endpoint (DTO de entrada/saida)
- Tratar erros por `status` e `title`
- Exibir mensagens de negocio vindas em `detail`
- Invalidar/atualizar cache apos criar/cancelar agendamento
- Evitar enviar request se slot nao estiver `DISPONIVEL`

## 6) cURL base para testes rapidos

```bash
curl -X GET "http://localhost:8080/api/v1/dominios/origens"
```

```bash
curl -X GET "http://localhost:8080/api/v1/cidadao/12345678901"
```

```bash
curl -X GET "http://localhost:8080/api/v1/equipe/1/profissionais"
```

```bash
curl -X GET "http://localhost:8080/api/v1/agenda/500001/grade?data=2026-03-23"
```

```bash
curl -X POST "http://localhost:8080/api/v1/agenda/agendar" \
  -H "Content-Type: application/json" \
  -d '{
    "coLotacao": 500001,
    "coProntuario": 900001,
    "data": "2026-03-23",
    "horaInicio": "08:20",
    "coOrigem": 1,
    "coMotivoReserva": 1,
    "observacao": "agendamento via front"
  }'
```

```bash
curl -X PATCH "http://localhost:8080/api/v1/agenda/991000/cancelar" \
  -H "Content-Type: application/json" \
  -d '{
    "motivoCancelamento": "CIDADAO",
    "observacao": "cancelado"
  }'
```

## 7) Checklist de compatibilidade para o front

- [ ] Formato de datas em `YYYY-MM-DD`
- [ ] CPF com 11 digitos para endpoint de cidadao
- [ ] Validacao de periodo maximo de 60 dias na busca de agendamentos
- [ ] Mapeamento de erros `ProblemDetail` (title/detail/status)
- [ ] Recarregar grade/lista apos criacao ou cancelamento
- [ ] Usar dominios para labels e ids de origem/motivo

