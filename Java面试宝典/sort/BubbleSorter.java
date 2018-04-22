package sort;

import java.util.Comparator;

/**
 * Created by 11981 on 2018/4/22.
 */
public class BubbleSorter implements Sorter {
    @Override
    public <T extends Comparable<T>> void sort(T[] list){
        boolean swapped = true;
        for (int i = 1, len = list.length; i < len && swapped; ++i){
            swapped = false;
            //两两调换，最大的放到后面
            for (int j = 0; j < len - i; ++j){
                if (list[j].compareTo(list[j+1]) > 0){
                    T temp = list[j];
                    list[j] = list[j + 1];
                    list[j + 1] = temp;
                    swapped = true;
                }
            }
        }
    }

    @Override
    public <T> void sort(T[] list, Comparator<T> comp){
        boolean swapped = true;
        for (int i = 1, len = list.length; i < len && swapped; ++i){
            swapped = false;
            for (int j = 0; j < len - i; ++j){
                if (comp.compare(list[j], list[j+1]) > 0){
                    T tmp = list[j];
                    list[j] = list[j+1];
                    list[j+1] = tmp;
                    swapped = true;
                }
            }
        }
    }
}
