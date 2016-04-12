import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.ArrayList;

public class StreamingSoundPlayer extends SoundPlayer {

    private AudioStreamWindow streamWindow;
    private ArrayList<byte[]> streamComponents;

    public StreamingSoundPlayer(MainPane pane, AudioStreamWindow streamWindow){
        this.pane = pane;
        this.streamWindow = streamWindow;
        streamComponents = new ArrayList<byte[]>();
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
            System.out.println("NO!" + streamComponents.size());
            if(streamComponents.size() != 0) {
                System.out.println("W");
                sourceLine.write(streamComponents.get(streamComponents.size() - 1), 0, streamComponents.get(streamComponents.size() - 1).length);
                streamWindow.queryNewLine();
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (playing);
    }

    public void addLine(byte[] bytes){
        streamComponents.add(bytes);
        System.out.println("Added: " + streamComponents.size());
    }
}
