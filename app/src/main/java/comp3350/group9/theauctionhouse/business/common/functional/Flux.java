package comp3350.group9.theauctionhouse.business.common.functional;

import androidx.core.util.Consumer;

import java.util.*;

public class Flux<T> {
    private final List<T> values;

    private Flux(List<T> values) {
        this.values = values;
    }

    public static <T> Flux<T> of(List<T> values) {
        return new Flux<>(values);
    }

    public Flux<T> by(Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T v : values)
            if (predicate.test(v)) result.add(v);
        return of(result);
    }

    public Mono<T> one() {
        return values.isEmpty() ? Mono.empty() : Mono.of(values.get(0));
    }

    public List<T> get() {
        return Collections.unmodifiableList(values);
    }

    public Flux<T> then(Consumer<T> f) {
        for (T v : values) f.accept(v);
        return this;
    }

    public <U> Flux<U> map(Converter<T, U> f) {
        List<U> result = new ArrayList<>();
        for (T v : values)
            result.add(f.apply(v));
        return of(result);
    }
}
