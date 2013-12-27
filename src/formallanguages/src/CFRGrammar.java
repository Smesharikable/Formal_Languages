package formallanguages.src;

import formallanguages.exceptions.TooLongRuleException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ListIterator;

/**
 *
 * @author Ilya Shkuratov
 */
public class CFRGrammar extends Grammar{
    
    public CFRGrammar(SymbolicTable pSymTable, RulesTable pRulesTable) {
        super(pSymTable, pRulesTable);
    }

    @Override
    public StringBuilder printGrammar(StringBuilder sb) {
        sb.append(Grammar.CFRG).append(eol);
        sb = super.printGrammar(sb);
        return sb;
    }
    
    public boolean regularize() throws TooLongRuleException {
        if (pNlvls == null) {
            TopologicalSort.sort(this);
        }
        ListIterator<NonterminalLevels.Level> liter = pNlvls.listIterator();
        liter.next(); // skip l0
        int[] bounds = pSymTable.getpNbounds();
        int min = bounds[SymbolicTable.MIN];
        int max = bounds[SymbolicTable.CURR];
        int length = pRulesTable.getMaxRegLength();
        boolean bracketFlag;
        int[] buf = new int[length];
        int[] rule;
        int position, i;
        
        while ( liter.hasNext() ) {
            for (int code : liter.next()) {
                position = 0;
                rule = pRulesTable.getRule(code);
                System.arraycopy(rule, 0, buf, 0, rule.length);
                if (buf[1] == SymbolicTable.DOT) 
                    bracketFlag = true;
                else
                    bracketFlag = false;
                i = 0;
                while (i < buf.length && buf[i] != SymbolicTable.DOT) {
                    if (buf[i] >= min && buf[i] < max) {
                        position = insertIntoRule(rule, position, buf[i], bracketFlag);
                    } else {
                        rule[position ++] = buf[i];
                    }
                    i ++;
                }
                rule[position ++] = SymbolicTable.DOT;
            }
        }
        
        return true;
    }
    
    public boolean regularizeAndLog(String filename) 
            throws TooLongRuleException, IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        if (pNlvls == null) {
            TopologicalSort.sort(this);
        }
        ListIterator<NonterminalLevels.Level> liter = pNlvls.listIterator();
        liter.next(); // skip l0
        int[] bounds = pSymTable.getpNbounds();
        int min = bounds[SymbolicTable.MIN];
        int max = bounds[SymbolicTable.CURR];
        int length = pRulesTable.getMaxRegLength();
        StringBuilder sb = new StringBuilder();
        boolean bracketFlag;
        int[] buf = new int[length];
        int[] rule;
        int position, i;
        
        while ( liter.hasNext() ) {
            for (int code : liter.next()) {
                position = 0;
                rule = pRulesTable.getRule(code);
                bw.write("Next Nonterminal processing\n");
                bw.write(pSymTable.getNonTermByRuleCode(code) + ": ");
                bw.write(getRuleAsString(code) + "\n");
                System.arraycopy(rule, 0, buf, 0, rule.length);
                i = 0;
                if (buf[1] == SymbolicTable.DOT) 
                    bracketFlag = true;
                else
                    bracketFlag = false;
                while (i < buf.length && buf[i] != SymbolicTable.DOT) {
                    if (buf[i] >= min && buf[i] < max) {
                        sb.append("Nonterminal for expanding: ");
                        sb.append(pSymTable.getSymbol(buf[i]));
                        sb.append('\n');
                        sb.append(pSymTable.getNonTermByRuleCode(code));
                        sb.append(": ");
                        for (int j = 0; j < position; j++) {
                            sb.append(pSymTable.getSymbol(rule[j]));
                        }
                        position = insertIntoRule(rule, position, buf[i], bracketFlag);
                        sb.append("_");
                        sb.append(getRuleAsString(buf[i] - SymbolicTable.OFFSET));
                        sb.append("_");
                        for (int j = i + 1; j < buf.length && buf[j] != SymbolicTable.DOT; j++) {
                            sb.append(pSymTable.getSymbol(buf[j]));
                        }
                        sb.append('\n');
                        bw.append(sb.toString());
                        sb.delete(0, sb.length());
                    } else {
                        rule[position ++] = buf[i];
                    }
                    i ++;
                }
                bw.append('\n');
                rule[position ++] = SymbolicTable.DOT;
            }
        }
        bw.newLine();
        bw.close();
        
        return true;
    }
    
    private int insertIntoRuleAllBrackets(int[] rule, int position, int code) 
            throws TooLongRuleException {
        int[] src = pRulesTable.getRule(code - SymbolicTable.OFFSET);
        int i = 0;
        try {
                rule[position ++] = SymbolicTable.LBRACKET;
                while (i < src.length && src[i] != SymbolicTable.DOT) {
                    rule[position ++] = src[i ++];
                }
                rule[position ++] = SymbolicTable.RBRACKET;
        } catch (IndexOutOfBoundsException ex) {
            throw new TooLongRuleException("Please, increase max length of rule in RulesTable");
        }
        return position;
    }
    
    private int insertIntoRule(int[] rule, int position, int code, boolean flag) 
            throws TooLongRuleException {
        int[] src = pRulesTable.getRule(code - SymbolicTable.OFFSET);
        int i = 0;
        try {
            if (src[3] == SymbolicTable.DOT && src[0] == SymbolicTable.QUOTE || flag) {
                while (i < src.length && src[i] != SymbolicTable.DOT) {
                    rule[position ++] = src[i ++];
                }
            } else {
                rule[position ++] = SymbolicTable.LBRACKET;
                while (i < src.length && src[i] != SymbolicTable.DOT) {
                    rule[position ++] = src[i ++];
                }
                rule[position ++] = SymbolicTable.RBRACKET;
            }
        } catch (IndexOutOfBoundsException ex) {
            throw new TooLongRuleException("Please, increase max length of rule in RulesTable");
        }
        return position;
    }
}
