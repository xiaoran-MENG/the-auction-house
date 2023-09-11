package comp3350.group9.theauctionhouse.core.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Report extends Entity {
    User reporter;
    User receiver;
    String reportType;
    String reportDescription;

    public Report(String id, User reporter, User receiver, String reportType, String reportDescription){
        super(id);
        this.reporter = reporter;
        this.receiver = receiver;
        this.reportType = reportType;
        this.reportDescription = reportDescription;
    }

    public String id() {
        return id;
    }

    public User reporter() {
        return reporter;
    }

    public User receiver() {
        return receiver;
    }

    public String reportType() {
        return reportType;
    }

    public String reportDescription() {
        return reportDescription;
    }

    /**
     * Valid forms for the report type field
    * */
    public enum ReportTypes {
        INAPPROPRIATE_PRODUCTS("Inappropriate Products"),
        INAPPROPRIATE_PROFILE("Inappropriate Profile"),
        LIKELY_SCAM("Likely Scam"),
        OTHER("Other");

        public final String label;
        private static final Map<String, ReportTypes> lookup = new HashMap<String, ReportTypes>();

        static {
            for (ReportTypes r : ReportTypes.values()) {
                lookup.put(r.label, r);
            }
        }

        ReportTypes(String s) {
            label = s;
        }

        public static List<String> getAllLabels() {
            ArrayList<String> labels = new ArrayList<>();
            for (ReportTypes r : ReportTypes.values()) {
                labels.add(r.label);
            }
            return labels;
        }

        public static ReportTypes getByLabel(String lab) {
            return lookup.get(lab);
        }
    }
}