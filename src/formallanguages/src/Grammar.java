package formallanguages.src;

import formallanguages.exceptions.TooLongRuleException;
import formallanguages.src.NonterminalLevels.Level;
import java.util.ListIterator;

/**
 *
 * @author Ilya Shkuratov
 */
public class Grammar {
    SymbolicTable pSymTable;
    RulesTable pRulesTable;
    NonterminalLevels pNlvls;

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
        
        for (int i = 0; i < count; i++) {
            rule = pRulesTable.getRule(i);
            j = 0;
            while (rule[j] != SymbolicTable.DOT) {
                System.out.print(pSymTable.getNonTerm(rule[j]));
                j ++;
            }
            System.out.println(pSymTable.getNonTerm(rule[j]));
        }
    }
    
    // TODO: implement this
    public boolean regularize() throws TooLongRuleException {
        if (pNlvls == null) return false;
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
