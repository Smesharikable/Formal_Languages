package formallanguages.src;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.TreeSet;

/**
 *
 * @author Ilya Shkuratov
 */
public class TopologicalSort {
    
    public static int[][] sort(RulesTable ruleTable, int[] nonTermBounds) {
        LinkedList<TreeSet<Integer>> levels = new LinkedList<TreeSet<Integer>>();
        TreeSet<Integer> ts = new TreeSet();
        int[][] relation = createRelation(ruleTable, nonTermBounds, ts);
        
        TreeSet<Integer> tsnew;
        Iterator<Integer> iter;
        ListIterator<TreeSet<Integer>> liter;
        int [] temp;
        int current;
        int nonterm;
        
        do {
            tsnew = new TreeSet();
            iter = ts.iterator();
            levels.add(ts);
            while (iter.hasNext()) {
                // add all nonterminals that reference to current
                temp = relation[iter.next()];
                current = 0;
                while ( (nonterm = temp[current]) != -1) {
                    tsnew.add(nonterm);
                    // delete
                    liter = levels.listIterator(levels.size());
                    while ( liter.hasPrevious() ) {
                        if ( liter.previous().remove(nonterm) ) break;
                    }
                    current ++;
                }
            }
            ts = tsnew;
        } while ( !ts.isEmpty() );
        
        return transformToArray(levels);
    }
    
    private static int[][] createRelation(RulesTable ruleTable, int[] nonTermBounds, TreeSet l0) {
        int maxId = nonTermBounds[SymbolicTable.CURR];
        int minId = nonTermBounds[SymbolicTable.MIN];
        int nonTermCount = maxId - minId;
        int[][] relation = new int[nonTermCount][nonTermCount];
        
        // initialization
        for (int i = 0; i < relation.length; i++) {
            int[] is = relation[i];
            for (int j = 0; j < is.length; j++) {
                is[j] = -1;
            }
        }
        
        
        int j, lex, count = 0;
        for (int i = 0; i < nonTermCount; i++) {
            int[] rule = ruleTable.getRule(i);
            j = 0;
            while ( (lex = rule[j]) != SymbolicTable.DOT) {
                if (lex >= minId && lex < maxId) {
                    insertIntoRelation(relation[lex - SymbolicTable.OFFSET], i);
                    count ++;
                }
                j ++;
            }
            
            // add to set of independent terminals
            if (count == 0) {
                l0.add(i);
            } else {
                count = 0;
            }
        }
        
        return relation;
    }
    
    private static void insertIntoRelation(int[] relation, int code) {
        int i = 0;
        while (relation[i] != -1) {
            if (relation[i] == code) break;
            i ++;
        }
        relation[i] = code;
    }
    
    private static int[][] transformToArray(LinkedList<TreeSet<Integer>> levels) {
        int[][] result  = new int[levels.size()][];
        ListIterator<TreeSet<Integer>> iter = levels.listIterator();
        TreeSet<Integer> ts;
        int j, i = 0;
        
        while (iter.hasNext()) {
            ts = iter.next();
            result[i] = new int[ts.size()];
            j = 0;
            for (int integer : ts) {
                result[i][j++] = integer + SymbolicTable.OFFSET; 
            }
            i ++;
        }
        
        return result;
    }
    
}
