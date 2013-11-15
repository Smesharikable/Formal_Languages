package formallanguages.src;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

/**
 *
 * @author Ilya Shukratov
 */
public class FirstSet extends TreeSet<Integer[]> {
    // set referenced to be full if it hasn't changed after last concatenation
    private int elementCapacity = 1;


    FirstSet(int elemCapacity) {
        super();
        elementCapacity = elemCapacity;
    }

    FirstSet(int elemCapacity, Collection<? extends Integer[]> c) {
        super(c);
        elementCapacity = elemCapacity;
    }

    FirstSet(int elemCapacity, Comparator<? super Integer[]> comparator) {
        super(comparator);
        elementCapacity = elemCapacity;
    }

    public int getElementCapacity() {
        return elementCapacity;
    }
    
    /**
     * Concatanate two sets into another with k as max length of elements
     * 
     * @param k - max length of elements
     * @param set1 - base set to concatenate
     * @param addSet - addition set to concatenate
     * @return
     */
    public boolean Concatenate(FirstSet addSet) {
        Integer[] temp;
        boolean exist;
        boolean full = false;
        int rest;
        int unfilled = 0;
        int j;
        int p = 0;
        for (Integer[] first : this) {
            // find end of chain
            rest = 0;
            for (int i = 0; i < elementCapacity; i++) {
                if (first[rest] != 0) {
                    rest++;
                }
            }
            // if chain is full, just add it into new set
            if (rest == elementCapacity) {
                temp = new Integer[elementCapacity];
                System.arraycopy(first, 0, temp, 0, elementCapacity);
                this.add(temp);
                break;
            }
            // concatenate with second set
            for (Integer[] second : addSet) {
                temp = new Integer[elementCapacity];
                System.arraycopy(first, 0, temp, 0, rest);
                for (j = rest; j < elementCapacity && p!= second.length && second[p] != 0; j ++) {
                    temp[j] = second[p++];
                }
                // TODO: check this statemant
                exist = this.add(temp);
                if (!exist && j != elementCapacity) {
                    unfilled++;
                }
                p = 0;
            }
        }
        if (unfilled != 0) {
            full = true;
        }
        return full;
    }
    
}
