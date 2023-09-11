package comp3350.group9.theauctionhouse.unittests.business.account;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import comp3350.group9.theauctionhouse.core.application.UserQueriable;
import comp3350.group9.theauctionhouse.core.domain.TrustFactor;
import comp3350.group9.theauctionhouse.core.domain.User;

@RunWith(MockitoJUnitRunner.Silent.class)

public class TrustFactorTests {

    @Mock
    private UserQueriable userQueriable;

    private TrustFactor trustFactor;

    @Before
    public void init() {
        when(userQueriable.findById(anyString())).thenReturn(new User("1", "John Doe", "john@myumanitoba.ca", "12345678"));
        User user = userQueriable.findById("1");
        trustFactor = new TrustFactor("1", "1");
    }

    @Test
    public void testShouldConstructTrustFactor() {
        assertEquals(0, trustFactor.getAverageRating(), 0.0);
        assertEquals(0, trustFactor.getReviewAmount());
    }

    @Test
    public void testRatingShouldStayInBounds() {
        trustFactor.addRating(0);
        trustFactor.addRating(6);
        ArrayList<TrustFactor.Rating> ratings = trustFactor.getPastRatings();
        assertEquals(1, ratings.get(0).getRating(), 0.0);
        assertEquals(5, ratings.get(1).getRating(), 0.0);
    }

    @Test
    public void testShouldAddRatings() {
        trustFactor.addRating(2);
        trustFactor.addRating(5);
        ArrayList<TrustFactor.Rating> ratings = trustFactor.getPastRatings();
        assertEquals(2, ratings.get(0).getRating(), 0.0);
        assertEquals(5, ratings.get(1).getRating(), 0.0);
    }

    @Test
    public void testShouldGetAverageRating() {
        trustFactor.addRating(5);
        assertEquals(5, trustFactor.getAverageRating(), 0.0);
        assertEquals(1, trustFactor.getReviewAmount());

        trustFactor.addRating(5);
        assertEquals(5, trustFactor.getAverageRating(), 0.0);
        assertEquals(2, trustFactor.getReviewAmount());
    }

    @Test
    public void testShouldReturnOneForAverageRatingWhenTotalRatingIsNegative() {
        trustFactor.addRating(-100);
        assertEquals(1, trustFactor.getAverageRating(), 0.0);
        assertEquals(1, trustFactor.getReviewAmount());

        trustFactor.addRating(-100);
        assertEquals(1, trustFactor.getAverageRating(), 0.0);
        assertEquals(2, trustFactor.getReviewAmount());
    }
}
