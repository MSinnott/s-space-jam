
import java.util.ArrayList;
import java.util.Random;

//Makes the musics
public class MusicGenerator {

    private Random random = new Random();
    private int[] key = new int[7];
    private int sampleRate;

    public MusicGenerator(int samplesPerSec){
        for(int i = 0; i < key.length; i++){
            key[i] = random.nextInt(660)+220;
        }
        key = sortAscending(key);
        sampleRate = samplesPerSec;
    }

    /*
        High level functions to build a song
     */

    public float[] generateSongV3(int themeLen, float volumeMultiplier, float peakedness){
        float[] theme = new float[themeLen];
        float[] noteLen = new float[themeLen];
        int totalLen = 0;
        for (int i = 0; i < themeLen; i++) {
            theme[i] = key[random.nextInt(key.length)];
            noteLen[i] = (float) (2f / Math.pow(2, random.nextInt(3)));
            totalLen += noteLen[i];
        }
        float[] music = new float[AudioFileManager.DEFAULT_SAMPLE_RATE * totalLen / 2];
        float[] toAdd;
        int loc = 0;
        for (int i = 0; i < themeLen; i += 1) {
            toAdd = getBeat(theme[i], noteLen[i], volumeMultiplier, 1f / noteLen[i], peakedness);
            System.arraycopy(toAdd, 0, music, loc, toAdd.length / 2);
            loc += toAdd.length / 3;
        }
        return music;
    }

    public float[] generateSongV2(int numThemeRepeats, float volumeMultiplier){
        int numNotes = random.nextInt(20) + 10;
        float[] noteLen = new float[numNotes];
        float[] notes = new float[numNotes];

        int themeLength = 0;
        for (int i = 0; i < numNotes; i++) {
            noteLen[i] = (float) (1.0 / (1 << random.nextInt(3)));
            notes[i] = key[random.nextInt(key.length)];
            themeLength += noteLen[i];
        }

        float[] theme = new float[themeLength * numThemeRepeats * sampleRate];

        int loc = 0;
        for (int i = 0; i < numThemeRepeats; i++) {
            for (int j = 0; j < noteLen.length; j++) {
                for (int k = 0; k < noteLen[j] * sampleRate; k++, loc++) {
                    if(loc == theme.length) break;
                    theme[loc] = volumeMultiplier * getTone(notes[j], loc);
                }
            }
        }

        return theme;
    }

    public float[] generateSongV1(int numThemeRepeats, float volumeMultiplier){
        ArrayList<Integer> theme = getSongV1(3, 0);

        int[] themeNotes = new int[theme.size()];

        int[] noteLengths = new int[theme.size()];

        for (int i = 0; i < theme.size(); i++) {
            themeNotes[i] = key[theme.get(i)];
        }
        int totalLength = 0;
        for(int i = 1; i < theme.size(); i++){
            noteLengths[i] = (themeNotes[i-1] * themeNotes[i] * random.nextInt(themeNotes[i] + 1) / 50 +5000);
            totalLength += noteLengths[i];
        }


        float[] music = new float[totalLength];
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
            samples[i] = volumeMultiplier * music[i % music.length];
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

    /*
        Digital synthesizers to build song components
        Standard header format --
            (float tone, float numSeconds, float volumeMultiplier, ...)
     */

    public float[] toneRamp(float tone, float numSeconds, float volumeMultiplier, float chngBy){
        if(Float.valueOf(tone).isNaN()) tone = key[0];
        float[] tune = new float[(int) (numSeconds * sampleRate)];
        float toneChng = (chngBy / (tune.length));
        for (int i = 0; i < tune.length; i++, tone+=toneChng) {
            tune[i] = (volumeMultiplier * ((float) i / tune.length) * getTone(tone, i));
        }
        return tune;
    }

    public float[] windowFunc(float[] tuneToWindow, String windowingFunction){
        float[] newTone = new float[tuneToWindow.length];
        ArrayList<String> func = EqnHandler.convertToRPN(EqnHandler.parse(windowingFunction));
        for(int i = 0; i < newTone.length; i++){
            newTone[i] = tuneToWindow[i] * EqnHandler.evalSample(i, func);
        }
        return newTone;
    }

    public float[] getBeat(float tone, float numSeconds, float volumeMultiplier, float beatsPerSecond, float peakedness){
        if(Float.valueOf(tone).isNaN()) tone = key[0];
        float[] beat = new float[(int) (numSeconds * sampleRate)];
        if(beatsPerSecond <= 0) return beat;
        float volume;
        float scale = (float) (2 * Math.PI * beatsPerSecond / sampleRate );
        for(int phase = 0; phase < beat.length; phase++){
            volume = (float) (volumeMultiplier *  Math.pow(Math.cos(phase * scale) + 1, peakedness) / Math.pow(2, peakedness));
            beat[phase] = volume * getTone(tone, phase);
        }
        return beat;
    }

    //Useful methods

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
            toReturn[i] = ret.get(i);
        }
        return toReturn;
    }

    public float getTone(double freq, float phase){
       return (float) Math.sin(2 * Math.PI * phase / freq);
    }

    public float[] getTone(double freq, float startPhase, int length){
        float[] ret = new float[length];
        for (int phase = 0; phase < length; phase++) {
            ret[phase] = getTone(freq, phase + startPhase);
        }
        return ret;
    }

}
