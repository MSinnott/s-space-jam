import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.ArrayList;
import java.util.List;

public class StreamingSoundPlayer extends SoundPlayer {

    private AudioStreamWindow streamWindow;
    private final static List<byte[]> streamComponents = new ArrayList<>();;

    public StreamingSoundPlayer(MainPane pane, AudioStreamWindow streamWindow){
        this.pane = pane;
        this.streamWindow = streamWindow;
        audioFile = new AudioFileManager(new float[1], new float[1]);
    }

    public List<byte[]> getStreamComponents(){
        return streamComponents;
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
                System.out.println(streamComponents.size() + " :(");

                if (streamComponents.size() != 0) {
                    System.out.println("W");
                    streamWindow.queryNewLine();
                    int slen = streamComponents.get(streamComponents.size() - 1).length;
                    sourceLine.write(streamComponents.get(streamComponents.size() - 1), 0,  slen/2 - ((slen/2) % 4));
                } else {
                    streamWindow.getView();
                }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (playing);
    }

}
