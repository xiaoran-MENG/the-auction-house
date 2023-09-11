package comp3350.group9.theauctionhouse.persistence.executor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import comp3350.group9.theauctionhouse.business.common.DateTime;
import comp3350.group9.theauctionhouse.core.application.BidQueriable;
import comp3350.group9.theauctionhouse.core.domain.Auction;
import comp3350.group9.theauctionhouse.core.domain.Bid;
import comp3350.group9.theauctionhouse.core.domain.Product;
import comp3350.group9.theauctionhouse.core.domain.User;

public class BidQueryExecutor extends HSQLQueryExecutor implements BidQueriable {
    private final String COLUMN_SELECTS = "bids.*, auctions.*, products.*," +
            "bids.id as bid_id," +
            "auctions.id as auction_id," +
            "products.id as product_id," +
            "auctions.description as auction_description, " +
            "products.description as product_description, " +
            "auction_seller.id as seller_id," +
            "auction_seller.username as seller_username," +
            "auction_seller.email as seller_email," +
            "bid_user.id as user_id," +
            "bid_user.username as bid_username," +
            "bid_user.email as bid_email";

    private final String TABLE_JOINS = "JOIN users as bid_user ON (bid_user.id = bids.userid) " +
            "JOIN auctions ON (auctions.id = bids.auctionid) " +
            "JOIN products on (products.id = auctions.id) " +
            "JOIN users as auction_seller ON (auction_seller.id = auctions.sellerid)";

    public BidQueryExecutor(String dbPath) {
        super(dbPath);
    }

    @Override
    public List<Bid> findAll() {
        final List<Bid> bids = new ArrayList<>();

        try (Connection c = connection()) {
            PreparedStatement st = c.prepareStatement("SELECT " + COLUMN_SELECTS + " FROM BIDS " + TABLE_JOINS);
            ResultSet rs = st.executeQuery();
            while (rs.next())
                bids.add(fromResultSet(rs));

            rs.close();
            st.close();
            return bids;
        } catch (final SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Bid findById(String id) {
        Bid bids = null;
        try (Connection c = connection()) {

            PreparedStatement st = c.prepareStatement("SELECT " + COLUMN_SELECTS + " FROM BIDS " + TABLE_JOINS + " WHERE BIDS.id=?");
            st.setInt(1, Integer.parseInt(id));
            ResultSet rs = st.executeQuery();
            if (rs.next()) bids = fromResultSet(rs);

            rs.close();
            st.close();
            return bids;
        } catch (final SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This does not add foreign key related tables.
     */
    @Override
    public boolean add(Bid bid) {
        try (Connection c = connection()) {
            PreparedStatement st = c.prepareStatement("INSERT INTO bids(userid,auctionid,price,status) VALUES(?, ?, ?, ?)");
            st.setInt(1, Integer.parseInt(bid.user().id()));
            st.setInt(2, Integer.parseInt(bid.auction().id()));
            st.setDouble(3, bid.price().doubleValue());
            st.setString(4, bid.status().toString());
            return st.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Sets the winner status on the winning bid, and loser status on all other bids
    // If winning bid is passed as NULL, sets losers status on all bids
    @Override
    public boolean updateCompletedAuctionBidStatus(String auctionId, String winningBidId, String winnerStatus, String loserStatus){
        String sql;
        if (winningBidId == null) sql = "UPDATE bids SET status=? WHERE bids.auctionid=?";
        else sql = "UPDATE bids SET status= CASE WHEN bids.id=? THEN CAST(? AS VARCHAR(36)) ELSE CAST(? AS VARCHAR(36)) END WHERE bids.auctionid=?";

        try (Connection c = connection()) {
            PreparedStatement update = c.prepareStatement(sql);

            int index = 1;
            if (winningBidId != null) {
                update.setInt(index++, Integer.parseInt(winningBidId));
                update.setString(index++, winnerStatus);
            }
            update.setString(index++, loserStatus);
            update.setInt(index++, Integer.parseInt(auctionId));

            return update.executeUpdate() >= 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(String id, Bid bid) {
        String sql = "UPDATE bids SET auctionid=?,userid=?,price=?,status=? WHERE bids.id=?";
        try (Connection c = connection()) {
            PreparedStatement update = c.prepareStatement(sql);
            update.setInt(1,    Integer.parseInt(bid.auction().id()));
            update.setInt(2,    Integer.parseInt(bid.user().id()));
            update.setDouble(3, bid.price().doubleValue());
            update.setString(4, bid.status().toString());
            update.setInt(5,    Integer.parseInt(id));
            return update.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Bid bid) {
        return false;
    }


    @Override
    public Bid findByUserIdANDAuctionId(String userId, String auctionId) {
        Bid bid = null;

        try (Connection c = connection()) {
            PreparedStatement st = c.prepareStatement("SELECT " + COLUMN_SELECTS + " FROM bids " + TABLE_JOINS + " WHERE bids.auctionid=? AND bids.userid=? LIMIT 1");
            st.setString(1,auctionId);
            st.setString(2,userId);

            ResultSet rs = st.executeQuery();
            if (rs.next()) bid = fromResultSet(rs);

            rs.close();
            st.close();
            return bid;
        } catch (final SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Bid> findByAuctionId(String auctionId) {
        final List<Bid> bids = new ArrayList<>();

        try (Connection c = connection()) {
            PreparedStatement st = c.prepareStatement("SELECT " + COLUMN_SELECTS + " FROM bids " + TABLE_JOINS + " WHERE bids.auctionid=? ORDER BY bids.price DESC");
            st.setString(1,auctionId);

            ResultSet rs = st.executeQuery();
            while (rs.next())
                bids.add(fromResultSet(rs));

            rs.close();
            st.close();
            return bids;
        } catch (final SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    /**
     * This function expects the ResultSet SQL query to have selected * and made JOINS with foreign keys
     **/
    private Bid fromResultSet(final ResultSet rs) throws SQLException {
        String id = rs.getString("bid_id");
        double price = rs.getDouble("price");
        String status = rs.getString("status");

        String userId = rs.getString("user_id");
        String bidUsername = rs.getString("bid_username");
        String bidEmail = rs.getString("bid_email");
        DateTime dateUpdated = DateTime.of(rs.getDate("dateupdated"));

        User bid_user = new User(userId, bidUsername, bidEmail, null);

        String auctionId = rs.getString("auction_id");
        String title = rs.getString("title");
        String description = rs.getString("auction_description");
        double minBid = rs.getDouble("minBid");
        Date expiry = new Date(rs.getTimestamp("expiry").getTime());

        String sellerId = rs.getString("seller_id");
        String seller_username = rs.getString("seller_username");
        String seller_email = rs.getString("seller_email");
        User seller = new User(sellerId, seller_username, seller_email, null);

        String productId = rs.getString("product_id");
        String productName = rs.getString("name");
        String productDesc = rs.getString("product_description");
        Product product = new Product(productId, productName, productDesc);

        Auction auction = Auction.builder()
                .id(auctionId)
                .seller(seller)
                .description(description)
                .title(title)
                .expiry(DateTime.of(expiry))
                .minBid(minBid)
                .product(product)
                .forceBuild();

        return new Bid(id, bid_user, auction, price, Bid.Status.Status(status),dateUpdated);
    }
}
