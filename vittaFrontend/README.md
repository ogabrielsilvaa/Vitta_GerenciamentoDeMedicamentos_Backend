# 💊 Vitta – Gestor de Uso de Medicamentos

**Vitta** é um aplicativo mobile desenvolvido em **React Native** com **Expo**, criado para ajudar os usuários a **gerenciar o uso de medicamentos pessoais** de forma simples, prática e organizada. O app visa promover a **adesão correta ao tratamento**, evitando esquecimentos e facilitando o acompanhamento da rotina medicamentosa. 💙

---

## 🧩 Funcionalidades

- **Sistema de Login/Cadastro:** cada Usuário pode criar ou logar em seu próprio perfil no aplicativo.
- **Cadastro de Tratamentos:** registre informações completas como medicamento, dosagem, frequência e duração do tratamento.
- **Geração Automática de Agendamentos:** os agendamentos são criados automaticamente com base nos dados do tratamento, definindo os horários e dias em que o medicamento deve ser tomado.
- **Lembretes e Notificações:** receba alertas nos horários corretos para não esquecer nenhum medicamento.
- **Histórico de Uso:** acompanhe todos os medicamentos tomados e exporte seu histórico para consulta médica.
- **Interface Intuitiva:** design limpo, com navegação fluida e componentes reutilizáveis para melhor usabilidade.
- **Gerenciamento Personalizado:** atualize, edite ou remova tratamentos e agendamentos conforme o progresso do tratamento.
- **Controle Visual:** visualize tratamentos ativos, finalizados e agendamentos futuros em seções separadas.

---

## 💼 Regras de Negócio

1. Cada usuário pode cadastrar múltiplos **tratamentos**, cada um associado a um medicamento específico.
2. Ao cadastrar um tratamento, o sistema gera automaticamente os **agendamentos** com base na frequência, horário e duração informados.
3. O sistema deve alertar o usuário nos horários definidos pelos agendamentos.
4. Quando todos os Agendamentos de um Tratamento são finalizados, este Tratamento recebe o status 'CONCLUIDO'.
5. O usuário pode **editar ou excluir** tratamentos e agendamentos ativos a qualquer momento.
6. O histórico pode ser **exportado ou baixado** em formato de arquivo (ex: PDF ou CSV).
7. A interface deve manter **responsividade e acessibilidade**, adaptando-se a diferentes tamanhos de tela.
8. Todos os dados são armazenados localmente (ou via backend, quando conectado ao servidor da aplicação).

---

## ⚙️ Tecnologias Utilizadas

- **React Native**
- **Expo**
- **JavaScript / TypeScript**
- **StyleSheet** (para estilização dos componentes)
- **Componentização** (estrutura modular e reutilizável)

---

## 🚀 Como Executar o Projeto

Siga os passos abaixo para rodar o **Vitta** localmente:

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/SEU_USUARIO/Vitta-App.git
   ```

2. **Acesse o diretório do projeto:**
   ```bash
   cd Vitta-App
   ```

3. **Instale as dependências:**
   ```bash
   npm install
   ```

4. **Execute o aplicativo com o Expo:**
   ```bash
   npx expo start
   ```

5. **Visualize o app:**
   - Escaneie o QR Code no terminal usando o app **Expo Go** (Android/iOS).
   - Ou execute no emulador Android/iOS configurado.

---

## 🎨 Estrutura do Projeto

```
Vitta-App/
│
├── assets/              # Ícones, imagens e fontes
├── components/          # Componentes reutilizáveis
├── screens/             # Telas principais do app
├── services/            # Lógica de negócio e integração com backend
├── styles/              # Estilos globais
├── App.js               # Arquivo principal da aplicação
└── package.json
```

---

## 👥 Autor

Desenvolvido por **Gabriel Santos Gonçalves da Silva** 💻  
📍 Duque de Caxias - RJ  
🔗 [LinkedIn](https://www.linkedin.com/in/ogabrieusiuva)
