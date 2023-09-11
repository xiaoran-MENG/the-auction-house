package comp3350.group9.theauctionhouse.persistence;

public class HSQLDBConfig {
    public static final String USER_DB_DIR = "db";
    public static final String USER_TEST_DB_DIR = "test_db";
    private static String db_name = "AH_DB";
    private static String path;

    public static void setPath(final String path) {
        try {
            Class.forName("org.hsqldb.jdbcDriver").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        HSQLDBConfig.path = path;
    }

    public static String getPath() {
        return path;
    }

    public static String getDb_name(){
        return db_name;
    }
}
