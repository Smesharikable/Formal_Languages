package formallanguages.src;

import formallanguages.exceptions.IncorrectSymbolCodeException;
import formallanguages.exceptions.IncorrectSymbolTypeException;

/**
 *
 * @author Ilya Shkuratov
 */
public class CFGrammar extends Grammar {
    private FirstTable firstTable;
    
    public CFGrammar(SymbolicTable pSymTable, RulesTable pRulesTable) {
        super(pSymTable, pRulesTable);
    }
    
    FirstSet getFirst(int k, int[] form) 
            throws IncorrectSymbolCodeException, IncorrectSymbolTypeException {
        if (firstTable == null || firstTable.getChainLen() != k) {
            firstTable = new FirstTable(this, k);
            return firstTable.First(form);
        }
        else {
            return firstTable.First(form);
        }
    }
    
    FirstSet getFirst(int k, int code) 
            throws IncorrectSymbolCodeException, IncorrectSymbolTypeException {
        if (firstTable == null || firstTable.getChainLen() != k) {
            firstTable = new FirstTable(this, k);
            return firstTable.First(code);
        }
        else {
            return firstTable.First(code);
        }
    }
}
