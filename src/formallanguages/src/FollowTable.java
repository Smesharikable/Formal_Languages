package formallanguages.src;

import formallanguages.exceptions.IncorrectSymbolTypeException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ilya Shkuratov
 */
class FollowTable {
    private CFGrammar grammar;
    private FirstSet[][] followTable;
    private int chainLen;

    FollowTable(CFGrammar grammar, int chainLen) {
        this.grammar = grammar;
        this.chainLen = chainLen;
        int count = grammar.pSymTable.getpNontermCount();
        followTable = new FirstSet[count][count];
        for (int i = 0; i < followTable.length; i++) {
            for (int j = 0; j < followTable.length; j++) {
                followTable[i][j] = new FirstSet(chainLen);
            }
        }
    }
    
    FirstSet Follow(int code) {
        SymbolicTable st = grammar.pSymTable;
        SymbolicTable.SymbolType type =  st.getSymbolType(code);
        if (type == SymbolicTable.SymbolType.NONTERMINAL) {
            return followTable[0][code - SymbolicTable.OFFSET];
        } else {
            return null;
        }
    }
    
    void init() {
        // Build phi_0
        RulesTable rt = grammar.pRulesTable;
        SymbolicTable st = grammar.pSymTable;
        grammar.initFirstTable(chainLen);
        FirstTable ft = grammar.firstTable;
        int[] bounds = st.getpNbounds();
        int min = bounds[SymbolicTable.MIN];
        int max = bounds[SymbolicTable.CURR];
        int nonTermCount = st.getpNontermCount();
        int code;
        int[] temp;
        int[] rule;
        int[][] rules;
        
        for (int i = 0; i < nonTermCount; i++) {
            rules = splitRule(rt.getRule(i));
            // go throw parts of rule
            for (int j = 0; j < rules.length; j ++) {
                rule = rules[j];
                for (int k = 0; k < rule.length; k++) {
                    code = rule[k];
                    if (min <= code && code < max) {
                        if (k == rule.length - 1) {
                            followTable[i][code - SymbolicTable.OFFSET].join(ft.FirstSetEmpty());
                        } else {
                            temp = Arrays.copyOfRange(rule, k + 2, rule.length);
                            try {
                                followTable[i][code - SymbolicTable.OFFSET].join(grammar.getFirst(temp));
                            } catch (IncorrectSymbolTypeException ex) {
                                Logger.getLogger(FollowTable.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            }
        }
        
        // interation phase
        FirstSet[][] new_follow;
        boolean isFull;
        do {
            isFull = false;
            new_follow = getCopy();
            for (int src = 0; src < nonTermCount; src++) {
                rules = splitRule(rt.getRule(src));
                for (int dest = 0; dest < nonTermCount; dest ++) {
                    // for every rule
                    for (int j = 0; j < rules.length; j++) {
                        rule = rules[j];
                        // find Nonterminal
                        isFull |= new_follow[src][dest].join(step(rule, dest, min, max));
                    }
                }
            }
            followTable = new_follow;
        } while (isFull);
    }
    
    private FirstSet step(int[] rule, int dest, int min, int max) {
        int code;
        int[] temp;
        FirstSet result = new FirstSet(chainLen);
        
        for (int k = 0; k < rule.length; k++) {
            code = rule[k];
            // if current symbol id Nonterminal
            if (min <= code && code < max) {
                if (k == rule.length - 1) {
                    result.join(followTable[code - SymbolicTable.OFFSET][dest]);
                } else {
                    temp = Arrays.copyOfRange(rule, k + 2, rule.length);
                    try {
                        result.join(
                                FirstSet.Concatenate(
                                    chainLen, followTable[code - SymbolicTable.OFFSET][dest], 
                                    grammar.getFirst(temp))
                                );
                    } catch (IncorrectSymbolTypeException ex) {
                        Logger.getLogger(FollowTable.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return result;
    }
    
    // split rule into parts with semicolon separator 
    private int[][] splitRule(int[] rule) {
        int[] bounds = new int[grammar.pRulesTable.getMaxRegLength() / 4];
        bounds[0] = -1;
        int count = 1, i;
        for (i = 0; rule[i] != SymbolicTable.DOT; i++) {
            if (rule[i] == SymbolicTable.SEMICOLON) {
                bounds[count ++] = i;
            }
        }
        bounds[count] = i;
        int[][] result = new int[count][];
        for (int j = 1; j < count + 1; j++) {
            result[j - 1] = Arrays.copyOfRange(rule, bounds[j - 1] + 1, bounds[j]);
        }
        return result;
    }
    
    private FirstSet[][] getCopy() {
        int count = grammar.pSymTable.getpNontermCount();
        FirstSet[][] result = new FirstSet[count][count];
        Integer[] temp;
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                result[i][j] = new FirstSet(chainLen);
                for (Integer[] integer : followTable[i][j]) {
                    temp = Arrays.copyOf(integer, chainLen);
                    result[i][j].add(temp);
                }
            }
        }
        return result;
    }
}
