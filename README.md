# Vitta API - Backend
API RESTful desenvolvida como o backend para o aplicativo Vitta, uma solução mobile para gestão pessoal de medicamentos.

## 📝 Descrição do Projeto
O Vitta é um aplicativo mobile projetado para auxiliar usuários no controle de seus tratamentos, medicamentos, horários, e todos os detalhes que o usuário precisa saber para ter certeza que sua saúde está em dia. Esta API é o cérebro por trás do aplicativo, responsável por gerenciar toda a lógica de negócio, persistência de dados e segurança das informações dos usuários.
A solução foi projetada para trazer praticidade, segurança e confiabilidade no acompanhamento da rotina medicamentosa dos usuários.

## ✨ Funcionalidades Principais
- 👤 **Gestão de Usuários:** Cadastro, autenticação (via JWT) e gerenciamento do perfil do usuário.
- 🏠 **Painel Principal (Home):** Um painel central que lista os agendamentos do dia e exibe um calendário interativo. O calendário destaca visualmente os dias com agendamentos, permitindo ao usuário selecionar um dia específico para filtrar e visualizar apenas os compromissos daquela data.
- 💊 **Gestão de Medicamentos:** CRUD completo que permite aos usuários cadastrarem seus medicamentos pessoais.
- 🩺 **Gestão de Tratamentos:** Criação e acompanhamento de tratamentos, definindo períodos (data de início e fim), instruções e associando os medicamentos que serão utilizados.
- ⏰ **Agendamentos Inteligentes:** Geração automática de agendamentos (doses) com base nos períodos e frequências definidos em cada Tratamento, com um sistema de alertas/notificações.
- 📜 **Histórico de Uso:** Registro de quando o usuário marcou cada dose como "tomada", permitindo um acompanhamento preciso da adesão ao tratamento.

## 🛠️ Tecnologias Utilizadas
- Linguagem: Java 21
- Framework: Spring Boot 3
- Segurança: Spring Security (com autenticação via JWT)
- Persistência: Spring Data JPA com Hibernate
- Validação: Spring Validation (Bean Validation)
- Banco de Dados (Dev): MySQL
- Banco de Dados (Prod): PostgreSQL
- Documentação API: SpringDoc (OpenAPI/Swagger)
- Build Tool: Maven (ou Gradle)
- Versionamento: Git & GitHub

## 🚀 Começando
### Instalação

1.  Clone o repositório:
    ```sh
    git clone [https://github.com/oj0rel/Vitta_GerenciamentoDeMedicamentos_Backend.git](https://github.com/oj0rel/Vitta_GerenciamentoDeMedicamentos_Backend.git)
    ```
2.  Navegue até o diretório do projeto:
    ```sh
    cd Vitta_GerenciamentoDeMedicamentos_Backend
    ```
3.  Instale as dependências (se usar Maven):
    ```sh
    mvn clean install
    ```
4.  Configure as variáveis de ambiente (veja a próxima seção).
5.  Rode a aplicação:
    ```sh
    mvn spring-boot:run
    ```

## ⚙️ Configuração de Ambiente
Este projeto utiliza **Variáveis de Ambiente** para armazenar informações sensíveis (como senhas de banco de dados e chaves de API). Para executar a aplicação, você precisará configurar as seguintes variáveis:

* `SPRING_DATASOURCE_URL`: A URL de conexão JDBC do seu banco.
    * *Exemplo:* `jdbc:mysql://localhost:3306/vittadb`
* `SPRING_DATASOURCE_USERNAME`: O nome de usuário do banco.
    * *Exemplo:* `root`
* `SPRING_DATASOURCE_PASSWORD`: A senha do banco de dados.
* `API_SECURITY_TOKEN_SECRET`: Uma chave secreta longa e aleatória para a assinatura dos tokens JWT.

## 📚 Documentação da API

A documentação completa da API, com todos os endpoints e modelos de dados, é gerada automaticamente pelo Swagger (SpringDoc).

Após iniciar a aplicação localmente, você pode acessar a interface do Swagger no seu navegador:

`http://localhost:8080/swagger-ui.html`

Abaixo está um resumo dos principais grupos de endpoints da API. Todos os endpoints (exceto os de `Autenticação`) são protegidos e requerem um token JWT.

### 🗓️ Agendamento
*Base: `/api/agendamentos`*

Endpoints para gerenciar os agendamentos (doses) individuais do usuário.

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET` | `/listar` | Lista todos os agendamentos do usuário. Permite filtrar por `dataInicio` e `dataFim`. |
| `GET` | `/listarAgendamentoPorId/{agendamentoId}` | Busca um agendamento único pelo seu ID. |
| `GET` | `/listarAgendamentosDoTratamento/{tratamentoId}` | Lista todos os agendamentos associados a um tratamento específico. |
| `POST` | `/cadastrar` | Cria um novo agendamento manual (não gerado automaticamente pelo tratamento). |
| `PUT` | `/atualizar/{agendamentoId}` | Atualiza os dados de um agendamento (ex: horário, tipo de alerta, status). |
| `DELETE` | `/deletar/{agendamentoId}` | Realiza a exclusão lógica de um agendamento. |
| `POST` | `/concluirAgendamento/{agendamentoId}` | **Ação principal:** Marca um agendamento como "TOMADO" e cria um registro no histórico de uso. |

### 💊 Medicamento
*Base: `/api/medicamentos`*

Endpoints para gerenciar a "biblioteca" pessoal de medicamentos do usuário. Estes são os medicamentos "base" que serão usados para criar os tratamentos.

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET` | `/listar` | Lista todos os medicamentos ativos cadastrados pelo usuário. |
| `GET` | `/listarMedicamentoPorId/{medicamentoId}` | Busca um medicamento específico pelo seu ID. |
| `POST` | `/cadastrar` | Adiciona um novo medicamento à biblioteca pessoal do usuário. |
| `PUT` | `/atualizar/{medicamentoId}` | Atualiza os dados de um medicamento existente. |
| `DELETE` | `/deletar/{medicamentoId}` | Realiza a exclusão lógica (desativação) de um medicamento. |

### 🩺 Tratamento
*Base: `/api/tratamentos`*

Endpoints centrais da aplicação. Um "Tratamento" é a entidade que une um `Medicamento` a um cronograma (datas, frequência, dosagem). Ele é o responsável por **gerar automaticamente** os `Agendamentos`.

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET` | `/listar` | Lista todos os tratamentos (ativos e inativos) do usuário. |
| `GET` | `/listarTratamentoPorId/{tratamentoId}` | Busca um tratamento específico e seus detalhes. |
| `POST` | `/cadastrar` | Cria um novo tratamento. **Ação principal:** Dispara a geração de todos os agendamentos futuros. |
| `PUT` | `/atualizar/{tratamentoId}` | Atualiza os dados de um tratamento (ex: instruções, data de término). |
| `DELETE` | `/deletar/{tratamentoId}` | Realiza a exclusão lógica (desativação) de um tratamento. |

### 📜 Histórico de Uso
*Base: `/api/medicamentoHistoricos`*

Endpoints para gerenciar o histórico de doses tomadas. Estes registros são criados principalmente pela ação de "Concluir Agendamento", mas também podem ser gerenciados manualmente por aqui.

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET` | `/listar` | Lista todos os registros de histórico de uso do usuário. |
| `GET` | `/listarMedicamentoHistoricoPorId/{id}` | Busca um registro de histórico específico. |
| `POST` | `/cadastrar` | Cria um registro de histórico de uso *manualmente*. |
| `PUT` | `/atualizar/{id}` | Atualiza um registro de histórico (ex: observação, dose tomada). |
| `DELETE` | `/deletar/{id}` | Realiza a exclusão lógica de um registro de histórico. |

### 👤 Usuário & Autenticação
*Base: `/api/usuarios`*

Endpoints para gerenciamento de contas de usuário, autenticação (login/cadastro) e listagens de dados agregados.

#### Autenticação (Público)
| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `POST` | `/cadastrar` | Cria uma nova conta de usuário. |
| `POST` | `/login` | Autentica um usuário (email e senha) e retorna um token JWT. |

#### Gerenciamento de Perfil (Protegido)
| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET` | `/buscarMeuPerfil` | Retorna os dados do perfil do usuário logado. |
| `PUT` | `/atualizarMeuPerfil` | Atualiza os dados do perfil (nome, telefone) do usuário logado. |
| `DELETE` | `/deletarMinhaConta` | Realiza a exclusão lógica (desativação) da conta do usuário logado. |

#### Listagens de Dados (Protegido)
| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET` | `/listarMeusAgendamentos` | Retorna o usuário e sua lista de agendamentos. |
| `GET` | `/listarMeusHistoricos` | Retorna o usuário e sua lista de históricos de uso. |
| `GET` | `/listarMeusMedicamentos` | Retorna o usuário e sua lista de medicamentos cadastrados. |
| `GET` | `/listarMeusTratamentos` | Retorna o usuário e sua lista de tratamentos. |
