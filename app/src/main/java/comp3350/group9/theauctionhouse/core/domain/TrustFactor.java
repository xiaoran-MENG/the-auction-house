package comp3350.group9.theauctionhouse.core.domain;


import androidx.core.math.MathUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;

import comp3350.group9.theauctionhouse.core.application.QueryEngine;

/*
* This Entity just needed to be an float rating and int total_ratings
* Then add the next rating by
* rating = ((rating * total_ratings) + (newRating)) / (total_rating+1); total_rating++;
*/

// Tracks the trust factors of users
public class TrustFactor extends Entity {
    // Stores an individual Rating
    public class Rating {
        private static final int MAX_RATING = 5;
        private static final int MIN_RATING = 1;

        private float rating = 0;
        public Rating(float rating) {
            // Ensures their are no invalid values
            rating = MathUtils.clamp(rating, MIN_RATING, MAX_RATING);

            this.rating = rating;
        }

        public float getRating() { return rating; }
    }

    static QueryEngine query = null;

    // Sets the DB to use whatever
    public static void setDB(QueryEngine query) {
        TrustFactor.query = query;
    }
    private ArrayList<Rating> pastRatings;
    private String userID = null;

    public TrustFactor(String id, String userID){
        super(id);
        // Loads the past rating
        this.userID = userID;
        pastRatings = new ArrayList<>();
    }

    // Overwrites the existing trust factor
    public boolean overwrite(TrustFactor newTrust) {
        if (newTrust == null) { return false; }

        this.pastRatings = newTrust.getPastRatings();
        return true;
    }

    // Adds a rating to the Trust Factor
    // Ratings are added to the bottom of the list
    public void addRating(float rating) {
        pastRatings.add(new Rating(rating));
    }

    // Gets the total ratings
    private float getTotalRating() {
        float totalRating = 0;
        for (Rating rating : getPastRatings()) {
            totalRating += rating.getRating();
        }
        return totalRating;
    }

    // Gets the rating of a user
    public final float getAverageRating() {
        // Avoids a /0 error
        if (getReviewAmount() == 0) { return 0; }
        return getTotalRating() / getReviewAmount();
    }

    // Gets the rating of a user so it can be displayed
    public final String displayRating() {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        float rating = getAverageRating();

        if (rating == 0) {
            return "No Ratings";
        }

        return "Rating: " + df.format(rating);
    }

    // Gets the amount of ratings
    public final int getReviewAmount() {
        return getPastRatings().size();
    }

    // Gets a Users previous Ratings
    public final ArrayList<Rating> getPastRatings() {
        return pastRatings;
    }

    // Gets the last added rating
    public final Rating getMostRecentRating() {
        if (getReviewAmount() == 0) { return null; }

        ArrayList<Rating> ratings = getPastRatings();
        return ratings.get(ratings.size() - 1);
    }

    // Gets the users ID which this is attached to
    public final String getUserID() { return userID; }
}
