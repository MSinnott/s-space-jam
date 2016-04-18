import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

public class AudioStreamWindow extends InternalWindow implements Runnable {

    private AudioFileManager audioStreamFile;
    private MusicGenerator generator = new MusicGenerator(AudioFileManager.DEFAULT_SAMPLE_RATE);
    private StreamingSoundPlayer soundPlayer = new StreamingSoundPlayer(pane, this);

    private boolean streaming = true;

    public AudioStreamWindow(int width, int height, AudioDesktop aDesk) {
        super(width, height, aDesk);
        resetColors();
        audioStreamFile = new AudioFileManager(new float[0], new float[0]);
        pane = new MainPane(this);
        add(pane);
    }

    public void beginStream(){
        Thread music = new Thread(this);
        music.start();
    }

    @Override
    public AudioFileManager getFileManager() {
        return audioStreamFile;
    }

    private boolean needLine = true;

    @Override
    public void run() {
        player = new StreamingSoundPlayer(pane, this);
        Thread music = new Thread(player);
        music.start();
        List<byte[]> streamComponents = soundPlayer.getStreamComponents();
        while(streaming) {
            if(needLine){
                needLine = false;
                AudioFileManager songLine = generator.genNewComplexSong();
                songLine.trim();
                streamComponents.add(songLine.getSoundData());
                audioStreamFile.pAdd(songLine, true);

                pane.setAudioFile(audioStreamFile);
                updatePane();

                System.out.println(streamComponents.size() + "!");
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public String getView(){
        System.out.println(soundPlayer.getStreamComponents().size() + "?");
        return soundPlayer.getStreamComponents().size() + "?";
    }

    public void queryNewLine(){
        needLine = true;
    }

    public boolean isStreaming(){
        return streaming;
    }

    public void buildMenus(){
        JMenuBar menuBar = new JMenuBar();
        this.add(menuBar, BorderLayout.NORTH);
        components.add(new ColoredComponent(menuBar, "txtColor", "bgColor"));

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        components.add(new ColoredComponent(fileMenu,"txtColor", "bgColor"));

        makeMenuItem(fileMenu, new JMenuItem("Exit"), "txtColor", "bgColor", new ExitAction(), components);

        resetColors();
    }

    public class ExitAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            AdaptiveDialog exitDialog = new AdaptiveDialog("Exit?");
            exitDialog.addDoneBinding(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    audioDesktop.removeWindow(window);
                }
            });
            exitDialog.buildDialog(window);
        }
    }

}
