package formallanguages.src;

/**
 *
 * @author Ilya Shkuratov
 */
public class Grammar {
    SymbolicTable pSymTable;
    RulesTable pRulesTable;

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
    
}
