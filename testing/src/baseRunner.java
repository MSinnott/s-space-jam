import java.io.*;

public class baseRunner {

    public static void main(String[] args) throws IOException, InterruptedException {
        MusicGenerator generator = new MusicGenerator();
        //making and initializing window --m
        AudioDesktop mainWindow = new AudioDesktop("sSpace -- Music Creator!", 600, 500);
        AudioFileManager oneTone = new AudioFileManager(generator.getStereoTone(300, 500, 8*44100));
        oneTone.buildFile("testing/music/singleTone.wav");
        mainWindow.buildWindow(oneTone);

        float[] song = generator.generateSong(10);
        AudioFileManager rSong = new AudioFileManager(song, song);
        rSong.buildFile("testing/music/rSong.wav");
        mainWindow.buildWindow(rSong);
    }

}