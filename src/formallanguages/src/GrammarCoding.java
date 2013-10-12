package formallanguages.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import formallanguages.dfa.CfrRule_dfa;
import static formallanguages.dfa.CfrRule_dfa.END;
import static formallanguages.dfa.CfrRule_dfa.ERROR;
import static formallanguages.dfa.CfrRule_dfa.NONTERMINAL;
import static formallanguages.dfa.CfrRule_dfa.START;
import static formallanguages.dfa.CfrRule_dfa.WHITESPACES;
import formallanguages.dfa.Cfr_dfa;
import formallanguages.exceptions.ReadGrammarException;

/**
 *
 * @author Ilya Shkuratov
 * 
 */
public class GrammarCoding {
    private int pNontermCount = 100;
    private int pTermCount = 50;
    private int pSemCount = 50;
    private int pMaxRegLength = 50;
    // Nonterminal index offset
    private int pOffset = 12; // must be equal to number of reseved symbols
    private final int MIN = 0;
    private final int CURR = 1;
    private final int MAX = 2;
    // {minimal index, current insex, maximal index}
    private int[] pLbounds = {0, 11, 11};
    private int[] pNbounds = new int[3];
    private int[] pTbounds = new int[3];
    private int[] pSbounds = new int[3];
    // reserved symbols, nonterminal, terminal, semantic
    private String[] pSymTable;
    private int[][] pRulesTable;

    public GrammarCoding() {
        initBounds();
    }
    
    public GrammarCoding(int pNontermCount, int pTermCount, 
                        int pSemCount, int pMaxRegLength) {
        setParameters(pNontermCount, pTermCount, pSemCount, pMaxRegLength);
    }
    
    
    public void setParameters(int pNontermCount, int pTermCount, 
                        int pSemCount, int pMaxRegLength) {
        this.pNontermCount = pNontermCount;
        this.pTermCount = pTermCount;
        this.pSemCount = pSemCount;
        this.pMaxRegLength = pMaxRegLength;
        initBounds();
    }
    
    public void Coding(String filename) throws FileNotFoundException, IOException, ReadGrammarException {
        FileInputStream fis = new FileInputStream(filename);
        BufferedReader br = new BufferedReader(
                new InputStreamReader(fis, Charset.forName("UTF-8"))
                );
        
        char[] buf;
        int position;
        int code;
        String line;
        CfrRule_dfa crd;    
        StringBuilder sb = new StringBuilder(20);
        initTables();
                
        while ( (line = br.readLine()) != null ) {
            
            position = 0;
            sb.delete(0, sb.length());
            buf = line.toCharArray();
            crd = CfrRule_dfa.START;
//            crd = crd.step(buf[position]);
            
            position = readLeftPart(crd, buf, sb);
            
            if (crd == CfrRule_dfa.ERROR) break;
            if (position == buf.length - 1) {
                terminate("Empty rule is not allowed.");
//                break;
            }
            
            code = insertSymTable(sb.toString(), Cfr_dfa.NONTERMINAL);
            
            if (code == -1) {
                terminate("Symbolic table is full, increase number of Nonternminals");
//                break;
            }
            
            // read right part of the rule
            readRightPart(buf, sb, position, code - pOffset);
        }
        
        br.close();
    }
    
    public int readLeftPart(CfrRule_dfa crd, char[] buf, StringBuilder sb) throws ReadGrammarException {
        int position = -1;
        
        while ( position < buf.length - 1 && 
                crd != CfrRule_dfa.ERROR && crd != CfrRule_dfa.END) {
            switch (crd) {
                case NONTERMINAL:
                    sb.append(buf[position]);
                case START: case WHITESPACES:
                    position ++;
                    crd = crd.step(buf[position]);
                    break;
                case ERROR:
                    terminate(null);
                    break;
                case END:
                    break;
            }
        }
        
        return position;
    }
    
    // TODO: fix this
    public void readRightPart(char[] buf, StringBuilder sb, int position, int nonTermCode) throws ReadGrammarException {
        Cfr_dfa curState = Cfr_dfa.START,
                prevState = Cfr_dfa.START;
        char c;
        int code;
        int current = 0;
        //int[] rule = pRulesTable[nonTermCode];
        sb.delete(0, sb.length());
        
        while (Character.isSpaceChar(buf[position])) {
            position ++;
        }
        prevState = curState = curState.step(buf[position], position);
        sb.append(buf[position ++]);
        
        while (curState != Cfr_dfa.ERROR && curState != Cfr_dfa.END && position < buf.length) {
            c = buf[position];
            
            curState = curState.step(c, position ++);
            if (curState != prevState || prevState == Cfr_dfa.START) {
                code = insertSymTable(sb.toString(), prevState);
                sb.delete(0, sb.length());
                if (code == -1) {
                    terminate("Symbolic table is full, increase number of Nonternminals");
//                    return;
                }
                current = insertToRuleTable(nonTermCode, code, current);
            }
            sb.append(c);
            
            prevState = curState;
        }
        
        insertToRuleTable(nonTermCode, insertSymTable(sb.toString(), curState), current);
        
        if (curState != Cfr_dfa.END && position == buf.length) {
            terminate(
                    String.format(
                    "Please, check regular expression for nonterminal '%s'.", pSymTable[nonTermCode]
                    ));
        }
    }
    
    
    private void initTables() {
        pSymTable = new String[pOffset + pNontermCount + pTermCount + pSemCount];
        pRulesTable = new int[pNontermCount][pMaxRegLength];
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
    
    private void initBounds() {
        pNbounds[MIN] = pNbounds[CURR] =  pOffset;
        pNbounds[MAX] = pOffset + pNontermCount - 1;
        pTbounds[MIN] = pTbounds[CURR] = pNbounds[MAX] + 1;
        pTbounds[MAX] = pNbounds[MAX] + pTermCount - 1;
        pSbounds[MIN] = pSbounds[CURR] = pSbounds[MAX] + 1;
        pSbounds[MAX] = pTbounds[MAX] + pSemCount - 1;
    }
    
    private int insertSymTable(String value, Cfr_dfa type) {
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
    
    // TODO: add check for case that key has aleready used
    private int insertToRuleTable(int nonTermCode, int code, int current) throws ReadGrammarException {
        if (code != -2) {
            if (current == pMaxRegLength) {
                terminate(String.format(
                        "The regular expression for nonterminal '%s'  is too long", pSymTable[nonTermCode]
                        ));
//                        return;
            }
            pRulesTable[nonTermCode][current ++] = code;
        }
        return current;
    }
    
    private void terminate(String errmsg) throws ReadGrammarException {
//        System.err.println(errmsg);
        pSymTable = null;
        pRulesTable = null;
        throw new ReadGrammarException(errmsg);
    }
    
    private enum termType {
        TERMINAL, NONTERMINAL, SEMANTIC
    }
    
}
