package formallanguages.exceptions;

/**
 *
 * @author Ilya Shkuratov
 */
public class ReadGrammarException extends Exception {

    /**
     * Creates a new instance of
     * <code>ReadGrammarException</code> without detail message.
     */
    public ReadGrammarException() {
    }

    /**
     * Constructs an instance of
     * <code>ReadGrammarException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ReadGrammarException(String msg) {
        super(msg);
    }
}
