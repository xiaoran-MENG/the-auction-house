package comp3350.group9.theauctionhouse.business.auth;

import comp3350.group9.theauctionhouse.exception.auth.UserLoginException;
import comp3350.group9.theauctionhouse.exception.auth.UserRegistrationException;
import comp3350.group9.theauctionhouse.business.common.Field;
import comp3350.group9.theauctionhouse.persistence.HSQLDBFactory;
import comp3350.group9.theauctionhouse.core.application.QueryEngine;
import comp3350.group9.theauctionhouse.core.domain.User;

public class UserAccountManager {
    private final QueryEngine queryEngine;
    private static volatile UserAccountManager o;
    private String userId;

    public UserAccountManager(QueryEngine queryEngine) {
        this.queryEngine = queryEngine;
    }

    public static synchronized UserAccountManager of(QueryEngine queryEngine, String loggedInUser) {
        if (o == null) {
            synchronized (UserAccountManager.class) {
                if (o == null) o = new UserAccountManager(queryEngine);
            }
        }
        o.userId = loggedInUser;
        return o;
    }

    public static synchronized UserAccountManager of() {
        if (o == null) {
            synchronized (UserAccountManager.class) {
                if (o == null) o = new UserAccountManager(HSQLDBFactory.get());
            }
        }
        return o;
    }

    public void registerUser(String username, String email, String password) throws UserRegistrationException {
        if (isUserFound(username)) {
            throw new UserRegistrationException("Username already in use", Field.USERNAME);
        }

        if (isEmailFound(email)) {
            throw new UserRegistrationException("Email already in use", Field.EMAIL);
        }

        User account = User.tryCreateAccount(username, email, password);
        boolean result = queryEngine.users().add(account);

        if (!result) throw new UserRegistrationException("Failed to register user", Field.UNKNOWN);
    }

    public User loginUser(String username, String password) throws UserLoginException {
        User user = getUserByUsername(username);

        if (user == null) throw new UserLoginException("No such user exists.", Field.USERNAME);

        if (user.tryLogin(password)) {
            userId = user.id();
            return user;
        } else {
            throw new UserLoginException("Incorrect password.", Field.PASSWORD);
        }
    }

    public User getLoggedInUser() {
        return getUserById(userId);
    }

    public User getUserById(String id) {
        return queryEngine.users().findById(id);
    }

    public User getUserByUsername(String name) {
        return queryEngine.users().findByUsername(name);
    }


    public User getUserByEmail(String email) {
        return queryEngine.users().findByEmail(email);
    }

    private boolean isUserFound(String name) {
        User user = queryEngine.users().findByUsername(name);
        return user != null;
    }

    private boolean isEmailFound(String email) {
        User user = queryEngine.users().findByEmail(email);
        return user != null;
    }
}

