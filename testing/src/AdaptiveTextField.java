import javax.swing.*;
import java.awt.geom.Arc2D;
import java.util.ArrayList;

public class AdaptiveTextField extends JTextField{

    private ArrayList<String> tokens = new ArrayList<String>();
    private String[] numbers = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "."};
    private String[] possibleTokens = new String[]{"+", "-", "*", "/", "^", "(", ")", "sin(", "cos(", "tan(", "t" };

    public AdaptiveTextField(String text){
        super(text);
    }

    public float[] generateSamples(int numSamples){
        tokens = parse(getText());

        float[] samples = new float[numSamples];

        for(int i = 0; i < samples.length; i++){
            samples[i] = evalSample(i);
        }

        return samples;
    }


    //NEEDS TO BE WRITTEN BETTER!! THIS IS __AWFUL__ ;( --m
    public float evalSample(int time){
        ArrayList<String> localTokens = new ArrayList<String>(tokens);
        for(int i = 0; i < localTokens.size(); i++){
            if(localTokens.get(i).equals("t")){
                localTokens.remove(i);
                localTokens.add(i, String.valueOf(time));
            }
        }


        for(int i = 0; i < localTokens.size(); i++) {
            if (localTokens.get(i).equals("sin(")) {

                float firstArg = Float.parseFloat(localTokens.get(i+1));

                localTokens.remove(i + 1);
                localTokens.remove(i);

                localTokens.add(i, String.valueOf(Math.sin(firstArg)));
                System.out.println("fArg = " + firstArg + " yields sin(fArg) = " + Math.sin(firstArg));
            } else if (localTokens.get(i).equals("cos(")) {

                float firstArg = Float.parseFloat(localTokens.get(i+1));

                localTokens.remove(i + 1);
                localTokens.remove(i);

                localTokens.add(i, String.valueOf(Math.cos(firstArg)));
            } else if (localTokens.get(i).equals("tan(")) {

                float firstArg = Float.parseFloat(localTokens.get(i+1));

                localTokens.remove(i + 1);
                localTokens.remove(i);

                localTokens.add(i, String.valueOf(Math.tan(firstArg)));
            }
        }


        for(int i = 0; i < localTokens.size(); i++){
            if(localTokens.get(i).equals("^")){
                float firstArg = Float.parseFloat(localTokens.get(i-1));
                float secondArg = Float.parseFloat(localTokens.get(i+1));
                localTokens.remove(i+1);
                localTokens.remove(i);
                localTokens.remove(i-1);

                localTokens.add(i - 1, String.valueOf(Math.pow(firstArg, secondArg)));
            }
        }

        for(int i = 0; i < localTokens.size(); i++){
            if(localTokens.get(i).equals("*")){
                float firstArg = Float.parseFloat(localTokens.get(i-1));
                float secondArg = Float.parseFloat(localTokens.get(i+1));
                localTokens.remove(i+1);
                localTokens.remove(i);
                localTokens.remove(i-1);

                localTokens.add(i - 1, String.valueOf(firstArg * secondArg));
            } else if(localTokens.get(i).equals("/")){
                float firstArg = Float.parseFloat(localTokens.get(i-1));
                float secondArg = Float.parseFloat(localTokens.get(i+1));
                localTokens.remove(i+1);
                localTokens.remove(i);
                localTokens.remove(i-1);

                localTokens.add(i - 1, String.valueOf(firstArg / secondArg));
            }
        }

        for(int i = 0; i < localTokens.size(); i++){
            if(localTokens.get(i).equals("+")){
                float firstArg = Float.parseFloat(localTokens.get(i-1));
                float secondArg = Float.parseFloat(localTokens.get(i+1));
                localTokens.remove(i+1);
                localTokens.remove(i);
                localTokens.remove(i-1);

                localTokens.add(i - 1, String.valueOf(firstArg + secondArg));
            } else if(localTokens.get(i).equals("-")){
                if(i == 0){
                    System.out.println(localTokens.get(i));
                    localTokens.remove(i);
                    localTokens.remove(i+1);

                    localTokens.add(i + 1, String.valueOf(-1 * Float.parseFloat(tokens.get(i+1))));
                    continue;
                }
                float firstArg = Float.parseFloat(localTokens.get(i-1));
                float secondArg = Float.parseFloat(localTokens.get(i+1));
                localTokens.remove(i+1);
                localTokens.remove(i);
                localTokens.remove(i-1);

                localTokens.add(i - 1, String.valueOf(firstArg - secondArg));
            }
        }
        return Float.valueOf(localTokens.get(0));
    }

    //this is actually a pretty dece parser --m
    private ArrayList<String> parse(String text){
        ArrayList<String> parsedTokens = new ArrayList<String>();

        int loc = 0;
        while (loc < text.length()){
            String num = "";
            while(text.length() > loc && contains(numbers, text.substring(loc, loc+1))){
                num  = num.concat(text.substring(loc, loc+1));
                loc++;
            }
            if (num.length() > 0){
                parsedTokens.add(num);
            }
            for (String possibleToken : possibleTokens) {
                if (text.indexOf(possibleToken) == loc) {
                    parsedTokens.add(possibleToken);
                    loc += possibleToken.length() - 1;
                    break;
                }
            }
            loc++;
        }

        return parsedTokens;
    }

    //yeah. so I can do .contains. WHAT NOW? --m
    private boolean contains(String[] arr, String str){
        for(String s: arr){
            if(str.equals(s)) return true;
        }
        return false;
    }

}
