package comp3350.group9.theauctionhouse.core.application;

import java.util.List;

import comp3350.group9.theauctionhouse.core.domain.Report;

public interface ReportQueriable extends Queriable<Report> {

    String addReport(Report report);

    List<Report> findAllByReceiverId(String id);

    List<Report> findAllByReporterId(String id);

}
