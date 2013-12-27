package formallanguages.src;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import formallanguages.dfa.CfrRule_dfa;
import static formallanguages.dfa.CfrRule_dfa.END;
import static formallanguages.dfa.CfrRule_dfa.ERROR;
import static formallanguages.dfa.CfrRule_dfa.NONTERMINAL;
import static formallanguages.dfa.CfrRule_dfa.START;
import static formallanguages.dfa.CfrRule_dfa.WHITESPACES;
import formallanguages.dfa.Cfr_dfa;
import static formallanguages.dfa.Cfr_dfa.LINK;
import formallanguages.exceptions.IncorrectSymbolCodeException;
import formallanguages.exceptions.ReadGrammarException;
import formallanguages.exceptions.TooLongRuleException;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileReader;

/**
 *
 * @author Ilya Shkuratov
 * 
 * TODO: implement verificator for CFG
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
    
    public static Grammar CodingFromFile(File grammarFile) 
            throws FileNotFoundException, IOException, 
            ReadGrammarException, TooLongRuleException, 
            IncorrectSymbolCodeException {
        BufferedReader input = new BufferedReader(new FileReader(grammarFile));
        return Coding(input);
    }
    
    public static Grammar CodingFromFile(String grammarFile) 
            throws FileNotFoundException, IOException, 
            ReadGrammarException, TooLongRuleException, 
            IncorrectSymbolCodeException {
        BufferedReader input = new BufferedReader(new FileReader(grammarFile));
        return Coding(input);
    }
    
    public static Grammar CodingFromFile(FileDescriptor grammarFile) 
            throws FileNotFoundException, IOException, 
            ReadGrammarException, TooLongRuleException, 
            IncorrectSymbolCodeException {
        BufferedReader input = new BufferedReader(new FileReader(grammarFile));
        return Coding(input);
    }
    
    /**
     * Read grammar from specified file and code 
     * nonterminals, terminals and rules of this grammar.
     * 
     * @param input - buffered reader with input grammar
     * @return 
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ReadGrammarException
     * @throws TooLongRuleException
     * @throws IncorrectSymbolCodeException  
     */
    public static Grammar Coding(BufferedReader input) 
            throws IOException, ReadGrammarException, 
            TooLongRuleException, IncorrectSymbolCodeException {
        char[] buf;
        int position;
        int code;
        String line;
        CfrRule_dfa crd;    
        StringBuilder sb = new StringBuilder(20);
        pSymTable = new SymbolicTable(pNontermCount, pTermCount, pSemCount);
        pRulesTable = new RulesTable(pNontermCount, pMaxRegLength);
        String type = input.readLine();
                
        while ( (line = input.readLine()) != null ) {
            
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
        
        input.close();
        if (type.equals(Grammar.CFRG)) {
            return new CFRGrammar(pSymTable, pRulesTable);
        } else if (type.equals(Grammar.CFG)) {
            return new CFGrammar(pSymTable, pRulesTable);
        } else return null;
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
            throws ReadGrammarException, TooLongRuleException, IncorrectSymbolCodeException {
        Cfr_dfa curState = Cfr_dfa.START,
                prevState = Cfr_dfa.START;
        char c;
        int code;
        int current = 0;
        sb.delete(0, sb.length());
        
        while (Character.isSpaceChar(buf[position])) {
            position ++;
        }
//        prevState = curState = curState.step(buf[position], position);
//        sb.append(buf[position ++]);
//        
//        while (curState != Cfr_dfa.ERROR && curState != Cfr_dfa.END && position < buf.length) {
//            c = buf[position];
//            
//            curState = curState.step(c, position ++);
//            if (curState != prevState || prevState == Cfr_dfa.START) {
//                code = pSymTable.insertSymTable(sb.toString(), prevState);
//                sb.delete(0, sb.length());
//                if (code == -1) {
//                    terminate("Symbolic table is full, increase number of Nonternminals");
//                }
//                if (code != SymbolicTable.QUOTE) {
//                    current = pRulesTable.insert(nonTermCode, code, current);
//                    sb.append(c);
//                }
//            } else {
//                sb.append(c);
//            }
//            
//            prevState = curState;
//        }
        
        
        boolean inter = false;
        while (curState != Cfr_dfa.ERROR && curState != Cfr_dfa.END && position < buf.length) {
            // do step and read current symbol
            if (sb.length() > 0) inter = true;
            
            c = buf[position];
            sb.append(c);
            curState = curState.step(c, position++);
            
            if (prevState == Cfr_dfa.START) {
                if (inter) {
                    code = pSymTable.insertSymTable(sb.substring(0, sb.length() - 1), prevState);
                    current = pRulesTable.insert(nonTermCode, code, current);
                    sb.delete(0, sb.length() - 1);
                }
                switch (curState) {
                    // match bracket or empty symbol
                    case START: case LINK:
                        code = pSymTable.insertSymTable(sb.toString(), curState);
                        sb.delete(0, sb.length());
                        current = pRulesTable.insert(nonTermCode, code, current);
                        break;
                    // start of terminal, delete quote
                    case QUOTES:
                        sb.deleteCharAt(sb.length() - 1);
                        break;
                    // start of nonterminal or semantic, contunue matching
                    case NONTERMINAL:
                        break;
                    case SEMANTIC:
                        sb.deleteCharAt(sb.length() - 1);
                        break;
                    default:
                        break;
                }
            } else if (curState != prevState) {
                switch (prevState) {
                    case QUOTES:
                        // math empty symbol
                        if (curState == Cfr_dfa.LINK) {
                            sb.delete(0, sb.length());
                            current = pRulesTable.insert(nonTermCode, SymbolicTable.EMPTY, current);
                        } else if (curState == Cfr_dfa.TERMINAL) {
                            // quote has been deleted already, continue matching Terminal
//                            sb.deleteCharAt(sb.length() - 1);
                        }
                        break;
                    case TERMINAL:
                        // match terminal
                        if (curState == Cfr_dfa.LINK) {
                            sb.deleteCharAt(sb.length() - 1);
                            code = pSymTable.insertSymTable(sb.toString(), prevState);
                            current = pRulesTable.insert(nonTermCode, code, current);
                            sb.delete(0, sb.length());
                        }
                        // if Terminal then continue matching
                        break;
                    case NONTERMINAL: case SEMANTIC:
                        // match Nonterminal
                        if (curState == Cfr_dfa.START) {
                            code = pSymTable.insertSymTable(sb.substring(0, sb.length() - 1), prevState);
                            current = pRulesTable.insert(nonTermCode, code, current);
                            sb.delete(0, sb.length() - 1);
                        } else if (curState == Cfr_dfa.END) {
                            if (sb.length() > 1) {
                                code = pSymTable.insertSymTable(sb.substring(0, sb.length() - 1), prevState);
                                current = pRulesTable.insert(nonTermCode, code, current);
                            }
                            sb.delete(0, sb.length());
                            // insert DOT
                            current = pRulesTable.insert(nonTermCode, SymbolicTable.DOT, current);
                        // match bracket
                        } else if (curState == Cfr_dfa.LINK) {
                            // insert Nonterminal, Semantic ot Link
                            code = pSymTable.insertSymTable(sb.substring(0, sb.length() - 1), prevState);
                            current = pRulesTable.insert(nonTermCode, code, current);
                            sb.delete(0, sb.length() - 1);
                            // insert Bracket
                            code = pSymTable.insertSymTable(sb.toString(), curState);
                            current = pRulesTable.insert(nonTermCode, code, current);
                            sb.delete(0, sb.length());
                        }
                        // if Nonterminal then continue matching 
                        // if Semantic then continue matching
                        break;
                    case LINK:
                        if (curState == Cfr_dfa.START) {
                        } else if (curState == Cfr_dfa.END) {
                            if (sb.length() > 1) {
                                code = pSymTable.insertSymTable(sb.substring(0, sb.length() - 1), prevState);
                                current = pRulesTable.insert(nonTermCode, code, current);
                            }
                            sb.delete(0, sb.length());
                            // insert DOT
                            current = pRulesTable.insert(nonTermCode, SymbolicTable.DOT, current);
                        } 
                        break;
                    default:
                        break;
                } 
            } if (prevState == Cfr_dfa.LINK && curState == Cfr_dfa.LINK) {
                // match bracket
                if (sb.length() > 1) {
                    code = pSymTable.insertSymTable(sb.substring(0, sb.length() - 1), prevState);
                    current = pRulesTable.insert(nonTermCode, code, current);
                    sb.delete(0, sb.length() - 1);
                }
                // insert Bracket
                code = pSymTable.insertSymTable(sb.toString(), curState);
                current = pRulesTable.insert(nonTermCode, code, current);
                sb.delete(0, sb.length());
            }
            prevState = curState;
            inter = false;
        }
        
//        pRulesTable.insert(nonTermCode, pSymTable.insertSymTable(sb.toString(), curState), current);
//        pRulesTable.insert(nonTermCode, pSymTable.insertSymTable(sb.toString(), curState), current);
        
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
