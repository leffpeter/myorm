package com.leff.myorm.util.list;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Sorted list implementation using LinkedList<T>.
 *
 * @param <T>
 * @author leff
 */
public final class SortedList<T extends Comparable<T>> extends LinkedList<T> {

    private static final long serialVersionUID = 1L;

    /**
     * Adds this element to the list at the proper sorting position. If the
     * element already exists, don't do anything.
     *
     * @param e
     * @return
     */
    @Override
    public boolean add(T e) {
        if (size() == 0) {
            return super.add(e);
        } else {
            // find insertion index
            int idx = -Collections.binarySearch(this, e) - 1;

            if (idx < 0) {
                return true; // already added
            }
            // add at this position
            super.add(idx, e);
            return true;
        }
    }

    /**
     * Add all elements.
     *
     * @param collection
     * @return
     */
    @Override
    public boolean addAll(Collection<? extends T> collection) {
        if (collection == null) {
            return false;
        }
        for (T t : collection) {
            this.add(t);
        }
        return true;
    }
}
