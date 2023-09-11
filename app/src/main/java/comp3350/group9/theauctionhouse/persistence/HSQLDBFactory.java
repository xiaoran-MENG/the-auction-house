package comp3350.group9.theauctionhouse.persistence;

public class HSQLDBFactory {
    private static HSQLDBQueryEngine o;

    public static synchronized HSQLDBQueryEngine get() {
        if (o == null) o = new HSQLDBQueryEngine(HSQLDBConfig.getPath());
        return o;
    }
}

