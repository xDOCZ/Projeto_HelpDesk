package entidades;

import dao.ChamadoDAO;
import java.util.Scanner;
import static entidades.LimparTela.limparTela;

public class MenuChamadosUsuario {    public void menuUsuario () {
    int opcao;
    int continuar = 1;
    Scanner entrada = new Scanner(System.in);
    ChamadoDAO dao = new ChamadoDAO();

    while (continuar == 1){
        System.out.println("""
                    
                    ===========================
                    1 - Criar chamado
                    2 - sair
                    ===========================
                    
                    """);

        System.out.println("Selecione uma opção acima ");
        opcao = entrada.nextInt();

        switch (opcao){
            case 1:
                limparTela();
                entrada.nextLine();
                System.out.print("""
                            
                            =============================
                            Digite a descrição do chamado
                            =============================
                            
                            """);
                String desc = entrada.nextLine();

                Chamado novo = new Chamado(0, desc);
                dao.create(novo);
                break;



            case 2:
                limparTela();
                System.out.println("Encerrando sessão...");
                break;
            default:
                System.out.println("""
                            
                            =======================================
                            Opção inválida. Digite uma opção válida
                            =======================================
                            
                            """);
        }
        limparTela();
        System.out.println("""
                                          Andamento
                            =======================================
                                  Digite 1 para continuar
                                  ou outra tecla para sair
                            =======================================
                            
                            """);

        continuar = entrada.nextInt();
        limparTela();
    }
}
}
