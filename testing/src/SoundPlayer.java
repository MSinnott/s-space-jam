import javax.sound.sampled.*;
import java.util.ArrayList;

public class SoundPlayer implements Runnable{

    private AudioFileManager audioFile;
    private final int FRAME_LEN = 10000;
    private SourceDataLine sourceLine;

    private MainPane pane;

    private int stIndex;
    private int endIndex;
    private int loc = 0;
    private boolean repeat = false;
    private boolean playing = false;

    public SoundPlayer(AudioFileManager fileManager, MainPane pane){
        this.pane = pane;
        audioFile = fileManager;
        stIndex = 0;
        endIndex = audioFile.getSoundData().length;
    }

    public SoundPlayer() {
        byte[] soundData = null;
        ArrayList<byte[]> songs = new ArrayList<byte[]>();
        final boolean[] addSong = {false};
        MusicGenerator generator = new MusicGenerator(AudioFileManager.DEFAULT_SAMPLE_RATE, new Scale(7, 40));
        AudioFileManager aman = generator.genNewComplexSong();
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

    public void playFile(){
        this.stIndex = 0;
        this.endIndex = -1;
        Thread playThread = new Thread(this);
        playThread.start();
    }

    public void playFile(int stIndex, int endIndex){
        this.stIndex = stIndex;
        this.endIndex = endIndex;
        if(playing) return;
        Thread playThread = new Thread(this);
        playThread.start();
    }

    @Override
    public void run() {
        playing = true;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFile.getAudioFormat());
        try {
            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(audioFile.getAudioFormat());
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            System.exit(1);
        }

        sourceLine.start();
        byte[] soundData = audioFile.getSoundData();
        do {
            if (stIndex < 0) stIndex = 0;
            if (endIndex < stIndex) endIndex = soundData.length / 2;
            for (int i = stIndex, loc = stIndex; i < endIndex && i < soundData.length; i += FRAME_LEN, loc += FRAME_LEN) {
                sourceLine.write(soundData, i, FRAME_LEN);
                pane.updateLoc(loc);
                Thread.yield();
                if (!pane.isDisplayable() || !playing) break;
            }
        } while (repeat && playing);
        playing = false;
        sourceLine.drain();
        sourceLine.close();
        loc = 0;
        pane.updateLoc(loc);
    }

    public void setRepeat(boolean repeat){
        this.repeat = repeat;
    }

    public void stop(){
        playing = false;
    }


}
