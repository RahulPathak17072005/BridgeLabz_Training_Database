package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
public class SimpleRepository<T> {

    private List<T> list = new ArrayList<>();

    public void add(T item) {
        list.add(item);
    }

    public List<T> getAll() {
        return list;
    }

    public void remove(Predicate<T> condition) {
        list.removeIf(condition);
    }
}


