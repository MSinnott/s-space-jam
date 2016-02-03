import java.util.Random;

//Makes the musics
public class MusicGenerator {

    private Random random = new Random();
    float[] key = new float[5];
    float[] theme;

    public MusicGenerator(int themeLength){
        for(int i = 0; i < key.length; i++){
            key[i] = random.nextInt(220)+220;
        }
        int[] noteLengths = new int[themeLength];
        int totalLength = 0;
        for(int i = 0 ; i < themeLength; i++){
            noteLengths[i] = random.nextInt(20000) + 10000;
            totalLength+= noteLengths[i];
        }
        theme = new float[totalLength];
        int loc = 0;
        for(int i = 0; i < themeLength; i++) {
            int volume = random.nextInt(6000) + 2000;
            float noteFreq = key[random.nextInt(key.length)];
            for(int j = 0; j < noteLengths[i]; j++, loc++) {
                theme[loc] = (float) (volume * Math.sin(2 * Math.PI * j / noteFreq));
            }
        }

    }

    public float[] generateSong(int numThemeRepeats){
        float[] samples  = new float[numThemeRepeats * theme.length];

        for(int i = 0; i < samples.length; i++){
            samples[i] = theme[i % theme.length];
        }

        return samples;
    }

    //just for reference -- freq in hz
    public static float[] getStereoTone(double freqLeft, double freqRight, int numSamples){
        float[] tone = new float[numSamples*=2];
        float rightSide;
        float leftSide;
        for(int i = 0; i < numSamples; i+= 2){
            leftSide = (float) (6000 * Math.sin(2 * Math.PI * i / freqLeft));
            rightSide = (float) (6000 * Math.sin(2 * Math.PI * i / freqRight));
            tone[i] = leftSide;
            tone[i+1] = rightSide;
        }
        return tone;
    }
}
