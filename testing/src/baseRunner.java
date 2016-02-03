import java.io.*;

public class baseRunner {

    public static void main(String[] args) throws IOException, InterruptedException {
        MusicGenerator generator = new MusicGenerator(15);
        //making and initializing window --m
        AudioDesktop mainWindow = new AudioDesktop("sSpace -- Music Creator!", 600, 500);
        //AudioFileManager oneTone = new AudioFileManager(generator.getStereoTone(20000, 500, 8*44100));
        //oneTone.buildFile("testing/music/singleTone.wav");
        //AudioFileManager oneToneMP3 = new AudioFileManager(getStereoTone(20000, 500, 8*44100));
        //oneToneMP3.buildFile("testing/music/singleTone.mp3");
        //mainWindow.buildWindow(oneTone);

        float[] song = generator.generateSong(10);
        AudioFileManager rSong = new AudioFileManager(song, song);
        rSong.buildFile("testing/music/rSong.wav");
        mainWindow.buildWindow(rSong);
    }

}