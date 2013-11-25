package formallanguages.src;

import formallanguages.exceptions.IncorrectSymbolCodeException;
import formallanguages.exceptions.IncorrectSymbolTypeException;
import formallanguages.exceptions.UnexpectedSymbolException;

/**
 *
 * @author Ilya Shkuratov
 */
public class CFGrammar extends Grammar {
    private FirstTable firstTable;
    private int k = 1;
    
    public CFGrammar(SymbolicTable pSymTable, RulesTable pRulesTable) {
        super(pSymTable, pRulesTable);
    }
    
    public void initFirstTable(int k) 
            throws IncorrectSymbolCodeException, IncorrectSymbolTypeException, 
            UnexpectedSymbolException {
        if (firstTable == null || this.k != k) {
            this.k = k;
            firstTable = new FirstTable(this, k);
            firstTable.init();
        }
    }
    
    public FirstSet getFirst(int[] form) 
            throws IncorrectSymbolCodeException, IncorrectSymbolTypeException, 
            UnexpectedSymbolException {
        if (firstTable == null) {
            initFirstTable(k);
            return firstTable.First(form);
        }
        else {
            return firstTable.First(form);
        }
    }
    
    public FirstSet getFirst(int code) 
            throws IncorrectSymbolCodeException, IncorrectSymbolTypeException,
            UnexpectedSymbolException {
        if (firstTable == null) {
            initFirstTable(k);
            return firstTable.First(code);
        }
        else {
            return firstTable.First(code);
        }
    }
}
