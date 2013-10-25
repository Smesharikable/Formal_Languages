package formallanguages.src;

import formallanguages.exceptions.TooLongRuleException;
import formallanguages.src.NonterminalLevels.Level;
import java.util.ListIterator;

/**
 *
 * @author Ilya Shkuratov
 */
public class Grammar {
    private SymbolicTable pSymTable;
    private RulesTable pRulesTable;
    private NonterminalLevels pNlvls;
    private int[][] pRelationTable;

    
    public Grammar(SymbolicTable pSymTable, RulesTable pRulesTable) {
        this.pSymTable = pSymTable;
        this.pRulesTable = pRulesTable;
    }
    
    public RulesTable getpRulesTable() {
        return pRulesTable;
    }

    public SymbolicTable getpSymTable() {
        return pSymTable;
    }
    
    public void printRules() {
        int[] bounds = pSymTable.getpNbounds();
        int count = bounds[SymbolicTable.CURR] - bounds[SymbolicTable.MIN];
        int[] rule;
        int j;
        
        System.out.println("Grammar rules table.");
        for (int i = 0; i < count; i++) {
            rule = pRulesTable.getRule(i);
            j = 0;
            System.out.print(pSymTable.getNonTerm(i + SymbolicTable.OFFSET) + ": ");
            while (rule[j] != SymbolicTable.DOT) {
                System.out.print(pSymTable.getNonTerm(rule[j]));
                j ++;
            }
            System.out.println(pSymTable.getNonTerm(rule[j]));
        }
        System.out.println();
    }
    
    public void printSortedRules() {
        int[] rule;
        
        ListIterator<Level> iter = pNlvls.listIterator(pNlvls.size());
        int index;
        
        System.out.println("Sorted Grammar rules table.");
        while ( iter.hasPrevious() ) {
            for (int code : iter.previous()) {
                System.out.print(pSymTable.getNonTerm(code + SymbolicTable.OFFSET) + ": ");
                index = 0;
                rule = pRulesTable.getRule(code);
                while (rule[index] != SymbolicTable.DOT) {
                    System.out.print(pSymTable.getNonTerm(rule[index]));
                    index ++;
                }
                System.out.println(pSymTable.getNonTerm(rule[index]));
            }
        }
        System.out.println();
    }
    
    public void printRelationTable() {
        if (pRelationTable == null) return;
        System.out.println("Nonterminal relation table.");
        for (int i = 0; i < pRelationTable.length; i++) {
            int[] is = pRelationTable[i];
            System.out.print(pSymTable.getNonTerm(i + SymbolicTable.OFFSET) + ": ");
            for (int j = 0; j < is.length; j++) {
                if (is[j] != -1)
                    System.out.print(pSymTable.getNonTerm(is[j] + SymbolicTable.OFFSET) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
    
    // TODO: implement this
    public boolean regularize() throws TooLongRuleException {
        if (pNlvls == null) {
            TopologicalSort.sort(this);
        }
        ListIterator<Level> liter = pNlvls.listIterator();
        liter.next(); // skip l0
        int[] bounds = pSymTable.getpNbounds();
        int min = bounds[SymbolicTable.MIN];
        int max = bounds[SymbolicTable.CURR];
        int length = pRulesTable.getMaxRegLength();
        int[] buf = new int[length];
        int[] rule;
        int position, i;
        
        while ( liter.hasNext() ) {
            for (int code : liter.next()) {
                position = 0;
                rule = pRulesTable.getRule(code);
                System.arraycopy(rule, 0, buf, 0, rule.length);
                i = 0;
                while (i < buf.length && buf[i] != SymbolicTable.DOT) {
                    if (buf[i] >= min && buf[i] < max) {
                        position = insertIntoRule(rule, position, buf[i]);
                    } else {
                        rule[position ++] = buf[i];
                    }
                    i ++;
                }
                rule[position ++] = SymbolicTable.DOT;
            }
        }
        
        return true;
    }
    
    void setNlvls(NonterminalLevels Nlvls) {
        this.pNlvls = Nlvls;
    }
    
    void setRelationTable(int[][] pRelationTable) {
        this.pRelationTable = pRelationTable;
    }
    
    private int insertIntoRule(int[] rule, int position, int code) throws TooLongRuleException {
        int[] src = pRulesTable.getRule(code - SymbolicTable.OFFSET);
        int i = 0;
        while (i < src.length && src[i] != SymbolicTable.DOT) {
            if (position == rule.length) {
                throw new TooLongRuleException("Please, increase max length of rule in RulesTable");
            }
            rule[position ++] = src[i ++];
        }
        return position;
    }
}
