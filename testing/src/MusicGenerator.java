import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

//Makes the musics
public class MusicGenerator {

    private Random random = new Random();
    private int sampleRate;
    private Scale scale;

    public MusicGenerator(int samplesPerSec) {
        scale = new Scale(7);
        sampleRate = samplesPerSec;
    }

    public MusicGenerator(int samplesPerSec, Scale scaleIn) {
        scale = scaleIn;
        sampleRate = samplesPerSec;
    }

    /*
        High level functions to build a song
     */

    public AudioFileManager genNewComplexSong(){
        AudioFileManager theme = genTheme(8);
        AudioFileManager prologue = genTheme(8);
        int prologueLen = prologue.getSoundLen();
        for (int i = 0; i < 1; i++) {
            prologue.pAdd(theme, prologueLen);
            prologueLen+=theme.getSoundLen();
        }
        return prologue;
    }

    public AudioFileManager genTheme(int themeLen){
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
                addedNote.addHarmonics(new float[]{ 1 , 0.8f, 0.2f, 0.3f, 0.1f});
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
