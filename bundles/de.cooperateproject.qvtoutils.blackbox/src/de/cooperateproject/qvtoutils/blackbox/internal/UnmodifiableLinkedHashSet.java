package de.cooperateproject.qvtoutils.blackbox.internal;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Spliterator;

/**
 * Wrapper for {@link LinkedHashSet} that disables all operations that modify the wrapped
 * collection.
 * 
 * @param <E>
 *            The type of the elements contained in the wrapped collection.
 */
public class UnmodifiableLinkedHashSet<E> extends LinkedHashSet<E> {

    private static final long serialVersionUID = 5854915573232923642L;

    private final LinkedHashSet<E> delegationTarget;

    @SuppressWarnings("squid:S1319")
    public UnmodifiableLinkedHashSet(LinkedHashSet<E> delegationTarget) {
        this.delegationTarget = delegationTarget;
    }

    @SuppressWarnings("squid:S1319")
    public static <E> UnmodifiableLinkedHashSet<E> create(LinkedHashSet<E> delegationTarget) {
        return new UnmodifiableLinkedHashSet<>(delegationTarget);
    }

    // blocked methods

    @Override
    public boolean removeAll(Collection<?> c) {
        return throwImmutableException();
    }

    @Override
    public boolean add(E e) {
        return throwImmutableException();
    }

    @Override
    public boolean remove(Object o) {
        return throwImmutableException();
    }

    @Override
    public void clear() {
        throwImmutableException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return throwImmutableException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return throwImmutableException();
    }

    private static boolean throwImmutableException() {
        throw new UnsupportedOperationException("This set is immutable.");
    }

    // delegated methods

    @Override
    public boolean equals(Object o) {
        return delegationTarget.equals(o);
    }

    @Override
    public int hashCode() {
        return delegationTarget.hashCode();
    }

    @Override
    public Object[] toArray() {
        return delegationTarget.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return delegationTarget.toArray(a);
    }

    @Override
    public Iterator<E> iterator() {
        return delegationTarget.iterator();
    }

    @Override
    public int size() {
        return delegationTarget.size();
    }

    @Override
    public boolean isEmpty() {
        return delegationTarget.isEmpty();
    }

    @Override
    public Spliterator<E> spliterator() {
        return delegationTarget.spliterator();
    }

    @Override
    public boolean contains(Object o) {
        return delegationTarget.contains(o);
    }

    @Override
    @SuppressWarnings({ "squid:S2975", "squid:S1182" })
    public Object clone() {
        return delegationTarget.clone();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return delegationTarget.containsAll(c);
    }

    @Override
    public String toString() {
        return delegationTarget.toString();
    }

}
