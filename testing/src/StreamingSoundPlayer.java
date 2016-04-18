import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StreamingSoundPlayer extends SoundPlayer {

    private AudioStreamWindow streamWindow;
    private final List<byte[]> streamComponents = Collections.synchronizedList(new ArrayList<>());

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
                int slen = streamComponents.get(streamComponents.size() - 1).length;
                sourceLine.write(streamComponents.get(streamComponents.size() - 1), 0, slen / 2 - ((slen / 2) % 4));
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (playing);
    }

    public void addLine(byte[] b){
        streamComponents.add(b);
    }

}
