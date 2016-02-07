
import java.util.ArrayList;
import java.util.Random;

//Makes the musics
public class MusicGenerator {

    private Random random = new Random();
    private int[] key = new int[7];
    private float[] music;
    private ArrayList<Integer> theme;
    private int sampleRate;

    public MusicGenerator(int samplesPerSec){
        for(int i = 0; i < key.length; i++){
            key[i] = random.nextInt(440)+100;
        }
        key = sortAscending(key);
        sampleRate = samplesPerSec;
    }

    public float[] getBeat(float beatsPerSecond, float numSeconds, float volumeMultiplier){
        float[] beat = new float[(int) (numSeconds * sampleRate)];
        if(beatsPerSecond == 0) return beat;
        float volume = 0;
        float samplesPerBeat = sampleRate / beatsPerSecond;
        for(int phase = 0; phase < beat.length; phase++){
            volume = (float) (volumeMultiplier * Math.pow(samplesPerBeat - phase % samplesPerBeat, 2) / (2 * samplesPerBeat));
            beat[phase] = volume * getTone(key[0], phase);
        }
        return beat;
    }

    public float[] generateSongV1(int numThemeRepeats){
        theme = getSongV1(3, 0);

        int[] themeNotes = new int[theme.size()];

        int[] noteLengths = new int[theme.size()];

        for (int i = 0; i < theme.size(); i++) {
            themeNotes[i] = key[theme.get(i)];
        }
        int totalLength = 0;
        for(int i = 1 ; i < theme.size(); i++){
            noteLengths[i] = (themeNotes[i-1] * themeNotes[i] * random.nextInt(themeNotes[i] + 1) / 50 +5000);
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

    public ArrayList<Integer> getSongV1(int numIterations, int seed){
        ArrayList<Integer> notes  = new ArrayList<Integer>();
        notes.add(seed);

        int[] randMaps = new int[10];
        for (int i = 0; i < randMaps.length; i++) {
            randMaps[i] = random.nextInt(key.length);
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

    //Usefule methods -- not really going to be used
    public float[] generateScale(int noteTimes){
        float[] scale = new float[noteTimes * key.length];
        int loc = 0;
        for(int tone: key){
            for(int i = 0; i < noteTimes; i++, loc++) {
                scale[loc] = (float) (6000 * Math.sin(2 * Math.PI * loc / tone));
            }
        }
        return scale;
    }

    public int[] sortAscending(int[] toSort){
        ArrayList<Integer> ret = new ArrayList<Integer>();

        for(Integer intg: toSort){
            if(ret.size() == 0 || ret.get(0) > intg){
                ret.add(0, intg);
            } else {
                for (int i = 0; i < ret.size() - 1; i++) {
                    if (ret.size() > 1 && ret.get(i) < intg && ret.get(i + 1) > intg) {
                        ret.add(i, intg);
                        break;
                    }
                }
            }
            if(ret.size() > 1 && intg > ret.get(ret.size() - 1)) ret.add(intg);
        }
        int[] toReturn = new int[ret.size()];
        for(int i = 0; i < toReturn.length; i++){
            System.out.println(ret.get(i));
            toReturn[i] = ret.get(i);
        }
        return toReturn;
    }

    public float getTone(double freq, int phase){
       return (float) Math.sin(2 * Math.PI * phase / freq);
    }

}
