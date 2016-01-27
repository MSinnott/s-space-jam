import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class AudioWindow extends JInternalFrame{

    private MainPane pane;
    private AudioFileManager audioFile;
    private AudioDesktop audioDesktop;
    private AudioWindow audioWindow = this;

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem cloneButton;
    private JMenuItem saveButton;
    private JMenuItem saveAsButton;
    private JMenuItem exitButton;
    private JMenu opMenu;
    private JMenuItem ftransformButton;
    private JMenuItem btransformButton;
    private JMenuItem scaleButton;

    private boolean saved = false;
    private String savePath = null;

    public AudioWindow(int width, int height, AudioFileManager fileManager, AudioDesktop aDesk){
        super(fileManager.getName());

        audioDesktop = aDesk;

        this.setSize(width, height);
        this.setResizable(true);

        this.setLayout(new BorderLayout());

        pane = new MainPane();
        loadFile(fileManager);
        savePath = fileManager.getPath();
        if(savePath != null) saved = true;

        Container c = this.getContentPane();
        c.setLayout(new BorderLayout());

        c.add(pane, BorderLayout.CENTER);
        c.addKeyListener(pane);
        pane.addKeyListener(pane);

        buildMenus();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        this.setSize(480, 360);
        this.setVisible(true);
    }

    public void loadFile(AudioFileManager fileManager){
        audioFile = fileManager;
        updatePane();
    }

    public void buildMenus(){
        menuBar = new JMenuBar();

        this.add(menuBar, BorderLayout.NORTH);

        fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        cloneButton = new JMenuItem("Clone");
        fileMenu.add(cloneButton);
        cloneButton.addActionListener(new CloneAction(this));

        saveButton = new JMenuItem("Save");
        fileMenu.add(saveButton);
        saveButton.addActionListener(new SaveAction("Save"));

        saveAsButton = new JMenuItem("Save As ...");
        fileMenu.add(saveAsButton);
        saveAsButton.addActionListener(new SaveAction("SaveAs"));

        exitButton = new JMenuItem("Exit");
        fileMenu.add(exitButton);
        exitButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdaptiveDialog exitDialog = new AdaptiveDialog("Exit?");
                exitDialog.addDoneBinding(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dispose();
                    }
                });
                exitDialog.buildDialog(audioWindow);
            }
        });

        opMenu = new JMenu("Operations");
        menuBar.add(opMenu);

        ftransformButton = new JMenuItem("Forward FFT");
        opMenu.add(ftransformButton);
        ftransformButton.addActionListener(new forwardFFtAction());

        btransformButton = new JMenuItem("Backward FFT");
        opMenu.add(btransformButton);
        btransformButton.addActionListener(new backwardFFtAction());

        scaleButton = new JMenuItem("Scale Vertically");
        opMenu.add(scaleButton);
        scaleButton.addActionListener(new scaleAction());

        resetColors();
    }

    public void resetColors(){
        menuBar.setBackground(AudioDesktop.theme[0]);
        menuBar.setForeground(AudioDesktop.theme[5]);
        fileMenu.setBackground(AudioDesktop.theme[0]);
        fileMenu.setForeground(AudioDesktop.theme[5]);
        saveButton.setBackground(AudioDesktop.theme[0]);
        saveButton.setForeground(AudioDesktop.theme[5]);
        saveAsButton.setBackground(AudioDesktop.theme[0]);
        saveAsButton.setForeground(AudioDesktop.theme[5]);
        cloneButton.setBackground(AudioDesktop.theme[0]);
        cloneButton.setForeground(AudioDesktop.theme[5]);
        exitButton.setBackground(AudioDesktop.theme[0]);
        exitButton.setForeground(AudioDesktop.theme[5]);
        opMenu.setBackground(AudioDesktop.theme[0]);
        opMenu.setForeground(AudioDesktop.theme[5]);
        ftransformButton.setBackground(AudioDesktop.theme[0]);
        ftransformButton.setForeground(AudioDesktop.theme[5]);
        btransformButton.setBackground(AudioDesktop.theme[0]);
        btransformButton.setForeground(AudioDesktop.theme[5]);
        scaleButton.setBackground(AudioDesktop.theme[0]);
        scaleButton.setForeground(AudioDesktop.theme[5]);
        this.setBackground(AudioDesktop.theme[2]);
        this.setForeground(AudioDesktop.theme[5]);

        this.invalidate();
        this.repaint();
        pane.invalidate();
        pane.repaint();
    }

    public void setView(int pan, double zoom){
        pane.setPan(pan);
        pane.setZoom(zoom);
    }

    //Saves the file
    public class SaveAction extends AbstractAction{
        private String type;

        public SaveAction(String type){
            this.type = type;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(!saved || "SaveAs".equals(type)){
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showSaveDialog(audioWindow) == JFileChooser.APPROVE_OPTION) {
                    try {
                        savePath = fileChooser.getSelectedFile().getAbsolutePath();
                        audioFile.buildFile(savePath);
                        saved = true;
                        String name = audioFile.getName();
                        audioWindow.setTitle(name);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            } else {
                try {
                    audioFile.buildFile(savePath);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    //Creates + adds a clone of this object to the desktop
    public class CloneAction extends AbstractAction {
        private AudioWindow audioWindow;
        public CloneAction(AudioWindow window){
            audioWindow = window;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            AudioFileManager newAudioFile = new AudioFileManager(audioFile);
            newAudioFile.setDefaultName("Clone of - " + audioFile.getName());
            AudioWindow clone = new AudioWindow(audioWindow.getWidth(), audioWindow.getHeight(), newAudioFile, audioDesktop);
            audioDesktop.addWindow(clone);
            clone.moveToFront();
            clone.setLocation(audioWindow.getX() + 32, audioWindow.getY() + 32);
            clone.setView(pane.getPan(), pane.getZoom());
        }
    }

    public class scaleAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            final AdaptiveDialog scaleDialog = new AdaptiveDialog("Scale Action");
            final JTextField textField = new JTextField();
            scaleDialog.addItem(textField, 0, 5, false);
            scaleDialog.addDoneBinding(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    audioFile.scale(Double.valueOf(textField.getText()));
                    updatePane();
                }
            });
            scaleDialog.buildDialog(audioWindow);
        }
    }

    public class forwardFFtAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            audioFile.ftransform();
            updatePane();
        }
    }

    public class backwardFFtAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            audioFile.btransform();
            updatePane();
        }
    }

    public void updatePane(){
        pane.setLeftData(audioFile.getLeftChannel());
        pane.setRightData(audioFile.getRightChannel());
        pane.invalidate();
        pane.repaint();
        this.invalidate();
        this.repaint();
    }

}
