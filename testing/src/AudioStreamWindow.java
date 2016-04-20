import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AudioStreamWindow extends InternalWindow implements Runnable {

    private MusicGenerator generator = new MusicGenerator(AudioFileManager.DEFAULT_SAMPLE_RATE);
    private boolean needLine = true;

    private boolean streaming = true;

    public AudioStreamWindow(int width, int height, AudioDesktop aDesk) {
        super(width, height, aDesk);
        audioFile = new AudioFileManager(new float[0], new float[0]);
        stopType = SoundPlayer.StopCode.ENDSTREAM;
    }

    public void beginStream(){
        Thread music = new Thread(this);
        music.start();
    }

    @Override
    public void run() {
        player = new StreamingSoundPlayer(pane, this);
        Thread music = new Thread(player);
        music.start();
        while(streaming) {
            if(needLine){
                needLine = false;
                AudioFileManager songLine = generator.genNewComplexSong();
                songLine.trim();
                player.addSound(songLine.getSoundData());
                audioFile.pAdd(songLine, true);

                pane.setAudioFile(audioFile);
                updatePane();
            }
            pane.updateLoc(player.getLoc());
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void queryNewLine(){
        needLine = true;
    }

    public void buildMenus(){
        JMenuBar menuBar = new JMenuBar();
        this.add(menuBar, BorderLayout.NORTH);
        components.add(new ColoredComponent(menuBar, "txtColor", "bgColor"));

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        components.add(new ColoredComponent(fileMenu,"txtColor", "bgColor"));

        makeMenuItem(fileMenu, new JMenuItem("Exit"), "txtColor", "bgColor", new ExitAction(), components);

        JMenu playMenu = new JMenu("\t▶");
        menuBar.add(playMenu);
        components.add(new ColoredComponent(playMenu,"txtColor", "bgColor"));

        makeMenuItem(playMenu, new JMenuItem("Stop"), "txtColor", "bgColor", new StopAction(), components);

        resetColors();
    }

    public void convertToFileWindow(){
        AudioFileWindow aw = new AudioFileWindow(this.getWidth(), this.getHeight(), audioFile, audioDesktop);
        audioDesktop.removeWindow(this);
        audioDesktop.addWindow(aw);
    }

}
