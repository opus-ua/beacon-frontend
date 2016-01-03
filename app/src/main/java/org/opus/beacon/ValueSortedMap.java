package org.opus.beacon;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class ValueSortedMap<K, V extends Comparable> extends TreeMap<K, V> {
    public ValueSortedMap(Map<K, V> map) {
        super(new ValueComparator(map));
        this.putAll(map);
    }

    private static class ValueComparator<K, V extends Comparable> implements Comparator<K> {
        Map<K, V> mMap;

        public ValueComparator(Map map) {
            mMap = map;
        }

        public int compare(K a, K b) {
            return mMap.get(a).compareTo(mMap.get(b));
        }
    }
}
