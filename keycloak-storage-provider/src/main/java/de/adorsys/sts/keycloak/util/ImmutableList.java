package de.adorsys.sts.keycloak.util;

import java.util.*;

public class ImmutableList<T> implements List<T> {
    private final List<T> list = new ArrayList<>();

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(T t) {
        throw new IllegalStateException("List is immutable");
    }

    @Override
    public boolean remove(Object o) {
        throw new IllegalStateException("List is immutable");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new IllegalStateException("List is immutable");
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new IllegalStateException("List is immutable");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new IllegalStateException("List is immutable");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new IllegalStateException("List is immutable");
    }

    @Override
    public void clear() {
        throw new IllegalStateException("List is immutable");
    }

    @Override
    public T get(int index) {
        return list.get(index);
    }

    @Override
    public T set(int index, T element) {
        throw new IllegalStateException("List is immutable");
    }

    @Override
    public void add(int index, T element) {
        throw new IllegalStateException("List is immutable");
    }

    @Override
    public T remove(int index) {
        throw new IllegalStateException("List is immutable");
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }
}
