package formallanguages.src;

import formallanguages.src.NonterminalLevels.Level;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Ilya Shkuratov
 */
public class NonterminalLevels extends LinkedList<Level>{
    private Grammar pGrammar;
    
    public NonterminalLevels() {
        super();
    }
    
    public NonterminalLevels(Collection<? extends Level> collection) {
        super(collection);
    }
    
    public int[][] transformToArray() {
        int[][] result  = new int[this.size()][];
        ListIterator<Level> iter = this.listIterator();
        Level ts;
        int j, i = 0;
        
        while (iter.hasNext()) {
            ts = iter.next();
            result[i] = new int[ts.size()];
            j = 0;
            for (int integer : ts) {
                result[i][j++] = integer; 
            }
            i ++;
        }
        
        return result;
    }
    
    public StringBuilder print(StringBuilder sb) {
        int lvl = this.size();
        ListIterator<Level> iter =  this.listIterator(lvl);
        Level ts;
        SymbolicTable st = pGrammar.getpSymTable();
        while ( iter.hasPrevious() ) {
            ts = iter.previous();
            lvl --;
            sb.append("S").append(lvl).append(" = {");
            for (int integer : ts) {
                sb.append(st.getNonTermByRuleCode(integer)).append(", ");
            }
            sb.delete(sb.length() - 2, sb.length());
            sb.append("}").append(pGrammar.eol);
        }
        sb.append(pGrammar.eol);
        return sb;
    }
    
    void setGrammar(Grammar grammar) {
        this.pGrammar = grammar;
    }
    
    public class Level extends TreeSet<Integer> {

        public Level() {
        }

        public Level(Comparator<? super Integer> comparator) {
            super(comparator);
        }

        public Level(Collection<? extends Integer> c) {
            super(c);
        }

        public Level(SortedSet<Integer> s) {
            super(s);
        }
        
    }
    
}
