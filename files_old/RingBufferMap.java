//package org.soradium.portablemessengerapp.tools;
//
//import java.util.*;
//
//public class RingBufferSet<T> implements BufferList<T>{
//    private final int maxSize;
//    private final Set <T> set;
//    private int position;
//    private T lastAdded;
//
//    public static class RingBufferMapBuilder<T> {
//        private int maxSize;
//        private Set<T> set;
//        private int position;
//        public RingBufferMapBuilder() {
//            this.maxSize = 50;
//            this.position = 0;
//        }
//        public RingBufferMapBuilder<T> withMaxSize(int maxSize) {
//            this.maxSize = maxSize;
//            return this;
//        }
//        public RingBufferMapBuilder<T> withPreparedMap(Set<T> map) {
//            this.set = set;
//            return this;
//        }
//        public RingBufferMapBuilder<T> withPreparedPosition(int position) {
//            this.position = position;
//            return this;
//        }
//        public RingBufferSet<T> build() {
//            if(this.set == null) {
//                this.set = new TreeSet<>();
//            }
//            return new RingBufferSet<>(this);
//        }
//    }
//
//    private RingBufferSet(RingBufferMapBuilder<T> builder) {
//        this.list = builder.list;
//        this.maxSize = builder.maxSize;
//        this.position = builder.position;
//
//    }
//
//    private int getRelativePosition() {
//        return this.position % this.maxSize;
//    }
//
//    /// This method returns overwritten values.
//    /// If no values are overwritten, null is returned
//    ///
//    ///  `[49],[50], [51*]` - override and return `[51*] = [1]`
//    ///
//    ///  `[3],[4], [5*]` - nothing to override, return nothing
//    public T addToList(T t) {
//        T returnT = null;
//        position++;
//        int relativePos = getRelativePosition();
//        //   Map<Map<String, Integer>, T> list;
//        if(position > 50) {
//            List<Set<Map<String, Integer>>> keys
//                    = (List<Set<Map<String, Integer>>>) list.keySet();
//
//            returnT = list.get();
//            list.set(Collections.singletonMap(), t);
//        } else {
//            list.set(relativePos, t);
//        }
//        return returnT;
//
//        // [49],[50], [51*] - override and return [1]
//        // [3],[4], [5] - nothing to override, return nothing
//    }
//
//    public T getLastAdded() {
//        return list.get(getRelativePosition());
//    }
//
//    public List<T> getList() {
//        return list;
//    }
//
//}
