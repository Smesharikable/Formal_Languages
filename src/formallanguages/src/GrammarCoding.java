package formallanguages.src;

import java.io.BufferedReader;
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
import formallanguages.exceptions.TooLongRuleException;

/**
 *
 * @author Ilya Shkuratov
 * 
 */
public class GrammarCoding {
    private static SymbolicTable pSymTable;
    private static RulesTable pRulesTable;
    
    private static int pNontermCount = 100;
    private static int pTermCount = 50;
    private static int pSemCount = 50;
    private static int pMaxRegLength = 150;
    
    
    public static void setNontermCount(int NontermCount) {
        pNontermCount = NontermCount;
    }
    
    public static void setTermCount(int TermCount) {
        pTermCount = TermCount;
    }
    
    public static void setSemCount(int SemCount) {
        pSemCount = SemCount;
    }
    
    public static void setMaxRegLength(int MaxRegLength) {
        pMaxRegLength = MaxRegLength;
    }
    
    /**
     * Read grammar from specified file and code 
     * nonterminals, terminals and rules of this grammar.
     * 
     * @param filename - file with grammar
     * @return 
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ReadGrammarException
     * @throws TooLongRuleException  
     */
    public static Grammar Coding(String filename) 
            throws FileNotFoundException, IOException, ReadGrammarException, TooLongRuleException {
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
        pSymTable = new SymbolicTable(pNontermCount, pTermCount, pSemCount);
        pRulesTable = new RulesTable(pNontermCount, pMaxRegLength);
                
        while ( (line = br.readLine()) != null ) {
            
            sb.delete(0, sb.length());
            buf = line.toCharArray();
            crd = CfrRule_dfa.START;
            
            position = readLeftPart(crd, buf, sb);
            
            if (crd == CfrRule_dfa.ERROR) break;
            if (position == buf.length - 1) {
                terminate("Empty rule is not allowed.");
            }
            
            code = pSymTable.insertSymTable(sb.toString(), Cfr_dfa.NONTERMINAL);
            
            if (code == -1) {
                terminate("Symbolic table is full, increase number of Nonternminals");
            }
            
            // read right part of the rule
            readRightPart(buf, sb, position, code - SymbolicTable.OFFSET);
        }
        
        br.close();
        return new CFRGrammar(pSymTable, pRulesTable);
    }
    
    
    private static int readLeftPart(CfrRule_dfa crd, char[] buf, StringBuilder sb) throws ReadGrammarException {
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
    
    private static void readRightPart(char[] buf, StringBuilder sb, int position, int nonTermCode) 
            throws ReadGrammarException, TooLongRuleException {
        Cfr_dfa curState = Cfr_dfa.START,
                prevState = Cfr_dfa.START;
        char c;
        int code;
        int current = 0;
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
                code = pSymTable.insertSymTable(sb.toString(), prevState);
                sb.delete(0, sb.length());
                if (code == -1) {
                    terminate("Symbolic table is full, increase number of Nonternminals");
                }
                current = pRulesTable.insert(nonTermCode, code, current);
            }
            sb.append(c);
            
            prevState = curState;
        }
        
        pRulesTable.insert(nonTermCode, pSymTable.insertSymTable(sb.toString(), curState), current);
        
        if (curState != Cfr_dfa.END && position == buf.length) {
            terminate(
                    String.format(
                    "Please, check regular expression for nonterminal '%s'.", pSymTable.getSymbol(nonTermCode)
                    ));
        }
    }
    
    
    private static void terminate(String errmsg) throws ReadGrammarException {
        pSymTable = null;
        pRulesTable = null;
        throw new ReadGrammarException(errmsg);
    }
    
}
