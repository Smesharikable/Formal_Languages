package formallanguages;

import formallanguages.exceptions.ReadGrammarException;
import formallanguages.src.GrammarCoding;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author Ilya Shkuratov
 */
public class ReadGrammar {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, ReadGrammarException {
        GrammarCoding gc = new GrammarCoding();
        gc.Coding(args[0]);
    }
}
