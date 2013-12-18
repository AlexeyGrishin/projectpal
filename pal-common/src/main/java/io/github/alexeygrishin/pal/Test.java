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

    public static <T> T listGet(Collection<T> input, int nr) {
        List<T> list = new ArrayList<T>(input);
        return list.size() - 1 > nr ? null : list.get(nr);
    }

    public static <T> T listLast(Collection<T> input) {
        return listGet(input, input.size() - 1);
    }

    public static <T> T listFirst(Collection<T> input) {
        return listGet(input, 0);
    }

    public static <T> Collection<T> listRest(Collection<T> input) {
        List<T> list = new ArrayList<T>(input);
        list.remove(0);
        return list;
    }

}
