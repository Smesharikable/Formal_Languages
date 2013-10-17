package formallanguages.src;

import java.util.Iterator;

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
    
    // TODO: implement this
    public boolean regularize() {
        if (pNlvls == null) return false;
        return true;
    }
    
    void setNlvls(NonterminalLevels Nlvls) {
        this.pNlvls = Nlvls;
    }
}
