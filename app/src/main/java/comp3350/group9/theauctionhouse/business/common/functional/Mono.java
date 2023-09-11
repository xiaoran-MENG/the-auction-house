package comp3350.group9.theauctionhouse.business.common.functional;

public class Mono<T> {
    private final T value;

    private Mono(T value) {
        this.value = value;
    }

    public static <R> Mono<R> of(R value) {
        return new Mono<>(value);
    }

    public static <R> Mono<R> empty() {
        return of(null);
    }

    public Mono<T> then(Action<T> action) {
        if (value == null) return this;
        action.invoke(value);
        return of(value);
    }

    public void or(Supplier<RuntimeException> supplier) {
        if ((value instanceof Boolean && !(Boolean) value) || value == null) throw supplier.get();
    }

    public <U> Mono<U> map(Converter<T, U> converter) {
        if (value == null) return Mono.empty();
        return of(converter.apply(value));
    }

    public T get() {
        return value;
    }
}
