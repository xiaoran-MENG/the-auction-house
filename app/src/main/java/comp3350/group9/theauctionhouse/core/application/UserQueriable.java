package comp3350.group9.theauctionhouse.core.application;

import comp3350.group9.theauctionhouse.core.domain.User;

public interface UserQueriable extends Queriable<User> {

    User findByUsername(String name);

    User findByEmail(String email);

    boolean deleteByUsername(String username);

}
