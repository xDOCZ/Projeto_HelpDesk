package dao;

import connection.ConnectionFactory;
import entidades.Chamado;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChamadoDAO {

    private static final Path CHAMADOS_FILE = Paths.get("data", "chamados.txt");

    public void create(Chamado chamado) {
        String sql = "INSERT INTO chamados (titulo, descricao, categoria, prioridade, status, usuario_id, tecnico_id, data_abertura) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, chamado.getTitulo());
            stmt.setString(2, chamado.getDescricao());
            stmt.setString(3, chamado.getCategoria());
            stmt.setInt(4, chamado.getPrioridade());
            stmt.setString(5, "Aberto");
            stmt.setInt(6, chamado.getUsuarioId());
            stmt.setInt(7, chamado.getTecnicoId());
            stmt.setString(8, chamado.getDataAbertura());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    chamado.setId(keys.getInt(1));
                }
            }

            System.out.println("Chamado salvo no banco! ID: " + chamado.getId());
            return;
        } catch (Exception e) {
            System.out.println("Não foi possível salvar no banco. Salvando localmente. Erro: " + e.getMessage());
        }

        saveLocal(chamado);
    }

    public List<Chamado> read() {
        String sql = "SELECT id, titulo, descricao, categoria, prioridade, status, usuario_id, tecnico_id, data_abertura, data_fechamento FROM chamados";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            List<Chamado> lista = new ArrayList<>();
            while (rs.next()) {
                Chamado c = new Chamado(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("descricao"),
                        rs.getString("categoria"),
                        rs.getInt("prioridade"),
                        rs.getString("status"),
                        rs.getInt("usuario_id"),
                        rs.getInt("tecnico_id"),
                        rs.getString("data_abertura"),
                        rs.getString("data_fechamento")
                );
                lista.add(c);
            }
            return lista;
        } catch (Exception e) {
            System.out.println("Não foi possível ler do banco. Lendo localmente.");
            return readLocal();
        }
    }

    public void update(Chamado chamado) {
        String sql = "UPDATE chamados SET titulo = ?, descricao = ?, categoria = ?, prioridade = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, chamado.getTitulo());
            stmt.setString(2, chamado.getDescricao());
            stmt.setString(3, chamado.getCategoria());
            stmt.setInt(4, chamado.getPrioridade());
            stmt.setInt(5, chamado.getId());
            stmt.executeUpdate();

            System.out.println("Chamado atualizado!");
            return;
        } catch (Exception e) {
            System.out.println("Não foi possível atualizar no banco. Atualizando localmente.");
        }

        updateLocal(chamado);
    }

    public void delete(int id) {
        String sql = "DELETE FROM chamados WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

            System.out.println("Chamado removido!");
            return;
        } catch (Exception e) {
            System.out.println("Não foi possível excluir no banco. Excluindo localmente.");
        }

        deleteLocal(id);
    }

    public void updateStatus(int id, String novoStatus) {
        String sql = "UPDATE chamados SET status = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novoStatus);
            stmt.setInt(2, id);
            stmt.executeUpdate();

            System.out.println("Status do chamado atualizado para: " + novoStatus);
            return;
        } catch (Exception e) {
            System.out.println("Erro ao atualizar status: " + e.getMessage());
        }
    }

    public void finalizar(int id) {
        String sql = "UPDATE chamados SET status = 'Fechado', data_fechamento = CAST(GETDATE() AS DATE) WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

            System.out.println("Chamado finalizado!");
            return;
        } catch (Exception e) {
            System.out.println("Não foi possível finalizar no banco. Finalizando localmente.");
        }

        finalizarLocal(id);
    }

    private void finalizarLocal(int id) {
        try {
            ensureChamadosFileExists();
            List<String> linhas = Files.readAllLines(CHAMADOS_FILE);
            List<String> updated = new ArrayList<>();
            for (String linha : linhas) {
                String[] partes = linha.split("\\|");
                if (partes.length >= 6 && Integer.parseInt(partes[0]) == id) {
                    partes[5] = "Fechado";
                    updated.add(String.join("|", partes));
                } else {
                    updated.add(linha);
                }
            }
            Files.write(CHAMADOS_FILE, updated, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Chamado finalizado localmente!");
        } catch (Exception e) {
            System.out.println("Erro ao finalizar chamado localmente: " + e.getMessage());
        }
    }

    public void atribuirTecnico(int id, int tecnicoId) {
        String sql = "UPDATE chamados SET tecnico_id = ?, status = 'Em Atendimento' WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tecnicoId);
            stmt.setInt(2, id);
            stmt.executeUpdate();

            System.out.println("Chamado atribuído ao técnico ID: " + tecnicoId);
            return;
        } catch (Exception e) {
            System.out.println("Erro ao atribuir chamado: " + e.getMessage());
        }
    }

    public List<Chamado> buscarPorUsuario(int usuarioId) {
        String sql = "SELECT id, titulo, descricao, categoria, prioridade, status, usuario_id, tecnico_id, data_abertura, data_fechamento " +
                    "FROM chamados WHERE usuario_id = ? ORDER BY data_abertura DESC";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            List<Chamado> lista = new ArrayList<>();
            while (rs.next()) {
                Chamado c = new Chamado(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("descricao"),
                        rs.getString("categoria"),
                        rs.getInt("prioridade"),
                        rs.getString("status"),
                        rs.getInt("usuario_id"),
                        rs.getInt("tecnico_id"),
                        rs.getString("data_abertura"),
                        rs.getString("data_fechamento")
                );
                lista.add(c);
            }
            return lista;
        } catch (Exception e) {
            System.out.println("Erro ao buscar por usuário: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Chamado> buscarPorStatus(String status) {
        String sql = "SELECT id, titulo, descricao, categoria, prioridade, status, usuario_id, tecnico_id, data_abertura, data_fechamento " +
                    "FROM chamados WHERE status = ? ORDER BY prioridade DESC, data_abertura ASC";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            List<Chamado> lista = new ArrayList<>();
            while (rs.next()) {
                Chamado c = new Chamado(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("descricao"),
                        rs.getString("categoria"),
                        rs.getInt("prioridade"),
                        rs.getString("status"),
                        rs.getInt("usuario_id"),
                        rs.getInt("tecnico_id"),
                        rs.getString("data_abertura"),
                        rs.getString("data_fechamento")
                );
                lista.add(c);
            }
            return lista;
        } catch (Exception e) {
            System.out.println("Erro ao buscar por status: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Chamado> buscarPorPrioridade(int prioridade) {
        String sql = "SELECT id, titulo, descricao, categoria, prioridade, status, usuario_id, tecnico_id, data_abertura, data_fechamento " +
                    "FROM chamados WHERE prioridade = ? ORDER BY data_abertura DESC";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, prioridade);
            ResultSet rs = stmt.executeQuery();

            List<Chamado> lista = new ArrayList<>();
            while (rs.next()) {
                Chamado c = new Chamado(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("descricao"),
                        rs.getString("categoria"),
                        rs.getInt("prioridade"),
                        rs.getString("status"),
                        rs.getInt("usuario_id"),
                        rs.getInt("tecnico_id"),
                        rs.getString("data_abertura"),
                        rs.getString("data_fechamento")
                );
                lista.add(c);
            }
            return lista;
        } catch (Exception e) {
            System.out.println("Erro ao buscar por prioridade: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Chamado> buscarPorTecnico(int tecnicoId) {
        String sql = "SELECT id, titulo, descricao, categoria, prioridade, status, usuario_id, tecnico_id, data_abertura, data_fechamento " +
                    "FROM chamados WHERE tecnico_id = ? ORDER BY prioridade DESC, data_abertura ASC";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tecnicoId);
            ResultSet rs = stmt.executeQuery();

            List<Chamado> lista = new ArrayList<>();
            while (rs.next()) {
                Chamado c = new Chamado(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("descricao"),
                        rs.getString("categoria"),
                        rs.getInt("prioridade"),
                        rs.getString("status"),
                        rs.getInt("usuario_id"),
                        rs.getInt("tecnico_id"),
                        rs.getString("data_abertura"),
                        rs.getString("data_fechamento")
                );
                lista.add(c);
            }
            return lista;
        } catch (Exception e) {
            System.out.println("Erro ao buscar por técnico: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public int totalChamados() {
        String sql = "SELECT COUNT(*) AS total FROM chamados";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        } catch (Exception e) {
            System.out.println("Não foi possível contar chamados no banco. Contando localmente.");
            return totalChamadosLocal();
        }
    }

    public int totalChamadosAbertos() {
        String sql = "SELECT COUNT(*) AS total FROM chamados WHERE status = 'Aberto'";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        } catch (Exception e) {
            System.out.println("Não foi possível contar chamados abertos no banco. Contando localmente.");
            return totalChamadosAbertosLocal();
        }
    }

    public int totalChamadosResolvidos() {
        String sql = "SELECT COUNT(*) AS total FROM chamados WHERE status = 'Resolvido' OR status = 'Fechado'";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        } catch (Exception e) {
            System.out.println("Erro ao contar chamados resolvidos: " + e.getMessage());
            return 0;
        }
    }

    public double tempoMedioAtendimento() {
        String sql = "SELECT AVG(DATEDIFF(day, data_abertura, ISNULL(data_fechamento, GETDATE()))) AS media FROM chamados";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("media");
            }
            return 0.0;
        } catch (Exception e) {
            System.out.println("Erro ao calcular tempo médio: " + e.getMessage());
            return 0.0;
        }
    }

    private void saveLocal(Chamado chamado) {
        try {
            ensureChamadosFileExists();
            
            if (chamado.getId() == 0) {
                chamado.setId(nextLocalId());
            }
            String linha = chamado.getId() + "|" +
                    chamado.getTitulo() + "|" +
                    chamado.getDescricao() + "|" +
                    chamado.getCategoria() + "|" +
                    chamado.getPrioridade() + "|" +
                    "Aberto" + "|" +
                    chamado.getUsuarioId() + "|" +
                    chamado.getTecnicoId() + "|" +
                    chamado.getDataAbertura() + "\n";

            Files.write(CHAMADOS_FILE, linha.getBytes(StandardCharsets.UTF_8), 
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println("Chamado salvo localmente!");
        } catch (Exception e) {
            System.out.println("Erro ao salvar chamado localmente: " + e.getMessage());
        }
    }

    private List<Chamado> readLocal() {
        List<Chamado> lista = new ArrayList<>();
        try {
            ensureChamadosFileExists();
            List<String> linhas = Files.readAllLines(CHAMADOS_FILE);
            int counter = 1;
            for (String linha : linhas) {
                String[] partes = linha.split("\\|");
                if (partes.length >= 9) {
                    Chamado c = new Chamado(
                            Integer.parseInt(partes[0]),
                            partes[1],
                            partes[2],
                            partes[3],
                            Integer.parseInt(partes[4]),
                            partes[5],
                            Integer.parseInt(partes[6]),
                            Integer.parseInt(partes[7]),
                            partes[8],
                            partes.length > 9 ? partes[9] : null
                    );
                    lista.add(c);
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao ler chamados locais: " + e.getMessage());
        }
        return lista;
    }

    private void updateLocal(Chamado chamado) {
        try {
            ensureChamadosFileExists();
            List<String> linhas = Files.readAllLines(CHAMADOS_FILE);
            List<String> updated = new ArrayList<>();
            for (String linha : linhas) {
                String[] partes = linha.split("\\|");
                if (partes.length >= 1 && Integer.parseInt(partes[0]) == chamado.getId()) {
                    String novaLinha = chamado.getId() + "|" +
                            chamado.getTitulo() + "|" +
                            chamado.getDescricao() + "|" +
                            chamado.getCategoria() + "|" +
                            chamado.getPrioridade() + "|" +
                            partes[5] + "|" +
                            chamado.getUsuarioId() + "|" +
                            chamado.getTecnicoId() + "|" +
                            chamado.getDataAbertura() + "|" +
                            (chamado.getDataFechamento() != null ? chamado.getDataFechamento() : "");
                    updated.add(novaLinha);
                } else {
                    updated.add(linha);
                }
            }
            Files.write(CHAMADOS_FILE, updated, StandardCharsets.UTF_8, 
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Chamado atualizado localmente!");
        } catch (Exception e) {
            System.out.println("Erro ao atualizar chamado localmente: " + e.getMessage());
        }
    }

    private void deleteLocal(int id) {
        try {
            ensureChamadosFileExists();
            List<String> linhas = Files.readAllLines(CHAMADOS_FILE);
            List<String> updated = new ArrayList<>();
            for (String linha : linhas) {
                String[] partes = linha.split("\\|");
                if (partes.length < 1 || Integer.parseInt(partes[0]) != id) {
                    updated.add(linha);
                }
            }
            Files.write(CHAMADOS_FILE, updated, StandardCharsets.UTF_8, 
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Chamado removido localmente!");
        } catch (Exception e) {
            System.out.println("Erro ao excluir chamado localmente: " + e.getMessage());
        }
    }

    private int totalChamadosLocal() {
        try {
            ensureChamadosFileExists();
            return Files.readAllLines(CHAMADOS_FILE).size();
        } catch (Exception e) {
            System.out.println("Erro ao contar chamados locais: " + e.getMessage());
            return 0;
        }
    }

    private int totalChamadosAbertosLocal() {
        try {
            ensureChamadosFileExists();
            return (int) Files.readAllLines(CHAMADOS_FILE).stream()
                    .map(line -> line.split("\\|"))
                    .filter(partes -> partes.length >= 6 && partes[5].trim().equalsIgnoreCase("Aberto"))
                    .count();
        } catch (Exception e) {
            System.out.println("Erro ao contar chamados abertos locais: " + e.getMessage());
            return 0;
        }
    }

    private int nextLocalId() {
        try {
            ensureChamadosFileExists();
            return Files.readAllLines(CHAMADOS_FILE).stream()
                    .map(line -> line.split("\\|"))
                    .filter(partes -> partes.length >= 1 && !partes[0].isBlank())
                    .mapToInt(partes -> Integer.parseInt(partes[0].trim()))
                    .max()
                    .orElse(0) + 1;
        } catch (Exception e) {
            return 1;
        }
    }

    private void ensureChamadosFileExists() throws Exception {
        if (!Files.exists(CHAMADOS_FILE)) {
            Files.createDirectories(CHAMADOS_FILE.getParent());
            Files.write(CHAMADOS_FILE, List.of(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        }
    }
}
