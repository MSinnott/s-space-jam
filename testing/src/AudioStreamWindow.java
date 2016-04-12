import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AudioStreamWindow extends InternalWindow implements Runnable{

    private AudioFileManager audioStreamFile;

    public AudioStreamWindow(int width, int height, AudioDesktop aDesk) {
        super(width, height, aDesk);
        resetColors();
        audioStreamFile = new AudioFileManager(new float[0], new float[0]);
    }

    public void beginStream(){
        Thread music = new Thread(this);
        music.start();
    }

    @Override
    public AudioFileManager getFileManager() {
        return audioStreamFile;
    }

    @Override
    public void run() {
        player = new SoundPlayer();
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
