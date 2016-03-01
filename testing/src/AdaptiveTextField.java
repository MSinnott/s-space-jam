import javax.swing.*;
import java.util.*;

/**
 * A text field that accepts and evaluates equations
 */
public class AdaptiveTextField extends JTextField{

    /**
     * Constructor
     * @param text starting text in this text field
     */
    public AdaptiveTextField(String text){
        super(text);
    }

    /**
     * Generates sound samples based on the text in the field
     * @param numSamples number of samples to generate
     * @return generated samples (as float[])
     */
    public float[] generateSamples(int numSamples){
        ArrayList<String> rpnTokens = new ArrayList<String>();
        try {
            ArrayList parsed = EqnHandler.parse(getText());
            rpnTokens = EqnHandler.convertToRPN(parsed);
        } catch (Exception e) {
            e.printStackTrace();
        }

        float[] data = new float[numSamples];
        for(int i = 0; i < numSamples; i++){
            data[i] = EqnHandler.evalSample(i, rpnTokens);
        }
        return data;
    }

}
