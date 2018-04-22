package sort;

import java.util.Comparator;

/**
 * Created by 11981 on 2018/4/22.
 */
public interface Sorter {

    /**
     * 排序
     * @param list
     * @param <T>
     */
    public <T extends Comparable<T>> void sort(T[] list);

    public <T> void sort(T[] list, Comparator<T> comp);
}
