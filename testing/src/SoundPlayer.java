import javax.sound.sampled.*;

public abstract class SoundPlayer implements Runnable{

    protected AudioFileManager audioFile;
    protected final int FRAME_LEN = 1000;
    protected SourceDataLine sourceLine;

    protected MainPane pane;

    protected int stIndex;
    protected int endIndex;
    protected int loc = 0;
    protected boolean repeat = false;
    protected boolean playing = false;

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

    public void setRepeat(boolean repeat){
        this.repeat = repeat;
    }

    public void stop(StopCode stype){
        playing = false;
    }

    public int getLoc(){
        return loc;
    }

    public abstract void addSound(byte[] b);

    public enum StopCode { CLOSE, PAUSE, ENDSTREAM}

}
