package ntf.function;

import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Function;

public class Functions {

    public static <A, B> Function<A, Pair<A, B>> compute2By1(Function<A, B> computer) {
        return a -> Pair.of(a, computer.apply(a));
    }

    public static <T, A, B> Function<Pair<A, B>, T> p(Function2<A, B, T> f2) {
        return pair -> f2.apply(pair.getLeft(), pair.getRight());
    }

    public static <A, B> Function<Pair<A, B>, A> reduce2By1(Consumer2<A, ? super B> consumer) {
        return pair -> {
            consumer.accept(pair.getLeft(), pair.getRight());
            return pair.getLeft();
        };
    }

//    public static <A, B> Consumer<Pair<A, B>> p(Consumer2<A, B> f2) {
//        return pair -> f2.apply(pair.getLeft(), pair.getRight());
//    }

    @FunctionalInterface
    public interface Function2<SA, SB, T> {
        T apply(SA a, SB b);
    }

    @FunctionalInterface
    public interface Consumer2<SA, SB> {
        void accept(SA a, SB b);
    }

}
