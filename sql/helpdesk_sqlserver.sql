USE master;
GO

IF DB_ID('helpdesk') IS NULL
    CREATE DATABASE helpdesk;
GO

USE helpdesk;
GO

IF OBJECT_ID('dbo.interacoes', 'U') IS NOT NULL
    DROP TABLE dbo.interacoes;
IF OBJECT_ID('dbo.chamados', 'U') IS NOT NULL
    DROP TABLE dbo.chamados;
IF OBJECT_ID('dbo.usuarios', 'U') IS NOT NULL
    DROP TABLE dbo.usuarios;
GO

CREATE TABLE dbo.usuarios (
    id INT IDENTITY(1,1) PRIMARY KEY,
    login VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(100) NOT NULL,
    tipo  VARCHAR(50)  NOT NULL
);
GO

CREATE TABLE dbo.chamados (
    id INT IDENTITY(1,1) PRIMARY KEY,
    titulo          VARCHAR(200),
    descricao       VARCHAR(500),
    categoria       VARCHAR(100),
    prioridade      INT DEFAULT 1,         
    status          VARCHAR(50) DEFAULT 'Aberto',
    usuario_id      INT,
    tecnico_id      INT,
    data_abertura   DATE,
    data_fechamento DATE NULL,
    CONSTRAINT fk_chamados_usuario FOREIGN KEY (usuario_id) REFERENCES dbo.usuarios(id)
);
GO

CREATE TABLE dbo.interacoes (
    id INT IDENTITY(1,1) PRIMARY KEY,
    chamado_id INT NOT NULL,
    usuario_id INT,
    tipo       VARCHAR(100),
    descricao  VARCHAR(500),
    data_hora  DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_interacoes_chamado FOREIGN KEY (chamado_id) REFERENCES dbo.chamados(id) ON DELETE CASCADE
);
GO

INSERT INTO dbo.usuarios (login, senha, tipo)
VALUES ('admin', 'admin', 'admin');
GO
