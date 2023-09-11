package comp3350.group9.theauctionhouse.business.common.functional;

public interface Comparator<T> {

    int on(T x, T y);

    static <T, U> Comparator<T> of(Converter<? super T, ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
        return (x, y) -> keyComparator.on(
            keyExtractor.apply(x),
            keyExtractor.apply(y));
    }

    default Comparator<T> then(Comparator<? super T> other) {
        return (x, y) -> {
            int n = on(x, y);
            return n == 0 ? other.on(x, y) : n;
        };
    }

}
