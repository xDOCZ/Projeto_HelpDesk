USE helpdesk;
GO

IF OBJECT_ID('dbo.tr_usuarios_bi', 'TR') IS NOT NULL DROP TRIGGER dbo.tr_usuarios_bi;
IF OBJECT_ID('dbo.tr_chamados_bi', 'TR') IS NOT NULL DROP TRIGGER dbo.tr_chamados_bi;
IF OBJECT_ID('dbo.tr_chamados_bu', 'TR') IS NOT NULL DROP TRIGGER dbo.tr_chamados_bu;
GO
IF OBJECT_ID('dbo.sp_chamado_create', 'P') IS NOT NULL DROP PROCEDURE dbo.sp_chamado_create;
IF OBJECT_ID('dbo.sp_chamado_list', 'P') IS NOT NULL DROP PROCEDURE dbo.sp_chamado_list;
IF OBJECT_ID('dbo.sp_chamado_update', 'P') IS NOT NULL DROP PROCEDURE dbo.sp_chamado_update;
IF OBJECT_ID('dbo.sp_chamado_delete', 'P') IS NOT NULL DROP PROCEDURE dbo.sp_chamado_delete;
IF OBJECT_ID('dbo.sp_chamado_finalizar', 'P') IS NOT NULL DROP PROCEDURE dbo.sp_chamado_finalizar;
IF OBJECT_ID('dbo.sp_usuario_create', 'P') IS NOT NULL DROP PROCEDURE dbo.sp_usuario_create;
IF OBJECT_ID('dbo.sp_usuario_login', 'P') IS NOT NULL DROP PROCEDURE dbo.sp_usuario_login;
GO
IF OBJECT_ID('dbo.fn_usuario_id', 'FN') IS NOT NULL DROP FUNCTION dbo.fn_usuario_id;
IF OBJECT_ID('dbo.fn_total_chamados', 'FN') IS NOT NULL DROP FUNCTION dbo.fn_total_chamados;
IF OBJECT_ID('dbo.fn_total_chamados_abertos', 'FN') IS NOT NULL DROP FUNCTION dbo.fn_total_chamados_abertos;
GO
IF OBJECT_ID('dbo.v_chamados', 'V') IS NOT NULL DROP VIEW dbo.v_chamados;
IF OBJECT_ID('dbo.v_usuarios_publicos', 'V') IS NOT NULL DROP VIEW dbo.v_usuarios_publicos;
GO

-- VIEWS --
CREATE VIEW dbo.v_chamados AS
SELECT id, titulo, descricao, categoria, prioridade, status,
       usuario_id, tecnico_id, data_abertura, data_fechamento
FROM dbo.chamados;
GO

CREATE VIEW dbo.v_usuarios_publicos AS
SELECT id, login, tipo
FROM dbo.usuarios;
GO

-- FUNCTIONS --
CREATE FUNCTION dbo.fn_usuario_id(@p_login VARCHAR(100), @p_senha VARCHAR(100))
RETURNS INT
AS
BEGIN
    DECLARE @v_id INT;
    SELECT TOP 1 @v_id = id
    FROM dbo.usuarios
    WHERE login = @p_login AND senha = @p_senha;
    RETURN ISNULL(@v_id, 0);
END
GO

CREATE FUNCTION dbo.fn_total_chamados()
RETURNS INT
AS
BEGIN
    DECLARE @v_total INT;
    SELECT @v_total = COUNT(*) FROM dbo.chamados;
    RETURN @v_total;
END
GO

CREATE FUNCTION dbo.fn_total_chamados_abertos()
RETURNS INT
AS
BEGIN
    DECLARE @v_total INT;
    SELECT @v_total = COUNT(*)
    FROM dbo.chamados
    WHERE status = 'Aberto';
    RETURN @v_total;
END
GO

-- PROCEDURES --
CREATE PROCEDURE dbo.sp_chamado_create
    @p_titulo VARCHAR(200),
    @p_descricao VARCHAR(500)
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO dbo.chamados (titulo, descricao, status, data_abertura)
    VALUES (@p_titulo, @p_descricao, 'Aberto', CAST(GETDATE() AS DATE));
END
GO

CREATE PROCEDURE dbo.sp_chamado_list
AS
BEGIN
    SET NOCOUNT ON;
    SELECT id, titulo, descricao, status FROM dbo.v_chamados;
END
GO

CREATE PROCEDURE dbo.sp_chamado_update
    @p_id INT,
    @p_descricao VARCHAR(500)
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE dbo.chamados SET descricao = @p_descricao WHERE id = @p_id;
END
GO

CREATE PROCEDURE dbo.sp_chamado_delete
    @p_id INT
AS
BEGIN
    SET NOCOUNT ON;
    DELETE FROM dbo.chamados WHERE id = @p_id;
END
GO

CREATE PROCEDURE dbo.sp_chamado_finalizar
    @p_id INT
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE dbo.chamados
    SET status = 'Fechado', data_fechamento = CAST(GETDATE() AS DATE)
    WHERE id = @p_id;
END
GO

CREATE PROCEDURE dbo.sp_usuario_create
    @p_login VARCHAR(100),
    @p_senha VARCHAR(100),
    @p_tipo VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO dbo.usuarios (login, senha, tipo) VALUES (@p_login, @p_senha, @p_tipo);
END
GO

CREATE PROCEDURE dbo.sp_usuario_login
    @p_login VARCHAR(100),
    @p_senha VARCHAR(100)
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @v_id INT = dbo.fn_usuario_id(@p_login, @p_senha);
    SELECT TOP 1 id, login, senha, tipo
    FROM dbo.usuarios
    WHERE id = @v_id;
END
GO

-- TRIGGERS --
CREATE TRIGGER dbo.tr_usuarios_bi
ON dbo.usuarios
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS (SELECT 1 FROM inserted
               WHERE LTRIM(RTRIM(ISNULL(login,''))) = ''
                  OR LTRIM(RTRIM(ISNULL(senha,''))) = '')
    BEGIN
        RAISERROR('Login and password are required', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END
    UPDATE u
    SET login = LTRIM(RTRIM(u.login)),
        senha = LTRIM(RTRIM(u.senha))
    FROM dbo.usuarios u
    INNER JOIN inserted i ON u.id = i.id;
END
GO

CREATE TRIGGER dbo.tr_chamados_bi
ON dbo.chamados
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS (SELECT 1 FROM inserted
               WHERE LTRIM(RTRIM(ISNULL(descricao,''))) = '')
    BEGIN
        RAISERROR('Descricao is required', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END
    UPDATE c
    SET descricao = LTRIM(RTRIM(c.descricao)),
        status = CASE WHEN c.status IS NULL OR LTRIM(RTRIM(c.status)) = ''
                      THEN 'Aberto' ELSE c.status END
    FROM dbo.chamados c
    INNER JOIN inserted i ON c.id = i.id;
END
GO

CREATE TRIGGER dbo.tr_chamados_bu
ON dbo.chamados
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    IF TRIGGER_NESTLEVEL(@@PROCID) > 1 RETURN;

    IF EXISTS (SELECT 1 FROM inserted
               WHERE LTRIM(RTRIM(ISNULL(descricao,''))) = '')
    BEGIN
        RAISERROR('Descricao is required', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END
    UPDATE c
    SET descricao = LTRIM(RTRIM(i.descricao)),
        status = CASE WHEN i.status IS NULL OR LTRIM(RTRIM(i.status)) = ''
                      THEN 'Aberto' ELSE i.status END
    FROM dbo.chamados c
    INNER JOIN inserted i ON c.id = i.id;
END
GO
