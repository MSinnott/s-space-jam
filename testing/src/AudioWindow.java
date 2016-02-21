import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
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

    private JMenu selectionMenu;

    public AudioWindow(int width, int height, AudioFileManager fileManager, AudioDesktop aDesk){
        super(fileManager.getName());

        audioDesktop = aDesk;

        this.setSize(width, height);
        this.setResizable(true);

        this.setLayout(new BorderLayout());

        pane = new MainPane(this);
        loadFile(fileManager);
        savePath = fileManager.getPath();
        if(savePath != null) saved = true;

        Container c = this.getContentPane();
        c.setLayout(new BorderLayout());

        c.add(pane, BorderLayout.CENTER);
        c.addKeyListener(pane);
        c.addMouseListener(pane);
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

        JMenuItem fsteptransformButton = new JMenuItem("Step Forward FFT");
        opMenu.add(fsteptransformButton);
        fsteptransformButton.addActionListener(new stepforwardFFtAction());
        components.add(fsteptransformButton);

        JMenuItem btransformButton = new JMenuItem("Backward FFT");
        opMenu.add(btransformButton);
        btransformButton.addActionListener(new backwardFFtAction());
        components.add(btransformButton);

        JMenuItem vscaleButton = new JMenuItem("Scale Vertically");
        opMenu.add(vscaleButton);
        vscaleButton.addActionListener(new vscaleAction(false));
        components.add(vscaleButton);

        JMenuItem vshiftButton = new JMenuItem("Shift Vertically");
        opMenu.add(vshiftButton);
        vshiftButton.addActionListener(new vshiftAction(false));
        components.add(vshiftButton);

        JMenuItem pbpAdd = new JMenuItem("Point-by-Point Add");
        opMenu.add(pbpAdd);
        pbpAdd.addActionListener(new pbpAddAction());
        components.add(pbpAdd);

        JMenuItem pbpMult = new JMenuItem("Point-by-Point Multiply");
        opMenu.add(pbpMult);
        pbpMult.addActionListener(new pbpMultAction());
        components.add(pbpMult);

        JMenuItem trimButton = new JMenuItem("Trim");
        opMenu.add(trimButton);
        trimButton.addActionListener(new TrimAction());
        components.add(trimButton);

        JMenuItem boxcarFilterButton = new JMenuItem("Boxcar Filter");
        opMenu.add(boxcarFilterButton);
        boxcarFilterButton.addActionListener(new BoxcarFilterAction(false));
        components.add(boxcarFilterButton);

        selectionMenu = new JMenu("Edit Selection");
        menuBar.add(selectionMenu);
        components.add(selectionMenu);

        JMenuItem zeroSelect = new JMenuItem("Zero Selected");
        selectionMenu.add(zeroSelect);
        zeroSelect.addActionListener(new ZeroSelectedAction());
        components.add(zeroSelect);

        JMenuItem zeroDeSelect = new JMenuItem("Zero Non-Selected");
        selectionMenu.add(zeroDeSelect);
        zeroDeSelect.addActionListener(new ZeroNonSelectedAction());
        components.add(zeroDeSelect);

        JMenuItem vscaleSelectButton = new JMenuItem("Scale Selection Vertically");
        selectionMenu.add(vscaleSelectButton);
        vscaleSelectButton.addActionListener(new vscaleAction(true));
        components.add(vscaleSelectButton);

        JMenuItem vshiftSelectButton = new JMenuItem("Shift Selection Vertically");
        selectionMenu.add(vshiftSelectButton);
        vshiftSelectButton.addActionListener(new vshiftAction(true));
        components.add(vshiftSelectButton);

        JMenuItem zoomToSelection = new JMenuItem("Zoom to Selection");
        selectionMenu.add(zoomToSelection);
        zoomToSelection.addActionListener(new ZoomToSelectionAction());
        components.add(zoomToSelection);

        JMenuItem boxcarSelectionFilterButton = new JMenuItem("Boxcar Filter");
        selectionMenu.add(boxcarSelectionFilterButton);
        boxcarSelectionFilterButton.addActionListener(new BoxcarFilterAction(true));
        components.add(boxcarSelectionFilterButton);

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
                    String name = audioFile.getName();
                    audioWindow.setTitle(name);
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
        private boolean toSelection = false;
        public vscaleAction(boolean toSelection){
            this.toSelection = toSelection;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            final AdaptiveDialog scaleDialog = new AdaptiveDialog("Scale Vertically");
            final JTextField textField = new JTextField();
            scaleDialog.addItem(textField, 0, 5, false);
            scaleDialog.addDoneBinding(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(toSelection) {
                        audioFile.vscale(Float.valueOf(textField.getText()), (int) pane.getSelection()[0], (int) pane.getSelection()[1]);
                    } else {
                        audioFile.vscale(Float.valueOf(textField.getText()));
                    }
                    updatePane();
                }
            });
            scaleDialog.buildDialog(audioWindow);
        }
    }

    public class vshiftAction extends AbstractAction {
        private boolean toSelection = false;
        public vshiftAction(boolean toSelection){
            this.toSelection = toSelection;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            final AdaptiveDialog shiftDialog = new AdaptiveDialog("Shift Vertically");
            final JTextField textField = new JTextField();
            shiftDialog.addItem(textField, 0, 5, false);
            shiftDialog.addDoneBinding(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(toSelection) {
                        audioFile.vshift(Float.valueOf(textField.getText()), (int) pane.getSelection()[0], (int) pane.getSelection()[1]);
                    } else {
                        audioFile.vshift(Float.valueOf(textField.getText()));
                    }
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
                audioFile.pAdd(selection.getChannels(), 0);
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
                audioFile.pMult(selection.getChannels());
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

    public class stepforwardFFtAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            final AdaptiveDialog stepDialog = new AdaptiveDialog("Step FFT");
            final JTextField textField = new JTextField();
            stepDialog.addItem(textField, 0, 5, false);
            stepDialog.addDoneBinding(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("!");
                    audioFile.smallFFT(Integer.valueOf(textField.getText()));
                    updatePane();
                }
            });
            stepDialog.buildDialog(audioWindow);
            System.out.println("????");
        }
    }

    public class backwardFFtAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            audioFile.btransform();
            updatePane();
        }
    }

    public class TrimAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            audioFile.trim();
            updatePane();
        }
    }

    public class BoxcarFilterAction extends AbstractAction {
        private boolean toSelection = false;
        public BoxcarFilterAction(boolean toSelection){
            this.toSelection = toSelection;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            final AdaptiveDialog filterDialog = new AdaptiveDialog("Boxcar Filter");
            final JTextField textField = new JTextField();
            filterDialog.addItem(textField, 0, 5, false);
            filterDialog.addDoneBinding(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(toSelection) {
                        audioFile.boxcarFilter(Integer.valueOf(textField.getText()), (int) pane.getSelection()[0], (int) pane.getSelection()[1]);
                    } else {
                        audioFile.boxcarFilter(Integer.valueOf(textField.getText()));
                    }
                    updatePane();
                }
            });
            filterDialog.buildDialog(audioWindow);
        }
    }

    public class ZeroSelectedAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e){
            float[] selection = pane.getSelection();
            audioFile.zeroFrom((int) selection[0], (int) selection[1]);
            updatePane();
        }
    }

    public class ZeroNonSelectedAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e){
            float[] selection = pane.getSelection();
            audioFile.zeroFrom(0, (int) selection[0]);
            audioFile.zeroFrom((int) selection[1], Integer.MAX_VALUE);
            updatePane();
        }
    }

    public class ZoomToSelectionAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e){
            float[] selection = pane.getSelection();
            pane.rescale((int) selection[0], (int) selection[1]);
            updatePane();
        }
    }

    public void updatePane(){
        pane.setAudioFile(audioFile);
        pane.invalidate();
        pane.repaint();
        this.invalidate();
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g){
        if(pane.getSelection()[0] ==  pane.getSelection()[1]){
            selectionMenu.setVisible(false);
        } else {
            selectionMenu.setVisible(true);
        }
    }

}