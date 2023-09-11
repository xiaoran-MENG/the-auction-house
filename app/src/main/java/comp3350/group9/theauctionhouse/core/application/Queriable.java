package comp3350.group9.theauctionhouse.core.application;

import java.util.List;
import comp3350.group9.theauctionhouse.core.domain.Entity;

public interface Queriable<T extends Entity> {

    List<T> findAll();

    T findById(String id);

    boolean add(T t);

    boolean update(String id, T t);

    boolean delete(T t);

}
