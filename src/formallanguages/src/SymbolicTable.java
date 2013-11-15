package formallanguages.src;

import formallanguages.exceptions.IncorrectSymbolCodeException;
import formallanguages.dfa.Cfr_dfa;
import static formallanguages.dfa.Cfr_dfa.END;
import static formallanguages.dfa.Cfr_dfa.LINK;
import static formallanguages.dfa.Cfr_dfa.NONTERMINAL;
import static formallanguages.dfa.Cfr_dfa.QUOTES;
import static formallanguages.dfa.Cfr_dfa.SEMANTIC;
import static formallanguages.dfa.Cfr_dfa.START;
import static formallanguages.dfa.Cfr_dfa.TERMINAL;

/**
 *
 * @author Ilya Shkuratov
 */
public class SymbolicTable {
    private int pNontermCount = 100;
    private int pTermCount = 50;
    private int pSemCount = 50;
    
    // Nonterminal index offset
    public static final int OFFSET = 12; // must be equal to number of reseved symbols
    public static final int DOT = 11; // must be equal to index of '.' in SymTable
    public static final int RBRACKET = 1; // must be equal to index of ')' in SymTable
    public static final int LBRACKET = 0; // must be equal to index of '(' in SymTable
    public static final int QUOTE = 2; // must be equal to index of '(' in SymTable
    // indexes for bounds arrays
    public static final int MIN = 0;
    public static final int CURR = 1;
    public static final int MAX = 2;
    // {minimal index, current insex, maximal index}
    private int[] pLbounds = {0, 11, 11};
    private int[] pNbounds = new int[3];
    private int[] pTbounds = new int[3];
    private int[] pSbounds = new int[3];
    // reserved symbols, nonterminal, terminal, semantic
    private String[] pSymTable;

    public SymbolicTable() {
        initBounds();
        initTables();
    }

    public SymbolicTable(int pNontermCount, int pTermCount, int pSemCount) {
        this.pNontermCount = pNontermCount;
        this.pTermCount = pTermCount;
        this.pSemCount = pSemCount;
        initBounds();
        initTables();
    }

    public int[] getpLbounds() {
        return pLbounds;
    }

    public int[] getpNbounds() {
        return pNbounds;
    }

    public int[] getpTbounds() {
        return pTbounds;
    }

    public int[] getpSbounds() {
        return pSbounds;
    }

    public int getpNontermMaxCount() {
        return pNontermCount;
    }

    public int getpTermMaxCount() {
        return pTermCount;
    }

    public int getpSemMaxCount() {
        return pSemCount;
    }
    
    public int getpNontermCount() {
        return pNbounds[CURR] - pNbounds[MIN];
    }

    public int getpTermCount() {
        return pTbounds[CURR] - pTbounds[MIN];
    }

    public int getpSemCount() {
        return pSbounds[CURR] - pSbounds[MIN];
    }
    
    public String getNonTerm(int rulecode) {
        return pSymTable[rulecode + OFFSET];
    }
    
    public String getSymbol(int code) {
        return pSymTable[code];
    }
    
    public SymbolType getSymbolType(int code) throws IncorrectSymbolCodeException {
        if (code >= pLbounds[MIN] && code <= pLbounds[MAX]) {
            return SymbolType.GRAMMAR;
        } else if (code >= pNbounds[MIN] && code <= pNbounds[MAX]) {
            return SymbolType.NONTERMINAL;
        } else if (code >= pTbounds[MIN] && code <= pTbounds[MAX]) {
            return SymbolType.TERMINAL;
        } else if (code >= pSbounds[MIN] && code <= pSbounds[MAX]) {
            return SymbolType.SEMANTIC;
        }
        throw new IncorrectSymbolCodeException();
    }
    
    public int insertSymTable(String value, Cfr_dfa type) {
        int[] indexes = null;
        switch (type) {
            case LINK: case START: case END: case QUOTES:
                indexes = pLbounds;
                break;
            case NONTERMINAL:
                indexes = pNbounds;
                break;
            case TERMINAL:
                indexes = pTbounds;
                break;
            case SEMANTIC:
                indexes = pSbounds;
                break;
            default:
                return -2;
        }
        
        for (int i = indexes[MIN]; i < indexes[CURR]; i++) {
            if ( pSymTable[i].equals(value) ) return i;
        }
        if (indexes[CURR] <= indexes[MAX]) {
            pSymTable[indexes[CURR] ++] = value;
            return indexes[CURR] - 1;
        }
        return -1;
    }
    
    static public enum SymbolType {
        GRAMMAR, NONTERMINAL, TERMINAL, SEMANTIC
    }
    
    private void initBounds() {
        pNbounds[MIN] = pNbounds[CURR] =  OFFSET;
        pNbounds[MAX] = OFFSET + pNontermCount - 1;
        pTbounds[MIN] = pTbounds[CURR] = pNbounds[MAX] + 1;
        pTbounds[MAX] = pNbounds[MAX] + pTermCount - 1;
        pSbounds[MIN] = pSbounds[CURR] = pSbounds[MAX] + 1;
        pSbounds[MAX] = pTbounds[MAX] + pSemCount - 1;
    }
    
    private void initTables() {
        pSymTable = new String[OFFSET + pNontermCount + pTermCount + pSemCount];
        pSymTable[0] = "(";
        pSymTable[1] = ")";
        pSymTable[2] = "'";
        pSymTable[3] = ";";
        pSymTable[4] = ",";
        pSymTable[5] = "!";
        pSymTable[6] = "[";
        pSymTable[7] = "]";
        pSymTable[8] = "#";
        pSymTable[9] = "@";
        pSymTable[10] = ":";
        pSymTable[11] = ".";
    }
    
}
