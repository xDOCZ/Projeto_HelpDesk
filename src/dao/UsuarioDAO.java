package dao;

import entidades.Usuario;
import java.sql.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import connection.ConnectionFactory;

public class UsuarioDAO {

    private static final Path USERS_FILE = Paths.get("data", "usuarios.txt");

    public void create(Usuario usuario) {
        String sql = "INSERT INTO usuarios (login, senha, tipo) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getLogin());
            stmt.setString(2, usuario.getSenha());
            stmt.setString(3, usuario.getTipo());
            stmt.executeUpdate();

            System.out.println("Usuário cadastrado!");
            return;
        } catch (Exception e) {
            System.out.println("Não foi possível salvar no banco. Salvando localmente. Erro: " + e.getMessage());
        }

        saveUserLocally(usuario);
    }

    public Usuario login(String login, String senha) {
        String sql = "SELECT * FROM usuarios WHERE login = ? AND senha = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setString(2, senha);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id"),
                            rs.getString("login"),
                            rs.getString("senha"),
                            rs.getString("tipo")
                    );
                }
            }
            return null;
        } catch (Exception e) {
            System.out.println("Conexão com o banco indisponível. Tentando autenticação local.");
            return loginLocally(login, senha);
        }
    }

    private Usuario loginLocally(String login, String senha) {
        try {
            ensureUsersFileExists();
            List<String> lines = Files.readAllLines(USERS_FILE, StandardCharsets.UTF_8);

            for (String line : lines) {
                String[] parts = line.split(":");
                if (parts.length == 3 && parts[0].equals(login) && parts[1].equals(senha)) {
                    return new Usuario(0, parts[0], parts[1], parts[2]);
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao ler credenciais locais: " + e.getMessage());
        }
        return null;
    }

    private void saveUserLocally(Usuario usuario) {
        try {
            ensureUsersFileExists();
            if (!userExists(usuario.getLogin())) {
                String line = usuario.getLogin() + ":" + usuario.getSenha() + ":" + usuario.getTipo();
                Files.write(USERS_FILE, List.of(line), StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                System.out.println("Usuário cadastrado localmente!");
            } else {
                System.out.println("Usuário já existe no armazenamento local.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao salvar usuário localmente: " + e.getMessage());
        }
    }

    private boolean userExists(String login) {
        try {
            ensureUsersFileExists();
            List<String> lines = Files.readAllLines(USERS_FILE, StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] parts = line.split(":");
                if (parts.length >= 1 && parts[0].equals(login)) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    private void ensureUsersFileExists() throws Exception {
        if (!Files.exists(USERS_FILE)) {
            Files.createDirectories(USERS_FILE.getParent());
            Files.write(USERS_FILE, List.of("admin:admin:admin"), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        }
    }
}

