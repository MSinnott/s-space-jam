import javax.swing.*;
import java.util.ArrayList;

public class AdaptiveTextField extends JTextField{

    private ArrayList<String> tokens = new ArrayList<String>();
    private String[] possibleTokens = new String[]{ "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "+", "-", "*", "/", "^", "(", ")", "sin(", "cos(", "tan(" };

    public AdaptiveTextField(String text){
        super(text);
    }

    public float[] generateSamples(int numSamples){
        tokens = parse(getText());

        float[] samples = new float[numSamples];

        float scale = (float) (10 * Math.random());

        for(int i = 0; i < samples.length; i++){
            samples[i] = scale * evalSample(i);
        }

        return samples;
    }

    public float evalSample(int time){
        return (float) Math.sin(2 * Math.PI * time / 1000);
    }

    private ArrayList<String> parse(String text){
        ArrayList<String> parsedTokens = new ArrayList<String>();

        int loc = 0;
        while (loc < text.length()){
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

}
