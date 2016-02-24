import javax.sound.sampled.*;

public class SoundPlayer implements Runnable{

    private AudioFileManager audioFile;
    private final int FRAME_LEN = 10000;
    private SourceDataLine sourceLine;

    private MainPane pane;

    private int stIndex;
    private int endIndex;
    private int loc = 0;
    private boolean repeat = false;
    private boolean stopPlaying = false;

    public SoundPlayer(AudioFileManager fileManager){
        audioFile = fileManager;
        stIndex = 0;
        endIndex = audioFile.getSoundData().length;
    }

    public void playFile(MainPane pane){
        this.pane = pane;
        this.stIndex = 0;
        this.endIndex = -1;
        Thread playThread = new Thread(this);
        playThread.start();
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

        System.out.println("enter");
        do {
            if (stIndex < 0) stIndex = 0;
            if (endIndex < stIndex) endIndex = soundData.length / 2;
            for (int i = stIndex, loc = stIndex; i < endIndex && i < soundData.length; i += FRAME_LEN, loc += FRAME_LEN) {
                sourceLine.write(soundData, i, FRAME_LEN);
                pane.updateLoc(loc);
                Thread.yield();
                if(stopPlaying) break;
            }
        } while(repeat && !stopPlaying);
        stopPlaying = false;
        sourceLine.drain();
        sourceLine.close();
        loc = 0;
        pane.updateLoc(loc);
    }

    public void setRepeat(boolean repeat){
        this.repeat = repeat;
    }

    public void stop(){
        stopPlaying = true;
    }


}
