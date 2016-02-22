import javax.sound.sampled.*;

public class SoundPlayer implements Runnable{

    private AudioFileManager audioFile;
    private final int FRAME_LEN = 10000;
    private SourceDataLine sourceLine;

    private MainPane pane;

    private int stIndex;
    private int endIndex;
    private int loc = 0;

    public SoundPlayer(AudioFileManager fileManager){
        audioFile = fileManager;
        stIndex = 0;
        endIndex = audioFile.getSoundData().length;
    }

    public void playFile(MainPane pane){
        playFile(pane, 0, audioFile.getChannels().length * audioFile.getChannels()[0].length);
    }

    public void playFile(MainPane pane, int stIndex, int endIndex){
        this.pane = pane;
        this.stIndex = stIndex;
        this.endIndex = endIndex;
        Thread playThread = new Thread(this);
        playThread.start();
    }

    @Override
    public void run() {
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
        for (int i = stIndex; i < endIndex; i+= FRAME_LEN, loc += FRAME_LEN) {
            sourceLine.write(soundData, i, FRAME_LEN);
            pane.updateLoc(loc);
            try { Thread.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
        }

        sourceLine.drain();
        sourceLine.close();
        loc = 0;
    }

}
