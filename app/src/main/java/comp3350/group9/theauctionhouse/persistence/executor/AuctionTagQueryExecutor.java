package comp3350.group9.theauctionhouse.persistence.executor;

import java.security.InvalidParameterException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import comp3350.group9.theauctionhouse.core.application.AuctionTagQueriable;
import comp3350.group9.theauctionhouse.core.domain.AuctionTag;
import kotlin.NotImplementedError;

public class AuctionTagQueryExecutor extends HSQLQueryExecutor implements AuctionTagQueriable {
    public AuctionTagQueryExecutor(String dbpath) {
        super(dbpath);
    }

    @Override
    public List<AuctionTag> findByAuctionId(String auctionId) {
        final List<AuctionTag> tags = new ArrayList<>();
        try (final Connection c = connection()) {
            final PreparedStatement st = c.prepareStatement("SELECT * FROM auction_tags WHERE auctionid=?");
            st.setString(1,auctionId);

            final ResultSet rs = st.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                String label = rs.getString("tag");

                try {
                    tags.add(AuctionTag.builder().id(id).auctionId(auctionId).tag(label).build());
                } catch (InvalidParameterException e) {
                    e.printStackTrace();
                    System.out.printf("Invalid auction tag (id=%s) in DB. Skipping...%n", id);
                }
            }
            rs.close();
            st.close();

            return tags;
        } catch (final SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<AuctionTag> findByTags(List<String> tagsToSearch) {
        final List<AuctionTag> tags = new ArrayList<>();
        try (final Connection c = connection()) {
            final PreparedStatement st = c.prepareStatement("SELECT * FROM auction_tags WHERE tag in (?)");
            Array array = c.createArrayOf("VARCHAR", tagsToSearch.toArray());
            st.setArray(1,array);

            final ResultSet rs = st.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                String auctionId = rs.getString("auctionid");
                String label = rs.getString("tag");
                try {
                    tags.add(AuctionTag.builder().id(id).auctionId(auctionId).tag(label).build());
                } catch (InvalidParameterException e){
                    e.printStackTrace();
                    System.out.printf("Invalid auction tag (id=%s) in DB. Skipping...%n",id);
                }
            }
            rs.close();
            st.close();

            return tags;
        } catch (final SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public boolean batchInsert(List<AuctionTag> tags, String auctionId) {
        if (tags.size() == 0) return true;

        try (final Connection c = connection()) {
            final PreparedStatement st = c.prepareStatement("INSERT INTO auction_tags(auctionid,tag) VALUES(?, ?)");

            for (AuctionTag t : tags) {
                st.setString(1, auctionId);
                st.setString(2, t.tag());
                st.addBatch();
            }

            return st.executeBatch().length == tags.size();
        } catch (final SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean add(AuctionTag auctionTag){
        try (final Connection c = connection()) {
            final PreparedStatement st = c.prepareStatement("INSERT INTO auction_tags(auctionid,tag) VALUES(?, ?)");
            st.setString(1, auctionTag.getAuctionId());
            st.setString(2, auctionTag.tag());
            return st.executeUpdate() == 1;
        } catch (final SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<AuctionTag> findAll() throws NotImplementedError {
        throw new NotImplementedError();
    }

    @Override
    public AuctionTag findById(String id) throws NotImplementedError {
        throw new NotImplementedError();
    }

    @Override
    public boolean update(String id, AuctionTag auctionTag) throws NotImplementedError {
        throw new NotImplementedError();
    }

    @Override
    public boolean delete(AuctionTag auctionTag) throws NotImplementedError {
        throw new NotImplementedError();
    }
}
