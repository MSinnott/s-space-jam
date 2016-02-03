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
        AudioFileManager rSong0 = new AudioFileManager(song0, song0);

        System.out.println("0");

        float[] song1 = generator.generateSong(10);
        AudioFileManager rSong1 = new AudioFileManager(song1, song1);

        System.out.println("1");

        float[] song2 = generator.generateSong(10);
        AudioFileManager rSong2 = new AudioFileManager(song2, song2);

        System.out.println("2");

        rSong0.pAdd(rSong1);
        rSong0.pAdd(rSong2);

        rSong0.buildFile("testing/music/BeepMusic/bM-" + songNum +".wav");
        mainWindow.buildWindow(rSong0);
    }

}