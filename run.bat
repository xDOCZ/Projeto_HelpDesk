@echo off
setlocal enabledelayedexpansion
if not exist bin mkdir bin
set SOURCES=
for /R src %%f in (*.java) do (
    if defined SOURCES (
        set SOURCES=!SOURCES! "%%f"
    ) else (
        set SOURCES="%%f"
    )
)
if not defined SOURCES (
    echo Nenhum arquivo .java encontrado em src\
    goto :end
)

echo Compilando projeto...
javac -d bin %SOURCES%
if errorlevel 1 (
    echo Falha na compilacao.
    goto :end
)

echo Executando Main com SQL Server JDBC driver...
java --enable-native-access=ALL-UNNAMED -cp "bin;lib\mssql-jdbc-12.2.0.jre8.jar" -Djava.library.path=lib Main
:end