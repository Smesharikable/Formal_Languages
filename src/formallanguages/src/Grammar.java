package formallanguages.src;

import formallanguages.exceptions.IncorrectSymbolCodeException;
import formallanguages.src.NonterminalLevels.Level;
import java.util.ListIterator;

/**
 *
 * @author Ilya Shkuratov
 */
public class Grammar {
    final static public String CFRG = "CFRG";
    final static public String CFG = "CFG";
    
    protected SymbolicTable pSymTable;
    protected RulesTable pRulesTable;
    protected NonterminalLevels pNlvls;
    protected int[][] pRelationTable;
    
    protected String eol = System.getProperty("line.separator");

    
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
    
    public String printGrammar() {
        return printGrammar(new StringBuilder()).toString();
    }
    
    public StringBuilder printGrammar(StringBuilder sb) {
        int[] bounds = pSymTable.getpNbounds();
        int count = bounds[SymbolicTable.CURR] - bounds[SymbolicTable.MIN];
        int[] rule;
        int j;
        
        for (int i = 0; i < count; i++) {
//            rule = pRulesTable.getRule(i);
//            j = 0;
            sb.append(pSymTable.getNonTermByRuleCode(i)).append(": ");
            sb.append(getRuleAsString(i)).append(eol);
//            while (rule[j] != SymbolicTable.DOT) {
//                sb.append(pSymTable.getSymbol(rule[j]));
//                j ++;
//            }
//            sb.append(pSymTable.getSymbol(rule[j]));
        }
        sb.append(eol);
        return sb;
    }
    
    /**
     *
     * @param code - Nonterminal grammar code
     * @return String representation of grammar rule
     * @throws IncorrectSymbolCodeException  
     */
    public String getRuleAsString(int code) {
        StringBuilder sb = new StringBuilder();
        int[] rule;
        int j = 0;
        
        rule = pRulesTable.getRule(code);
        while (rule[j] != SymbolicTable.DOT) {
            sb.append(pSymTable.getSymbol(rule[j]));
            j ++;
        }
        sb.append(pSymTable.getSymbol(SymbolicTable.DOT));
        return sb.toString();
    }
    
    public void printSortedRules() 
            throws IncorrectSymbolCodeException {
        int[] rule;
        
        ListIterator<Level> iter = pNlvls.listIterator(pNlvls.size());
        int index;
        
        System.out.println("Sorted Grammar rules table.");
        while ( iter.hasPrevious() ) {
            for (int code : iter.previous()) {
                System.out.print(pSymTable.getNonTermByRuleCode(code) + ": ");
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
    
    public StringBuilder printRelationTable(StringBuilder sb) {
        if (pRelationTable == null) return sb;
        for (int i = 0; i < pRelationTable.length; i++) {
            int[] is = pRelationTable[i];
            sb.append(pSymTable.getNonTermByRuleCode(i)).append(": ");
            for (int j = 0; j < is.length; j++) {
                if (is[j] != -1)
                    sb.append(pSymTable.getNonTermByRuleCode(is[j])).append(" ");
            }
            sb.append(eol);
        }
        sb.append(eol);
        return sb;
    }
    
    
    
    void setNlvls(NonterminalLevels Nlvls) {
        this.pNlvls = Nlvls;
    }
    
    void setRelationTable(int[][] pRelationTable) {
        this.pRelationTable = pRelationTable;
    }
    
}
