package io.github.alexeygrishin.pal;

import java.util.*;

public class Test {
    public static <A1, R> Collection<R> map(Collection<A1> list, Func<A1, R> action) {
        List<R> result = new ArrayList<R>(list.size());
        for (A1 s: list) {
            result.add(action.act(s));
        }
        return result;
    }

    public static <A1> String join(Collection<A1> list, String separator) {
        StringBuilder bld = new StringBuilder();
        boolean first = true;
        for (A1 s: list) {
            if (!first) bld.append(separator);
            bld.append(s.toString());
            first = false;
        }
        return bld.toString();
    }

    public static interface Func<A1, R> {
        R act(A1 arg1);
    }
}
