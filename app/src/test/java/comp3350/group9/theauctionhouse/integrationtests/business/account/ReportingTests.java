package comp3350.group9.theauctionhouse.integrationtests.business.account;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import comp3350.group9.theauctionhouse.business.accounts.ReportingManager;
import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.core.application.UserQueriable;
import comp3350.group9.theauctionhouse.exception.Reporting.FailedToReportException;
import comp3350.group9.theauctionhouse.core.domain.Report;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.exception.auth.UserLoginException;
import comp3350.group9.theauctionhouse.exception.auth.UserRegistrationException;
import comp3350.group9.theauctionhouse.persistence.HSQLDBFactory;
import comp3350.group9.theauctionhouse.utils.IntegrationTesting;

public class ReportingTests {

    User userToReport;
    User userReporting;
    ReportingManager reportManager;

    UserAccountManager accManager;

    @Before
    @Test
    public void setup() {
        try {
            IntegrationTesting.setupDB();
        } catch (IOException e) {
            fail(e.getMessage());
        }

        try {
            accManager = UserAccountManager.of();
            accManager.registerUser("tester1", "validemail1@myumanitoba.ca", "password123");
            accManager.registerUser("tester2", "validemail2@myumanitoba.ca", "password123");
            accManager.registerUser("tester3", "validemail3@myumanitoba.ca", "password123");
            accManager.registerUser("tester4", "validemail4@myumanitoba.ca", "password123");

            accManager.loginUser("tester1", "password123");
            userToReport = accManager.getUserByUsername("tester2");
            userReporting = accManager.getUserByUsername("tester1");

            reportManager = ReportingManager.of();
        } catch (Exception e) {
            String message = e.getMessage();
        }
    }

    @Test
    public void testMakingValidReport() {
        String reportType = Report.ReportTypes.LIKELY_SCAM.label;
        String reportId = reportManager.makeReport(userToReport.id(), reportType, "This user stole my money!");
        assertNotNull(reportId);
    }

    @Test
    public void testMakingInvalidReport() {
        String reportType = Report.ReportTypes.LIKELY_SCAM.label + "random_string_here";
        assertThrows(FailedToReportException.class, () -> {
            reportManager.makeReport(userToReport.id(), reportType, "This user stole my money!");
        });
    }

    @Test
    public void testGettingReport() {
        String reportType = Report.ReportTypes.LIKELY_SCAM.label;
        String description = "This user stole my money!";
        String reportId = reportManager.makeReport(userToReport.id(), reportType, description);

        Report report = reportManager.getById(reportId);

        assertEquals(report.reportDescription(),description);
        assertEquals(report.reportType(), reportType);
        assertEquals(report.receiver().id(),userToReport.id());
        assertEquals(report.reporter().id(),userReporting.id());
    }

    @Test
    public void testGettingAllReports(){
        List<Report> startReports = reportManager.getAll().get();

        String reportType1 = Report.ReportTypes.LIKELY_SCAM.label;
        reportManager.makeReport(userToReport.id(), reportType1, "This user stole all of my money!");

        List<Report> middleReports = reportManager.getAll().get();

        String reportType2 = Report.ReportTypes.INAPPROPRIATE_PRODUCTS.label;
        reportManager.makeReport(userToReport.id(), reportType2, "Broken upon arrival!");

        List<Report> reports = reportManager.getAll().get();
        assertEquals(reports.size(),2);
    }

    @Test
    public void testGettingAllReceivedReports(){
        //overwrite @before to get some other users
        User userToReport1 = accManager.getUserByUsername("tester3");
        User userToReport2 = accManager.getUserByUsername("tester4");

        reportManager = ReportingManager.of();

        String reportType1 = Report.ReportTypes.LIKELY_SCAM.label;
        reportManager.makeReport(userToReport1.id(), reportType1, "This user stole my money!");

        String reportType2 = Report.ReportTypes.INAPPROPRIATE_PRODUCTS.label;
        reportManager.makeReport(userToReport1.id(), reportType2, "Wait.. these arent school supplies!");

        String reportType3 = Report.ReportTypes.INAPPROPRIATE_PROFILE.label;
        reportManager.makeReport(userToReport2.id(), reportType3, "Bad picture.");

        List<Report> reports1 = reportManager.getAllByReceiverId(userToReport1.id()).get();
        assertEquals(reports1.size(),2);
        List<Report> reports2 = reportManager.getAllByReceiverId(userToReport2.id()).get();
        assertEquals(reports2.size(),1);
    }

    @Test
    public void testGettingAllReportedReports(){

        try {
            accManager.registerUser("uniquetester1", "uniquetester1@myumanitoba.ca", "password123");
            accManager.registerUser("uniquetester2", "uniqueteste2@myumanitoba.ca", "password123");
        } catch (UserRegistrationException e) {
            assertTrue(false);
        }

        User reporter = accManager.getUserByUsername("uniquetester1");
        User reportee = accManager.getUserByUsername("uniquetester2");

        String reportType1 = Report.ReportTypes.OTHER.label;
        String reportType3 = Report.ReportTypes.INAPPROPRIATE_PROFILE.label;
        String reportType2 = Report.ReportTypes.INAPPROPRIATE_PRODUCTS.label;

        try {
            accManager.loginUser("uniquetester1", "password123");
        } catch (UserLoginException e){
            // This cannot error
            assertTrue(false);
        }

        reportManager = ReportingManager.of();

        reportManager.makeReport(reportee.id(), reportType1, "Don't like this guy.");
        reportManager.makeReport(reportee.id(), reportType2, "Wait.. these arent school supplies!");
        reportManager.makeReport(reportee.id(), reportType3, "Bad picture.");

        try {
            accManager.loginUser("uniquetester2", "password123");
        } catch (UserLoginException e){
            // This is an existing test user and should never error
            assertTrue(false);
        }

        reportManager = ReportingManager.of();

        reportManager.makeReport(reporter.id(), reportType1, "Don't like this guy.");
        reportManager.makeReport(reporter.id(), reportType2, "Wait.. these arent school supplies!");

        List<Report> reports1 = reportManager.getAllByReporterId(reporter.id()).get();
        assertEquals(reports1.size(),3);

        List<Report> reports2 = reportManager.getAllByReporterId(reportee.id()).get();
        assertEquals(reports2.size(),2);
    }
}
