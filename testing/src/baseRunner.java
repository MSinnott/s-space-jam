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

        float[] song0 = generator.generateSongV1(3);
        System.out.println("s");
        AudioFileManager rSong = new AudioFileManager(song0, song0);

        float[] beat = generator.getBeat(8, rSong.getLength(), 3);
        AudioFileManager beatMan = new AudioFileManager(beat, beat);

        beatMan.pAdd(song0, song0, AudioFileManager.DEFAULT_SAMPLE_RATE);

        beatMan.buildFile("testing/music/rsong.wav");
        mainWindow.buildWindow(beatMan);
    }

}