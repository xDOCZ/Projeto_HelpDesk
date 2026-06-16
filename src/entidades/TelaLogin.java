package entidades;
import dao.UsuarioDAO;

import java.util.Scanner;
import static entidades.LimparTela.limparTela;

public class TelaLogin {

    public void login() {

        Scanner entrada = new Scanner(System.in);
        int opcao = 0;
        do{
            System.out.println("""
                                LOGIN
                    =============================
                    1 - Realizar Login
                    2 - Cadastrar usuário
                    3 - Sair
                    =============================
                    Escolha uma das opções acima
                    """);

            opcao = entrada.nextInt();
            limparTela();

            switch(opcao){

                case 1:
                    System.out.print("Login: ");
                    entrada.nextLine();
                    String loginUser = entrada.nextLine();

                    System.out.print("Senha: ");
                    String senhaUser = entrada.nextLine();

                    Usuario usuario = new UsuarioDAO().login(loginUser, senhaUser);

                    if (usuario != null) {
                        if (usuario.getTipo().equals("admin")) {
                            System.out.println("Login ADMIN realizado!");
                            MenuChamados menuChamados = new MenuChamados();
                            menuChamados.menu();
                        } else {
                            System.out.println("Login USER realizado!");
                            MenuChamadosUsuario menuChamados = new MenuChamadosUsuario();
                            menuChamados.menuUsuario();
                        }
                    } else {
                        System.out.println("Login inválido!");
                    }
                    break;

                case 2:
                    entrada.nextLine();

                    System.out.print("Novo login: ");
                    String novoLogin = entrada.nextLine();

                    System.out.print("Senha: ");
                    String novaSenha = entrada.nextLine();

                    Usuario novoUsuario = new Usuario(0, novoLogin, novaSenha, "user");
                    new UsuarioDAO().create(novoUsuario);

                    System.out.println("Usuário cadastrado!");
                    break;

                case 3:
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
        } while(opcao != 3);


    }
}