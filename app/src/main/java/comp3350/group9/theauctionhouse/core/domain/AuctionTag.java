package comp3350.group9.theauctionhouse.core.domain;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AuctionTag extends Entity {
    public static final List<String> validTags = new ArrayList<>(Arrays.asList(
            "Laptops",
            "Ear Phones",
            "Pencils",
            "Pens",
            "Paper",
            "Art Supplies",
            "Notebooks",
            "Study Material",
            "Old Exams/Midterms",
            "Textbooks"
    ));

    /** Reference ID since this will never be created/accessed without already having an Auction. */
    private final String auctionId;
    private final String tag;
    private AuctionTag(String id, String auctionId, String tag) {
        super(id);
        this.tag = tag;
        this.auctionId = auctionId;
    }

    public static Builder builder(){
        return new Builder();
    }

    public String tag() {
        return tag;
    }

    public String getAuctionId() {
        return auctionId;
    }

    public static class Builder {
        private String id;
        private String auctionId;
        private String tag;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder auctionId(String auctionId){
            this.auctionId = auctionId;
            return this;
        }

        public AuctionTag build() throws InvalidParameterException {
            if (validTags.contains(this.tag))
                return new AuctionTag(id, auctionId, tag);
            throw new InvalidParameterException("Not a valid tag");
        }
    }
}
