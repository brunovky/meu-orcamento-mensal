
# Meu Orçamento Mensal 📊

Aplicativo Android para **gestão de orçamento mensal**, permitindo o registro e acompanhamento de gastos recorrentes, com envio de notificações de vencimento.

O app foi desenvolvido com **Jetpack Compose**, seguindo arquitetura **Clean Architecture + MVVM + Hilt DI**, com foco em boas práticas e manutenibilidade.

---

## ✨ **Funcionalidades**

- Cadastro de gastos com nome, valor, data de vencimento e status (Pago/Em aberto)
- Edição e exclusão de gastos
- Swipe para excluir com confirmação
- Cópia de gastos para um próximo mês
- Notificações diárias para avisar sobre gastos vencendo no dia
- Configuração personalizada do horário de notificação
- Persistência offline com Room e DataStore
- Interface moderna utilizando Material Design 3

---

## ⚙️ **Tecnologias e Bibliotecas**

| **Tecnologia**                | **Descrição**                        |
| ---------------------------- | ------------------------------------ |
| **Kotlin**                    | Linguagem principal do projeto      |
| **Jetpack Compose**           | UI declarativa                       |
| **Hilt**                      | Injeção de dependências              |
| **Room**                      | Banco de dados local SQLite          |
| **DataStore Preferences**     | Armazenamento de configurações       |
| **WorkManager**               | Agendamento de notificações          |
| **Coroutines/Flow**           | Programação assíncrona e reativa     |
| **Material 3 (Compose)**      | Design moderno e responsivo          |

---

## 🗂️ **Estrutura do Projeto**

```
com.brunooliveira.meuorcamentomensal
│
├── data
│   ├── local              // Room (DAO, Database, Entity, Converters)
│   ├── mapper             // Mapeamento Entity ↔ Model
│   └── repository         // Implementações dos repositórios
│   └── worker             // Worker de notificações (ExpenseReminderWorker)
│
├── domain
│   ├── model              // Modelos de domínio (Expense)
│   ├── repository         // Interfaces dos repositórios
│   └── usecase            // Casos de uso (ExpenseUseCases)
│
├── di                     // Módulos Hilt (Database, Repository, UseCase, Preferences)
│
├── presentation            // UI e lógica de tela (MVVM)
│   ├── home                // Tela inicial (listar gastos)
│   ├── add                 // Tela de adição de gasto
│   ├── edit                // Tela de edição de gasto
│   ├── settings            // Tela de configurações
│   └── common              // Utils de UI (ex: CurrencyVisualTransformation)
│
├── ui.theme               // Definição de temas e cores do app
│
├── notification           // Funções auxiliares para notificações
│
├── Constants.kt           // Constantes do projeto
├── MainActivity.kt        // Entry point da aplicação
└── MyApplication.kt       // Classe Application com Hilt
```

---

## 🚀 **Como rodar o projeto**

1. Clone o repositório:
   ```
   git clone https://github.com/brunovky/meu-orcamento-mensal.git
   ```

2. Abra no **Android Studio Hedgehog ou mais recente**

3. Compile e rode em um emulador ou dispositivo real com **Android 9 (API 27)** ou superior.

---

## 🔧 **Configurações e Customizações**

- O horário padrão das notificações é **9h da manhã**, podendo ser alterado na tela de **Configurações**.
- Notificações são reprogramadas automaticamente ao editar um gasto ou alterar a hora.

---

## 🛠️ **Possíveis melhorias futuras**

- Autenticação de usuário (Google/Firebase)
- Backup na nuvem
- Suporte a múltiplas carteiras ou perfis
- Relatórios e gráficos de gastos
- Widget na Home Screen

---

## 🧑‍💻 **Desenvolvedor**

Bruno Oliveira  
[LinkedIn](https://www.linkedin.com/in/broliveira92)  
Desenvolvedor Android Sênior apaixonado por tecnologia, finanças e boas práticas de desenvolvimento.

---

## 📄 **Licença**

Este projeto está licenciado sob a **MIT License**.
