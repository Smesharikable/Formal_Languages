package formallanguages.dfa;

import java.util.Stack;

/**
 *
 * @author Ilya Shkuratov
 */
public enum Cfr_dfa {
    
    START {
        @Override
        public Cfr_dfa step(char c, int position) {
            if ( Character.isUpperCase(c)) return NONTERMINAL;
            if (c == '\'') return QUOTES;
            if (c == '$') return SEMANTIC;
            if (c == '@') return LINK;
            if (c == '(') {
                st.push(')');
                return START;
            }
            if (c == '[') {
                st.push(']');
                return START;
            }
            System.err.format("Unexpected character '%c' in position '%d'%n.", c, position);
            System.err.format("Expected characters are ' ! @ ( [ or 'word'%n.");
            return ERROR;
        }
    },
    
    QUOTES {
        @Override
        public Cfr_dfa step(char c, int position) {
            if ( isTerminal(c) ) return TERMINAL;
            if (c == '\'') return LINK;
            System.err.format("Unexpexted character '%c' in position '%d'%n.", c, position);
            System.err.format("Expected characters are ' or 'word'%n.");
            return ERROR;
        }
    },

    TERMINAL {
        @Override
        public Cfr_dfa step(char c, int position) {
            if ( isTerminal(c) ) return TERMINAL;
            if (c == '\'') return LINK;
            System.err.format("Unexpexted character '%c' in position '%d'%n.", c, position);
            System.err.format("Expected characters are ' or 'word'%n.");
            return ERROR;
        }
    }, 

    NONTERMINAL {
        @Override
        public Cfr_dfa step(char c, int position) {
            if ( isNonterminal(c) ) return NONTERMINAL;
            return subStep(c, position);
        }
    }, 

    SEMANTIC {
        @Override
        public Cfr_dfa step(char c, int position) {
            if ( isWord(c) ) return SEMANTIC;
            return subStep(c, position);
        }
    },

    
    LINK {
        @Override
        public Cfr_dfa step(char c, int position) {
            return subStep(c, position);
        }
    },
        
    END,
    
    ERROR;
    
    private static Stack<Character> st = new Stack();;
    private static Cfr_dfa state;
    private static int position;
    private static char[] termSymbols = 
        {'+', '(', ')', '.', '*', '@', '!', '$', '%', '^', '-', '\\', '|', '<', '>'};
    
    public Cfr_dfa step(char c, int position) {
            return null;
    }
    
    
    public static boolean isAdditional(char c) {
        if (c == ' ' || c == '_' || c == '-')
            return true;
        return false;
    }
    
    public static boolean isTermSymbols(char c) {
        for (int i = 0; i < termSymbols.length; i++) {
            if (c == termSymbols[i]) return true;
        }
        return false;
    }
    
    public static boolean isWord(char c) {
        if (Character.isLetterOrDigit(c) || isAdditional(c))
            return true;
        return false;
    }
    
    
    public static boolean isNonterminal(char c) {
        if (Character.isUpperCase(c) || Character.isDigit(c) || isAdditional(c))
            return true;
        return false;
    }
    
    public static boolean isTerminal(char c) {
        if (Character.isLowerCase(c) || Character.isDigit(c) || isAdditional(c) ||
                isTermSymbols(c))
            return true;
        return false;
    }
    
    // check if expression is a correct regular expression
    public static boolean isCorrect(String expression) {
        // preparation
        char[] exp = expression.toCharArray();
        position = 0;
        state = Cfr_dfa.START;
        st.clear();
        
        while ( state != Cfr_dfa.ERROR && state != Cfr_dfa.END && position < exp.length) {
            state = state.step(exp[position], position);
            position++;
        }
        
        if (state == Cfr_dfa.ERROR) return false;
        if (state == Cfr_dfa.END)
            return true;
        else {
            System.err.println("Expression has been over, but '.' not found");
            return false;
        }
    }

    private static Cfr_dfa checkBracket(char c, int position) {
        if ( st.isEmpty() ) {
            System.err.format("Open bracket was missed before position '%d'%n.", position);
            return ERROR;
        }
        if ( c == st.peek() ) {
            st.pop();
            return LINK;
        }
        else {
            System.err.format("Wrong type of bracket. Met %c, but needed %c%n", c, st.peek());
            return ERROR;
        }
    }
    
    private static Cfr_dfa subStep(char c, int position) {
        if (c == '#' || c == ',' || c == ';') return START;
        if (c == ')' || c == ']') {
            return checkBracket(c, position);
        }
        if (c == '.') {
            if ( st.isEmpty() ) return END;
            System.err.format("Unexpected character '%c' in position '%d'%n.", c, position);
            System.err.format("There are one or several brackets wasn't closed%n");
            return ERROR;
        }
        System.err.format("Unexpected character '%c' in position '%d'%n.", c, position);
        System.err.format("Expected character are ) ] # . ; or 'word'%n.");
        return ERROR;
    }
    
}
