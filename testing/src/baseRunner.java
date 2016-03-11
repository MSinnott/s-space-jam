import java.io.*;

public class baseRunner {

    public static void main(String[] args) throws IOException {
        MusicGenerator generator = new MusicGenerator(AudioFileManager.DEFAULT_SAMPLE_RATE);
        //making and initializing window --m
        AudioDesktop mainWindow = new AudioDesktop("sSpace -- Music Creator!", 600, 500);

        AudioFileManager song = generator.genNewComplexSong(32);
        mainWindow.buildWindow(song);
    }

}