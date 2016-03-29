import java.io.*;

public class baseRunner {

    public static void main(String[] args) throws IOException {
        MusicGenerator generator = new MusicGenerator(AudioFileManager.DEFAULT_SAMPLE_RATE, new Scale(7, 40));
        //making and initializing window --m
        AudioDesktop mainWindow = new AudioDesktop("sSpace -- Music Creator!", 600, 500);
    }

}