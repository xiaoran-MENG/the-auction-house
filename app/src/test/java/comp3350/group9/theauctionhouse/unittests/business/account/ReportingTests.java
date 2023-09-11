package comp3350.group9.theauctionhouse.unittests.business.account;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import comp3350.group9.theauctionhouse.business.accounts.ReportingManager;
import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.exception.Reporting.FailedToReportException;
import comp3350.group9.theauctionhouse.core.application.QueryEngine;
import comp3350.group9.theauctionhouse.core.application.ReportQueriable;
import comp3350.group9.theauctionhouse.core.application.UserQueriable;
import comp3350.group9.theauctionhouse.core.domain.Report;
import comp3350.group9.theauctionhouse.core.domain.User;

@RunWith(MockitoJUnitRunner.Silent.class)

public class ReportingTests {
    private User reporter, reportee;
    private ReportingManager reports;

    @Mock
    private QueryEngine queryEngine;

    @Mock
    private ReportQueriable reportQueriable;

    @Before
    public void init() {
        when(queryEngine.reports()).thenReturn(reportQueriable);

        reporter = new User("1", "John Doe", "john@myumanitoba.ca", "12345678");
        reportee = new User("2", "Jane Doe", "jane@myumanitoba.ca", "12345678");

        UserQueriable userQueriable = mock(UserQueriable.class);
        when(queryEngine.users()).thenReturn(userQueriable);
        when(userQueriable.findById(reporter.id())).thenReturn(reporter);
        when(userQueriable.findById(reportee.id())).thenReturn(reportee);
        UserAccountManager profile = UserAccountManager.of(queryEngine, reporter.id());

        reports = new ReportingManager(queryEngine, profile);
    }

    @Test
    public void testShouldMakeReport() {
        when(reportQueriable.addReport(any(Report.class))).thenReturn("1");
        reports.makeReport(reportee.id(), Report.ReportTypes.LIKELY_SCAM.label, "This user stole my money!");
        verify(reportQueriable).addReport(any(Report.class));
    }

    @Test(expected = FailedToReportException.class)
    public void testShouldThrowAndFailToMakeReportWithInvalidReportType() {
        when(reportQueriable.addReport(any(Report.class))).thenReturn("1");
        reports.makeReport(reportee.id(), Report.ReportTypes.LIKELY_SCAM.label + "Invalid Type", "This user stole my money!");
        fail("Expected FailedToReportException, but none was thrown");
    }

    @Test
    public void testShouldGetReport() {
        Report report = new Report("1", reporter, reportee, Report.ReportTypes.LIKELY_SCAM.label, "Test Report");
        when(reportQueriable.findById(anyString())).thenReturn(report);
        Report result = reports.getById("1");
        assertEquals(report.id(), result.id());
        verify(reportQueriable).findById("1");
    }

    @Test
    public void testShouldGetAllReports() {
        List<Report> list = new ArrayList<Report>() {{
           add(new Report("1", reporter, reportee, Report.ReportTypes.LIKELY_SCAM.label, "Test Report"));
           add(new Report("2", reporter, reportee, Report.ReportTypes.LIKELY_SCAM.label, "Test Report"));
        }};
        Set<String> ids = list.stream().map(Report::id).collect(Collectors.toSet());
        when(reportQueriable.findAll()).thenReturn(list);
        reports.getAll().then(x -> assertTrue(ids.contains(x.id())));
        verify(reportQueriable).findAll();
    }

    @Test
    public void testShouldGetAllReportsByReporteeId() {
        List<Report> list = new ArrayList<Report>() {{
            add(new Report("1", reporter, reportee, Report.ReportTypes.LIKELY_SCAM.label, "Test Report"));
            add(new Report("2", reporter, reportee, Report.ReportTypes.LIKELY_SCAM.label, "Test Report"));
        }};
        when(reportQueriable.findAllByReceiverId(anyString())).thenReturn(list);
        reports.getAllByReceiverId("2").then(x -> assertEquals(reportee.id(), x.receiver().id()));
        verify(reportQueriable).findAllByReceiverId("2");
    }

    @Test
    public void testShouldGetAllReportsByReporterId() {
        List<Report> list = new ArrayList<Report>() {{
            add(new Report("1", reporter, reportee, Report.ReportTypes.LIKELY_SCAM.label, "Test Report"));
            add(new Report("2", reporter, reportee, Report.ReportTypes.LIKELY_SCAM.label, "Test Report"));
        }};
        when(reportQueriable.findAllByReporterId(anyString())).thenReturn(list);
        reports.getAllByReporterId("1").then(x -> assertEquals(reporter.id(), x.reporter().id()));
        verify(reportQueriable).findAllByReporterId("1");
    }

}
