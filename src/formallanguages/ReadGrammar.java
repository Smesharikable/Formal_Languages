package formallanguages;

import formallanguages.exceptions.IncorrectSymbolCodeException;
import formallanguages.exceptions.IncorrectSymbolTypeException;
import formallanguages.exceptions.ReadGrammarException;
import formallanguages.exceptions.TooLongRuleException;
import formallanguages.exceptions.UnexpectedSymbolException;
import formallanguages.src.CFGrammar;
import formallanguages.src.CFRGrammar;
import formallanguages.src.FirstSet;
import formallanguages.src.Grammar;
import formallanguages.src.GrammarCoding;
import formallanguages.src.NonterminalLevels;
import formallanguages.src.SymbolicTable;
import formallanguages.src.TopologicalSort;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author Ilya Shkuratov
 */
public class ReadGrammar {

    /**
     * @param args the command line arguments
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ReadGrammarException
     * @throws TooLongRuleException
     * @throws IncorrectSymbolCodeException
     * @throws IncorrectSymbolTypeException
     * @throws UnexpectedSymbolException  
     */
    public static void main(String[] args) 
            throws FileNotFoundException, IOException, ReadGrammarException, 
            TooLongRuleException, IncorrectSymbolCodeException, 
            IncorrectSymbolTypeException, UnexpectedSymbolException {
        /*
        CFRGrammar gr = (CFRGrammar) GrammarCoding.Coding(args[0]);
        gr.printRules();
        NonterminalLevels result = TopologicalSort.sort(gr);
        result.print();
        
        gr.printRelationTable();
        
        boolean b = gr.regularizeAndLog("Logfile.txt");
        if (b) {
            gr.printSortedRules();
        }
        */
        CFGrammar gr = (CFGrammar) GrammarCoding.Coding("TestGrammar2.txt");
        gr.initFirstTable(1);
        SymbolicTable st = gr.getpSymTable();
        int code = st.getSymbolCode("E0");
        FirstSet fs = gr.getFirst(code);
        int[] form = new int[2];
        form[0] = st.getSymbolCode("T1");
        form[1] = st.getSymbolCode("'+'");
        String[] input = {"T1","'+'"};
        fs = gr.getFirst(st.getFormCode(input));
        System.out.println(gr.getFirstAsString(fs));
        fs = gr.getFollow("T1", 1);
        System.out.println(gr.getFirstAsString(fs));
    }
}
