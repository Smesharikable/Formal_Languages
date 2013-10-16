package formallanguages.src;

import formallanguages.exceptions.TooLongRuleException;

/**
 *
 * @author Shuratov Ilya
 */
public class RulesTable {
    private int pMaxRegLength = 50;
    private int pNontermCount = 100;
    private int[][] pRulesTable;

    public RulesTable() {
        initialize();
    };
    
    public RulesTable(int pNontermCount) {
        this.pNontermCount = pNontermCount;
        initialize();
    }
    
    public RulesTable(int pNontermCount, int pMaxRegLength) {
        this.pNontermCount = pNontermCount;
        this.pMaxRegLength = pMaxRegLength;
        initialize();
    }
    
    public int[] getRule(int code) {
        return pRulesTable[code];
    }    
    
    public int getCapacity() {
        return pNontermCount;
    }
    
    // TODO: add check for case that key has aleready used
    public int insert(int nonTermCode, int code, int position) throws TooLongRuleException {
        if (code != -2) {
            if (position == pMaxRegLength) {
                throw new TooLongRuleException(String.format(
                        "The regular expression for nonterminal '%d'  is too long", nonTermCode
                        ));
            }
            pRulesTable[nonTermCode][position ++] = code;
        }
        return position;
    }
    
    private void initialize() {
        pRulesTable = new int[pNontermCount][pMaxRegLength];
        for (int i = 0; i < pRulesTable.length; i++) {
            pRulesTable[i][0] = SymbolicTable.DOT;
        }
    }
}
