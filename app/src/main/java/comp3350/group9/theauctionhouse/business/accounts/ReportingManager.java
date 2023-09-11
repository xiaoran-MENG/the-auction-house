package comp3350.group9.theauctionhouse.business.accounts;

import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.business.common.functional.Flux;
import comp3350.group9.theauctionhouse.exception.Reporting.FailedToReportException;
import comp3350.group9.theauctionhouse.core.application.QueryEngine;
import comp3350.group9.theauctionhouse.persistence.HSQLDBFactory;
import comp3350.group9.theauctionhouse.core.domain.Report;
import comp3350.group9.theauctionhouse.core.domain.User;

public class ReportingManager {
    private final User reporter;
    private final QueryEngine queryEngine;
    private final UserAccountManager profile;

    public ReportingManager(QueryEngine queryEngine) {
        this.queryEngine = queryEngine;
        this.profile = UserAccountManager.of();
        this.reporter = profile.getLoggedInUser();
    }

    public ReportingManager(QueryEngine queryEngine, UserAccountManager profile) {
        this.queryEngine = queryEngine;
        this.profile = profile;
        this.reporter = profile.getLoggedInUser();
    }

    public static ReportingManager of(){
        return new ReportingManager(HSQLDBFactory.get());
    }

    public String makeReport(String reportReceiverId, String reportType, String reportDescription) throws FailedToReportException {
        if (Report.ReportTypes.getByLabel(reportType) == null)
            throw new FailedToReportException(String.format("'%s' is not a valid report type.", reportType));

        User reportReceiver = profile.getUserById(reportReceiverId);

        String newId = java.util.UUID.randomUUID().toString();
        Report newReport = new Report(
                newId,
                reporter,
                reportReceiver,
                reportType,
                reportDescription);

        return this.queryEngine.reports().addReport(newReport);
    }

    public Report getById(String id) {
        return this.queryEngine.reports().findById(id);
    }

    public Flux<Report> getAll() {
        return Flux.of(this.queryEngine.reports().findAll());
    }

    public Flux<Report> getAllByReceiverId(String id) {
        return Flux.of(this.queryEngine.reports().findAllByReceiverId(id));
    }

    public Flux<Report> getAllByReporterId(String id) {
        return Flux.of(this.queryEngine.reports().findAllByReporterId(id));
    }

}
