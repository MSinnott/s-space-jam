import java.io.*;

public class baseRunner {

    public static void main(String[] args) throws IOException {
        MusicGenerator generator = new MusicGenerator(AudioFileManager.DEFAULT_SAMPLE_RATE);
        //making and initializing window --m
        AudioDesktop mainWindow = new AudioDesktop("sSpace -- Music Creator!", 600, 500);

        mainWindow.buildWindow(generator.genNewComplexSong());
        float[] arr = generator.getBeat(164, 16, 32, 3, 32);
        mainWindow.buildWindow(new AudioFileManager(arr, arr));
    }

}