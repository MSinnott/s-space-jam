
import java.util.ArrayList;
import java.util.Random;

//Makes the musics
public class MusicGenerator {

    private Random random = new Random();
    private int[] key = new int[7];
    private float[] music;
    private ArrayList<Integer> theme;

    public MusicGenerator(){
        for(int i = 0; i < key.length; i++){
            key[i] = random.nextInt(440)+100;
        }

    }

    public float[] generateSong(int numThemeRepeats){
        theme = getSong(4, 0);

        int[] themeNotes = new int[theme.size()];

        int[] noteLengths = new int[theme.size()];

        for (int i = 0; i < theme.size(); i++) {
            themeNotes[i] = key[theme.get(i)];
        }
        int totalLength = 0;
        for(int i = 1 ; i < theme.size(); i++){
            noteLengths[i] = (themeNotes[i-1] / 5 * themeNotes[i] / 7 * random.nextInt((themeNotes[i]) + 1)+5000);
            totalLength += noteLengths[i];
        }


        music = new float[totalLength];
        int loc = 0;
        int lastLoc = 0;
        for(int i = 0; i < theme.size(); i++) {
            int volume = random.nextInt(6000) + 2000;
            float noteFreq = themeNotes[i];
            for(int j = lastLoc; j < noteLengths[i] + lastLoc; j++, loc++) {
                music[loc] = (float) (volume * Math.cos(2 * Math.PI * j / noteFreq));
            }
            lastLoc = loc;
        }

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

        int[] randMaps = new int[10];
        for (int i = 0; i < randMaps.length; i++) {
            randMaps[i] = random.nextInt(5);
        }

        for (int i = 0; i < numIterations; i++) {
            int loc = 0;
            while (loc < notes.size()){
                if(notes.get(loc) == 0){
                    notes.add(loc + 1, randMaps[0]);
                    notes.add(loc + 1, randMaps[1]);
                } else if(notes.get(loc) == 1){
                    notes.add(loc + 1, randMaps[2]);
                    notes.add(loc + 1, randMaps[3]);
                } else if(notes.get(loc) == 2){
                    notes.add(loc + 1, randMaps[4]);
                    notes.add(loc + 1, randMaps[5]);
                } else if(notes.get(loc) == 3){
                    notes.add(loc + 1, randMaps[6]);
                    notes.add(loc + 1, randMaps[7]);
                } else if(notes.get(loc) == 4){
                    notes.add(loc + 1, randMaps[8]);
                    notes.add(loc + 1, randMaps[9]);
                }
                loc+=3;
            }
        }

        return notes;
    }
}