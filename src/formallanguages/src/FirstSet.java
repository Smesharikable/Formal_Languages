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
    private boolean full = false;
    private static Comparator comparator = new IntArrayComparator();;


    FirstSet(int elemCapacity) {
        super(comparator);
        elementCapacity = elemCapacity;
    }


    public int getElementCapacity() {
        return elementCapacity;
    }

    public boolean isFull() {
        return full;
    }
    
    /**
     *
     * @param addSet - set wich will be join
     * @return true - if at least one new element has been added
     */
    public boolean join(FirstSet addSet) {
        boolean isNew = true;
        for (Integer[] integers : addSet) {
            isNew &= this.add(integers);
        }
        return isNew;
    }
    
    /**
     * Concatanate two sets into another with k as max length of elements
     * 
     * @param k - max length of elements
     * @param set1 - base set to concatenate
     * @param set2  - addition set
     * @return
     */
    static public FirstSet Concatenate(int k, FirstSet set1, FirstSet set2) {
        // if first set is empty we don't know prefix of resulting chaing
        if (set1.size() == 0 || set2.size() == 0) return set1;
        
        FirstSet result = new FirstSet(k);
        Integer[] temp;
        int rest;
        int unfilled = 0;
        int j;
        int p = 0;
        for (Integer[] first : set1) {
            // find end of chain
            rest = 0;
            // if current chain id empty just add all from second set into first set
            if (first[0] != SymbolicTable.EMPTY) {
                for (int i = 0; i < k; i++) {
                    if (first[rest] != 0) {
                        rest++;
                    }
                }
            }
            
            // if chain is full, just add it into new set
            if (rest == k) {
                result.add(first);
                continue;
            }
            // concatenate with second set
            for (Integer[] second : set2) {
                if (second[0] == SymbolicTable.EMPTY) {
                    result.add(first);
                    unfilled ++;
                    continue;
                }
                temp = new Integer[k];
                for (int i = 0; i < rest; i++) {
                    temp[i] = first[i];
                }
                for (j = rest; j < k && p != second.length && second[p] != 0; j ++) {
                    temp[j] = second[p++];
                }
                result.add(temp);
                // TODO: check this statemant
                if (j != k) {
                    unfilled++;
                }
                p = 0;
            }
        }
        if (unfilled == 0) {
            result.full = true;
        }
        return result;
    }
    
    private static class IntArrayComparator implements Comparator<Integer[]> {

        @Override
        public int compare(Integer[] o1, Integer[] o2) {
            if (o1.length < o2.length) return -1;
            if (o1.length > o2.length) return 1;
            for (int i = 0; i < o2.length; i++) {
                if (o1[i] < o2[i]) return -1;
                if (o1[i] > o2[i]) return 1;
            }
            return 0;
        }
        
    }
    
}
