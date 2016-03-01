import java.io.*;

public class baseRunner {

    public static void main(String[] args) throws IOException {
        MusicGenerator generator = new MusicGenerator(AudioFileManager.DEFAULT_SAMPLE_RATE);
        //making and initializing window --m
        AudioDesktop mainWindow = new AudioDesktop("sSpace -- Music Creator!", 600, 500);

        float[] song0 = generator.generateSongV3(64, 2 << 10, 32);
        AudioFileManager song = new AudioFileManager(song0, song0);
        float[] ramp0 = generator.toneRamp(Float.NaN, 4, 2 << 10, -128);
        AudioFileManager ramp = new AudioFileManager(ramp0, ramp0);

        float[] beat0 = generator.getBeat(440, song.getSoundTime() / 2, 2 << 10, 3, 32);

        AudioFileManager beat = new AudioFileManager(beat0, beat0);
        beat.addNoise(3, 2);
        beat.pAdd(song);
        beat.pAdd(ramp);
        String rSngLocLinx = "s-space-jam/testing/music/sngR.wav";
        String rSngLocWind = "testing/music/sngR.wav";
        beat.buildFile(rSngLocWind);

        mainWindow.buildWindow(beat);
    }

}