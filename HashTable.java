package heap;
//Author: Rose Wei
//Date: 5/23/2022
//Purpose: implement a hashtable which stored key and value (phase 2)

import com.google.common.collect.Table;

/** A hash table modeled after java.util.Map. It uses chaining for collision
 * resolution and grows its underlying storage by a factor of 2 when the load
 * factor exceeds 0.8. */
public class HashTable<K,V> {

    protected Pair[] buckets; // array of list nodes that store K,V pairs
    protected int size; // how many items currently in the map


    /** class Pair stores a key-value pair and a next pointer for chaining
     * multiple values together in the same bucket, linked-list style*/
    public class Pair {
        protected K key;
        protected V value;
        protected Pair next;

        /** constructor: sets key and value */
        public Pair(K k, V v) {
            key = k;
            value = v;
            next = null;
        }

        /** constructor: sets key, value, and next */
        public Pair(K k, V v, Pair nxt) {
            key = k;
            value = v;
            next = nxt;
        }

        /** returns (k, v) String representation of the pair */
        public String toString() {
            return "(" + key + ", " + value + ")";
        }
    }

    /** constructor: initialize with default capacity 17 */
    public HashTable() {
        this(17);
    }

    /** constructor: initialize the given capacity */
    public HashTable(int capacity) {
        buckets = createBucketArray(capacity);
    }

    /** Return the size of the map (the number of key-value mappings in the
     * table) */
    public int getSize() {
        return size;
    }

    /** Return the current capacity of the table (the size of the buckets
     * array) */
    public int getCapacity() {
        return buckets.length;
    }

    /** Return the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     * Runtime: average case O(1); worst case O(size) */
    public V get(K key) {
        int keyHash = Math.abs(key.hashCode())%buckets.length;
        Pair list = buckets[keyHash];
        while(list != null){
            if(list.key.equals(key)){
                return list.value;
            }
            list = list.next;
        }
        return null;
        // TODO 2.1 - do this together with put.
        //throw new UnsupportedOperationException();
    }

    /** Associate the specified value with the specified key in this map. If
     * the map previously contained a mapping for the key, the old value is
     * replaced. Return the previous value associated with key, or null if
     * there was no mapping for key. If the load factor exceeds 0.8 after this
     * insertion, grow the array by a factor of two and rehash.
     * Precondition: val is not null.
     * Runtime: average case O(1); worst case O(size + a.length)*/
    public V put(K key, V val) {
        // TODO 2.2
        //   do this together with get. For now, don't worry about growing the
        //   array and rehashing.
        //   Tips:
        //     - Use the key's hashCode method to find which bucket it belongs in.
        //     - It's possible for hashCode to return a negative integer.
        //
        int keyHash = Math.abs(key.hashCode())%buckets.length;
        Pair list = buckets[keyHash];
        while(list != null){
            if(list.key.equals(key)){
                V preV = list.value;
                list.value = val;
                return preV;
            }
            list = list.next;
        }
        if(list == null){
            Pair newPair = new Pair(key, val);
            list = newPair;
            newPair.next = buckets[keyHash];
            buckets[keyHash] = newPair;
            size++;
            growIfNeeded();
        }
        
        return null;
        // TODO 2.5 - modify this method to grow and rehash if the load factor
        //            exceeds 0.8.
        //throw new UnsupportedOperationException();
    }

    /** Return true if this map contains a mapping for the specified key.
     *  Runtime: average case O(1); worst case O(size) */
    public boolean containsKey(K key) {
        // TODO 2.3
        int keyHash = Math.abs(key.hashCode()) % buckets.length;
        Pair list = buckets[keyHash];
        while(list != null){
            if(list.key.equals(key)){
                return true;
            }
            list = list.next;
        }
        return false;
        //throw new UnsupportedOperationException();
    }

    /** Remove the mapping for the specified key from this map if present.
     *  Return the previous value associated with key, or null if there was no
     *  mapping for key.
     *  Runtime: average case O(1); worst case O(size)*/
    public V remove(K key) {
        // TODO 2.4
        int keyHash = Math.abs(key.hashCode()) % buckets.length;
        Pair list = buckets[keyHash];
        if(buckets[keyHash] == null){
            return null;
        }
        if(list.key.equals(key)){
            V preV = list.value;
            buckets[keyHash] = buckets[keyHash].next;
            size--;
            return preV;
        }
        Pair previous = buckets[keyHash];
        Pair preNext = previous.next;
        while(preNext != null && !(preNext.key.equals(key))){
            preNext = preNext.next;
            previous = preNext;
        }
        if(preNext != null){
            V preV = preNext.value;
            previous.next = preNext.next;
            size--;
            return preV;
        }

        return null;
        //throw new UnsupportedOperationException();
    }


    // suggested helper method:
    /* check the load factor; if it exceeds 0.8, double the array size
     * (capacity) and rehash values from the old array to the new array */
    private void growIfNeeded() {
        //copy old array to new array which is copy to new array [0..buckets' size/2] and keep remaining empty
        double loadFactor = (double)size/(double)buckets.length;
        if(loadFactor > 0.8){
            Pair[] newArr = createBucketArray((buckets.length)*2);
            for(int i = 0; i < buckets.length; i++){
                Pair newList = buckets[i];
                while(newList != null){
                    Pair next = newList.next;
                    int hashKey = Math.abs(newList.key.hashCode()) % newArr.length;
                    newList.next = newArr[hashKey];
                    newArr[hashKey] = newList;
                    newList = next;
                }
            }
            
            buckets = newArr;
        }
      //throw new UnsupportedOperationException();
    }

    /* useful method for debugging - prints a representation of the current
     * state of the hash table by traversing each bucket and printing the
     * key-value pairs in linked-list representation */
    protected void dump() {
        System.out.println("Table size: " + getSize() + " capacity: " +
                getCapacity());
        for (int i = 0; i < buckets.length; i++) {
            System.out.print(i + ": --");
            Pair node = buckets[i];
            while (node != null) {
                System.out.print(">" + node + "--");
                node = node.next;

            }
            System.out.println("|");
        }
    }

    /*  Create and return a bucket array with the specified size, initializing
     *  each element of the bucket array to be an empty LinkedList of Pairs.
     *  The casting and warning suppression is necessary because generics and
     *  arrays don't play well together.*/
    @SuppressWarnings("unchecked")
    protected Pair[] createBucketArray(int size) {
        return (Pair[]) new HashTable<?,?>.Pair[size];
    }
}