import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public abstract class InternalWindow extends JInternalFrame{

    protected MainPane pane;
    protected AudioDesktop audioDesktop;
    protected InternalWindow window = this;
    protected ArrayList<ColoredComponent> selectionComponents = new ArrayList<ColoredComponent>();
    protected ArrayList<ColoredComponent> components = new ArrayList<ColoredComponent>();
    protected boolean saved = false;
    protected String savePath = null;

    protected SoundPlayer player;
    protected AudioFileManager audioFile;

    protected SoundPlayer.StopCode stopType = SoundPlayer.StopCode.PAUSE;

    public InternalWindow(int width, int height, AudioDesktop aDesk){
        audioDesktop = aDesk;
        this.setSize(width, height);
        this.setResizable(true);
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        this.setVisible(true);

        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addInternalFrameListener(new WindowListener());

        pane = new MainPane(this);
        add(pane);

        buildMenus();
        resetColors();
    }

    public void makeMenuItem(JMenu menu, JMenuItem j, String fgColr, String bgColr, AbstractAction action, ArrayList<ColoredComponent> collection){
        j.addActionListener(action);
        collection.add(new ColoredComponent(j, fgColr, bgColr));
        menu.add(j);
    }

    public void resetColors(){
        components.forEach(ColoredComponent::resetColors);
        selectionComponents.forEach(ColoredComponent::resetColors);

        this.setBackground(Theme.getThemeColor("accColor"));
        this.setForeground(Theme.getThemeColor("txtColor"));

        this.invalidate();
        this.repaint();
        if(pane != null) {
            pane.invalidate();
            pane.repaint();
        }
    }

    public void updatePane(){
        pane.invalidate();
        pane.repaint();
        this.invalidate();
        this.repaint();
    }

    public AudioFileManager getFileManager(){
        return audioFile;
    }

    public abstract void buildMenus();

    public class ExitAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            AdaptiveDialog exitDialog = new AdaptiveDialog("Exit?");
            exitDialog.addDoneBinding(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    audioDesktop.removeWindow(window);
                    player.stop(SoundPlayer.StopCode.CLOSE);
                }
            });
            exitDialog.buildDialog(window);
        }
    }

    public class SaveAction extends AbstractAction{
        private String type;

        public SaveAction(String type){
            this.type = type;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(!saved || "SaveAs".equals(type)){
                File root;
                try {
                    root = new File(AudioDesktop.LinuxPathHead);
                } catch (Exception ex) {
                    root = new File(AudioDesktop.WindowsPathHead);
                }
                JFileChooser fileChooser = new JFileChooser(root);
                if (fileChooser.showSaveDialog(window) == JFileChooser.APPROVE_OPTION) {
                    try {
                        savePath = fileChooser.getSelectedFile().getAbsolutePath();
                        audioFile.buildFile(savePath);
                        saved = true;
                        String name = audioFile.getName();
                        window.setTitle(name);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            } else {
                try {
                    audioFile.buildFile(savePath);
                    String name = audioFile.getName();
                    window.setTitle(name);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public class CloneAction extends AbstractAction {
        private AudioFileWindow audioFileWindow;
        public CloneAction(AudioFileWindow window){
            audioFileWindow = window;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            AudioFileManager newAudioFile = new AudioFileManager(audioFile);
            newAudioFile.setDefaultName("Clone of - " + audioFile.getName());
            AudioFileWindow clone = new AudioFileWindow(audioFileWindow.getWidth(), audioFileWindow.getHeight(), newAudioFile, audioDesktop);
            audioDesktop.addWindow(clone);
            clone.moveToFront();
            clone.setLocation(audioFileWindow.getX() + 32, audioFileWindow.getY() + 32);
            clone.setView(pane.getPan(), pane.getZoom());
        }
    }

    public class StopAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e){
            player.stop(stopType);
        }
    }

    public class WindowListener implements InternalFrameListener {
        @Override
        public void internalFrameOpened(InternalFrameEvent internalFrameEvent) {

        }

        @Override
        public void internalFrameClosing(InternalFrameEvent internalFrameEvent) {
            ExitAction e = new ExitAction();
            e.actionPerformed(new ActionEvent(this, 0, ""));
        }

        @Override
        public void internalFrameClosed(InternalFrameEvent internalFrameEvent) {

        }

        @Override
        public void internalFrameIconified(InternalFrameEvent internalFrameEvent) {

        }

        @Override
        public void internalFrameDeiconified(InternalFrameEvent internalFrameEvent) {

        }

        @Override
        public void internalFrameActivated(InternalFrameEvent internalFrameEvent) {

        }

        @Override
        public void internalFrameDeactivated(InternalFrameEvent internalFrameEvent) {

        }
    }

}
