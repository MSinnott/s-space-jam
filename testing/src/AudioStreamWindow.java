import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

public class AudioStreamWindow extends InternalWindow implements Runnable{

    public AudioStreamWindow(int width, int height, AudioDesktop aDesk) {
        super(width, height, aDesk);
        resetColors();
    }

    public void beginStream(){
        Thread music = new Thread(this);
        music.start();
    }

    @Override
    public AudioFileManager getFileManager() {
        return null;
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
