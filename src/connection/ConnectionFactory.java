package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

public class ConnectionFactory {

    private static final Map<String, String> ENV = System.getenv();
    private static final String URL = ENV.getOrDefault("DB_URL",
            "jdbc:sqlserver://localhost:1433;databaseName=helpdesk;integratedSecurity=true;encrypt=true;trustServerCertificate=true");

    public static Connection getConnection() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(URL);
        } catch (Exception e) {
            throw new RuntimeException("Erro na conexão: " + e.getMessage());
        }
    }
}