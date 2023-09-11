package comp3350.group9.theauctionhouse.exception.Reporting;

public class FailedToReportException extends RuntimeException{
    public FailedToReportException(String cause){
        super(String.format("Failed to report: %s", cause));
    }
}
