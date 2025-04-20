package org.soradium.portablemessengerapp.tools;

import java.util.ArrayList;
import java.util.List;

public class RingBufferListWithLimitedSize<T> {
    private final int maxSize;
    private List<T> list;
    private int position = 0;

    public RingBufferListWithLimitedSize(int maxSize) {
        this.list = new ArrayList<>(maxSize + 1); //+1, just to be sure that size stays fixed
        this.maxSize = maxSize;
    }

    private int getRelativePosition() {
        return this.position % this.maxSize;
    }

    public T addToList(T t) {
        list.set(getRelativePosition(), t);
        position++;
        return t;
    }

    public T getLastAdded() {
        return list.get(getRelativePosition());
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
