import java.io.*;

public class baseRunner {

    public static void main(String[] args) throws IOException, InterruptedException {
        MusicGenerator generator = new MusicGenerator(AudioFileManager.DEFAULT_SAMPLE_RATE);
        //making and initializing window --m
        AudioDesktop mainWindow = new AudioDesktop("sSpace -- Music Creator!", 600, 500);

        float[] song0 = generator.generateSongV2(32, 2 << 8);
        float[] ramp = generator.toneRamp(Float.NaN, 4, 2 << 8, -128);
        AudioFileManager rSong = new AudioFileManager(ramp, ramp);

        float[] beat0 = generator.getBeat(440, song0.length / AudioFileManager.DEFAULT_SAMPLE_RATE, 2 << 7, 2, 32);
        float[] beat1 = generator.getBeat(880, song0.length / AudioFileManager.DEFAULT_SAMPLE_RATE, 2 << 8, 4, 32);
        float[] beat2 = generator.getBeat(220, song0.length / AudioFileManager.DEFAULT_SAMPLE_RATE, 2 << 9, 1, 32);

        rSong.pAdd(beat0, beat0, AudioFileManager.DEFAULT_SAMPLE_RATE * 6);
        rSong.pAdd(beat1, beat1, AudioFileManager.DEFAULT_SAMPLE_RATE * 6);
        rSong.pAdd(beat2, beat2, AudioFileManager.DEFAULT_SAMPLE_RATE * 6);
        rSong.pAdd(song0, song0, AudioFileManager.DEFAULT_SAMPLE_RATE * 4);

        rSong.buildFile("testing/music/sng.wav");


        rSong.ftransform();
        rSong.roughData(500);
        rSong.btransform();
        rSong.buildFile("testing/music/sngR.wav");

        mainWindow.buildWindow(rSong);
    }

}