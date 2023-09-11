package comp3350.group9.theauctionhouse.unittests.business.account;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import comp3350.group9.theauctionhouse.business.accounts.TrustFactorManager;
import comp3350.group9.theauctionhouse.core.application.QueryEngine;
import comp3350.group9.theauctionhouse.core.application.TrustFactorQueriable;
import comp3350.group9.theauctionhouse.core.application.UserQueriable;
import comp3350.group9.theauctionhouse.core.domain.TrustFactor;
import comp3350.group9.theauctionhouse.core.domain.User;

@RunWith(MockitoJUnitRunner.Silent.class)

public class TrustFactorManagerTests {
    @Mock
    private QueryEngine queryEngine;
    @Mock
    private UserQueriable userQueriable;
    @Mock
    private TrustFactorQueriable trustFactorQueriable;
    private TrustFactor trustFactor;
    private TrustFactorManager trustFactorManager;
    private User user;

    @Before
    public void init() {
        when(queryEngine.trustFactors()).thenReturn(trustFactorQueriable);
        when(userQueriable.findById(anyString())).thenReturn(new User("1", "John Doe", "john@myumanitoba.ca", "12345678"));
        user = userQueriable.findById("1");
        trustFactor = new TrustFactor("1", "1");
        trustFactorManager = new TrustFactorManager(queryEngine);
    }

    @Test
    public void testShouldAddRating() {
        when(trustFactorQueriable.add(any(TrustFactor.class))).thenReturn(true);
        trustFactor.addRating(5);
        trustFactorManager.addRating(trustFactor);
        verify(trustFactorQueriable).add(trustFactor);
    }

    @Test
    public void testShouldFailToAddInvalidRating() {
        String uuid = UUID.randomUUID().toString();
        User u = new User(uuid,null,null,null);
        TrustFactor invalid = new TrustFactor("2", uuid);
        boolean result = trustFactorManager.addRating(invalid);
        assertFalse(trustFactorManager.addRating(invalid));
    }

    @Test
    public void testShouldGetTrustFactorByUserId() {
        trustFactor.addRating(3);
        when(trustFactorQueriable.findByUserId(anyString())).thenReturn(trustFactor);
        assert(trustFactor.getAverageRating() == trustFactorManager.getByUserId(user.id()).getAverageRating());
        verify(trustFactorQueriable).findByUserId(user.id());
    }

}
