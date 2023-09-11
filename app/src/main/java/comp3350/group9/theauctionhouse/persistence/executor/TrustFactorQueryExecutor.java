package comp3350.group9.theauctionhouse.persistence.executor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import comp3350.group9.theauctionhouse.business.common.functional.Flux;
import comp3350.group9.theauctionhouse.core.application.TrustFactorQueriable;
import comp3350.group9.theauctionhouse.core.domain.TrustFactor;
import comp3350.group9.theauctionhouse.core.domain.User;

public class TrustFactorQueryExecutor extends HSQLQueryExecutor implements TrustFactorQueriable {
    private final String COLUMN_SELECTS = "trustfactors.*, users.*, trustfactors.id as trust_id, users.id as user_id";
    private final String TABLE_JOINS = "JOIN users ON (users.id = trustfactors.userid)";

    public TrustFactorQueryExecutor(String dbPath) {
        super(dbPath);
    }

    // Ensures that no duplicate Trust factors are made
    private List<TrustFactor> listFromResultSet(final ResultSet rs) throws SQLException {
        List<TrustFactor> trusts = new ArrayList<TrustFactor>();

        if (!rs.next()) {
            return trusts;
        }

        do {
            final String id = rs.getString("trust_id");
            final String rating = rs.getString("rating");

            final String userID = rs.getString("user_id");
            final String username = rs.getString("username");
            final String email = rs.getString("email");

            TrustFactor matchingTrust = Flux.of(trusts)
                    .by(u -> Objects.equals(u.getUserID(), userID))
                    .one()
                    .get();

            // Uses the existing trust factor
            if (matchingTrust != null) {
                matchingTrust.addRating(Float.parseFloat(rating));
            }
            // Creates a new one if one does not exist
            else {
                TrustFactor newTrust = new TrustFactor(id,userID);
                newTrust.addRating(Float.parseFloat(rating));
                trusts.add(newTrust);
            }
        }
        while (rs.next());

        return trusts;
    }

    @Override
    public List<TrustFactor> findAll() {
        try (final Connection c = connection()) {
            final PreparedStatement st = c.prepareStatement("SELECT " + COLUMN_SELECTS + " FROM trustfactors " + TABLE_JOINS);

            final ResultSet rs = st.executeQuery();

            List<TrustFactor> trusts = listFromResultSet(rs);

            rs.close();
            st.close();

            return trusts;
        } catch (final SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public TrustFactor findById(String id) {
        return null;
    }

    @Override
    public boolean add(TrustFactor trustfactor) {
        try (Connection c = connection()) {
            // Uploads the trust factor
            PreparedStatement st = c.prepareStatement("INSERT INTO trustfactors(userid,rating) VALUES(?, ?)");
            st.setInt(1, Integer.parseInt(trustfactor.getUserID()));
            st.setString(2, Float.toString(trustfactor.getMostRecentRating().getRating()));
            return st.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(String id, TrustFactor trustfactor) {
        return false;
    }

    @Override
    public boolean delete(TrustFactor trustfactor) {
        return false;
    }

    @Override
    public TrustFactor findByUserId(String id) {
        List<TrustFactor> trusts = findAll();

        TrustFactor matchingTrust = Flux.of(trusts)
                .by(u -> u.getUserID().equals(id))
                .one()
                .get();

        return matchingTrust;
    }

    /**
     * This function expects the ResultSet SQL query to have selected * and made JOINS with foreign keys
     **/
    private TrustFactor fromResultSet(final ResultSet rs) throws SQLException {
        User user = new User(rs.getString("user_id"),rs.getString("username"),rs.getString("email"),null);

        TrustFactor trust = new TrustFactor(rs.getString("trust_id"),rs.getString("user_id"));
        final String rating = rs.getString("rating");
        trust.addRating(Float.valueOf(rating));

        return trust;
    }
}
