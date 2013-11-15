package formallanguages.src;

import formallanguages.src.NonterminalLevels.Level;
import java.util.ListIterator;

/**
 *
 * @author Ilya Shkuratov
 */
public class Grammar {
    protected SymbolicTable pSymTable;
    protected RulesTable pRulesTable;
    protected NonterminalLevels pNlvls;
    protected int[][] pRelationTable;

    
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
            System.out.print(pSymTable.getNonTerm(i) + ": ");
            while (rule[j] != SymbolicTable.DOT) {
                System.out.print(pSymTable.getSymbol(rule[j]));
                j ++;
            }
            System.out.println(pSymTable.getSymbol(rule[j]));
        }
        System.out.println();
    }
    
    /**
     *
     * @param code - Nonterminal grammar code
     * @return String representation of grammar rule
     */
    public String getRuleAsString(int code) {
        StringBuilder sb = new StringBuilder();
        int[] rule;
        int j = 0;
        
        //code -= SymbolicTable.OFFSET;
        rule = pRulesTable.getRule(code);
        //sb.append(pSymTable.getNonTerm(code)).append(": ");
        while (rule[j] != SymbolicTable.DOT) {
            sb.append(pSymTable.getSymbol(rule[j]));
            j ++;
        }
        //sb.append(pSymTable.getSymbol(rule[j]));
        return sb.toString();
    }
    
    public void printSortedRules() {
        int[] rule;
        
        ListIterator<Level> iter = pNlvls.listIterator(pNlvls.size());
        int index;
        
        System.out.println("Sorted Grammar rules table.");
        while ( iter.hasPrevious() ) {
            for (int code : iter.previous()) {
                System.out.print(pSymTable.getNonTerm(code) + ": ");
                index = 0;
                rule = pRulesTable.getRule(code);
                while (rule[index] != SymbolicTable.DOT) {
                    System.out.print(pSymTable.getSymbol(rule[index]));
                    index ++;
                }
                System.out.println(pSymTable.getSymbol(rule[index]));
            }
        }
        System.out.println();
    }
    
    public void printRelationTable() {
        if (pRelationTable == null) return;
        System.out.println("Nonterminal relation table.");
        for (int i = 0; i < pRelationTable.length; i++) {
            int[] is = pRelationTable[i];
            System.out.print(pSymTable.getNonTerm(i) + ": ");
            for (int j = 0; j < is.length; j++) {
                if (is[j] != -1)
                    System.out.print(pSymTable.getNonTerm(is[j]) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
    
    
    
    void setNlvls(NonterminalLevels Nlvls) {
        this.pNlvls = Nlvls;
    }
    
    void setRelationTable(int[][] pRelationTable) {
        this.pRelationTable = pRelationTable;
    }
    
}
