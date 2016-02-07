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

        float[] song0 = generator.generateSongV2(32, 12800);
        System.out.println("s");
        AudioFileManager rSong = new AudioFileManager(song0, song0);
        rSong.buildFile("testing/music/rsong.wav");
        mainWindow.buildWindow(rSong);

        float[] beat0 = generator.getBeat(8, rSong.getLength() / 4, .5f);
        AudioFileManager beatMan0 = new AudioFileManager(beat0, beat0);
        float[] beat1 = generator.getBeat(1, rSong.getLength() / 4, .25f);

        beatMan0.pAdd(beat1, beat1, AudioFileManager.DEFAULT_SAMPLE_RATE * 2);

        beatMan0.pAdd(song0, song0, AudioFileManager.DEFAULT_SAMPLE_RATE * 4);

        beatMan0.buildFile("testing/music/bman.wav");
        mainWindow.buildWindow(beatMan0);
    }

}