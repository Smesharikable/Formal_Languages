package formallanguages;

import formallanguages.src.Verify;
import java.util.Scanner;

/**
 *
 * @author Ilya Shkuratov
 */
public class Verification {
    private static Scanner sc = new Scanner(System.in);
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        System.out.println("Type '-example' to see default examples.");
        System.out.println("Type 'quit' or 'q' to exit.");
        System.out.println("Please, type desired regular expression.\n");
        
        String input;
        while ( sc.hasNextLine() ) {
            input = sc.nextLine();
            if (input.equals("-example")) {
                test();
            } else if (input.equals("q") || input.equals("quit")) {
                System.out.println("Bye.");
                break;
            } else {
                System.out.println(Verify.isCorrect(input));
            }
        }

    }
    
    private static void test() {
        
        System.out.println("'a'#'b';(A,'c'#'d')#B.");
        System.out.println(Verify.isCorrect("'a'#'b';(A,'c'#'d')#B."));
        
        System.out.println("'c'#'d'.");
        System.out.println(Verify.isCorrect("'c'#'d'."));
        
        System.out.println("('A';'B'),('C';'D');'E'.");
        System.out.println(Verify.isCorrect("('A';'B'),('C';'D');'E'."));
        
        System.out.println("(('a';'b'),('a';'b'),'abc')#(('a';'b'),('a';'b')).");
        System.out.println(Verify.isCorrect("(('a';'b'),('a';'b'),'abc')#(('a';'b'),('a';'b'))."));
        
        System.out.println("@#(('a';'b'),('a';'b'),'abc').");
        System.out.println(Verify.isCorrect("@#(('a';'b'),('a';'b'),'abc')."));
        
        System.out.println("@#(('a';'b'),('a';'b'),'abc').");
        System.out.println(Verify.isCorrect("@#(('a';'b'),('a';'b'),'abc')."));
        
        System.out.println("@#(('a';'b'),('a';'b'),'abc');'abc'.");
        System.out.println(Verify.isCorrect("@#(('a';'b'),('a';'b'),'abc');'abc'."));
        
        System.out.println("(('a';'b'),$S1,$S2,('a';'b'),'abc';$S3,@,$S4)#'abc'.");
        System.out.println(Verify.isCorrect("(('a';'b'),$S1,@,$S2,('a';'b'),'abc')#($S3,@,$S4)#'abc'."));
    }
}
