import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class StaticSoundPlayer extends SoundPlayer {

    public StaticSoundPlayer(AudioFileManager fileManager, MainPane pane){
        this.audioFile = fileManager;
        this.pane = pane;
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
}
