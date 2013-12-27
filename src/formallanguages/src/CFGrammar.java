package formallanguages.src;

import formallanguages.exceptions.IncorrectSymbolTypeException;

/**
 *
 * @author Ilya Shkuratov
 */
public class CFGrammar extends Grammar {
    protected FirstTable firstTable;
    protected FollowTable followTable;
    private int k = 1;
    
    public CFGrammar(SymbolicTable pSymTable, RulesTable pRulesTable) {
        super(pSymTable, pRulesTable);
    }

    @Override
    public StringBuilder printGrammar(StringBuilder sb) {
        sb.append(Grammar.CFG).append(eol);
        sb = super.printGrammar(sb);
        return sb;
    }
    
    public void initFirstTable(int k) {
        if (firstTable == null || this.k != k) {
            this.k = k;
            firstTable = new FirstTable(this, k);
            firstTable.init();
        }
    }
    
    public FirstSet getFirst(int[] form) throws IncorrectSymbolTypeException {
        if (firstTable == null) {
            initFirstTable(k);
            return firstTable.First(form);
        }
        else {
            return firstTable.First(form);
        }
    }
    
    public FirstSet getFirst(int code) {
        if (firstTable == null) {
            initFirstTable(k);
            return firstTable.First(code);
        }
        else {
            return firstTable.First(code);
        }
    }
    
    public FirstSet getFollow(String nonterminal, int k) throws IncorrectSymbolTypeException {
        int code = pSymTable.getSymbolCode(nonterminal);
        return getFollow(code, k);
    }
    
    public FirstSet getFollow(int nontermCode, int k) throws IncorrectSymbolTypeException {
        if (followTable == null || this.k != k) {
            followTable = new FollowTable(this, k);
            followTable.init();
        }
        FirstSet result = followTable.Follow(nontermCode);
        if (result == null) {
            throw new IncorrectSymbolTypeException("Imput parameter must be Nonterminal");
        } else {
            return result;
        }
    }
    
    public String getFirstAsString(FirstSet fs) {
        StringBuilder sb = new StringBuilder();
        for (Integer[] form : fs) {
            for (int i = 0; i < form.length; i++) {
                int code = form[i];
                if (code != 0) {
                    sb.append(pSymTable.getTerminalWithoutBrackets(code));
                }
            }
            sb.append(", ");
        }
        sb.setCharAt(sb.length() - 2, '.');
        return sb.toString();
    }
}
