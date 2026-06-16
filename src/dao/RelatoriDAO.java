package dao;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RelatoriDAO {

    private ChamadoDAO chamadoDAO;

    public RelatoriDAO() {
        this.chamadoDAO = new ChamadoDAO();
    }

    public void gerarRelatorioPrincipal() {
        System.out.println("\n========== RELATÓRIO DE CHAMADOS ==========\n");
        
        int totalChamados = chamadoDAO.totalChamados();
        int chamadosAbertos = chamadoDAO.totalChamadosAbertos();
        int chamadosResolvidos = chamadoDAO.totalChamadosResolvidos();
        double tempoMedio = chamadoDAO.tempoMedioAtendimento();

        System.out.println("Data do Relatório: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        System.out.println("=====================================");
        System.out.println("Total de Chamados: " + totalChamados);
        System.out.println("Chamados Abertos: " + chamadosAbertos);
        System.out.println("Chamados Resolvidos: " + chamadosResolvidos);
        System.out.println("Tempo Médio de Atendimento: " + String.format("%.2f", tempoMedio) + " dias");
        System.out.println("Taxa de Resolução: " + String.format("%.2f", (totalChamados > 0 ? (chamadosResolvidos * 100.0 / totalChamados) : 0)) + "%");
        System.out.println("=====================================\n");
    }

    public void salvarRelatorioPrincipal(String nomeArquivo) {
        try (FileWriter fw = new FileWriter(nomeArquivo)) {
            int totalChamados = chamadoDAO.totalChamados();
            int chamadosAbertos = chamadoDAO.totalChamadosAbertos();
            int chamadosResolvidos = chamadoDAO.totalChamadosResolvidos();
            double tempoMedio = chamadoDAO.tempoMedioAtendimento();

            fw.write("========== RELATÓRIO DE CHAMADOS ==========\n\n");
            fw.write("Data do Relatório: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n");
            fw.write("=====================================\n");
            fw.write("Total de Chamados: " + totalChamados + "\n");
            fw.write("Chamados Abertos: " + chamadosAbertos + "\n");
            fw.write("Chamados Resolvidos: " + chamadosResolvidos + "\n");
            fw.write("Tempo Médio de Atendimento: " + String.format("%.2f", tempoMedio) + " dias\n");
            fw.write("Taxa de Resolução: " + String.format("%.2f", (totalChamados > 0 ? (chamadosResolvidos * 100.0 / totalChamados) : 0)) + "%\n");
            fw.write("=====================================\n");

            System.out.println("Relatório salvo em: " + nomeArquivo);
        } catch (IOException e) {
            System.out.println("Erro ao salvar relatório: " + e.getMessage());
        }
    }

    public void gerarRelatorioUltimosPeriodos(int dias) {
        System.out.println("\n========== RELATÓRIO DOS ÚLTIMOS " + dias + " DIAS ==========\n");
        
        int totalChamados = chamadoDAO.totalChamados();
        int chamadosAbertos = chamadoDAO.totalChamadosAbertos();
        int chamadosResolvidos = chamadoDAO.totalChamadosResolvidos();

        System.out.println("Período: Últimos " + dias + " dias");
        System.out.println("Data: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        System.out.println("=====================================");
        System.out.println("Total de Chamados: " + totalChamados);
        System.out.println("Chamados Abertos: " + chamadosAbertos);
        System.out.println("Chamados Resolvidos: " + chamadosResolvidos);
        System.out.println("=====================================\n");
    }
}
