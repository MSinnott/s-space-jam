import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.*;
import java.util.ArrayList;

public class baseRunner {

    public static void main(String[] args) throws IOException {
        MusicGenerator generator = new MusicGenerator(AudioFileManager.DEFAULT_SAMPLE_RATE, new Scale(7, 40));
        //making and initializing window --m
        AudioDesktop mainWindow = new AudioDesktop("sSpace -- Music Creator!", 600, 500);

        AudioFileManager aman = generator.genNewComplexSong();

        mainWindow.addWindow(new SoundScriptingWindow(mainWindow));

        byte[] soundData = null;
        ArrayList<byte[]> songs = new ArrayList<byte[]>();
        final boolean[] addSong = {false};
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
                byte[] toWrite;
                songs.add(generator.genNewComplexSong().getSoundData());
                songs.add(generator.genNewComplexSong().getSoundData());
                sourceLine.start();
                do {
                    toWrite = songs.get(0);
                    sourceLine.write(toWrite, 0, toWrite.length);
                    Thread.yield();
                    addSong[0] = true;
                } while (true);
            }
        };
        audioThread.start();
        do {
            while(!addSong[0]){
                Thread.yield();
            }
            soundData = generator.genNewComplexSong().getSoundData();
            songs.add(soundData);
            songs.remove(0);
            addSong[0] = false;
        } while (true);
    }

}