import javax.swing.*;
import java.util.*;

public class AdaptiveTextField extends JTextField{

    public AdaptiveTextField(String text){
        super(text);
    }

    public float[] generateSamples(int numSamples){
        ArrayList<String> rpnTokens = new ArrayList<String>();
        try {
            rpnTokens = EqnHandler.convertToRPN(EqnHandler.parse(getText()));
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
