package comp3350.group9.theauctionhouse.business.common.functional;

public interface Action<T> {

    void invoke(T t);

}
