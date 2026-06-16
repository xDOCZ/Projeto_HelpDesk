package entidades;
import dao.ChamadoDAO;
import dao.InteracaoDAO;
import dao.RelatoriDAO;
import java.util.Scanner;
import java.util.List;
import static entidades.LimparTela.limparTela;

public class MenuChamados {

    public void menu() {
        int opcao;
        int continuar = 1;
        Scanner entrada = new Scanner(System.in);
        ChamadoDAO chamadoDAO = new ChamadoDAO();
        InteracaoDAO interacaoDAO = new InteracaoDAO();
        RelatoriDAO relatoriDAO = new RelatoriDAO();

        while (continuar == 1) {
            System.out.println("""
                    
                    ===========================
                    MENU ADMINISTRADOR
                    ===========================
                    1  - Criar chamado
                    2  - Consultar chamados
                    3  - Excluir chamado
                    4  - Editar chamado 
                    5  - Atualizar status 
                    6  - Atribuir técnico 
                    7  - Ver histórico 
                    8  - Pesquisar chamados 
                    9  - Gerar relatório 
                    10 - Estatísticas
                    11 - Sair
                    ===========================
                    
                    """);

            System.out.println("Selecione uma opção acima: ");
            opcao = entrada.nextInt();

            switch (opcao) {
                case 1:
                    limparTela();
                    entrada.nextLine();
                    System.out.print("Digite o título do chamado: ");
                    String titulo = entrada.nextLine();
                    System.out.print("Digite a descrição: ");
                    String desc = entrada.nextLine();
                    System.out.print("""
                            Prioridade (1-Baixa, 2-Média, 3-Alta): """);
                    int prioridade = entrada.nextInt();
                    System.out.print("""
                            Categoria (1-Suporte, 2-Financeiro, 3-RH, 4-Admin): """);
                    int categoria = entrada.nextInt();
                    
                    String categoriaNome = switch(categoria) {
                        case 1 -> "Suporte Técnico";
                        case 2 -> "Financeiro";
                        case 3 -> "Recursos Humanos";
                        case 4 -> "Administrativo";
                        default -> "Geral";
                    };

                    String dataAbertura = java.time.LocalDate.now().toString();
                    Chamado novo = new Chamado(0, titulo, desc, categoriaNome, prioridade,
                            "Aberto", 1, 0, dataAbertura, null);
                    chamadoDAO.create(novo);
                    interacaoDAO.create(new Interacao(0, novo.getId(), 1, "criação", "Chamado criado"));
                    break;

                case 2:
                    limparTela();
                    chamadoDAO.read().forEach(System.out::println);
                    break;

                case 3:
                    limparTela();
                    System.out.print("Digite o ID do chamado para excluir: ");
                    int idExcluir = entrada.nextInt();
                    chamadoDAO.delete(idExcluir);
                    break;

                case 4:
                    limparTela();
                    System.out.print("Digite o ID do chamado para editar: ");
                    int idEditar = entrada.nextInt();
                    entrada.nextLine();

                    System.out.print("Novo título: ");
                    String novoTitulo = entrada.nextLine();
                    System.out.print("Nova descrição: ");
                    String novaDesc = entrada.nextLine();
                    System.out.print("Nova prioridade (1-3): ");
                    int novaPrioridade = entrada.nextInt();

                    Chamado atualizado = new Chamado(idEditar, novoTitulo, novaDesc, "Geral", novaPrioridade,
                            "Aberto", 1, 0, "", null);
                    chamadoDAO.update(atualizado);
                    interacaoDAO.create(new Interacao(0, idEditar, 1, "edição", "Chamado editado"));
                    break;

                case 5:
                    limparTela();
                    System.out.print("Digite o ID do chamado: ");
                    int idStatus = entrada.nextInt();
                    entrada.nextLine();
                    System.out.println("Status disponíveis:");
                    System.out.println("1 - Aberto");
                    System.out.println("2 - Em Atendimento");
                    System.out.println("3 - Aguardando");
                    System.out.println("4 - Resolvido");
                    System.out.println("5 - Fechado");
                    System.out.print("Escolha o novo status: ");
                    int statusOpcao = entrada.nextInt();
                    
                    String novoStatus = switch(statusOpcao) {
                        case 1 -> "Aberto";
                        case 2 -> "Em Atendimento";
                        case 3 -> "Aguardando";
                        case 4 -> "Resolvido";
                        case 5 -> "Fechado";
                        default -> "Aberto";
                    };
                    
                    chamadoDAO.updateStatus(idStatus, novoStatus);
                    interacaoDAO.create(new Interacao(0, idStatus, 1, "atualização de status", "Status alterado para: " + novoStatus));
                    break;

                case 6:
                    limparTela();
                    System.out.print("Digite o ID do chamado: ");
                    int idAtribuir = entrada.nextInt();
                    System.out.print("Digite o ID do técnico: ");
                    int idTecnico = entrada.nextInt();
                    
                    chamadoDAO.atribuirTecnico(idAtribuir, idTecnico);
                    interacaoDAO.create(new Interacao(0, idAtribuir, 1, "atribuição", "Atribuído ao técnico ID: " + idTecnico));
                    break;

                case 7:
                    limparTela();
                    System.out.print("Digite o ID do chamado para ver histórico: ");
                    int idHistorico = entrada.nextInt();
                    
                    List<Interacao> interacoes = interacaoDAO.getByChamadoId(idHistorico);
                    if (interacoes.isEmpty()) {
                        System.out.println("Nenhuma interação registrada para este chamado.");
                    } else {
                        System.out.println("\n===== HISTÓRICO DE INTERAÇÕES =====");
                        interacoes.forEach(System.out::println);
                        System.out.println("====================================\n");
                    }
                    break;

                case 8:
                    limparTela();
                    System.out.println("Pesquisar por:");
                    System.out.println("1 - Status");
                    System.out.println("2 - Prioridade");
                    System.out.println("3 - Técnico responsável");
                    System.out.print("Escolha: ");
                    int opcaoPesquisa = entrada.nextInt();
                    entrada.nextLine();
                    
                    switch(opcaoPesquisa) {
                        case 1:
                            System.out.print("Status a buscar (Aberto/Em Atendimento/Resolvido/Fechado): ");
                            String statusBusca = entrada.nextLine();
                            chamadoDAO.buscarPorStatus(statusBusca).forEach(System.out::println);
                            break;
                        case 2:
                            System.out.print("Prioridade (1-Baixa, 2-Média, 3-Alta): ");
                            int prioridadeBusca = entrada.nextInt();
                            chamadoDAO.buscarPorPrioridade(prioridadeBusca).forEach(System.out::println);
                            break;
                        case 3:
                            System.out.print("ID do técnico: ");
                            int idTecnicoBusca = entrada.nextInt();
                            chamadoDAO.buscarPorTecnico(idTecnicoBusca).forEach(System.out::println);
                            break;
                        default:
                            System.out.println("Opção inválida!");
                    }
                    break;

                case 9:
                    limparTela();
                    System.out.println("1 - Exibir relatório no console");
                    System.out.println("2 - Salvar relatório em arquivo");
                    System.out.print("Escolha: ");
                    int opcaoRelatorio = entrada.nextInt();
                    entrada.nextLine();
                    
                    if (opcaoRelatorio == 1) {
                        relatoriDAO.gerarRelatorioPrincipal();
                    } else if (opcaoRelatorio == 2) {
                        System.out.print("Nome do arquivo: ");
                        String nomeArquivo = entrada.nextLine();
                        relatoriDAO.salvarRelatorioPrincipal(nomeArquivo);
                    }
                    break;

                case 10:
                    limparTela();
                    int total = chamadoDAO.totalChamados();
                    int abertos = chamadoDAO.totalChamadosAbertos();
                    int resolvidos = chamadoDAO.totalChamadosResolvidos();
                    System.out.println("========== ESTATÍSTICAS ==========");
                    System.out.println("Total de chamados: " + total);
                    System.out.println("Chamados abertos: " + abertos);
                    System.out.println("Chamados resolvidos: " + resolvidos);
                    System.out.println("==================================");
                    break;

                case 11:
                    limparTela();
                    System.out.println("Encerrando sessão...");
                    continuar = 0;
                    break;

                default:
                    System.out.println("Opção inválida. Digite uma opção válida!");
            }

            if (continuar == 1 && opcao != 11) {
                System.out.println("""
                        
                        =======================================
                        Digite 1 para continuar ou outra tecla para sair
                        =======================================
                        
                        """);
                continuar = entrada.nextInt();
                limparTela();
            }
        }
    }
}

