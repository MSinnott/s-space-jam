import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Arc2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AudioWindow extends JInternalFrame{

    private ArrayList<Component> components = new ArrayList<Component>();

    private MainPane pane;
    private AudioFileManager audioFile;
    private AudioDesktop audioDesktop;
    private AudioWindow audioWindow = this;

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
        JMenuBar menuBar = new JMenuBar();
        this.add(menuBar, BorderLayout.NORTH);
        components.add(menuBar);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        components.add(fileMenu);

        JMenuItem cloneButton = new JMenuItem("Clone");
        fileMenu.add(cloneButton);
        cloneButton.addActionListener(new CloneAction(this));
        components.add(cloneButton);

        JMenuItem saveButton = new JMenuItem("Save");
        fileMenu.add(saveButton);
        saveButton.addActionListener(new SaveAction("Save"));
        components.add(saveButton);

        JMenuItem saveAsButton = new JMenuItem("Save As ...");
        fileMenu.add(saveAsButton);
        saveAsButton.addActionListener(new SaveAction("SaveAs"));
        components.add(saveAsButton);

        JMenuItem exitButton = new JMenuItem("Exit");
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
        components.add(exitButton);

        JMenu opMenu = new JMenu("Operations");
        menuBar.add(opMenu);
        components.add(opMenu);

        JMenuItem ftransformButton = new JMenuItem("Forward FFT");
        opMenu.add(ftransformButton);
        ftransformButton.addActionListener(new forwardFFtAction());
        components.add(ftransformButton);

        JMenuItem btransformButton = new JMenuItem("Backward FFT");
        opMenu.add(btransformButton);
        btransformButton.addActionListener(new backwardFFtAction());
        components.add(btransformButton);

        JMenuItem vscaleButton = new JMenuItem("Scale Vertically");
        opMenu.add(vscaleButton);
        vscaleButton.addActionListener(new vscaleAction());
        components.add(vscaleButton);

        JMenuItem vshiftButton = new JMenuItem("Shift Vertically");
        opMenu.add(vshiftButton);
        vshiftButton.addActionListener(new vshiftAction());
        components.add(vshiftButton);

        JMenuItem hscaleButton = new JMenuItem("Scale Horizontally");
        opMenu.add(hscaleButton);
        hscaleButton.addActionListener(new hscaleAction());
        components.add(hscaleButton);

        JMenuItem hshiftButton = new JMenuItem("Shift Horizontally");
        opMenu.add(hshiftButton);
        hshiftButton.addActionListener(new hshiftAction());
        components.add(hshiftButton);

        JMenuItem pbpAdd = new JMenuItem("Point-by-Point Add");
        opMenu.add(pbpAdd);
        pbpAdd.addActionListener(new pbpAddAction());
        components.add(pbpAdd);

        JMenuItem pbpMult = new JMenuItem("Point-by-Point Multiply");
        opMenu.add(pbpMult);
        pbpMult.addActionListener(new pbpMultAction());
        components.add(pbpMult);

        resetColors();
    }

    public void resetColors(){
        for(Component c: components) {
            c.setForeground(AudioDesktop.theme[5]);
            c.setBackground(AudioDesktop.theme[0]);
        }

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

    public class vscaleAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            final AdaptiveDialog scaleDialog = new AdaptiveDialog("Scale Vertically");
            final JTextField textField = new JTextField();
            scaleDialog.addItem(textField, 0, 5, false);
            scaleDialog.addDoneBinding(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    audioFile.vscale(Float.valueOf(textField.getText()));
                    updatePane();
                }
            });
            scaleDialog.buildDialog(audioWindow);
        }
    }

    public class vshiftAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            final AdaptiveDialog shiftDialog = new AdaptiveDialog("Shift Vertically");
            final JTextField textField = new JTextField();
            shiftDialog.addItem(textField, 0, 5, false);
            shiftDialog.addDoneBinding(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    audioFile.vshift(Float.valueOf(textField.getText()));
                    updatePane();
                }
            });
            shiftDialog.buildDialog(audioWindow);
        }
    }

    public class hscaleAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            final AdaptiveDialog shiftDialog = new AdaptiveDialog("Scale Horizontally");
            final JTextField textField = new JTextField();
            shiftDialog.addItem(textField, 0, 5, false);
            shiftDialog.addDoneBinding(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    audioFile.hscale(Float.valueOf(textField.getText()));
                    updatePane();
                }
            });
            shiftDialog.buildDialog(audioWindow);
        }
    }

    public class hshiftAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            final AdaptiveDialog shiftDialog = new AdaptiveDialog("Shift Horizontally");
            final JTextField textField = new JTextField();
            shiftDialog.addItem(textField, 0, 5, false);
            shiftDialog.addDoneBinding(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    audioFile.hshift(Integer.valueOf(textField.getText()));
                    updatePane();
                }
            });
            shiftDialog.buildDialog(audioWindow);
        }
    }

    public class pbpAddAction extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setBackground(AudioDesktop.theme[0]);
            fileChooser.setForeground(AudioDesktop.theme[5]);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "WAV Files", "wav", "mp3 Files", "mp3");
            //need to throw an if mp3 file, call decode function. Use Jlayer / MP3SPI library
            //looks like .au and .aiff files are already supported.
            fileChooser.setFileFilter(filter);
            int returnVal = fileChooser.showOpenDialog(audioWindow);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                AudioFileManager selection = new AudioFileManager(fileChooser.getSelectedFile());
                audioFile.pAdd(selection.getLeftChannel(), selection.getRightChannel());
                updatePane();
            }
        }
    }

    public class pbpMultAction extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setBackground(AudioDesktop.theme[0]);
            fileChooser.setForeground(AudioDesktop.theme[5]);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "WAV Files", "wav", "mp3 Files", "mp3");
            //need to throw an if mp3 file, call decode function. Use Jlayer / MP3SPI library
            //looks like .au and .aiff files are already supported.
            fileChooser.setFileFilter(filter);
            int returnVal = fileChooser.showOpenDialog(audioWindow);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                AudioFileManager selection = new AudioFileManager(fileChooser.getSelectedFile());
                audioFile.pMult(selection.getLeftChannel(), selection.getRightChannel());
                updatePane();
            }
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