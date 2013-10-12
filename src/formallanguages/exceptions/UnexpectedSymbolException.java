package formallanguages.exceptions;

/**
 *
 * @author Shkuratov Ilya
 */
public class UnexpectedSymbolException extends Exception {

    /**
     * Creates a new instance of
     * <code>UnexpectedSymbolException</code> without detail message.
     */
    public UnexpectedSymbolException() {
    }

    /**
     * Constructs an instance of
     * <code>UnexpectedSymbolException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public UnexpectedSymbolException(String msg) {
        super(msg);
    }
}
