package br.com.dio.ui;

import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardColumnKindEnum;
import br.com.dio.persistence.entity.BoardEntity;
import br.com.dio.service.BoardQueryService;
import br.com.dio.service.BoardService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static br.com.dio.persistence.config.ConnectionConfig.getConnection;
import static br.com.dio.persistence.entity.BoardColumnKindEnum.*;

public class MainMenuPersonalizado {

    private final Scanner scanner = new Scanner(System.in);

    public void execute() throws SQLException {
        System.out.println("Bem-vindo ao Board do Julio! Escolha sua aventura:");
        var option = -1;
        while (true){
            System.out.println("\n1 - Criar um novo board");
            System.out.println("2 - Selecionar um board existente");
            System.out.println("3 - Excluir um board");
            System.out.println("4 - Sair");

            option = readInt("Escolha uma opção:");

            switch (option){
                case 1 -> createBoard();
                case 2 -> selectBoard();
                case 3 -> deleteBoard();
                case 4 -> System.exit(0);
                default -> System.out.println("Opção inválida! Escolha novamente.");
            }
        }
    }

    private void createBoard() throws SQLException {
        var entity = new BoardEntity();
        System.out.println("Informe o nome do seu board:");
        entity.setName(scanner.nextLine());

        System.out.println("Escolha o tipo de board (1-Estudo / 2-Trabalho / 3-Pessoal):");
        int tipoBoard = readInt("");

        List<BoardColumnEntity> columns = new ArrayList<>();
        switch (tipoBoard) {
            case 1 -> {
                columns.add(createColumn("📚 Para Ler", PENDING, 0));
                columns.add(createColumn("🖊 Estudando", PENDING, 1));
                columns.add(createColumn("✅ Concluído", FINAL, 2));
            }
            case 2 -> {
                columns.add(createColumn("📝 A Fazer", PENDING, 0));
                columns.add(createColumn("🚀 Em Progresso", PENDING, 1));
                columns.add(createColumn("✅ Concluído", FINAL, 2));
            }
            case 3 -> {
                columns.add(createColumn("🎯 Metas", PENDING, 0));
                columns.add(createColumn("💡 Ideias", PENDING, 1));
                columns.add(createColumn("✅ Realizado", FINAL, 2));
            }
            default -> {
                System.out.println("Tipo inválido, criando board padrão.");
                columns.add(createColumn("Para Iniciar", PENDING, 0));
                columns.add(createColumn("Em Progresso", PENDING, 1));
                columns.add(createColumn("Concluído", FINAL, 2));
            }
        }

        // Coluna de cancelamento padrão
        columns.add(createColumn("❌ Cancelado", CANCEL, columns.size()));

        entity.setBoardColumns(columns);
        try(var connection = getConnection()){
            var service = new BoardService(connection);
            service.insert(entity);
        }

        System.out.println("Board criado com sucesso! Que comece a organização das tarefas! 🎉");
    }

    private void selectBoard() throws SQLException {
        System.out.println("Informe o ID do board que deseja acessar:");
        long boardId = readLong("Digite o ID:");

        try (var connection = getConnection()) {
            var queryService = new BoardQueryService(connection);
            var optionalBoard = queryService.findById(boardId);

            optionalBoard.ifPresentOrElse(
                board -> new BoardMenuPersonalizado(board).execute(),
                () -> System.out.printf("Não existe board com ID %d%n", boardId)
            );
        }
    }

    private void deleteBoard() throws SQLException {
        System.out.println("Informe o ID do board que deseja excluir:");
        long boardId = readLong("Digite o ID:");

        try (var connection = getConnection()) {
            var service = new BoardService(connection);
            if (service.delete(boardId)) {
                System.out.printf("Board com ID %d excluído com sucesso! 🗑️%n", boardId);
            } else {
                System.out.printf("Não existe board com ID %d%n", boardId);
            }
        }
    }

    private BoardColumnEntity createColumn(final String name, final BoardColumnKindEnum kind, final int order){
        var boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setKind(kind);
        boardColumn.setOrder(order);
        return boardColumn;
    }

    private int readInt(String prompt) {
        int value;
        while (true) {
            System.out.println(prompt);
            try {
                value = Integer.parseInt(scanner.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite apenas números inteiros!");
            }
        }
        return value;
    }

    private long readLong(String prompt) {
        long value;
        while (true) {
            System.out.println(prompt);
            try {
                value = Long.parseLong(scanner.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite apenas números inteiros!");
            }
        }
        return value;
    }
}
