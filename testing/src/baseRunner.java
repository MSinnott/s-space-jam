import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.*;

public class baseRunner {

    public static void main(String[] args) throws IOException {
        MusicGenerator generator = new MusicGenerator(AudioFileManager.DEFAULT_SAMPLE_RATE);
        //making and initializing window --m
        AudioDesktop mainWindow = new AudioDesktop("sSpace -- Music Creator!", 600, 500);

        AudioFileManager aman = generator.genNewComplexSong();

        mainWindow.addWindow(new SoundScriptingWindow(mainWindow));

        byte[] soundData = null;
        final byte[] finalSoundData = new byte[10000000];
        Thread audioThread = new Thread() {
            @Override
            public void run() {
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, aman.getAudioFormat());
                SourceDataLine sourceLine = null;
                try {
                    sourceLine = (SourceDataLine) AudioSystem.getLine(info);
                    sourceLine.open(aman.getAudioFormat());
                } catch (LineUnavailableException e){
                    e.printStackTrace();
                    System.exit(1);
                }

                sourceLine.start();
                do {
                    sourceLine.write(finalSoundData, 0, finalSoundData.length);
                    Thread.yield();
                } while (true);
            }
        };
        audioThread.start();
        do {
            soundData = generator.genNewComplexSong().getSoundData();
            System.arraycopy(soundData, 0, finalSoundData, 0, finalSoundData.length);
        } while (true);
    }

}