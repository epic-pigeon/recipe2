package com.kar.recipe.DBHandle;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.*;
import java.util.function.Function;

public class Collection<T> extends ArrayList<T> {
    public Collection() {
        super();
    }
    public Collection(int k) {
        super(k);
    }
    public Collection(T... args) {
        this(Arrays.asList(args));
    }
    public Collection(List<T> list) {
        super(list);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public<E> Collection<E> map(Function<T, E> fn) {
        Collection<E> collection = new Collection<>();
        for (T obj: this) collection.add(fn.apply(obj));
        return collection;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public Collection<T> find(Function<T, Boolean> fn) {
        Collection<T> collection = new Collection<>();

        for (T o : this) {
            if (fn.apply(o)) collection.add(o);
        }

        return collection;
    }
    public Collection<T> merge(Collection<T> collection) {
        Collection<T> result = this;
        result.addAll(collection);
        return result;
    }
    public String join(String glue) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < this.size(); i++) {
            if (i != 0) result.append(glue);
            result.append(this.get(i).toString());
        }
        return result.toString();
    }
    public String join() {
        return join(" ");
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public T findFirst(Function<T, Boolean> fn) {
        for (T element: this) if (fn.apply(element)) return element;
        return null;
    }
    public T last() {
        return (size() > 0 ? get(size() - 1) : null);
    }
    public T pop() {
        return remove(size() - 1);
    }
    public<E> Collection<E> to(Class<E> clazz) {
        Collection<E> collection = new Collection<>();
        for (T element: this) collection.add(clazz.cast(element));
        return collection;
    }
    public Collection<T> slice(int start, int end) {
        return new Collection<>(subList(start, end + 1));
    }
    public Collection<T> slice(int start) {
        return slice(start, size() - 1);
    }
}
