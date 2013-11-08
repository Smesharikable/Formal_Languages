package formallanguages;

import formallanguages.exceptions.ReadGrammarException;
import formallanguages.exceptions.TooLongRuleException;
import formallanguages.src.Grammar;
import formallanguages.src.GrammarCoding;
import formallanguages.src.NonterminalLevels;
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
     */
    public static void main(String[] args) 
            throws FileNotFoundException, IOException, ReadGrammarException, TooLongRuleException {
        Grammar gr = GrammarCoding.Coding(args[0]);
        gr.printRules();
        NonterminalLevels result = TopologicalSort.sort(gr);
        result.print();
        
        gr.printRelationTable();
        
        boolean b = gr.regularizeAndLog("Logfile.txt");
        if (b) {
//            gr.printRules();
            gr.printSortedRules();
        }
    }
}
