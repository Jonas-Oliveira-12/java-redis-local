package LojaApp;

import redis.clients.jedis.Jedis;
import java.util.Scanner;

public class LojaApp {

    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost", 6379);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Adicionar Produto");
            System.out.println("2. Listar Produtos");
            System.out.println("3. Atualizar Produto (Nome/Preço)");
            System.out.println("4. Deletar Produto");
            System.out.println("5. Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine(); // Limpa o buffer

            switch (opcao) {
                case 1:
                    System.out.print("Digite o ID do produto: ");
                    String id = scanner.nextLine();
                    System.out.print("Digite o nome do produto: ");
                    String nome = scanner.nextLine();

                    double preco = 0;
                    boolean entradaValida = false;
                    while (!entradaValida) {
                        try {
                            System.out.print("Digite o preço do produto: ");
                            preco = Double.parseDouble(scanner.nextLine());
                            entradaValida = true;
                        } catch (NumberFormatException e) {
                            System.out.println("Valor inválido para o preço. Por favor, insira um número.");
                        }
                    }

                    // Adicionando o produto no Redis como um hash
                    jedis.hset("produto:" + id, "nome", nome);
                    jedis.hset("produto:" + id, "preco", Double.toString(preco));
                    System.out.println("Produto adicionado com sucesso!");
                    break;

                case 2:
                    System.out.println("Lista de Produtos:");
                    // Recuperando todos os produtos
                    for (String key : jedis.keys("produto:*")) {
                        System.out.println(key + " - Nome: " + jedis.hget(key, "nome") + ", Preço: " + jedis.hget(key, "preco"));
                    }
                    break;

                case 3:
                    System.out.print("Digite o ID do produto que deseja atualizar: ");
                    String produtoId = scanner.nextLine();
                    if (jedis.exists("produto:" + produtoId)) {
                        System.out.print("Digite o novo nome do produto: ");
                        String novoNome = scanner.nextLine();

                        double novoPreco = 0;
                        boolean novoPrecoValido = false;
                        while (!novoPrecoValido) {
                            try {
                                System.out.print("Digite o novo preço do produto: ");
                                novoPreco = Double.parseDouble(scanner.nextLine());
                                novoPrecoValido = true;
                            } catch (NumberFormatException e) {
                                System.out.println("Valor inválido para o preço. Por favor, insira um número.");
                            }
                        }

                        // Atualizando nome e preço do produto
                        jedis.hset("produto:" + produtoId, "nome", novoNome);
                        jedis.hset("produto:" + produtoId, "preco", Double.toString(novoPreco));
                        System.out.println("Produto atualizado com sucesso!");
                    } else {
                        System.out.println("Produto não encontrado.");
                    }
                    break;

                case 4:
                    System.out.print("Digite o ID do produto que deseja deletar: ");
                    String deleteId = scanner.nextLine();
                    if (jedis.exists("produto:" + deleteId)) {
                        // Deletando o produto
                        jedis.del("produto:" + deleteId);
                        System.out.println("Produto deletado com sucesso!");
                    } else {
                        System.out.println("Produto não encontrado.");
                    }
                    break;

                case 5:
                    jedis.close();
                    scanner.close();
                    return;

                default:
                    System.out.println("Opção inválida!");
            }
        }
    }
}
