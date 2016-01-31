import javax.swing.*;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.Properties;

public class AdaptiveTextField extends JTextField{

    private ArrayList<Token> tokens = new ArrayList<Token>();
    private String[] numbers = new String[]{"-", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "."};
    private String[] possibleTokens = new String[]{"+", "*", "/", "^", "(", ")", "sin(", "cos(", "tan(", "t" };
    private int[] numValues = new int[] {2, 2, 2, 2, 0, 0, 1, 1, 1, 0};

    public AdaptiveTextField(String text){
        super(text);
    }

    public float[] generateSamples(int numSamples){
        tokens = parse(getText());

        float[] samples = new float[numSamples];

        for(int i = 0; i < samples.length; i++) {
            samples[i] = 0; // evalSample(i);
        }

        return samples;
    }

    //need to write eval sample

    /*
        Test cases:
        -1*(-3+-5)-7    pass
        4*sin(-3-5)     pass
        -(3+5)+-(8*7)
        --add more test cases here--
     */

    //this is actually a pretty dece parser --m
    private ArrayList<Token> parse(String text){
        ArrayList<Token> parsedTokens = new ArrayList<Token>();

        int loc = 0;
        while (loc < text.length()){
            String num = "";
            while(text.length() > loc && Token.isNumber(text.substring(loc, loc+1))) {
                if(text.substring(loc, loc+1).equals("-")){
                    if (!Token.isNumber(text.substring(loc+1, loc+2))) break;
                    if (loc > 0 && ( text.substring(loc-1, loc).equals(")") || Token.isNumber(text.substring(loc-1, loc)))) {
                        if(num.length() > 0) parsedTokens.add(new Token(num));
                        parsedTokens.add(new Token("+"));
                        num = "";
                    }
                }
                num = num.concat(text.substring(loc, loc + 1));
                loc++;
            }
            if (num.length() > 0){
                parsedTokens.add(new Token(num));
            }
            for (String possibleToken : Token.getPossibleTokens()) {
                if (text.indexOf(possibleToken) == loc) {
                    parsedTokens.add(new Token(possibleToken));
                    loc += possibleToken.length() - 1;
                    break;
                }
            }
            loc++;
        }

        return parsedTokens;
    }

    //See "shunting-yard algorithm"
    private String[] convertToRPN(ArrayList<Token> parsedTokens){
        ArrayList<Token> infTokens = new ArrayList<Token>(parsedTokens);
        ArrayList<Token> rpnTokens = new ArrayList<Token>();
        ArrayList<Token> opStack = new ArrayList<Token>();

        int loc = 0;
        Token currentToken;
        while(infTokens.size() > 0){
            currentToken = infTokens.get(loc);
            if(currentToken.isNumber()){
                rpnTokens.add(currentToken);
            } else if(currentToken.isFunction()){
                opStack.add(currentToken);
            } else if(currentToken.isOperation()){
                while(opStack.size() > 0){
                    Token lastOp = opStack.get(opStack.size() - 1);
                    if(Token.isFunction(lastOp)){
                        rpnTokens.add(lastOp);
                        opStack.remove(opStack.size() - 1);
                    } else {
                        if( ( currentToken.isLeftAssociative() && currentToken.getPrecedence() <= lastOp.getPrecedence() ) || ( !currentToken.isLeftAssociative() && currentToken.getPrecedence() < lastOp.getPrecedence() )){
                            rpnTokens.add(lastOp);
                            opStack.remove(opStack.size() - 1);
                        }
                    }
                }
                opStack.add(currentToken);
            }
        }


        return (String[]) rpnTokens.toArray();
    }

    private int indexOf(Object[] arr, Object obj){
        for(int i = 0; i < arr.length; i++){
            if(obj.equals(arr[i])) return i;
        }
        return -1;
    }

    private boolean contains(Object[] arr, Object obj){
        return indexOf(arr, obj) != -1;
    }

}
