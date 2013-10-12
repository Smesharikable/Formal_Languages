package formallanguages.dfa;

/**
 *
 * @author Ilya Shkuratov
 */
public enum CfrRule_dfa {
    
    START {
        @Override
        public CfrRule_dfa step(char c) {
            if ( Character.isSpaceChar(c) ) return START;
            if ( Character.isUpperCase(c) ) return NONTERMINAL;
            System.err.println("Nonterminal must be started with uppercase letter");
            return ERROR;
        }
    },

    /**
     *
     */
    WHITESPACES {
        @Override
        public CfrRule_dfa step(char c) {
            if ( Character.isSpaceChar(c) ) return WHITESPACES;
            return END;
        }
    },

    NONTERMINAL {
        @Override
        public CfrRule_dfa step(char c) {
            if ( c == ':' ) return WHITESPACES;
            if ( Cfr_dfa.isNonterminal(c) ) return NONTERMINAL;
            System.err.println("Unacceptable character for nonterminal");
            return ERROR;
        }
    },

    END,

    ERROR;

    public CfrRule_dfa step(char c) {
        return null;
    }
    
}
