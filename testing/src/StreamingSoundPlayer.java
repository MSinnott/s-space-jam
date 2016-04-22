import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.ArrayList;
import java.util.List;

public class StreamingSoundPlayer extends SoundPlayer {

    private AudioStreamWindow streamWindow;
    private final List<byte[]> streamComponents = new ArrayList<>();;
    private int sloc;

    public StreamingSoundPlayer(MainPane pane, AudioStreamWindow streamWindow){
        this.pane = pane;
        this.streamWindow = streamWindow;
        audioFile = new AudioFileManager(new float[1], new float[1]);
    }

    @Override
    public void run(){
        playing = true;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFile.getAudioFormat());
        try {
            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(audioFile.getAudioFormat());
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        streamWindow.queryNewLine();

        sourceLine.start();
        do {
            if (streamComponents.size() != 0) {
                streamWindow.queryNewLine();
                int streamLen = streamComponents.get(streamComponents.size() - 1).length;
                int index = streamComponents.size() - 1;
                while (sloc < streamLen / 2 && playing) {
                    sourceLine.write(streamComponents.get(index), sloc, FRAME_LEN);
                    loc += FRAME_LEN;
                    sloc += FRAME_LEN;
                }
                sloc = 0;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (playing);
    }

    public void addSound(byte[] b){
        streamComponents.add(b);
    }

    @Override
    public void stop(StopCode stype){
        playing = false;
        if(stype == StopCode.ENDSTREAM) streamWindow.convertToFileWindow();
    }

}
