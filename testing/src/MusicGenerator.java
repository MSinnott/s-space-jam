import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Random;

//Makes the musics
public class MusicGenerator {

    private Random random = new Random();
    float[] key = new float[5];
    float[] music;

    public MusicGenerator(){
        for(int i = 0; i < key.length; i++){
            key[i] = random.nextInt(440);
        }
        ArrayList<Integer> theme = getSong(3, 0);
        float[] themeNotes = new float[theme.size()];

        int[] noteLengths = new int[theme.size()];

        for (int i = 0; i < theme.size(); i++) {
            themeNotes[i] = key[theme.get(i)];
        }
        int totalLength = 0;
        for(int i = 1 ; i < theme.size(); i++){
            noteLengths[i] = (int) ((themeNotes[i] / 20 * random.nextInt((int) (themeNotes[i]))+5000));
            totalLength += noteLengths[i];
        }


        music = new float[totalLength];
        int loc = 0;
        for(int i = 0; i < theme.size(); i++) {
            int volume = random.nextInt(6000) + 2000;
            float noteFreq = themeNotes[i];
            for(int j = 0; j < noteLengths[i]; j++, loc++) {
                music[loc] = (float) (volume * Math.cos(2 * Math.PI * j / noteFreq));
            }
        }

    }

    public float[] generateSong(int numThemeRepeats){
        float[] samples  = new float[numThemeRepeats * music.length];

        for(int i = 0; i < samples.length; i++){
            samples[i] = music[i % music.length];
        }

        return samples;
    }

    //just for reference -- freq in hz
    public float[] getStereoTone(double freqLeft, double freqRight, int numSamples){
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

    public ArrayList<Integer> getSong(int numIterations, int seed){
        ArrayList<Integer> notes  = new ArrayList<Integer>();
        notes.add(seed);

        for (int i = 0; i < numIterations; i++) {
            int loc = 0;
            while (loc < notes.size()){
                if(notes.get(loc) == 0){
                    notes.add(loc + 1, 2);
                    notes.add(loc + 1, 3);
                } else if(notes.get(loc) == 1){
                    notes.add(loc + 1, 4);
                    notes.add(loc + 1, 1);
                } else if(notes.get(loc) == 2){
                    notes.add(loc + 1, 0);
                    notes.add(loc + 1, 0);
                } else if(notes.get(loc) == 3){
                    notes.add(loc + 1, 4);
                    notes.add(loc + 1, 1);
                } else if(notes.get(loc) == 4){
                    notes.add(loc + 1, 2);
                    notes.add(loc + 1, 1);
                }
                loc+=3;
            }
            System.out.println(i);
        }

        return notes;
    }
}
