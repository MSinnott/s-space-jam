import javax.swing.*;

public class AdaptiveTextField extends JTextField{

    public AdaptiveTextField(String text){
        super(text);
    }

    public float[] generateSamples(int numSamples){
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

}
