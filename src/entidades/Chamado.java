package entidades;

public class Chamado {
    private int id;
    private String titulo;
    private String descricao;
    private String categoria;
    private int prioridade;
    private String status;
    private int usuarioId;
    private int tecnicoId;
    private String dataAbertura;
    private String dataFechamento;

    public Chamado(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
        this.status = "Aberto";
    }

    // Construtor completo
    public Chamado(int id, String titulo, String descricao, String categoria, int prioridade,
                   String status, int usuarioId, int tecnicoId, String dataAbertura, String dataFechamento) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.categoria = categoria;
        this.prioridade = prioridade;
        this.status = status;
        this.usuarioId = usuarioId;
        this.tecnicoId = tecnicoId;
        this.dataAbertura = dataAbertura;
        this.dataFechamento = dataFechamento;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getTecnicoId() {
        return tecnicoId;
    }

    public void setTecnicoId(int tecnicoId) {
        this.tecnicoId = tecnicoId;
    }

    public String getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(String dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public String getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(String dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public String getPrioridadeString() {
        return switch (prioridade) {
            case 1 -> "Baixa";
            case 2 -> "Média";
            case 3 -> "Alta";
            default -> "Não definida";
        };
    }

    @Override
    public String toString() {
        return String.format(
            "ID: %d | Título: %s | Status: %s | Prioridade: %s | Técnico: %d | Data: %s",
            id, titulo, status, getPrioridadeString(), tecnicoId, dataAbertura
        );
    }
}
