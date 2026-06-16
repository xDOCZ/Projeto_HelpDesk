# Projeto Helpdesk

Este projeto foi ajustado para usar SQL Server.

## Configuração SQL Server

1. Crie a base de dados usando o script:
   - `sql/helpdesk_sqlserver.sql`

2. Defina as variáveis de ambiente, se quiser usar valores diferentes dos padrões:
   - `DB_URL` (padrão: `jdbc:sqlserver://localhost:1433;databaseName=helpdesk`)
   - `DB_USER` (padrão: `sa`)
   - `DB_PASSWORD` (padrão: `1234`)

3. O usuário padrão é:
   - login: `admin`
   - senha: `admin`

## Execução

- Rode `run.bat` na raiz do projeto.
- Ou use a configuração `.vscode/launch.json` para executar com o driver SQL Server no classpath.

## Observações

- O driver JDBC SQL Server já está incluído em `lib/mssql-jdbc-12.2.0.jre8.jar`.
- Se o banco não estiver disponível, o sistema ainda salva localmente em `data/chamados.txt`.
