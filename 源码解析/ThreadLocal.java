package middle;

import java.lang.ref.WeakReference;
import java.security.KeyStore.PrivateKeyEntry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ThreadLocal<T>{
	
	
	private final int threadLocalHashCode = nextHashCode();
	
	private static AtomicInteger nextHashCode = new AtomicInteger();
	
	private static final int HASH_INCREMENT = 0x61c88647;
	//return the next hash code
	public static int nextHashCode() {
		return nextHashCode.getAndAdd(HASH_INCREMENT);
	}
	
	public T get() {
		Thread t = Thread.currentThread();
		ThreadLocalMap map = getMap(t);
		if (map != null) {
			ThreadLocalMap.Entry entry = map.getEntry(this);
			if (entry != null) {
				@SuppressWarnings("unchecked")
				T result = (T)entry.value;
				return result;
			}
		}
		return setInitialValue();
	}
	private T setInitialValue() {
		T value = initialValue();
		Thread t = Thread.currentThread();
		ThreadLocalMap map = getMap(t);
		if (map != null) {
			map.set(this, value);
		}else {
			createMap(t, value);
		}
		return value;
 	}
	protected T initialValue() {
		return null;
	}
    public static <S> ThreadLocal<S> withInitial(Supplier<? extends S> supplier) {
        return new SuppliedThreadLocal<>(supplier);
    }
	
	public ThreadLocal() {
		
	}
	public void set(T value) {
		Thread t = new Thread();
		ThreadLocalMap map  = getMap(t);
		if(map != null) {
			map.set(this, value);
		}else {
			createMap(t, value);
		}
	}
	
	public void remove() {
		ThreadLocalMap map = getMap(Thread.currentThread());
		if (map != null) {
			map.remove(this);
		}
	}
	/**
	 * get the map associated with a ThreadLocal
	 * @param t
	 * @return
	 */
	ThreadLocalMap getMap(Thread t) {
		return t.threadLocals;
	}
	
	void createMap(Thread t, T firstValue) {
		t.threadLocals = new ThreadLocalMap(this, firstValue);
	}
	
	static ThreadLocalMap createInheritedMap(ThreadLocalMap parentMap) {
		return new ThreadLocalMap(parentMap);
	}
	
	T childValue(T parentValue) {
		throw new UnsupportedOperationException();
	}
	
    /**???
     * An extension of ThreadLocal that obtains its initial value from
     * the specified {@code Supplier}.
     */
    static final class SuppliedThreadLocal<T> extends ThreadLocal<T> {

        private final Supplier<? extends T> supplier;

        SuppliedThreadLocal(Supplier<? extends T> supplier) {
            this.supplier = Objects.requireNonNull(supplier);
        }

        @Override
        protected T initialValue() {
            return supplier.get();
        }
    }
	static class ThreadLocalMap {
		static class Entry extends WeakReference<ThreadLocal<?>>{

			Object value;
			public Entry(ThreadLocal<?> referent, Object v) {
				super(referent);
				value = v;
			}
		}
	    /**
	     * The initial capacity -- MUST be a power of two.
	     */
	    private static final int INITIAL_CAPACITY = 16;
	    private Entry[] table;
	    //The number of entries in table
	    private int size = 0;
	    /**
	     * The next size value at which to resize.
	     */
	    private int threshold;
	    /**
	     * Set the resize threshold to maintain at worst a 2/3 load factor.
	     * @param len
	     */
	    private void setThreshold(int len) {
	        threshold = len * 2 / 3;
	    }
	    private static int nextIndex(int i, int len) {
	    	return ((i+1 < len) ? i+1 : 0);
	    }
	    private static int prevIndex(int i, int len) {
            return ((i - 1 >= 0) ? i - 1 : len - 1);
        }
	    
	    public ThreadLocalMap(ThreadLocal<?> firstKey, Object firstValue) {
	    	table = new Entry[INITIAL_CAPACITY];
	    	//Ïàµ±ÓÚi = threadLocalHashCode / INITIAL_CAPACITY
	    	int i = firstKey.threadLocalHashCode & (INITIAL_CAPACITY - 1);
	    	table[i] = new Entry(firstKey, firstValue);
	    	size  = 1;
	    	setThreshold(INITIAL_CAPACITY);
		}
	    private ThreadLocalMap(ThreadLocalMap parentMap) {
	    	Entry[] parentTable = parentMap.table;
	    	int len = parentTable.length;
	    	setThreshold(len);
	    	table = new Entry[len];
	    	
	    	for(int j=0; j < len; j++) {
	    		Entry e = parentTable[j];
	    		if (e != null) {
					ThreadLocal<Object> key = (ThreadLocal<Object>) e.get();
					if (key != null) {
						Object value = key.childValue(e.value);
						Entry c = new Entry(key, value);
						int h = key.threadLocalHashCode & (len-1);
						while(table[h] != null) {
							h = nextIndex(h, len);
						}
						table[h] = c;
						size++;
					}
				}
	    	}
	    }
	    private void set(ThreadLocal<?> key, Object value) {
	    	Entry[] tab = table;
	    	int len = tab.length;
	    	int i = key.threadLocalHashCode & (len-1);
	    	
	    	for(Entry e = tab[i]; e != null; e = tab[i = nextIndex(i, len)]) {
	    		ThreadLocal<?> k = e.get();
	    		
	    		if (k == key) {
					e.value = value;
					return;
				}
	    		if (k == null) {
					replaceStaleEntry(key, value, i);
					return;
				}
	    	}
	    }
	    

	    
	    private Entry getEntry(ThreadLocal<?> key) {
	    	int i = key.threadLocalHashCode & (table.length-1);
	    	Entry e = table[i];
	    	if (e != null && e.get() == key) {
	    		return e;
			}else {
				return getEntryAfterMiss(key, i, e);
			}
	    }
	    
	    private Entry getEntryAfterMiss(ThreadLocal<?> key, int i, Entry e) {
	    	Entry[] tab = table;
	    	int len = tab.length;
	    	
	    	while(e != null) {
	    		ThreadLocal<?> k = e.get();
	    		if (k == key) {
					return e;
				}
	    		if (k == null) {
	    			expungeStaleEntry(i);
				}else {
					i = nextIndex(i, len);
				}
	    		e = tab[i];
	    	}
	    	return null;
	    }
	    
	    private void remove(ThreadLocal<?> key) {
	    	Entry[] tab = table;
	    	int len = tab.length;
	    	int i = key.threadLocalHashCode & (len-1);
	    	for(Entry e = tab[i]; e != null; e = tab[i = nextIndex(i, len)]) {
	    		if (e.get() == key) {
	    			e.clear();
	    			expungeStaleEntry(i);
	    			return;
				}
	    	}
	    }
	    
	    
	    
	    /*--------------------????-----------------------------*/
	    private void replaceStaleEntry(ThreadLocal<?> key, Object value, int staleSlot) {
		    Entry[] tab = table;
		    int len = tab.length;
		    Entry e;
	
		    // Back up to check for prior stale entry in current run.
		    // We clean out whole runs at a time to avoid continual
		    // incremental rehashing due to garbage collector freeing
		    // up refs in bunches (i.e., whenever the collector runs).
		    int slotToExpunge = staleSlot;
		    for (int i = prevIndex(staleSlot, len);
		         (e = tab[i]) != null;
		         i = prevIndex(i, len))
		        if (e.get() == null)
		            slotToExpunge = i;
	
		    // Find either the key or trailing null slot of run, whichever
		    // occurs first
		    for (int i = nextIndex(staleSlot, len);
		         (e = tab[i]) != null;
		         i = nextIndex(i, len)) {
		        ThreadLocal<?> k = e.get();
	
		        // If we find key, then we need to swap it
		        // with the stale entry to maintain hash table order.
		        // The newly stale slot, or any other stale slot
		        // encountered above it, can then be sent to expungeStaleEntry
		        // to remove or rehash all of the other entries in run.
		        if (k == key) {
		            e.value = value;
	
		            tab[i] = tab[staleSlot];
		            tab[staleSlot] = e;
	
		            // Start expunge at preceding stale entry if it exists
		            if (slotToExpunge == staleSlot)
		                slotToExpunge = i;
		            cleanSomeSlots(expungeStaleEntry(slotToExpunge), len);
		            return;
		        }
	
		        // If we didn't find stale entry on backward scan, the
		        // first stale entry seen while scanning for key is the
		        // first still present in the run.
		        if (k == null && slotToExpunge == staleSlot)
		            slotToExpunge = i;
		    }
	
		    // If key not found, put new entry in stale slot
		    tab[staleSlot].value = null;
		    tab[staleSlot] = new Entry(key, value);
	
		    // If there are any other stale entries in run, expunge them
		    if (slotToExpunge != staleSlot)
		        cleanSomeSlots(expungeStaleEntry(slotToExpunge), len);
		   
	    }
	    
	    /**
         * Expunge a stale entry by rehashing any possibly colliding entries
         * lying between staleSlot and the next null slot.  This also expunges
         * any other stale entries encountered before the trailing null.  See
         * Knuth, Section 6.4
         *
         * @param staleSlot index of slot known to have null key
         * @return the index of the next null slot after staleSlot
         * (all between staleSlot and this slot will have been checked
         * for expunging).
         */
        private int expungeStaleEntry(int staleSlot) {
            Entry[] tab = table;
            int len = tab.length;

            // expunge entry at staleSlot
            tab[staleSlot].value = null;
            tab[staleSlot] = null;
            size--;

            // Rehash until we encounter null
            Entry e;
            int i;
            for (i = nextIndex(staleSlot, len);
                 (e = tab[i]) != null;
                 i = nextIndex(i, len)) {
                ThreadLocal<?> k = e.get();
                if (k == null) {
                    e.value = null;
                    tab[i] = null;
                    size--;
                } else {
                    int h = k.threadLocalHashCode & (len - 1);
                    if (h != i) {
                        tab[i] = null;

                        // Unlike Knuth 6.4 Algorithm R, we must scan until
                        // null because multiple entries could have been stale.
                        while (tab[h] != null)
                            h = nextIndex(h, len);
                        tab[h] = e;
                    }
                }
            }
            return i;
        }
        private boolean cleanSomeSlots(int i, int n) {
            boolean removed = false;
            Entry[] tab = table;
            int len = tab.length;
            do {
                i = nextIndex(i, len);
                Entry e = tab[i];
                if (e != null && e.get() == null) {
                    n = len;
                    removed = true;
                    i = expungeStaleEntry(i);
                }
            } while ( (n >>>= 1) != 0);
            return removed;
        }
	}
}
