import java.io.*;

public class baseRunner {

    public static void main(String[] args) throws IOException, InterruptedException {
        MusicGenerator generator = new MusicGenerator();
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

        float[] song0 = generator.generateSong(10);
        float[] song1 = generator.generateSong(10);
        AudioFileManager rSong = new AudioFileManager(song0, song1);

        rSong.buildFile("testing/music/BeepMusic/bM-" + songNum +".wav");
        mainWindow.buildWindow(rSong);
    }

}