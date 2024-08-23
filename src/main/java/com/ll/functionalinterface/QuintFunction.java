package com.ll.functionalinterface;

import java.util.Objects;
import java.util.function.Function;

/*
 * Function taking 5 arguments.
 */
@FunctionalInterface
public interface QuintFunction<T, U, V, X, Y, R> {
    default <W> QuintFunction<T, U, V, X, Y, W> andThen(Function<? super R, ? extends W> after) {
        Objects.requireNonNull(after);
        return (t, u, v, x, y) -> {
            return after.apply(this.apply(t, u, v, x, y));
        };
    }

    R apply(T var1, U var2, V var3, X var4, Y var5);
}