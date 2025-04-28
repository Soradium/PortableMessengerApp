package org.soradium.portablemessengerapp.tools;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class RingBufferList<T> implements BufferList<T> {
    private final int maxSize;
    private final List<T> list;
    private final Set<T> set;
    private final Map<String, Integer> valueMap;
    private int position;

    private RingBufferList(RingBufferListBuilder<T> builder) {
        this.list = builder.list;
        this.maxSize = builder.maxSize;
        this.position = builder.position;
        this.set = new LinkedHashSet<>();
        if (!builder.list.isEmpty()) {
            int[] mapIntPosition = {0};
            this.valueMap = new LinkedHashMap<>(list.stream().collect(Collectors.toMap(
                    e -> builder.identifiers.get(mapIntPosition[0]),
                    e -> mapIntPosition[0]++)
            ));
            this.set.addAll(builder.list);
        } else {
            this.valueMap = new LinkedHashMap<>((int) (maxSize + maxSize * 0.4));
        }
    }

    /// `T addToList` returns overwritten values.
    /// If no values are overwritten, null is returned
    ///
    ///  `[49],[50],[51*]` - override and return `[51*] = [1]`
    ///
    ///  `[3],[4],[5*]` - nothing to override, return nothing
    ///
    ///  `[3]`, put at `[3*]` again - nothing to override, return nothing
    public T addToList(String identifier, T t) {
        int relativePos = getRelativePosition();
        T returnT = null;
        if (!valueMap.isEmpty() && valueMap.containsKey(identifier)) {
            T replacedT = list.set(valueMap.get(identifier), t);
            if (replacedT != null) {
                set.remove(replacedT);
            }
            set.add(t);
            return null;
        }
        if (position >= maxSize) {
            String[] keys = valueMap.keySet().toArray(new String[0]);
            valueMap.remove(keys[relativePos]);
        }
        if (!set.contains(t) && list.size() < maxSize) {
            list.add(t);
            set.add(t);
        } else {
            returnT = list.set(relativePos, t);
            if (returnT != null) {
                set.remove(returnT);
            }
            set.add(t);
        }
        valueMap.put(identifier, relativePos);
        position++;
        return returnT; // can be either null or overridden
    }

    @Override
    public T getByString(String s) {
        T value;
        try {
            if (valueMap.isEmpty() || valueMap.get(s) == null) {
                return null;
            }
            value = list.get(valueMap.get(s));
        } catch (Exception e) {
            throw e;
        }
        return value;
    }

    @Override
    public T getByRelativeIndex(int i) {
        T value;
        try {
            value = list.get(i);
        } catch (Exception e) {
            throw e;
        }
        return value;
    }

    @Override
    public T setByString(String str, T object) {
        T value;
        try {
            Integer index;
            index = valueMap.get(str);
            if (index == null) {
                log.debug("Index is null," +
                                " adding object: {}," +
                                " with desired String id index: {}," +
                                " at {}."
                        , object.toString(), str, this.position);
                value = addToList(str, object);
            } else {
                value = list.set(index, object);
            }
        } catch (Exception e) {
            throw e;
        }
        return value;
    }

    @Override
    public T setByRelativeIndex(int i, T object) {
        T value;
        try {
            if (i > this.list.size() || i < 0) {
                throw new IndexOutOfBoundsException();
            }
            value = list.set(i, object);
        } catch (Exception e) {
            throw e;
        }
        return value;
    }

    private int getRelativePosition() {
        return this.position % this.maxSize;
    }

    public T getLastAdded() {
        return list.get(getRelativePosition());
    }

    public List<T> getList() {
        return list;
    }

    public static class RingBufferListBuilder<T> {
        private int maxSize;
        private List<T> list;
        private List<String> identifiers;
        private int position;

        public RingBufferListBuilder() {
            this.maxSize = 50;
            this.position = 0;
        }

        public RingBufferListBuilder<T> withMaxSize(int maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public RingBufferListBuilder<T> withPreparedList(List<T> list, List<String> identifiers) {
            this.list = list;
            return this;
        }

        public RingBufferListBuilder<T> withPreparedPosition(int position) {
            this.position = position;
            return this;
        }

        public RingBufferList<T> build() {
            if (this.list == null) {
                this.list = new ArrayList<>(maxSize + 1);
            }
            return new RingBufferList<>(this);
        }
    }

}
