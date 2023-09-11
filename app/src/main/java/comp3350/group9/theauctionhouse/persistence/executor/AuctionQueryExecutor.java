package comp3350.group9.theauctionhouse.persistence.executor;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import comp3350.group9.theauctionhouse.business.common.DateTime;
import comp3350.group9.theauctionhouse.business.common.functional.Flux;
import comp3350.group9.theauctionhouse.core.application.AuctionQueriable;
import comp3350.group9.theauctionhouse.core.application.AuctionTagQueriable;
import comp3350.group9.theauctionhouse.core.application.BidQueriable;
import comp3350.group9.theauctionhouse.core.domain.Auction;
import comp3350.group9.theauctionhouse.core.domain.AuctionTag;
import comp3350.group9.theauctionhouse.core.domain.Bid;
import comp3350.group9.theauctionhouse.core.domain.Product;
import comp3350.group9.theauctionhouse.core.domain.User;

public class AuctionQueryExecutor extends HSQLQueryExecutor implements AuctionQueriable {
    private final String COLUMN_SELECTS = "auctions.*, users.*, products.*, " +
            "auctions.id as auction_id, " +
            "users.id as seller_id, " +
            "products.id as product_id, " +
            "auctions.description as auction_description, " +
            "products.description as product_description, " +
            "auctions.imagepath as imagepath ";
    private final String TABLE_JOINS = "JOIN users ON (users.id = auctions.sellerid) " +
            "JOIN products ON (products.id = auctions.productid)";

    private final BidQueriable bidsDB;
    private final AuctionTagQueriable auctionTagsDB;

    public AuctionQueryExecutor(String dbpath, BidQueriable bids, AuctionTagQueriable auctions) {
        super(dbpath);
        bidsDB = bids;
        auctionTagsDB = auctions;
    }

    @Override
    public List<Auction> findBySellerIdOrderedByDateCreated(String sellerId, int limit) {
        final List<Auction> auctions = new ArrayList<>();
        try (final Connection c = connection()) {
            final PreparedStatement st = c.prepareStatement("SELECT " + COLUMN_SELECTS + " FROM auctions " + TABLE_JOINS + "WHERE sellerid=? ORDER BY datecreated ASC LIMIT ?");
            st.setInt(1, Integer.parseInt(sellerId));
            st.setInt(2, limit);

            final ResultSet rs = st.executeQuery();
            while (rs.next()) {
                final Auction auction = fromFullResultSet(rs);
                auctions.add(auction);
            }
            rs.close();
            st.close();

            return auctions;
        } catch (final SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Auction> findByTags(List<String> tagsToSearch) {
        List<AuctionTag> tags = auctionTagsDB.findByTags(tagsToSearch);
        Set<String> auctionIds = new HashSet<>(Flux.of(tags).map(AuctionTag::getAuctionId).get());

        final List<Auction> auctions = new ArrayList<>();

        try (final Connection c = connection()) {
            final PreparedStatement st = c.prepareStatement("SELECT " + COLUMN_SELECTS + " FROM auctions " + TABLE_JOINS + "WHERE id in (?)");
            Array array = c.createArrayOf("VARCHAR", auctionIds.toArray());
            st.setArray(1, array);

            final ResultSet rs = st.executeQuery();
            while (rs.next()) {
                final Auction auction = fromFullResultSet(rs);
                auctions.add(auction);
            }
            rs.close();
            st.close();

            return auctions;
        } catch (final SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Auction> findByTitle(String title) {
        ArrayList<Auction> auctions = new ArrayList<>();
        try (final Connection c = connection()) {
            final PreparedStatement st = c.prepareStatement("SELECT " + COLUMN_SELECTS + " FROM AUCTIONS " + TABLE_JOINS + " WHERE AUCTIONS.TITLE=?");
            st.setString(1, title);

            final ResultSet rs = st.executeQuery();

            if (rs.next()) {
                auctions.add(fromFullResultSet(rs));
            }

            rs.close();
            st.close();

            return auctions;
        } catch (final SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Auction> findIncompleteExpired() {
        final List<Auction> auctions = new ArrayList<>();
        String statement = "SELECT " + COLUMN_SELECTS + " FROM auctions " + TABLE_JOINS + "WHERE auctions.expiry <= CURRENT_DATE AND auctions.completed=FALSE";
        try (final Connection c = connection()) {
            final PreparedStatement st = c.prepareStatement(statement);
            final ResultSet rs = st.executeQuery();
            while (rs.next()) {
                final Auction auction = fromFullResultSet(rs);
                auctions.add(auction);
            }
            rs.close();
            st.close();

            return auctions;
        } catch (final SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Auction> findAll() {
        final List<Auction> auctions = new ArrayList<>();
        String statement = "SELECT " + COLUMN_SELECTS + " FROM auctions " + TABLE_JOINS + " ORDER BY expiry ASC";

        try (final Connection c = connection()) {
            final PreparedStatement st = c.prepareStatement(statement);
            final ResultSet rs = st.executeQuery();
            while (rs.next()) {
                final Auction auction = fromFullResultSet(rs);
                auctions.add(auction);
            }
            rs.close();
            st.close();

            return auctions;
        } catch (final SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Auction findById(String id) {
        String sql = "SELECT " + COLUMN_SELECTS + " FROM AUCTIONS " + TABLE_JOINS + " WHERE AUCTIONS.id=?";
        Auction result = null;

        try (Connection c = connection()) {
            PreparedStatement byId = c.prepareStatement(sql);
            byId.setInt(1, Integer.parseInt(id));
            ResultSet rs = byId.executeQuery();

            if (rs.next()) {
                result = fromFullResultSet(rs);
            }

            rs.close();
            byId.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Adds the auction information to the Auctions table. <br>
     * Also adds any Tags attached to the auction.
     * This will fail if the auction.product().id() does not correspond to an already existing Product
     * This will fail if the auction.seller().id() does not correspond to an already existing User <br>
     **/
    @Override
    public boolean add(Auction auction) {
        try (final Connection c = connection()) {
            final PreparedStatement st = c.prepareStatement("INSERT INTO AUCTIONS(sellerid, productid, title, description, imagepath, expiry, minBid) VALUES(?, ?, ?, ?, ?, ?, ?)",RETURN_GENERATED_KEYS);
            st.setInt(1, Integer.parseInt(auction.seller().id()));
            st.setInt(2, Integer.parseInt(auction.product().id()));
            st.setString(3, auction.title());
            st.setString(4, auction.description());
            st.setString(5, auction.imagePath());
            st.setString(6, auction.expiry().toString("yyyy-MM-dd"));
            st.setDouble(7, auction.minBid());

            if (st.executeUpdate() == 1) {
                String auctionId = getGeneratedIdKeys(st).get(0);
                return auctionTagsDB.batchInsert(auction.tags(), auctionId);
            }
            return false;
        } catch (final SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This function does not update the related foreign tables. Only the Auction table.
     */
    @Override
    public boolean update(String id, Auction o) {
        String sql = "UPDATE PUBLIC.AUCTIONS SET sellerId=?, productId=?, title=?, description=?, expiry=?, minBid=?, completed=? WHERE AUCTIONS.ID=?";
        try (Connection c = connection()) {
            PreparedStatement update = c.prepareStatement(sql);
            update.setInt(1, Integer.parseInt(o.seller().id()));
            update.setInt(2, Integer.parseInt(o.product().id()));
            update.setString(3, o.title());
            update.setString(4, o.description());
            update.setString(5, o.expiry().toString("yyyy-MM-dd"));
            update.setDouble(6, o.minBid());
            update.setBoolean(7, o.isCompleted());
            update.setInt(8, Integer.parseInt(id));
            return update.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Auction o) {
        String sql = "DELETE FROM PUBLIC.AUCTIONS WHERE AUCTIONS.ID=?";
        try (Connection c = connection()) {
            PreparedStatement del = c.prepareStatement(sql);
            del.setInt(1, Integer.parseInt(o.id()));
            return del.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //prepared statement must be executed with parameter RETURN_GENERATED_KEYS
    private List<String> getGeneratedIdKeys(PreparedStatement st) throws SQLException {
        List<String> ids = new ArrayList<>();
        int index = 1;
        ResultSet generatedKeys = st.getGeneratedKeys();
        while (generatedKeys.next())
            ids.add(String.valueOf(generatedKeys.getInt(index++)));
        return ids;
    }

    /**
     * This function expects the ResultSet SQL query to have selected * and made JOINS with foreign keys
     **/
    private Auction fromFullResultSet(final ResultSet rs) throws SQLException {
        String id = rs.getString("auction_id");
        String title = rs.getString("title");
        String description = rs.getString("auction_description");
        String path = rs.getString("imagepath");
        double minBid = rs.getDouble("minBid");
        boolean completed = rs.getBoolean("completed");
        Date expiry = new Date(rs.getTimestamp("expiry").getTime());

        String sellerId = rs.getString("seller_id");
        String seller_username = rs.getString("username");
        String seller_email = rs.getString("email");
        User seller = new User(sellerId, seller_username, seller_email, null);

        String productId = rs.getString("product_id");
        String productName = rs.getString("name");
        String productDesc = rs.getString("product_description");
        Product product = new Product(productId, productName, productDesc);

        List<Bid> bids = bidsDB.findByAuctionId(id);
        List<AuctionTag> tags = auctionTagsDB.findByAuctionId(id);

        return Auction.builder()
                .id(id)
                .seller(seller)
                .description(description)
                .title(title)
                .expiry(DateTime.of(expiry))
                .minBid(minBid)
                .product(product)
                .imagePath(path)
                .completed(completed)
                .tags(tags)
                .bids(bids)
                .forceBuild();
    }
}