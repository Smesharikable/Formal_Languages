package formallanguages.dfa;

import formallanguages.src.SymbolicTable;

/**
 * DFA for extracting terminal prefixes from CFG rule
 * @author Shkuratov Ilya
 */
public enum Cf_chain_dfa {
    
    /**
     * initial and final state of dfa
     */
    START {

        @Override
        public Cf_chain_dfa step(SymbolicTable.SymbolType type, int code) {
            detect = false;
            switch (type) {
                case NONTERMINAL:
                    return GRAMMAR;
                case TERMINAL:
                    chain = new int[chainSize];
                    chain[0] = code;
                    count = 1;
                    if (count == chainSize) {
                        detect = true;
                        return GRAMMAR;
                    }
                    return TERMINAL;
                case GRAMMAR:
                    if (code == SymbolicTable.EMPTY) {
                        chain[0] = code;
                        count = 1;
                        return EMPTY;
                    }
                default:
                    System.err.println("First symbol of the rule must be TERMINAL or NONTERMINAL");
                    return ERROR;
            }
        }
        
    },
    
    TERMINAL {

        @Override
        public Cf_chain_dfa step(SymbolicTable.SymbolType type, int code) {
            switch (type) {
                case GRAMMAR:
                    if (code == SymbolicTable.SEMICOLON) {
                        detect = true;
                        return START;
                    } else if (code == SymbolicTable.DOT) {
                        detect = true;
                        return END;
                    } else if (code == SymbolicTable.COMMA) {
                        return TERMINAL;
                    } else return ERROR;
                case NONTERMINAL:
                    return GRAMMAR;
                case TERMINAL:
                    chain[count ++] = code;
                    if (count == chainSize) {
                        detect = true;
                        return GRAMMAR;
                    }
                    return TERMINAL;
                default:
                    return ERROR;
            }
        }
        
    },
    
    /**
     * state which skip all symbols till GRAMMAR symbol appear
     */
    GRAMMAR {

        @Override
        public Cf_chain_dfa step(SymbolicTable.SymbolType type, int code) {
            switch (type) {
                case GRAMMAR:
                    if (code == SymbolicTable.SEMICOLON) {
                        return START;
                    } else if (code == SymbolicTable.DOT) {
                        return END;
                    } else if (code == SymbolicTable.COMMA) {
                        return GRAMMAR;
                    } else
                        return ERROR;
                case NONTERMINAL:
                    return GRAMMAR;
                case TERMINAL:
                    return GRAMMAR;
                default:
                    return ERROR;
            }
        }
        
    },
    
    /**
     * State for "empty" symbol
     */
    EMPTY {

        @Override
        public Cf_chain_dfa step(SymbolicTable.SymbolType type, int code) {
            switch (type) {
                case GRAMMAR:
                    if (code == SymbolicTable.DOT) {
                        detect = true;
                        return END;
                    }
                    if (code == SymbolicTable.SEMICOLON) {
                        detect = true;
                        return START;
                    }
                default:
                    System.err.println("Empty symbol must be first and single in rule.");
                    return ERROR;
            }
        }
        
    }, 
    
    /**
     * final state of dfa
     */
    END,
    
    ERROR;
    
    private static boolean detect = false;
    private static int chainSize = 1;
    private static int[] chain; // result of rule processing, terminal prefix
    private static int count;
    
    /**
     *
     * @param type - current symbol type
     * @param code - code iof symbol
     * @return next state of dfa
     */
    public Cf_chain_dfa step(SymbolicTable.SymbolType type, int code) {
        return null;
    }
    
    public static void init(int k) {
        chainSize = k;
    }

    public static boolean isDetect() {
        return detect;
    }
    
    public static Integer[] getChain() {
        Integer[] result = new Integer[chainSize];
        for (int i = 0; i < count; i++) {
            result[i] = chain[i];
        }
        return result;
    }
    
    public static int getCount() {
        return count;
    }
}
