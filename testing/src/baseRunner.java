import java.io.*;

public class baseRunner {

    public static void main(String[] args) throws IOException, InterruptedException {
        MusicGenerator generator = new MusicGenerator(AudioFileManager.DEFAULT_SAMPLE_RATE);
        //making and initializing window --m
        AudioDesktop mainWindow = new AudioDesktop("sSpace -- Music Creator!", 600, 500);

        File beepNums = new File("testing/music/BeepMusic/bm.txt");
        FileInputStream fileIn = new FileInputStream(beepNums);
        int songNum = fileIn.read();
        beepNums.delete();
        File nBeepNums = new File("testing/music/BeepMusic/bm.txt");
        nBeepNums.createNewFile();
        FileOutputStream fileOut = new FileOutputStream(nBeepNums);
        fileOut.write(++songNum);

        float[] ramp = generator.toneRamp(Float.NaN, 4, 2 << 8, -128);
        AudioFileManager rampMan = new AudioFileManager(ramp, ramp);
        //mainWindow.buildWindow(rampMan);

        float[] song0 = generator.generateSongV2(32, 2 << 8);
        AudioFileManager rSong = new AudioFileManager(song0, song0);
        //rSong.buildFile("testing/music/rsong.wav");
        //mainWindow.buildWindow(rSong);

        float[] beat0 = generator.getBeat(440, rSong.getSoundTime(), 2 << 8, 8, 32);
        AudioFileManager beatMan0 = new AudioFileManager(beat0, beat0);
        float[] beat1 = generator.windowFunc(generator.getTone(440, 0, 44100 * 4), "sin(t)+1");


        beatMan0.pAdd(beat1, beat1, AudioFileManager.DEFAULT_SAMPLE_RATE * 2);
        beatMan0.pAdd(song0, song0, AudioFileManager.DEFAULT_SAMPLE_RATE * 4);
        beatMan0.pAdd(ramp, ramp, 0);

        beatMan0.buildFile("testing/music/sng.wav");
        mainWindow.buildWindow(beatMan0);
    }

}