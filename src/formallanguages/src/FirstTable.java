package formallanguages.src;

import formallanguages.exceptions.IncorrectSymbolCodeException;
import formallanguages.exceptions.IncorrectSymbolTypeException;
import static formallanguages.src.SymbolicTable.SymbolType.NONTERMINAL;
import static formallanguages.src.SymbolicTable.SymbolType.TERMINAL;

/**
 *
 * @author Ilya Shkuratov
 */
class FirstTable {
    private CFGrammar grammar;
    private FirstSet[] firstTable;
    private int chainLen;

    FirstTable(CFGrammar grammar, int chainLen) {
        this.grammar = grammar;
        this.chainLen = chainLen;
    }

    public int getChainLen() {
        return chainLen;
    }
    
    /**
     *
     * @param chainLen - max length if terminal chains
     * @param form - chain of terminals and nonterminals without grammar symbols
     * @return list of all terminal chains with max length k that derives from form
     * @throws IncorrectSymbolCodeException
     * @throws IncorrectSymbolTypeException  
     */
    FirstSet First(int[] form) 
            throws IncorrectSymbolCodeException, IncorrectSymbolTypeException {
        int count = grammar.pSymTable.getpNontermCount();
        FirstSet[] ls = new FirstSet[count];
        Integer[] prefix = new Integer[chainLen];
        int i = 0;
        
        // find terminal prefix
        while (i <= chainLen && i < form.length && 
                grammar.pSymTable.getSymbolType(form[i]) == SymbolicTable.SymbolType.TERMINAL) {
            prefix[i] = form[i];
            i ++;
        }
        FirstSet fl = new FirstSet(chainLen);
        fl.add(prefix);
        
        if (i == chainLen || i == form.length) return fl;
        
        // chains accumulation
        boolean full = false;
        FirstSet next;
        while (!full) {
            while (i < form.length) {
                next = First(form[i]);
                full |= fl.Concatenate(next);
            }
        }
        
        return fl;
    }
    
    FirstSet First(int symbol) 
            throws IncorrectSymbolCodeException, IncorrectSymbolTypeException {
        SymbolicTable.SymbolType t = grammar.pSymTable.getSymbolType(symbol);
        switch (t) {
            case NONTERMINAL:
                return FirstSetNonterminal(symbol);
            case TERMINAL:
                return FirstSetTerminal(symbol);
            default:
                throw new IncorrectSymbolTypeException("Symbol must be TERMINAL or NONTERMINAL");
            
        }
    }
    
    private FirstSet FirstSetTerminal(int symbol) {
        FirstSet fs = new FirstSet(chainLen);
        Integer[] chain = new Integer[chainLen];
        chain[0] = symbol;
        fs.add(chain);
        return fs;
    }
    
    // TODO: implement this
    private FirstSet FirstSetNonterminal(int symbol) {
        FirstSet result = new FirstSet(chainLen);
        
        return result;
    }
}
