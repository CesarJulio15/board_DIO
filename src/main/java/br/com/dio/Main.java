package br.com.dio;

import br.com.dio.persistence.migration.MigrationStrategy;
import br.com.dio.ui.MainMenuPersonalizado;

import java.sql.SQLException;

import static br.com.dio.persistence.config.ConnectionConfig.getConnection;

public class Main {

    public static void main(String[] args) {
        try (var connection = getConnection()) {
            // Executa migrações do banco
            new MigrationStrategy(connection).executeMigration();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        // Menu principal personalizado
        try {
            new MainMenuPersonalizado().execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
