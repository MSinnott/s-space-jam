import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;

public class AudioWindow extends JInternalFrame{

    private MainPane pane;
    private AudioFileManager audioFile;
    private AudioDesktop audioDesktop;
    private AudioWindow audioWindow = this;

    private SoundPlayer player;

    private boolean saved = false;
    private String savePath = null;

    private ArrayList<ColoredComponent> selectionComponents = new ArrayList<ColoredComponent>();
    private ArrayList<ColoredComponent> components = new ArrayList<ColoredComponent>();

    public AudioWindow(int width, int height, AudioFileManager fileManager, AudioDesktop aDesk){
        super(fileManager.getName());

        player = new SoundPlayer(fileManager);
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

    public MainPane getMainPane(){
        return pane;
    }

    public AudioFileManager getFileManager(){
        return audioFile;
    }

    public void buildMenus(){
        JMenuBar menuBar = new JMenuBar();
        this.add(menuBar, BorderLayout.NORTH);
        components.add(new ColoredComponent(menuBar, 5, 0));

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        components.add(new ColoredComponent(fileMenu, 5, 0));

        JMenuItem cloneButton = new JMenuItem("Clone");
        fileMenu.add(cloneButton);
        cloneButton.addActionListener(new CloneAction(this));
        components.add(new ColoredComponent(cloneButton, 5, 0));

        JMenuItem saveButton = new JMenuItem("Save");
        fileMenu.add(saveButton);
        saveButton.addActionListener(new SaveAction("Save"));
        components.add(new ColoredComponent(saveButton, 5, 0));

        JMenuItem saveAsButton = new JMenuItem("Save As ...");
        fileMenu.add(saveAsButton);
        saveAsButton.addActionListener(new SaveAction("SaveAs"));
        components.add(new ColoredComponent(saveAsButton, 5, 0));

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
        components.add(new ColoredComponent(exitButton, 5, 0));

        JMenu opMenu = new JMenu("Operations");
        menuBar.add(opMenu);
        components.add(new ColoredComponent(opMenu, 5, 0));

        JMenuItem ftransformButton = new JMenuItem("Forward FFT");
        opMenu.add(ftransformButton);
        ftransformButton.addActionListener(new forwardFFtAction());
        components.add(new ColoredComponent(ftransformButton, 5, 0));

        JMenuItem fsteptransformButton = new JMenuItem("Step Forward FFT");
        opMenu.add(fsteptransformButton);
        fsteptransformButton.addActionListener(new stepforwardFFtAction());
        components.add(new ColoredComponent(fsteptransformButton, 5, 0));

        JMenuItem btransformButton = new JMenuItem("Backward FFT");
        opMenu.add(btransformButton);
        btransformButton.addActionListener(new backwardFFtAction());
        components.add(new ColoredComponent(btransformButton, 5, 0));

        JMenuItem vscaleButton = new JMenuItem("Scale Vertically");
        opMenu.add(vscaleButton);
        vscaleButton.addActionListener(new vscaleAction(false));
        components.add(new ColoredComponent(vscaleButton, 5, 0));

        JMenuItem vshiftButton = new JMenuItem("Shift Vertically");
        opMenu.add(vshiftButton);
        vshiftButton.addActionListener(new vshiftAction(false));
        components.add(new ColoredComponent(vshiftButton, 5, 0));

        JMenuItem pbpAdd = new JMenuItem("Point-by-Point Add");
        opMenu.add(pbpAdd);
        pbpAdd.addActionListener(new pbpAddAction());
        components.add(new ColoredComponent(pbpAdd, 5, 0));

        JMenuItem pbpMult = new JMenuItem("Point-by-Point Multiply");
        opMenu.add(pbpMult);
        pbpMult.addActionListener(new pbpMultAction());
        components.add(new ColoredComponent(pbpMult, 5, 0));

        JMenuItem trimButton = new JMenuItem("Trim");
        opMenu.add(trimButton);
        trimButton.addActionListener(new TrimAction());
        components.add(new ColoredComponent(trimButton, 5, 0));

        JMenuItem boxcarFilterButton = new JMenuItem("Boxcar Filter");
        opMenu.add(boxcarFilterButton);
        boxcarFilterButton.addActionListener(new BoxcarFilterAction(false));
        components.add(new ColoredComponent(boxcarFilterButton, 5, 0));

        JMenuItem filterButton = new JMenuItem("Threshold Filter");
        opMenu.add(filterButton);
        filterButton.addActionListener(new FilterThresholdAction(false));
        components.add(new ColoredComponent(filterButton, 5, 0));

        JMenu selectionMenu = new JMenu("Edit Selection");
        menuBar.add(selectionMenu);
        selectionComponents.add(new ColoredComponent(selectionMenu, 5, 0));

        JMenuItem zeroSelect = new JMenuItem("Zero Selected");
        selectionMenu.add(zeroSelect);
        zeroSelect.addActionListener(new ZeroSelectedAction());
        selectionComponents.add(new ColoredComponent(zeroSelect, 5, 0));

        JMenuItem zeroDeSelect = new JMenuItem("Zero Non-Selected");
        selectionMenu.add(zeroDeSelect);
        zeroDeSelect.addActionListener(new ZeroNonSelectedAction());
        selectionComponents.add(new ColoredComponent(zeroDeSelect, 5, 0));

        JMenuItem vscaleSelectButton = new JMenuItem("Scale Selection Vertically");
        selectionMenu.add(vscaleSelectButton);
        vscaleSelectButton.addActionListener(new vscaleAction(true));
        selectionComponents.add(new ColoredComponent(vscaleSelectButton, 5, 0));

        JMenuItem vshiftSelectButton = new JMenuItem("Shift Selection Vertically");
        selectionMenu.add(vshiftSelectButton);
        vshiftSelectButton.addActionListener(new vshiftAction(true));
        selectionComponents.add(new ColoredComponent(vshiftSelectButton, 5, 0));

        JMenuItem zoomToSelection = new JMenuItem("Zoom to Selection");
        selectionMenu.add(zoomToSelection);
        zoomToSelection.addActionListener(new ZoomToSelectionAction());
        selectionComponents.add(new ColoredComponent(zoomToSelection, 5, 0));

        JMenuItem boxcarSelectionFilterButton = new JMenuItem("Boxcar Filter");
        selectionMenu.add(boxcarSelectionFilterButton);
        boxcarSelectionFilterButton.addActionListener(new BoxcarFilterAction(true));
        selectionComponents.add(new ColoredComponent(boxcarSelectionFilterButton, 5, 0));

        JMenuItem filterSelectionButton = new JMenuItem("Threshold Filter");
        selectionMenu.add(filterSelectionButton);
        filterSelectionButton.addActionListener(new FilterThresholdAction(true));
        selectionComponents.add(new ColoredComponent(filterSelectionButton, 5, 0));

        JMenu playMenu = new JMenu("\t▶");
        menuBar.add(playMenu);
        components.add(new ColoredComponent(playMenu, 5, 0));

        JMenuItem playButton = new JMenuItem("\t▶ File");
        playMenu.add(playButton);
        playButton.addActionListener(new PlayAction());
        components.add(new ColoredComponent(playButton, 5, 0));

        JMenuItem playSelectedButton = new JMenuItem("\t▶ Selected");
        playMenu.add(playSelectedButton);
        playSelectedButton.addActionListener(new PlaySelectedAction());
        selectionComponents.add(new ColoredComponent(playSelectedButton, 5, 0));

        JCheckBoxMenuItem repeatButton = new JCheckBoxMenuItem("Repeat?");
        playMenu.add(repeatButton);
        repeatButton.addActionListener(new RepeatAction(repeatButton));
        components.add(new ColoredComponent(repeatButton, 5, 0));

        JMenuItem stopButton = new JMenuItem("Stop");
        playMenu.add(stopButton);
        stopButton.addActionListener(new StopAction());
        components.add(new ColoredComponent(stopButton, 5, 0));

        resetColors();
    }

    public void resetColors(){
        components.forEach(ColoredComponent::resetColors);
        selectionComponents.forEach(ColoredComponent::resetColors);

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
            final AdaptiveDialog addDialog = new AdaptiveDialog("Scale Vertically");
            final ArrayList<AudioFileManager> toAdd = new ArrayList<AudioFileManager>();
            for(AudioWindow aw : audioDesktop.getAudioWindows()){
                JButton jTextButton = new JButton(aw.getFileManager().getName());
                ColoredComponent coloredComp = new ColoredComponent(jTextButton, 5, 0);
                jTextButton.addActionListener(new AbstractAction() {
                    boolean selected = false;
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        if(selected) {
                            coloredComp.setFgColor(5);
                            coloredComp.setBgColor(0);
                            toAdd.remove(aw.getFileManager());
                            selected = false;
                        } else {
                            coloredComp.setFgColor(0);
                            coloredComp.setBgColor(5);
                            toAdd.add(aw.getFileManager());
                            selected = true;
                        }
                    }
                });
                addDialog.addItem(coloredComp, false);
            }

            addDialog.addDoneBinding(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    for(AudioFileManager fileManager: toAdd) {
                        audioFile.pAdd(fileManager);
                    }
                    updatePane();
                }
            });
            addDialog.buildDialog(audioWindow);
        }
    }
    
    public class pbpMultAction extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e) {
            final AdaptiveDialog multDialog = new AdaptiveDialog("Scale Vertically");
            final ArrayList<AudioFileManager> toMult = new ArrayList<AudioFileManager>();
            for(AudioWindow aw : audioDesktop.getAudioWindows()){
                JButton jTextButton = new JButton(aw.getFileManager().getName());
                ColoredComponent coloredComp = new ColoredComponent(jTextButton, 5, 0);
                jTextButton.addActionListener(new AbstractAction() {
                    boolean selected = false;
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        if(selected) {
                            coloredComp.setFgColor(5);
                            coloredComp.setBgColor(0);
                            toMult.remove(aw.getFileManager());
                            selected = false;
                        } else {
                            coloredComp.setFgColor(0);
                            coloredComp.setBgColor(5);
                            toMult.add(aw.getFileManager());
                            selected = true;
                        }
                    }
                });
                multDialog.addItem(coloredComp, false);
            }

            multDialog.addDoneBinding(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    for(AudioFileManager fileManager: toMult) {
                        audioFile.pMult(fileManager);
                    }
                    updatePane();
                }
            });

            multDialog.buildDialog(audioWindow);
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

    public class PlayAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e){
            player.playFile(pane);
        }
    }

    public class PlaySelectedAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e){
            player.playFile(pane, (int) pane.getSelection()[0], (int) pane.getSelection()[1]);
        }
    }

    public class RepeatAction extends AbstractAction {
        JCheckBoxMenuItem checkBox;
        public RepeatAction(JCheckBoxMenuItem jCheckBoxMenuItem){
            checkBox = jCheckBoxMenuItem;
        }
        @Override
        public void actionPerformed(ActionEvent e){
            player.setRepeat(checkBox.getState());
        }
    }

    public class StopAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e){
            player.stop();
        }
    }

    public class FilterThresholdAction extends AbstractAction {
        private boolean toSelection = false;
        public FilterThresholdAction(boolean toSelection){
            this.toSelection = toSelection;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            final AdaptiveDialog filterDialog = new AdaptiveDialog("Filter");
            final JTextField textField = new JTextField();
            final JCheckBox removeBelowBox = new JCheckBox("Remove Below?");
            filterDialog.addItem(textField, 5, 0, false);
            filterDialog.addItem(removeBelowBox, 0, 5, false);
            filterDialog.addDoneBinding(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(toSelection) {
                        audioFile.filter(Float.valueOf(textField.getText()), (int) pane.getSelection()[0], (int) pane.getSelection()[1], removeBelowBox.isSelected());
                    } else {
                        audioFile.filter(Float.valueOf(textField.getText()), removeBelowBox.isSelected());
                    }
                    updatePane();
                }
            });
            filterDialog.buildDialog(audioWindow);
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
            for(ColoredComponent c: selectionComponents) {
                c.setVisible(false);
            }
        } else {
            for(ColoredComponent c: selectionComponents) {
                c.setVisible(true);
            }
        }
    }

}