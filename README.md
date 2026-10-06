# InovaGAB | Backend — Sprint 2

API REST desenvolvida para o **InovaGAB**, aplicativo de gestão da inovação no contexto do desafio do Grupo Águia Branca. A plataforma organiza orientações estratégicas, ideias de colaboradores, projetos e indicadores para a liderança, com autenticação e permissões por perfil.

**Instituição:** FIAP  
**Curso:** Análise e Desenvolvimento de Sistemas (ADS)  
**Entrega:** Sprint 2

> Este repositório contém o **backend**. O projeto Android e o APK fazem parte de uma entrega separada. Este README descreve como executar a API e seus contratos principais; a documentação complementar está em [`ENDPOINTS.md`](ENDPOINTS.md).

## 1. Tecnologias e arquitetura

| Tecnologia | Utilização |
|---|---|
| Java 17 | Linguagem e versão do projeto |
| Spring Boot 3.3.4 | Inicialização e execução da API |
| Spring Web | Controllers REST e respostas HTTP |
| Spring Security | Autenticação e autorização por perfil |
| JWT (JJWT 0.11.5) | Token de acesso às rotas protegidas |
| Spring Data MongoDB | Persistência documental |
| MongoDB Atlas ou MongoDB local | Banco de dados NoSQL |
| Maven | Dependências, testes e empacotamento |
| Google Gemini 2.5 Flash | Geração de insights gerenciais, recurso Plus que exige configuração de chave |

**Organização do código:**

```text
inovagab-backend/
├── pom.xml
├── README.md
├── ENDPOINTS.md
└── src/
    ├── main/
    │   ├── java/br/com/fiap/inovagab/backend/
    │   │   ├── config/       # Segurança e usuários de demonstração
    │   │   ├── controller/   # Rotas e respostas HTTP
    │   │   ├── dto/          # Contratos de entrada e saída
    │   │   ├── exception/    # Tratamento de erros
    │   │   ├── model/        # Documentos do MongoDB
    │   │   ├── repository/   # Operações de persistência
    │   │   ├── security/     # JWT e usuário autenticado
    │   │   └── service/      # Regras de negócio e integração de IA
    │   └── resources/application.properties
    └── test/java/           # Testes unitários existentes
```

Fluxo simplificado: **Android/Insomnia → Controller → Service → Repository → MongoDB**. O Spring Security valida o JWT antes das chamadas protegidas. A rota de IA usa os dados do dashboard para consultar a API do Gemini a partir do backend, sem expor a chave ao Android.

## 2. Pré-requisitos

- **JDK 17** configurado no projeto e no Maven. O projeto foi desenvolvido para Java 17; utilizar JDK 24 com a versão de Lombok deste projeto pode causar erro de compilação.
- **Maven 3.9+**, ou suporte Maven do IntelliJ IDEA.
- **MongoDB Atlas** acessível ou MongoDB local em execução.
- Conexão com a internet para baixar as dependências na primeira execução; a funcionalidade Gemini também depende de acesso externo.
- IntelliJ IDEA ou IDE compatível, opcional para execução por terminal.

No Atlas, habilite o cluster, crie um **usuário de banco de dados** com permissões adequadas e autorize o IP público do computador em **Network Access**. A senha do banco de dados é diferente da chave JWT e da senha dos usuários de demonstração.

## 3. Configuração do ambiente

O arquivo `src/main/resources/application.properties` lê as variáveis abaixo. **Não inclua credenciais reais no código, no GitHub, no APK ou no ZIP da entrega.**

| Variável | Necessária? | Finalidade |
|---|---|---|
| `MONGODB_URI` | Sim para Atlas; opcional com MongoDB local padrão | URI de conexão; deve informar o banco `inovagab` |
| `JWT_SECRET` | **Sim** | Segredo aleatório exclusivo, com pelo menos 32 bytes em UTF-8, para assinatura HS256 |
| `DEMO_SEED_ENABLED` | Opcional | `true` habilita a criação de contas didáticas na inicialização; padrão `false` |
| `DEMO_SEED_PASSWORD` | Se seed habilitado | Senha das contas didáticas recém-criadas |
| `GEMINI_API_KEY` | Apenas para geração real de insights com IA | Chave do Google AI Studio |
| `GEMINI_API_URL` | Opcional | URL do modelo; padrão `gemini-2.5-flash:generateContent` |
| `JWT_EXPIRATION_MS` | Opcional | Validade do JWT; padrão `86400000` ms (24 h) |
| `PORT` | Opcional | Porta do servidor; padrão `8080` |

Exemplo **ilustrativo** de URI Atlas, substituindo usuário, senha e cluster pelos dados do próprio ambiente:

```text
mongodb+srv://USUARIO:SENHA@SEU_CLUSTER.mongodb.net/inovagab?retryWrites=true&w=majority
```

**Atenção:** o trecho `/inovagab` define o banco. A ausência do nome provoca `Database name must not be empty`. Caracteres especiais na senha da URI precisam estar codificados corretamente para uma URL.

### No IntelliJ IDEA (Windows)

1. Abra a pasta `inovagab-backend` como projeto Maven e aguarde a sincronização do `pom.xml`.
2. Em **File → Project Structure → Project**, selecione **SDK Java 17**; confira também o JDK do Maven, se necessário.
3. Abra `InovagabBackendApplication.java` e use o botão verde **Run** para criar a configuração de execução.
4. Em **Run → Edit Configurations → Environment variables**, informe `MONGODB_URI`, `JWT_SECRET` e, para a demonstração, `DEMO_SEED_ENABLED=true` e `DEMO_SEED_PASSWORD`.
5. Se for demonstrar IA real, informe também `GEMINI_API_KEY`.
6. Salve e execute `InovagabBackendApplication.java`.

A aplicação estará disponível em `http://localhost:8080`, salvo alteração da variável `PORT`. Mensagens como `Tomcat started on port 8080` e `Started InovagabBackendApplication` indicam inicialização; execute uma chamada à API para confirmar a operação com o banco.

### Pelo terminal

Configure as variáveis de ambiente **na sessão do terminal** antes de iniciar o processo. Exemplo PowerShell com MongoDB local:

```powershell
$env:MONGODB_URI="mongodb://localhost:27017/inovagab"
$env:JWT_SECRET="COLOQUE_AQUI_UM_SEGREDO_ALEATORIO_COM_PELO_MENOS_32_BYTES"
$env:DEMO_SEED_ENABLED="true"
$env:DEMO_SEED_PASSWORD="ESCOLHA_UMA_SENHA_PRIVADA_DE_TESTE"
mvn spring-boot:run
```

O exemplo contém apenas marcadores; **substitua todos eles por valores próprios**. Para MongoDB Atlas, informe a URI do seu cluster em `MONGODB_URI`.

Para executar testes e gerar o JAR:

```bash
mvn clean test
mvn clean package
java -jar target/inovagab-backend.jar
```

O `java -jar` também precisa receber as variáveis de ambiente no terminal ou no ambiente em que for executado. Os comandos acima são instruções de reprodução, não um registro de que os testes passaram em todos os computadores.

## 4. Contas de demonstração

Com `DEMO_SEED_ENABLED=true` e `DEMO_SEED_PASSWORD` definida, o backend cria **somente as contas ainda inexistentes**:

| Perfil | E-mail |
|---|---|
| Operador | `operador@inovagab.com` |
| Gestor | `gestor@inovagab.com` |
| Líder | `lider@inovagab.com` |

A senha é o valor privado de `DEMO_SEED_PASSWORD`. Se a conta já existir, o seeder **não redefine sua senha**. Desative o seed em ambientes públicos.

## 5. Autenticação e primeiro acesso

**Login:**

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json
```

```json
{
  "email": "lider@inovagab.com",
  "senha": "SENHA_DE_DEMONSTRACAO"
}
```

Resposta de sucesso: **`200 OK`**, contendo `token`, `uid`, `nome`, `email` e `perfil`. Nas demais rotas protegidas:

```http
Authorization: Bearer SEU_JWT
```

`JWT_SECRET` é **a chave do servidor**, não o token a ser enviado pelo aplicativo. Não compartilhe JWTs reais em relatórios ou prints.

## 6. Endpoints principais

Base URL: `http://localhost:8080`. Salvo o login, as rotas listadas exigem usuário autenticado com Bearer JWT.

| Método | Rota | Acesso e finalidade |
|---|---|---|
| `POST` | `/api/auth/login` | Público; autenticar e receber token |
| `POST` | `/api/auth/cadastro` | Líder; cadastrar usuário |
| `GET` | `/api/orientacoes` | Autenticados; listar todas, inclusive não vigentes |
| `GET` | `/api/orientacoes/vigentes` | Autenticados; listar apenas vigentes |
| `GET` | `/api/orientacoes/{id}` | Autenticados; consultar uma orientação |
| `GET` | `/api/orientacoes/{id}/historico` | Autenticados; histórico de alterações |
| `POST` | `/api/orientacoes` | Líder; criar orientação |
| `PUT` | `/api/orientacoes/{id}` | Líder; atualizar orientação vigente |
| `DELETE` | `/api/orientacoes/{id}` | Líder; desativar orientação (exclusão lógica) |
| `GET` | `/api/ideias` | Operador: próprias ideias; gestor/líder: todas |
| `GET` | `/api/ideias/{id}` | Operador: somente própria ideia; gestor/líder: consulta |
| `POST` | `/api/ideias` | Operador; criar ideia |
| `PUT`, `DELETE` | `/api/ideias/{id}` | Operador; própria ideia ainda pendente |
| `PATCH` | `/api/ideias/{id}/status` | Gestor; priorizar, aprovar ou rejeitar |
| `GET` | `/api/projetos`, `/api/projetos/{id}` | Autenticados; consultar projetos |
| `POST` | `/api/projetos` | Gestor; criar projeto |
| `PUT`, `DELETE` | `/api/projetos/{id}` | Gestor responsável; atualizar ou excluir |
| `PATCH` | `/api/projetos/{id}/resultados` | Gestor responsável; registrar resultados |
| `GET` | `/api/dashboard/resumo` | Líder; indicadores gerais |
| `GET` | `/api/dashboard/estrategias/{id}` | Líder; indicadores da estratégia |
| `GET` | `/api/dashboard/projetos/{id}` | Líder; indicadores do projeto |
| `GET` | `/api/dashboard/insight` | Líder; insight com Gemini quando configurado |

Exemplos dos principais **payloads de entrada**:

**Criar orientação como líder:**

```json
{
  "titulo": "Melhorar a eficiência da frota",
  "descricao": "Reduzir o consumo de combustível na operação.",
  "categoria": "Eficiência operacional",
  "campanha": "InovaGAB 2026"
}
```

**Criar ideia como operador:**

```json
{
  "titulo": "Treinamento de direção econômica",
  "descricao": "Capacitar motoristas em práticas de direção eficiente.",
  "orientacaoId": "ID_DE_ORIENTACAO_VIGENTE"
}
```

**Avaliar ideia como gestor:**

```json
{
  "status": "aprovada",
  "prioridade": 5
}
```

Os status aceitos são `pendente`, `aprovada` e `rejeitada`; a prioridade não pode ser negativa.

**Criar projeto como gestor:**

```json
{
  "titulo": "Projeto piloto de direção econômica",
  "descricao": "Treinamento inicial em uma unidade.",
  "etapa": "piloto",
  "status": "em_andamento",
  "investimento": 10000,
  "retornoFinanceiro": 14000,
  "ganhosProdutividade": 8,
  "prazo": "2026-12-15",
  "orientacaoId": "ID_DE_ORIENTACAO_VIGENTE",
  "ideiaId": null,
  "resultadosObtidos": ""
}
```

`ideiaId` pode ficar `null`; se informado, deve apontar para uma ideia **aprovada** e vinculada à mesma orientação. Investimento, retorno financeiro e ganho de produtividade não podem ser negativos.

**Registrar resultados no projeto:**

```json
{
  "resultadosObtidos": "Redução de consumo observada no piloto."
}
```

**Códigos HTTP principais:** `200 OK` (consulta/atualização), `201 Created` (criação), `204 No Content` (remoção/desativação), `400 Bad Request` (validação/regra de negócio), `401 Unauthorized` (credenciais/JWT ausentes ou inválidos), `403 Forbidden` (sem permissão) e `404 Not Found` (registro inexistente). O endpoint de IA pode retornar `200` com uma mensagem explicativa quando sua chave não está configurada ou quando a consulta externa falha; isso **não** comprova geração real pelo modelo.

## 7. Regras de negócio resumidas

- **Líder:** gerencia orientações, cadastra usuários e consulta dashboard/insights.
- **Operador:** cadastra ideias vinculadas a uma orientação vigente e pode editar/excluir apenas as próprias ideias pendentes.
- **Gestor:** consulta as ideias, altera status/prioridade, cria projetos vinculados a orientação vigente e registra seus resultados; apenas o gestor responsável pode alterar/excluir o próprio projeto.
- **Histórico:** `DELETE /api/orientacoes/{id}` altera `vigente` para `false`, preservando o documento. Atualizações e desativações registram a versão anterior em `orientacoes_historico`.

## 8. Inteligência artificial (Plus)

A funcionalidade de IA está implementada no código do backend em `IAController` e `IAService`. A rota `GET /api/dashboard/insight`, restrita ao líder, reúne números do dashboard e monta um prompt que solicita ao **Gemini 2.5 Flash** um parágrafo em português com insights e sugestões gerenciais. A IA **não aprova ideias nem projetos automaticamente**.

Para executar a chamada real, configure `GEMINI_API_KEY` **somente no ambiente do backend** e consulte a rota com o token do líder. Se a chave estiver ausente, o serviço devolve um texto informando que a IA não está configurada. **A chamada real ao Gemini e sua visualização no aplicativo devem ser verificadas antes da demonstração do recurso Plus.**

## 9. Integração Android

O endereço base depende de onde o backend é executado:

| Cenário | URL base |
|---|---|
| Insomnia no computador do backend | `http://localhost:8080/` |
| Emulador Android padrão, backend no mesmo computador do emulador | `http://10.0.2.2:8080/` |
| Celular físico, na mesma rede do computador do backend | `http://IP_LOCAL_DO_COMPUTADOR:8080/` |
| Android e backend em computadores de redes diferentes | Executar backend também no computador do emulador ou disponibilizar uma API em endereço acessível |

O app precisa da permissão `INTERNET` e, para desenvolvimento por HTTP local, configuração de tráfego claro compatível com a versão Android. Para uma implantação pública, prefira **HTTPS** e desabilite credenciais de demonstração. `10.0.2.2` **não é** endereço público e não permite que um APK funcione sozinho em qualquer celular.


## 10. DevOps, Docker e CI/CD

### 10.1 Containerização

A aplicação possui um `Dockerfile` para criação da imagem do backend e um `docker-compose.yml` para execução do ambiente local.

O ambiente Docker local utiliza:

- **Backend:** aplicação Spring Boot com Java 17;
- **MongoDB:** banco de dados utilizado pela aplicação;
- **Volume:** persistência dos dados do MongoDB;
- **Network:** comunicação entre os containers;
- **Variáveis de ambiente:** configuração de credenciais e parâmetros sem armazená-los diretamente no código.

Para executar o ambiente local:

```bash
docker compose up --build
```

A API fica disponível em:

```text
http://localhost:8080
```

### 10.2 Pipeline CI/CD

Foi implementado um pipeline de integração e entrega contínua utilizando **GitHub Actions**.

O arquivo do pipeline está localizado em:

```text
.github/workflows/ci-cd.yml
```

O pipeline é acionado automaticamente a partir de alterações enviadas para as branches configuradas do projeto.

### 10.3 Etapas do pipeline

O pipeline possui as seguintes etapas:

#### 1. Build e Testes

- Checkout do código-fonte;
- Configuração do Java 17 utilizando Eclipse Temurin;
- Configuração do cache do Maven;
- Execução do build da aplicação;
- Execução dos testes automatizados utilizando Maven.

Comando utilizado:

```bash
mvn clean package -DskipTests=false
```

#### 2. Build da Imagem Docker

Após a conclusão do build e dos testes, o pipeline realiza a construção da imagem Docker da aplicação.

Essa etapa utiliza o `Dockerfile` existente no projeto e valida se a imagem pode ser construída corretamente.

#### 3. Deploy em Staging

Quando ocorre um push na branch `staging`, o pipeline executa automaticamente o deploy no ambiente de Staging.

O deploy é realizado através de um Deploy Hook do Render, armazenado como GitHub Secret.

#### 4. Deploy em Produção

Quando ocorre um push na branch `main`, o pipeline executa automaticamente o deploy no ambiente de Produção utilizando o Deploy Hook configurado no GitHub.

### 10.4 Fluxo do CI/CD

O fluxo implementado pode ser representado da seguinte forma:

```text
Desenvolvimento
       ↓
   Git Push
       ↓
 GitHub Actions
       ↓
 Build + Testes
       ↓
Build da Imagem Docker
       ↓
   ┌───────────────┐
   │               │
   ↓               ↓
staging           main
   ↓               ↓
Deploy            Deploy
Staging           Produção
   ↓               ↓
Render            Render
```

Dessa forma, alterações enviadas para `staging` são direcionadas ao ambiente de Staging, enquanto alterações enviadas para `main` são direcionadas ao ambiente de Produção.

### 10.5 Ambientes

| Ambiente | Branch | Plataforma | Estratégia de Deploy |
|---|---|---|---|
| Staging | `staging` | Render | Automático via GitHub Actions |
| Produção | `main` | Render | Automático via GitHub Actions |

O ambiente de Staging é utilizado para validar a aplicação antes de alterações serem disponibilizadas em Produção.

### 10.6 Gerenciamento de Secrets

As informações sensíveis utilizadas pela aplicação e pelo processo de deploy não são armazenadas diretamente no código-fonte.

Entre as configurações protegidas estão:

- `MONGODB_URI`;
- `JWT_SECRET`;
- `DEMO_SEED_PASSWORD`;
- Deploy Hook do ambiente de Staging;
- Deploy Hook do ambiente de Produção.

Os Deploy Hooks utilizados pelo pipeline são armazenados no GitHub Secrets.

As configurações de ambiente da aplicação são fornecidas por variáveis de ambiente no ambiente de execução.

### 10.7 Deploy e validação

O pipeline foi validado utilizando a branch `staging`.

Durante a validação foram executadas com sucesso as etapas de:

- Build e testes da aplicação;
- Build da imagem Docker;
- Deploy automático no ambiente de Staging.

Após o deploy, o serviço de Staging ficou disponível no Render e a API foi validada através de uma requisição de autenticação.

O ambiente de Produção também possui deploy automatizado através da branch `main`.

## 11. Problemas comuns

| Sintoma | Verificação inicial |
|---|---|
| `TypeTag :: UNKNOWN` na compilação | Confirme **JDK 17** no IntelliJ e no Maven; evite JDK 24 nesta configuração |
| `Database name must not be empty` | Inclua `/inovagab` na URI do MongoDB |
| Falha de conexão com Atlas | Cluster ativo, usuário/senha, IP autorizado e acesso à internet |
| `401 Unauthorized` | Login, validade do JWT e cabeçalho `Authorization: Bearer ...` |
| `403 Forbidden` | Confira o perfil autenticado e o dono do registro |
| `200 OK` com `[]` | Consulta válida, mas nenhum documento corresponde ao filtro |
| Não conecta pelo Android | Confira URL base, porta, onde o backend roda, internet/permissões e HTTP local |
| Insight informa IA não configurada | Defina `GEMINI_API_KEY` no backend; não a coloque no app |

---

**Segurança:** não inclua credenciais do MongoDB, senha de demonstração, `JWT_SECRET`, `GEMINI_API_KEY` ou tokens JWT reais nos arquivos entregues. Configure-os separadamente no ambiente de quem executará a API.
