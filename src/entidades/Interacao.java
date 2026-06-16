package entidades;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Interacao {
    private int id;
    private int chamadoId;
    private int usuarioId;
    private String tipo;
    private String descricao;
    private String dataHora;

    public Interacao(int id, int chamadoId, int usuarioId, String tipo, String descricao) {
        this.id = id;
        this.chamadoId = chamadoId;
        this.usuarioId = usuarioId;
        this.tipo = tipo;
        this.descricao = descricao;
        this.dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public Interacao(int id, int chamadoId, int usuarioId, String tipo, String descricao, String dataHora) {
        this.id = id;
        this.chamadoId = chamadoId;
        this.usuarioId = usuarioId;
        this.tipo = tipo;
        this.descricao = descricao;
        this.dataHora = dataHora;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public int getChamadoId() {
        return chamadoId;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDataHora() {
        return dataHora;
    }

    @Override
    public String toString() {
        return String.format(
            "[%s] Tipo: %s | Usuário ID: %d | Descrição: %s",
            dataHora, tipo, usuarioId, descricao
        );
    }
}
