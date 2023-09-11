package comp3350.group9.theauctionhouse.persistence.executor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class HSQLQueryExecutor {
    private final String dbPath;

    public HSQLQueryExecutor(final String dbPath) {
        this.dbPath = dbPath;
    }

    protected Connection connection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:file:" + dbPath + ";shutdown=true;ifexist=true", "SA", "");
    }

    public static Connection getConnection(String path) throws SQLException{
        return DriverManager.getConnection("jdbc:hsqldb:file:" + path + ";shutdown=true;ifexist=true", "SA", "");
    }
}
