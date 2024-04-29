package com.net.file.wrapper;

import java.util.function.Function;


public class LambdaFunctionWrapper {
    public static <T,R> Function<T,R> wrap(CheckedFunction<T,R> function){
        return t ->{
            try {
                return function.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
    @FunctionalInterface
    public interface CheckedFunction<T,R> {

        R apply(T t) throws Exception;

    }
}
