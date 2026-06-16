package dao;

import connection.ConnectionFactory;
import entidades.Interacao;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InteracaoDAO {

    private static final Path INTERACOES_FILE = Paths.get("data", "interacoes.txt");

    public void create(Interacao interacao) {
        String sql = "INSERT INTO interacoes (chamado_id, usuario_id, tipo, descricao, data_hora) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, interacao.getChamadoId());
            stmt.setInt(2, interacao.getUsuarioId());
            stmt.setString(3, interacao.getTipo());
            stmt.setString(4, interacao.getDescricao());
            stmt.setString(5, interacao.getDataHora());
            stmt.executeUpdate();

            System.out.println("Interação registrada!");
            return;
        } catch (Exception e) {
            System.out.println("Erro ao salvar interação: " + e.getMessage());
        }

        saveLocal(interacao);
    }

    public List<Interacao> getByChamadoId(int chamadoId) {
        String sql = "SELECT id, chamado_id, usuario_id, tipo, descricao, data_hora FROM interacoes WHERE chamado_id = ? ORDER BY data_hora DESC";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, chamadoId);
            ResultSet rs = stmt.executeQuery();

            List<Interacao> lista = new ArrayList<>();
            while (rs.next()) {
                Interacao i = new Interacao(
                        rs.getInt("id"),
                        rs.getInt("chamado_id"),
                        rs.getInt("usuario_id"),
                        rs.getString("tipo"),
                        rs.getString("descricao"),
                        rs.getString("data_hora")
                );
                lista.add(i);
            }
            return lista;
        } catch (Exception e) {
            System.out.println("Erro ao buscar interações: " + e.getMessage());
            return readLocal(chamadoId);
        }
    }

    private void saveLocal(Interacao interacao) {
        try {
            String linha = interacao.getChamadoId() + "|" +
                    interacao.getUsuarioId() + "|" +
                    interacao.getTipo() + "|" +
                    interacao.getDescricao() + "|" +
                    interacao.getDataHora() + "\n";

            Files.write(INTERACOES_FILE, linha.getBytes(StandardCharsets.UTF_8), 
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception e) {
            System.out.println("Erro ao salvar localmente: " + e.getMessage());
        }
    }

    private List<Interacao> readLocal(int chamadoId) {
        List<Interacao> lista = new ArrayList<>();
        try {
            if (!Files.exists(INTERACOES_FILE)) return lista;

            List<String> linhas = Files.readAllLines(INTERACOES_FILE);
            int counter = 1;
            for (String linha : linhas) {
                String[] partes = linha.split("\\|");
                if (partes.length >= 5 && Integer.parseInt(partes[0]) == chamadoId) {
                    Interacao i = new Interacao(
                            counter++,
                            Integer.parseInt(partes[0]),
                            Integer.parseInt(partes[1]),
                            partes[2],
                            partes[3],
                            partes[4]
                    );
                    lista.add(i);
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao ler localmente: " + e.getMessage());
        }
        return lista;
    }
}
