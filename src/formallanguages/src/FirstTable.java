package formallanguages.src;

import formallanguages.dfa.Cf_chain_dfa;
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
    
    private String error_msg_1 = "Wrong symbol in rule for Nonterminal: %s\n";
    private String error_msg_2 = "Symbol must be TERMINAL or NONTERMINAL: %s\n";

    FirstTable(CFGrammar grammar, int chainLen) {
        this.grammar = grammar;
        this.chainLen = chainLen;
        int count = grammar.pSymTable.getpNontermCount();
        firstTable = new FirstSet[count];
    }

    public int getChainLen() {
        return chainLen;
    }
    
    /*
     * fill first table
     */
    public void init() 
            throws IncorrectSymbolCodeException, IncorrectSymbolTypeException {
        // build F_0
        int[] bounds = grammar.pSymTable.getpNbounds();
        int start = bounds[SymbolicTable.MIN] - SymbolicTable.OFFSET;
        int max = bounds[SymbolicTable.MAX] - SymbolicTable.OFFSET;
        SymbolicTable st = grammar.pSymTable;
        Cf_chain_dfa.init(chainLen);
        Cf_chain_dfa dfa = Cf_chain_dfa.START;
        Integer[] chain;
        int[] rule;
        int count = 0;
        
        
        for (int i = start; i < max; i++) {
            rule = grammar.pRulesTable.getRule(i);
            dfa = dfa.step(st.getSymbolType(rule[count]), rule[count]);
            count ++;
            while (dfa != Cf_chain_dfa.END || dfa != Cf_chain_dfa.ERROR) {
                switch(dfa) {
                    case START:
                        if (Cf_chain_dfa.isDetect()) {
                            chain = Cf_chain_dfa.getChain();
                            firstTable[i - SymbolicTable.OFFSET].add(chain);
                        }
                        count = 0;
                    case TERMINAL: case GRAMMAR:
                        dfa = dfa.step(st.getSymbolType(rule[count]), rule[count]);
                        count ++;
                        break;
                    case END:
                        break;
                    default:
                        throw new IncorrectSymbolTypeException(
                                String.format(error_msg_1, st.getSymbol(count))
                                );
                }
            }
        }
        
        // Build F_i
        int j;
        FirstSet fs = new FirstSet(chainLen);
        boolean isFull;
        do {
            isFull = false;
            for (int i = start; i < max; i++) {
                rule = grammar.pRulesTable.getRule(i);
                j = 0;
                while (rule[j] != SymbolicTable.DOT) {
                    if (rule[j] == SymbolicTable.SEMICOLON) {
                        isFull |= firstTable[i -SymbolicTable.OFFSET].join(fs);
                        fs = new FirstSet(chainLen);
                    } else if (!fs.isFull()){
                        fs = FirstSet.Concatenate(chainLen, fs, First(rule[j]));
                    }
                }
            }
        } while (isFull);
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
        FirstSet next;
        while (!fl.isFull() && i < form.length) {
            next = First(form[i]);
            fl = FirstSet.Concatenate(chainLen, fl, next);
        }
        
        return fl;
    }
    
    FirstSet First(int symbol) 
            throws IncorrectSymbolCodeException, IncorrectSymbolTypeException {
        SymbolicTable.SymbolType t = grammar.pSymTable.getSymbolType(symbol);
        switch (t) {
            case NONTERMINAL:
                return firstTable[symbol - SymbolicTable.OFFSET];
            case TERMINAL:
                return FirstSetTerminal(symbol);
            default:
                throw new IncorrectSymbolTypeException(
                        String.format(error_msg_2, grammar.pSymTable.getSymbol(symbol))
                        );
            
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
