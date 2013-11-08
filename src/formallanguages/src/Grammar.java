package formallanguages.src;

import formallanguages.exceptions.TooLongRuleException;
import formallanguages.src.NonterminalLevels.Level;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ListIterator;

/**
 *
 * @author Ilya Shkuratov
 */
public class Grammar {
    private SymbolicTable pSymTable;
    private RulesTable pRulesTable;
    private NonterminalLevels pNlvls;
    private int[][] pRelationTable;

    
    public Grammar(SymbolicTable pSymTable, RulesTable pRulesTable) {
        this.pSymTable = pSymTable;
        this.pRulesTable = pRulesTable;
    }
    
    public RulesTable getpRulesTable() {
        return pRulesTable;
    }

    public SymbolicTable getpSymTable() {
        return pSymTable;
    }
    
    public void printRules() {
        int[] bounds = pSymTable.getpNbounds();
        int count = bounds[SymbolicTable.CURR] - bounds[SymbolicTable.MIN];
        int[] rule;
        int j;
        
        System.out.println("Grammar rules table.");
        for (int i = 0; i < count; i++) {
            rule = pRulesTable.getRule(i);
            j = 0;
            System.out.print(pSymTable.getNonTerm(i) + ": ");
            while (rule[j] != SymbolicTable.DOT) {
                System.out.print(pSymTable.getSymbol(rule[j]));
                j ++;
            }
            System.out.println(pSymTable.getSymbol(rule[j]));
        }
        System.out.println();
    }
    
    /**
     *
     * @param code - Nonterminal grammar code
     * @return String representation of grammar rule
     */
    public String getRuleAsString(int code) {
        StringBuilder sb = new StringBuilder();
        int[] rule;
        int j = 0;
        
        //code -= SymbolicTable.OFFSET;
        rule = pRulesTable.getRule(code);
        //sb.append(pSymTable.getNonTerm(code)).append(": ");
        while (rule[j] != SymbolicTable.DOT) {
            sb.append(pSymTable.getSymbol(rule[j]));
            j ++;
        }
        //sb.append(pSymTable.getSymbol(rule[j]));
        return sb.toString();
    }
    
    public void printSortedRules() {
        int[] rule;
        
        ListIterator<Level> iter = pNlvls.listIterator(pNlvls.size());
        int index;
        
        System.out.println("Sorted Grammar rules table.");
        while ( iter.hasPrevious() ) {
            for (int code : iter.previous()) {
                System.out.print(pSymTable.getNonTerm(code) + ": ");
                index = 0;
                rule = pRulesTable.getRule(code);
                while (rule[index] != SymbolicTable.DOT) {
                    System.out.print(pSymTable.getSymbol(rule[index]));
                    index ++;
                }
                System.out.println(pSymTable.getSymbol(rule[index]));
            }
        }
        System.out.println();
    }
    
    public void printRelationTable() {
        if (pRelationTable == null) return;
        System.out.println("Nonterminal relation table.");
        for (int i = 0; i < pRelationTable.length; i++) {
            int[] is = pRelationTable[i];
            System.out.print(pSymTable.getNonTerm(i) + ": ");
            for (int j = 0; j < is.length; j++) {
                if (is[j] != -1)
                    System.out.print(pSymTable.getNonTerm(is[j]) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
    
    public boolean regularize() throws TooLongRuleException {
        if (pNlvls == null) {
            TopologicalSort.sort(this);
        }
        ListIterator<Level> liter = pNlvls.listIterator();
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
    
    public boolean regularizeAndLog(String filename) throws TooLongRuleException, IOException {
        BufferedWriter br = new BufferedWriter(new FileWriter(filename));
        if (pNlvls == null) {
            TopologicalSort.sort(this);
        }
        ListIterator<Level> liter = pNlvls.listIterator();
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
                br.write("Next Nonterminal processing\n");
                br.write(pSymTable.getNonTerm(code) + ": ");
                br.write(getRuleAsString(code) + "\n");
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
                        sb.append(pSymTable.getNonTerm(code));
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
                        br.append(sb.toString());
                        sb.delete(0, sb.length());
                    } else {
                        rule[position ++] = buf[i];
                    }
                    i ++;
                }
                br.append('\n');
                rule[position ++] = SymbolicTable.DOT;
            }
        }
        br.newLine();
        br.close();
        
        return true;
    }
    
    void setNlvls(NonterminalLevels Nlvls) {
        this.pNlvls = Nlvls;
    }
    
    void setRelationTable(int[][] pRelationTable) {
        this.pRelationTable = pRelationTable;
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
