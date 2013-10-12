/*
 * 
 */
package formallanguages.src;

import java.util.Stack;

/**
 * Class verifying regular expression of cfg in regular form
 * 
 * @author 1 Ilya Shkuratov
 */
public class Verify {
    private static Stack<Character> st = new Stack();
    private static RegDFA state;
    private static int position;
    
    
    public static boolean isCorrect(String expression) {
        // preparation
        char[] exp = expression.toCharArray();
        position = 0;
        state = RegDFA.start;
        st.clear();
        
        while ( state != RegDFA.error && state != RegDFA.end && position < exp.length) {
            state = state.step(exp[position ++]);
        }
        
        if (state == RegDFA.error) return false;
        if (state == RegDFA.end)
            return true;
        else {
            System.err.println("Expression has been over, but '.' not found");
            return false;
        }
    }
    
    /**
     *
     * @return RegDFA - dfa for regular expression verification
     */
    public static RegDFA getDFA() {
        return state;
    }
    
    public static boolean isAdditional(char c) {
        if (c == ' ' || c == '_' || c == '-')
            return true;
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
        if (Character.isLowerCase(c) || Character.isDigit(c) || isAdditional(c))
            return true;
        return false;
    }
    
    
    public static enum RegDFA {
        
        
        start {
            @Override
            public RegDFA step(char c) {
                if ( Character.isUpperCase(c) || c == '!') return nonterminal;
                if (c == '\'') return terminal;
                if (c == '$') return semantic;
                if (c == '@') return link;
                if (c == '(') {
                    st.push(')');
                    return start;
                }
                if (c == '[') {
                    st.push(']');
                    return start;
                }
                System.err.format("Unexpected character '%c' in position '%d'%n.", c, position);
                System.err.format("Expected characters are ' ! @ ( [ or 'word'%n.");
                return error;
            }
        }, 
        
        terminal {
            @Override
            public RegDFA step(char c) {
                if ( isTerminal(c) ) return terminal;
                if (c == '\'') return link;
                System.err.format("Unexpexted character '%c' in position '%d'%n.", c, position);
                System.err.format("Expected characters are ' or 'word'%n.");
                return error;
            }
        }, 
        
        nonterminal {
            @Override
            public RegDFA step(char c) {
                if ( isNonterminal(c) ) return nonterminal;
                if (c == '#' || c == ',' || c == ';') return start;
                if (c == ')' || c == ']') {
                    return checkBracket(c);
                }
                if (c == '.') {
                    if ( st.isEmpty() ) return end;
                    System.err.format("Unexpected character '%c' in position '%d'%n.", c, position);
                    System.err.format("There are one or several brackets wasn't closed%n");
                    return error;
                }
                System.err.format("Unexpected character '%c' in position '%d'%n.", c, position);
                System.err.format("Expected character are # , ; . or 'word'%n.");
                return error;
            }
        }, 
        
        semantic {
            @Override
            public RegDFA step(char c) {
                if ( isWord(c) ) return semantic;
                if (c == '#' || c == ',' || c == ';') return start;
                if (c == ')' || c == ']') {
                    return checkBracket(c);
                }
                if (c == '.') {
                    if ( st.isEmpty() ) return end;
                    System.err.format("Unexpected character '%c' in position '%d'%n.", c, position);
                    System.err.format("There are one or several brackets wasn't closed%n");
                    return error;
                }
                System.err.format("Unexpected character '%c' in position '%d'%n.", c, position);
                System.err.format("Expected character are # , ; . or 'word'%n.");
                return error;
            }
        },
        
        // Empty operand
        link {
            @Override
            public RegDFA step(char c) {
                if (c == '#' || c == ',' || c == ';') return start;
                if (c == ')' || c == ']') {
                    return checkBracket(c);
                }
                if (c == '.') {
                    if ( st.isEmpty() ) return end;
                    System.err.format("Unexpected character '%c' in position '%d'%n.", c, position);
                    System.err.format("There are one or several brackets wasn't closed%n");
                    return error;
                }
                System.err.format("Unexpected character '%c' in position '%d'%n.", c, position);
                System.err.format("Expected character are ) ] # . ; or 'word'%n.");
                return error;
            }
        }, 
        
        end,
        
        error;
        
        public RegDFA step(char c) {
            return null;
        }
        
        private static RegDFA checkBracket(char c) {
            if ( st.isEmpty() ) {
                System.err.format("Open bracket was missed before position '%d'%n.", position);
                return error;
            }
            if ( c == st.peek() ) {
                st.pop();
                return link;
            }
            else {
                System.err.format("Wrong type of bracket. Met %c, but needed %c%n", c, st.peek());
                return error;
            }
        }
    }
}
