package comp3350.group9.theauctionhouse.persistence.executor;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import comp3350.group9.theauctionhouse.core.application.ReportQueriable;
import comp3350.group9.theauctionhouse.core.domain.Report;
import comp3350.group9.theauctionhouse.core.domain.User;

public class ReportQueryExecutor extends HSQLQueryExecutor implements ReportQueriable {
    private final String COLUMN_SELECTS = "reports.*," +
            "reports.id as report_id," +
            "reporter.id as reporter_id," +
            "reporter.username as reporter_username," +
            "reporter.email as reporter_email," +
            "receiver.id as receiver_id," +
            "receiver.username as receiver_username," +
            "receiver.email as receiver_email," +
            "users.*";

    private final String TABLE_JOINS = "JOIN users as reporter ON (reporter.id = reports.reporterid) " +
            "JOIN users as receiver ON (receiver.id = reports.reportreceiverid)";

    public ReportQueryExecutor(String dbPath) {
        super(dbPath);
    }


    @Override
    public List<Report> findAll() {
        final List<Report> reports = new ArrayList<>();

        try (final Connection c = connection()) {
            final PreparedStatement st = c.prepareStatement("SELECT " + COLUMN_SELECTS + " FROM reports, users " + TABLE_JOINS);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                reports.add(fromResultSet(rs));
                rs.next(); rs.next(); rs.next();
            }

            rs.close();
            st.close();

            return reports;
        } catch (final SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Report findById(String id) {
        Report report = null;
        try (final Connection c = connection()) {
            final PreparedStatement st = c.prepareStatement("SELECT " + COLUMN_SELECTS + " FROM reports, users " + TABLE_JOINS + "WHERE USERS.id=?");
            st.setInt(1, Integer.parseInt(id));

            final ResultSet rs = st.executeQuery();

            if (rs.next()) {
                report = fromResultSet(rs);
                rs.next(); rs.next(); rs.next();
            }

            rs.close();
            st.close();

            return report;
        } catch (final SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This only inserts the Report. Any other table entity must already exist or else foreign key constraint will fail.
     */
    @Override
    public boolean add(Report report) {
        try (final Connection c = connection()) {
            final PreparedStatement st = c.prepareStatement("INSERT INTO reports(reporterid,reportreceiverid,reporttype,reportdescription) VALUES(?, ?, ?, ?)");
            st.setInt(1, Integer.parseInt(report.reporter().id()));
            st.setInt(2, Integer.parseInt(report.receiver().id()));
            st.setString(3, report.reportType());
            st.setString(4, report.reportDescription());
            return st.executeUpdate() == 1;
        } catch (final SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(String id, Report report) {
        return false;
    }

    @Override
    public boolean delete(Report report) {
        return false;
    }

    @Override
    public String addReport(Report report) {
        try (final Connection c = connection()) {
            final PreparedStatement st = c.prepareStatement("INSERT INTO reports(reporterid,reportreceiverid,reporttype,reportdescription) VALUES(?, ?, ?, ?)", RETURN_GENERATED_KEYS);
            st.setInt(1, Integer.parseInt(report.reporter().id()));
            st.setInt(2, Integer.parseInt(report.receiver().id()));
            st.setString(3, report.reportType());
            st.setString(4, report.reportDescription());
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

    @Override
    public List<Report> findAllByReceiverId(String id) {
        final List<Report> reports = new ArrayList<>();

        try (final Connection c = connection()) {
            final PreparedStatement st = c.prepareStatement("SELECT " + COLUMN_SELECTS + " FROM users, reports " + TABLE_JOINS + " WHERE reports.reportreceiverid = ?");
            st.setInt(1, Integer.parseInt(id));

            final ResultSet rs = st.executeQuery();
            while (rs.next()) {
                final Report report = fromResultSet(rs);
                reports.add(report);
                rs.next(); rs.next(); rs.next();
            }
            rs.close();
            st.close();

            return reports;
        } catch (final SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Report> findAllByReporterId(String id) {
        final List<Report> reports = new ArrayList<>();

        try (final Connection c = connection()) {
            final PreparedStatement st = c.prepareStatement("SELECT " + COLUMN_SELECTS + " FROM users, reports " + TABLE_JOINS + " WHERE reports.reporterid = ?");
            st.setInt(1, Integer.parseInt(id));

            final ResultSet rs = st.executeQuery();
            while (rs.next()) {
                final Report report = fromResultSet(rs);
                reports.add(report);
                rs.next(); rs.next(); rs.next(); rs.next(); rs.next();
            }
            rs.close();
            st.close();

            return reports;
        } catch (final SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * This function expects the ResultSet SQL query to have selected * and made JOINS with foreign keys
     **/
    private Report fromResultSet(final ResultSet rs) throws SQLException {
        String id = rs.getString("report_id");
        String reportType = rs.getString("reporttype");
        String reportDesc = rs.getString("reportdescription");

        String reporterId = rs.getString("reporter_id");
        String reporterUsername = rs.getString("reporter_username");
        String reporterEmail = rs.getString("reporter_Email");
        User reporter = new User(reporterId, reporterUsername, reporterEmail, null);

        String receiverId = rs.getString("receiver_id");
        String receiverUsername = rs.getString("receiver_username");
        String receiverEmail = rs.getString("receiver_Email");
        User receiver = new User(receiverId, receiverUsername, receiverEmail, null);

        return new Report(id, reporter, receiver, reportType, reportDesc);
    }
}