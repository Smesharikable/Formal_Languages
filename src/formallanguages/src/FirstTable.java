package formallanguages.src;

import formallanguages.dfa.Cf_chain_dfa;
import formallanguages.exceptions.IncorrectSymbolCodeException;
import formallanguages.exceptions.IncorrectSymbolTypeException;
import static formallanguages.src.SymbolicTable.SymbolType.GRAMMAR;
import static formallanguages.src.SymbolicTable.SymbolType.NONTERMINAL;
import static formallanguages.src.SymbolicTable.SymbolType.TERMINAL;
import java.util.Arrays;

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
        int count = grammar.pSymTable.getpNontermCount();
        firstTable = new FirstSet[count];
        for (int i = 0; i < firstTable.length; i++) {
            firstTable[i] = new FirstSet(chainLen);
        }
    }

    int getChainLen() {
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
    FirstSet First(int[] form) throws IncorrectSymbolTypeException {
        Cf_chain_dfa dfa = Cf_chain_dfa.START;
        SymbolicTable st = grammar.pSymTable;
        int position = 0;
        
        // Create extended form with DOT at the end,
        // so Cf_chain_dfa will process it properly.
        int[] ext_form = Arrays.copyOf(form, form.length + 1);
        ext_form[form.length] = SymbolicTable.DOT;
        // find terminal prefix
        while (position < ext_form.length && dfa != Cf_chain_dfa.ERROR) {
            dfa = dfa.step(st.getSymbolType(ext_form[position]), ext_form[position]);
            position ++;
        }
        if (dfa == Cf_chain_dfa.ERROR) {
            throw new IncorrectSymbolTypeException("Form may contains only Terminals, Nonterminal and Commas");
        }
        
        FirstSet fl = new FirstSet(chainLen);
        if (Cf_chain_dfa.isDetect()) {
            fl.add(Cf_chain_dfa.getChain());
            position = Cf_chain_dfa.getCount();
        } else {
            position = 0;
        }
        
        if (position == chainLen || position == form.length) return fl;
        
        // chains accumulation
        fl = FirstSetTerminal(SymbolicTable.EMPTY);
        do {
            if (form[position] != SymbolicTable.COMMA) {
                fl = FirstSet.Concatenate(chainLen, fl, First(form[position]));
            }
            position ++;
        } while (!fl.isFull() && position < form.length);
        
        return fl;
    }
    
    FirstSet First(int symbol) {
        SymbolicTable.SymbolType t = grammar.pSymTable.getSymbolType(symbol);
        switch (t) {
            case NONTERMINAL:
                return firstTable[symbol - SymbolicTable.OFFSET];
            case TERMINAL:
                return FirstSetTerminal(symbol);
            case GRAMMAR:
                if (symbol == SymbolicTable.EMPTY) {
                    return FirstSetTerminal(symbol);
                }
            default:
                return null;
        }
    }
    
    /*
     * fill first table
     */
    void init() {
        // build F_0
        int[] bounds = grammar.pSymTable.getpNbounds();
        int start = bounds[SymbolicTable.MIN] - SymbolicTable.OFFSET;
        int max = bounds[SymbolicTable.CURR] - SymbolicTable.OFFSET;
        SymbolicTable st = grammar.pSymTable;
        Cf_chain_dfa.init(chainLen);
        Cf_chain_dfa dfa;
        Integer[] chain;
        int[] rule;
        int count = 0;
        
        
        for (int i = start; i < max; i++) {
            rule = grammar.pRulesTable.getRule(i);
            dfa = Cf_chain_dfa.START;
            dfa = dfa.step(st.getSymbolType(rule[count]), rule[count]);
            count ++;
            while (dfa != Cf_chain_dfa.END && dfa != Cf_chain_dfa.ERROR) {
                switch(dfa) {
                    case START:
                        if (Cf_chain_dfa.isDetect()) {
                            chain = Cf_chain_dfa.getChain();
                            firstTable[i].add(chain);
                        }
                    case TERMINAL: case GRAMMAR: case EMPTY:
                        dfa = dfa.step(st.getSymbolType(rule[count]), rule[count]);
                        count ++;
                        break;
                    default:
                        System.err.println(String.format("Wrong symbol in rule for Nonterminal: %s\n", 
                                st.getSymbol(count)));
                }
            }
            if (dfa == Cf_chain_dfa.ERROR) {
                System.err.println(
                        String.format("Unexpected symbol in rule for Nonterminal: %s", 
                            st.getNonTermByRuleCode(i))
                        );
            } else {
                if (Cf_chain_dfa.isDetect()) {
                    chain = Cf_chain_dfa.getChain();
                    firstTable[i].add(chain);
                }
            }
            count = 0;
        }
        
        // Build F_i
        int j;
        FirstSet fs;
        FirstSet[] ft_new = this.getCopy();
        boolean isFull;
        do {
            isFull = false;
            for (int i = start; i < max; i++) {
                rule = grammar.pRulesTable.getRule(i);
                j = 0;
                fs = First(rule[j++]);
                while (rule[j] != SymbolicTable.DOT) {
                    if (rule[j] == SymbolicTable.SEMICOLON) {
                        isFull |= ft_new[i].join(fs);
                        fs = First(rule[++ j]);
                    } else if (!fs.isFull() && rule[j] != SymbolicTable.COMMA){
                        fs = FirstSet.Concatenate(chainLen, fs, First(rule[j]));
                    }
                    j ++;
                }
                isFull |= ft_new[i].join(fs);
            }
            firstTable = ft_new;
            ft_new = getCopy();
        } while (isFull);
    }
    
    
    FirstSet FirstSetEmpty() {
        return FirstSetTerminal(SymbolicTable.EMPTY);
    }
    
    private FirstSet FirstSetTerminal(int symbol) {
        FirstSet fs = new FirstSet(chainLen);
        Integer[] chain = new Integer[chainLen];
        chain[0] = symbol;
        for (int i = 1; i < chain.length; i++) {
            chain[i] = 0;
        }
        fs.add(chain);
        return fs;
    }
    
    
    private FirstSet[] getCopy() {
        FirstSet[] result = new FirstSet[firstTable.length];
        Integer[] temp;
        for (int i = 0; i < firstTable.length; i++) {
            result[i] = new FirstSet(chainLen);
            for (Integer[] integer : firstTable[i]) {
                temp = Arrays.copyOf(integer, chainLen);
                result[i].add(temp);
            }
        }
        return result;
    }
}
