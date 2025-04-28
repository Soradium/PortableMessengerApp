package org.soradium.portablemessengerapp.tools;

import java.util.List;

public interface BufferList<T> {
    T addToList(String identifier, T t);

    List<T> getList();

    T getLastAdded();

    T getByRelativeIndex(int i);

    T getByString(String s);

    T setByRelativeIndex(int i, T set);

    T setByString(String str, T object);
}