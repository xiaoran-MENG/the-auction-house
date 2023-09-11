package comp3350.group9.theauctionhouse.core.domain;

public class Product extends Entity {
    private String name;
    private String description;

    public Product(String id) {
        super(id);
    }

    public Product(String id, String name) {
        super(id);
        this.name = name;
    }

    public Product(String id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }
}
