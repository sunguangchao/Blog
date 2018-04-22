package sort;

import java.util.Comparator;

/**
 * Created by 11981 on 2018/4/22.
 * 手写二分查找的代码
 */
public class BinarySort {

    public static <T extends Comparable<T>> int binarySearch(T[] x, T key){
        return binarySearch(x, 0, x.length - 1, key);
    }

    /**
     * 用循环实现的二分查找
     * @param x
     * @param key
     * @param comp
     * @param <T>
     * @return
     */
    public static <T> int binarySearch(T[] x, T key, Comparator<T> comp){
        int low = 0;
        int high = x.length - 1;
        while (low <= high){
            int mid = (low + high) >>> 1;
            int cmp = comp.compare(x[mid], key);
            if (cmp < 0){
                low = mid + 1;
            }else if (cmp > 0){
                high = mid - 1;
            }else{
                return mid;
            }

        }
        return -1;
    }

    /**
     * 用递归实现二分查找
     * @param x
     * @param low
     * @param high
     * @param key
     * @param <T>
     * @return
     */
    private static <T extends Comparable<T>> int binarySearch(T[] x, int low, int high, T key){
        if (low <= high){
            int mid = low + ((high - low) >> 1);
            if (key.compareTo(x[mid]) == 0){
                return mid;
            }
            else if (key.compareTo(x[mid]) < 0){
                return binarySearch(x, low, mid-1, key);
            }else{
                return binarySearch(x, mid+1, high, key);
            }
        }
        return -1;
    }
}
