
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

//Makes the musics
public class MusicGenerator {

    private Random random = new Random();
    private int sampleRate;
    private Scale scale;

    public MusicGenerator(int samplesPerSec){
        scale = new Scale(7);
        sampleRate = samplesPerSec;
    }

    /*
        High level functions to build a song
     */

    public float[] generateSongV4(int len, float volumeMultiplier){
        float[] ret = new float[len];
        float note = scale.getRandomFromScale();
        for(int i = 0; i < len; i++){
            ret[i] = volumeMultiplier * getTone(note, i);
            if(i % 10000 == 0) note = scale.getRandomFromScale();
        }
        return ret;
    }

    public AudioFileManager genNewComplexSong(){
        AudioFileManager theme = genTheme(16);
        AudioFileManager prologue = genTheme(8);
        AudioFileManager beat = getBeatFile(scale.getNoteAt(0), 4 * theme.getSoundLen(), 2 << 10 , 1.5f, 32);
        int prologueLen = prologue.getSoundLen();
        prologue.pAdd(beat, prologueLen / 2);
        for (int i = 0; i < 4; i++) {
            prologue.pAdd(theme, prologueLen);
            prologueLen+=theme.getSoundLen();
        }
        return prologue;
    }

    public AudioFileManager genTheme(int themeLen){
        System.out.println(Arrays.toString(scale.getScale()));
        AudioFileManager res = new AudioFileManager(new float[]{0}, new float[]{0});
        int[] noteLens = new int[themeLen];
        int phraseLen = 3 + random.nextInt(3);
        for (int i = 0; i < noteLens.length; i++) {
            if(i % phraseLen == 0) {
                noteLens[i] = AudioFileManager.DEFAULT_SAMPLE_RATE / 2;
            } else {
                noteLens[i] = (int) (2 * AudioFileManager.DEFAULT_SAMPLE_RATE / (Math.pow(2, random.nextInt(3))));
            }
        }
        int loc = 0;
        for(int a = 0; a < themeLen; a++) {
            float note = scale.getRandomFromScale();
            if (note != 0) {
                float[] seed = getTone(note, 0, noteLens[a]);
                AudioFileManager addedNote = new AudioFileManager(seed, seed);
                addedNote.addNoise(2, 1);
                res.pAdd(addedNote, loc);
                loc += addedNote.getSoundLen() + AudioFileManager.DEFAULT_SAMPLE_RATE / 8;
            } else{
                loc += noteLens[a] + AudioFileManager.DEFAULT_SAMPLE_RATE / 8;
            }
        }
        res.vscale(1000);
        res.pAdd(res, AudioFileManager.DEFAULT_SAMPLE_RATE);
        return res;
    }

    public float[] generateSongV3(int themeLen, float volumeMultiplier, float peakedness){
        float[] theme = new float[themeLen];
        float[] noteLen = new float[themeLen];
        int totalLen = 0;
        for (int i = 0; i < themeLen; i++) {
            theme[i] = scale.getRandomFromScale();
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
            notes[i] = scale.getRandomFromScale();
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

        float[] themeNotes = new float[theme.size()];

        int[] noteLengths = new int[theme.size()];

        for (int i = 0; i < theme.size(); i++) {
            themeNotes[i] = scale.getNoteAt(i);
        }
        int totalLength = 0;
        for(int i = 1; i < theme.size(); i++){
            noteLengths[i] = (int) (themeNotes[i-1] * themeNotes[i] * random.nextInt((int) (themeNotes[i] + 1)) / 50 +5000);
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
            randMaps[i] = random.nextInt(scale.getScale().length);
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

    public float[] toneRamp(float tone, float numSamples, float volumeMultiplier, float chngBy){
        if(Float.valueOf(tone).isNaN()) tone = scale.getNoteAt(0);
        float[] tune = new float[(int) (numSamples)];
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

    public float[] getBeat(float tone, float numSamples, float volumeMultiplier, float beatsPerSecond, float peakedness){
        if(Float.valueOf(tone).isNaN()) tone = scale.getNoteAt(0);
        float[] beat = new float[(int) (numSamples)];
        if(beatsPerSecond <= 0) return beat;
        float volume;
        float scale = (float) (2 * Math.PI * beatsPerSecond / sampleRate );
        for(int phase = 0; phase < beat.length; phase++){
            volume = (float) (volumeMultiplier *  Math.pow(Math.cos(phase * scale) + 1, peakedness) / Math.pow(2, peakedness));
            beat[phase] = volume * getTone(tone, phase);
        }
        return beat;
    }

    public AudioFileManager getBeatFile(float tone, float numSamples, float volumeMultiplier, float beatsPerSecond, float peakedness){
        float[] arr = getBeat(tone, numSamples, volumeMultiplier, beatsPerSecond, peakedness);
        return new AudioFileManager(arr, arr);
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
