package comp3350.group9.theauctionhouse.persistence.executor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import comp3350.group9.theauctionhouse.core.application.UserQueriable;
import comp3350.group9.theauctionhouse.core.domain.User;

public class UserQueryExecutor extends HSQLQueryExecutor implements UserQueriable {
    public UserQueryExecutor(String dbPath) {
        super(dbPath);
    }

    private User fromResultSet(final ResultSet rs) throws SQLException {
        String id = String.valueOf(rs.getInt("id"));
        String username = rs.getString("username");
        String email = rs.getString("email");
        final String password = rs.getString("password");
        return new User(id,username,email,password);
    }

    @Override
    public List<User> findAll(){
        final List<User> users = new ArrayList<>();

        try (final Connection c = connection()) {
            final PreparedStatement st = c.prepareStatement("SELECT * FROM users");

            final ResultSet rs = st.executeQuery();
            while (rs.next())
            {
                final User course = fromResultSet(rs);
                users.add(course);
            }
            rs.close();
            st.close();

            return users;
        }
        catch (final SQLException e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public User findById(String id) {
        User user = null;
        try (final Connection c = connection()) {
            final PreparedStatement st = c.prepareStatement("SELECT * FROM USERS WHERE USERS.id=?");
            st.setInt(1, Integer.parseInt(id));

            final ResultSet rs = st.executeQuery();

            if (rs.next()) {
                user = fromResultSet(rs);
            }

            rs.close();
            st.close();

            return user;
        }
        catch (final SQLException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean add(User user) {
        try (final Connection c = connection()) {
            final PreparedStatement st = c.prepareStatement("INSERT INTO users(username,email,password) VALUES(?, ?, ?)");
            st.setString(1, user.username());
            st.setString(2, user.email());
            st.setString(3, user.password());
            return st.executeUpdate() == 1;
        } catch (final SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(String id, User user) {
        return false;
    }

    @Override
    public boolean delete(User user) {
        return false;
    }

    @Override
    public User findByUsername(String name) {
        User user = null;
        try (final Connection c = connection()) {
            final PreparedStatement st = c.prepareStatement("SELECT * FROM USERS WHERE USERS.USERNAME=?");
            st.setString(1, name);

            final ResultSet rs = st.executeQuery();

            if (rs.next()) user = fromResultSet(rs);

            rs.close();
            st.close();

            return user;
        }
        catch (final SQLException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public User findByEmail(String email) {
        User user = null;
        try (final Connection c = connection()) {
            final PreparedStatement st = c.prepareStatement("SELECT * FROM users WHERE users.email=?");
            st.setString(1, email);

            final ResultSet rs = st.executeQuery();

            if (rs.next()) {
                user = fromResultSet(rs);
            }

            rs.close();
            st.close();

            return user;
        }
        catch (final SQLException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deleteByUsername(String username) {
        try (final Connection c = connection()) {
            final PreparedStatement st = c.prepareStatement("DELETE FROM USERS WHERE USERS.USERNAME=?");
            st.setString(1, username);

            int row = st.executeUpdate();

            st.close();

            return row == 1;
        }
        catch (final SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
