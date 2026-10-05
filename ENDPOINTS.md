# Endpoints InovaGAB — Sprint 2

Base URL local: `http://localhost:8080`. Todos, exceto login, exigem Bearer JWT. `POST /api/auth/cadastro` é exclusivo do líder.

| Método | Rota | Role |
|---|---|---|
| POST | /api/auth/login | Público |
| POST | /api/auth/cadastro | LIDER |
| GET | /api/orientacoes | Qualquer autenticado |
| GET | /api/orientacoes/vigentes | Qualquer autenticado |
| GET | /api/orientacoes/{id} | Qualquer autenticado |
| GET | /api/orientacoes/{id}/historico | Qualquer autenticado |
| POST, PUT, DELETE | /api/orientacoes, /api/orientacoes/{id} | LIDER |
| GET | /api/ideias, /api/ideias/{id} | Qualquer autenticado, operador apenas as suas |
| POST | /api/ideias | OPERADOR |
| PUT, DELETE | /api/ideias/{id} | OPERADOR, apenas própria ideia pendente |
| PATCH | /api/ideias/{id}/status | GESTOR |
| GET | /api/projetos, /api/projetos/{id} | Qualquer autenticado |
| POST | /api/projetos | GESTOR |
| PUT, DELETE | /api/projetos/{id} | GESTOR criador |
| PATCH | /api/projetos/{id}/resultados | GESTOR criador |
| GET | /api/dashboard/resumo | LIDER |
| GET | /api/dashboard/estrategias/{id} | LIDER |
| GET | /api/dashboard/projetos/{id} | LIDER |
| GET | /api/dashboard/insight | LIDER, com chave Gemini configurada |

**Exemplos de payload**

```http
POST /api/auth/login
{"email":"lider@inovagab.com","senha":"SENHA_DEMONSTRACAO"}
```

```json
// POST /api/orientacoes ou PUT /api/orientacoes/{id}
{"titulo":"Eficiência operacional","descricao":"Reduzir desperdícios","categoria":"eficiencia","campanha":"2026"}
```

```json
// POST /api/ideias ou PUT /api/ideias/{id}
{"titulo":"Automatizar triagem","descricao":"Reduzir a espera","orientacaoId":"ID_DE_ORIENTACAO_VIGENTE"}
```

```json
// PATCH /api/ideias/{id}/status
{"status":"aprovada","prioridade":5}
```

```json
// POST /api/projetos ou PUT /api/projetos/{id}
{"titulo":"Piloto de triagem","descricao":"Teste com 1 unidade","etapa":"piloto","status":"em_andamento","investimento":10000,"retornoFinanceiro":14000,"ganhosProdutividade":8,"prazo":"2026-12-15","orientacaoId":"ID_DE_ORIENTACAO_VIGENTE","ideiaId":null,"resultadosObtidos":"Impacto no tempo de espera"}
```

```json
// PATCH /api/projetos/{id}/resultados
{"resultadosObtidos":"10 horas de trabalho economizadas por semana"}
```

Status esperados: 200 (consulta/atualização), 201 (criação), 204 (remoção ou desativação), 400 (validação ou estratégia não vigente), 401 (login/token inválido ou ausente), 403 (perfil/dono), 404 (registro inexistente). `DELETE /api/orientacoes/{id}` desativa em vez de excluir fisicamente.
