package comp3350.group9.theauctionhouse.persistence.executor;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import comp3350.group9.theauctionhouse.core.application.ProductQueriable;
import comp3350.group9.theauctionhouse.core.application.Queriable;
import comp3350.group9.theauctionhouse.core.domain.Product;

public class ProductQueryExecutor extends HSQLQueryExecutor implements ProductQueriable {
    public ProductQueryExecutor(String dbPath) {
        super(dbPath);
    }

    private Product fromResultSet(final ResultSet rs) throws SQLException {
        return new Product(
            String.valueOf(rs.getInt("id")),
            rs.getString("name"),
            rs.getString("description"));
    }

    @Override
    public List<Product> findAll() {
        final List<Product> products = new ArrayList<>();

        try (final Connection c = connection()) {
            final PreparedStatement st = c.prepareStatement("SELECT * FROM products");
            final ResultSet rs = st.executeQuery();
            while (rs.next())
            {
                final Product product = fromResultSet(rs);
                products.add(product);
            }
            rs.close();
            st.close();

            return products;
        }
        catch (final SQLException e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Product findById(String id) {
        Product product=null;
        try(final Connection c = connection()){
            final PreparedStatement st = c.prepareStatement("SELECT * FROM PRODUCTS WHERE PRODUCTS.id=? LIMIT 1");
            st.setInt(1, Integer.parseInt(id));
            final ResultSet rs = st.executeQuery();

            if (rs.next()) {
                product = fromResultSet(rs);
            }
            rs.close();
            st.close();
            return product;

        }catch (final SQLException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean add(Product product) {
        try (final Connection c = connection()) {
            final PreparedStatement st = c.prepareStatement("INSERT INTO PRODUCTS(name,description) VALUES(?,?)");
            st.setString(1,product.name());
            st.setString(2,product.description());
            return st.executeUpdate() == 1;
        } catch (final SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(String id, Product product) {
        return false;
    }

    @Override
    public boolean delete(Product product) {
        return false;
    }

    @Override
    public String addProduct(Product product) {
        try (final Connection c = connection()) {
            final PreparedStatement st = c.prepareStatement("INSERT INTO PRODUCTS(name,description) VALUES(?,?)",RETURN_GENERATED_KEYS);
            st.setString(1,product.name());
            st.setString(2,product.description());
            if (st.executeUpdate() == 1) {
                ResultSet generatedKeys = st.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return String.valueOf(generatedKeys.getInt(1));
                }
            }
            return null;
        } catch (final SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
