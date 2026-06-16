package entidades;

public class Usuario {

    private int id;
    private String login;
    private String senha;
    private String tipo;

    public Usuario(int id, String login, String senha, String tipo){
        this.id = id;
        this.login = login;
        this.senha = senha;
        this.tipo = tipo;
    }

    public String getLogin() {
        return login;
    }

    public String getSenha() {
        return senha;
    }

    public String getTipo() {
        return tipo;
    }
}
