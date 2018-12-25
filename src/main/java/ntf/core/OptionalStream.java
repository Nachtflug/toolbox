
package ntf.core;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Optional + Stream 针对返回结果是列表的情况
 *
 */
public class OptionalStream<T> {

    private static final OptionalStream<?> EMPTY = new OptionalStream<>();


    private final Stream<T> value;


    private OptionalStream() {
        this.value = null;
    }


    public static<T> OptionalStream<T> empty() {
        @SuppressWarnings("unchecked")
        OptionalStream<T> t = (OptionalStream<T>) EMPTY;
        return t;
    }


    private OptionalStream(Stream<T> value) {
        this.value = Objects.requireNonNull(value);
    }
    private <U> OptionalStream(U value, Function<U, Stream<T>> streamAdapter) {
        this.value = streamAdapter.apply(Objects.requireNonNull(value));
    }


    public static <T> OptionalStream<T> of(Collection<T> value) {
        return new OptionalStream<>(value, Collection::stream);
    }

    public static <T> OptionalStream<T> of(Stream<T> value) {
        return new OptionalStream<>(value);
    }

    public static <U, T> OptionalStream<T> of(U value, Function<U, Stream<T>> streamAdapter) {
        return new OptionalStream<>(value, streamAdapter);
    }


    public static <T> OptionalStream<T> ofNullable(Collection<T> value) {
        return value == null ? empty() : of(value);
    }

    public static <T> OptionalStream<T> ofNullable(Stream<T> value) {
        return value == null ? empty() : of(value);
    }

    public static <U, T> OptionalStream<T> ofNullable(U value, Function<U, Stream<T>> streamAdapter) {
        return value == null ? empty() : of(value, streamAdapter);
    }


    public Stream<T> get() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    public <C extends Collection<T>> C collection(Supplier<C> suppier) {
        return get().collect(Collectors.toCollection(suppier));
    }


    public Optional<List<T>> optionalList(Supplier<List<T>> suppier) {
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(get().collect(Collectors.toCollection(suppier)));
    }



    public boolean isPresent() {
        return value != null;
    }


    public void ifPresent(Consumer<? super T> consumer) {
        if (value != null)
            value.forEach(consumer);
    }


    public OptionalStream<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (!isPresent())
            return this;
        else {
            return of(value.filter(predicate));
        }
    }


    public<W> OptionalStream<W> map(Function<? super T, ? extends W> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent())
            return empty();
        else {
            return ofNullable(value.map(mapper));
        }
    }

    public OptionalStream<T> peek(Consumer<? super T> consumer) {
        Objects.requireNonNull(consumer);
        if (!isPresent())
            return empty();
        else {
            return of(value.peek(consumer));
        }
    }


    public<U> OptionalStream<U> flatMap(Function<? super T, Stream<U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent())
            return empty();
        else {
            return Objects.requireNonNull(of(value.flatMap(mapper)));
        }
    }



    public Stream<T> orElse(Stream<T> other) {
        return value != null ? value : other;
    }

    public <C extends Collection<T>> C orOtherCollection(C other, Supplier<? extends C> suppier) {
        return value == null ? other : value.collect(Collectors.toCollection(suppier));
    }

    public Optional<T> any() {
        return value == null ? Optional.empty() : value.findAny();
    }


    public Stream<T> orElseGet(Supplier<? extends Stream<T>> other) {
        return value != null ? value : other.get();
    }


    public <X extends Throwable> Stream<T> orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (value != null) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof OptionalStream)) {
            return false;
        }

        OptionalStream<?> other = (OptionalStream<?>) obj;
        return Objects.equals(value, other.value);
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }


    @Override
    public String toString() {
        return value != null
                ? String.format("OptionalStream[%s]", value)
                : "OptionalStream.empty";
    }

}
