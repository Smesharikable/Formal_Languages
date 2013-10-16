package formallanguages.exceptions;

/**
 *
 * @author Ilya Shkuratov
 */
public class TooLongRuleException extends Exception {

    /**
     * Creates a new instance of
     * <code>TooLongRuleException</code> without detail message.
     */
    public TooLongRuleException() {
    }

    /**
     * Constructs an instance of
     * <code>TooLongRuleException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public TooLongRuleException(String msg) {
        super(msg);
    }
}
