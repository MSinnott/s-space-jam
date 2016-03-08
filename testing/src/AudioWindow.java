import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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

    private String windowHistory = "";

    public AudioWindow(int width, int height, AudioFileManager fileManager, AudioDesktop aDesk){
        super(fileManager.getName());

        addHistory(fileManager.getName());

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

    private void addHistory(String histToAdd) {
        windowHistory += "\n" + histToAdd;
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

        makeMenuItem(fileMenu, new JMenuItem("Clone"), 5, 0, new CloneAction(this), components);

        makeMenuItem(fileMenu, new JMenuItem("Save"), 5, 0, new SaveAction("Save"), components);

        makeMenuItem(fileMenu, new JMenuItem("Save As ..."), 5, 0, new SaveAction("SaveAs"), components);

        makeMenuItem(fileMenu, new JMenuItem("Exit"), 5, 0, new ExitAction(), components);

        JMenu opMenu = new JMenu("Operations");
        menuBar.add(opMenu);
        components.add(new ColoredComponent(opMenu, 5, 0));

        makeMenuItem(opMenu, new JMenuItem("Forward FFT"), 5, 0, new forwardFFtAction(), components);

        makeMenuItem(opMenu, new JMenuItem("Step Forward FFT"), 5, 0, new stepforwardFFtAction(), components);

        makeMenuItem(opMenu, new JMenuItem("Backward FFT"), 5, 0, new backwardFFtAction(), components);

        makeMenuItem(opMenu, new JMenuItem("Scale Vertically"), 5, 0, new vscaleAction(false), components);

        makeMenuItem(opMenu, new JMenuItem("Shift Vertically"), 5, 0, new vshiftAction(false), components);

        makeMenuItem(opMenu, new JMenuItem("Shift Horizontally"), 5, 0, new hshiftAction(), components);

        makeMenuItem(opMenu, new JMenuItem("Point-by-Point Add"), 5, 0, new pbpAddAction(), components);

        makeMenuItem(opMenu, new JMenuItem("Point-by-Point Multiply"), 5, 0, new pbpMultAction(), components);

        makeMenuItem(opMenu, new JMenuItem("Trim"), 5, 0, new TrimAction(), components);

        makeMenuItem(opMenu, new JMenuItem("Boxcar Filter"), 5, 0, new BoxcarFilterAction(false), components);

        makeMenuItem(opMenu, new JMenuItem("Threshold Filter"), 5, 0, new FilterThresholdAction(false), components);

        makeMenuItem(opMenu, new JMenuItem("Add noise"), 5, 0, new AddNoiseAction(), components);

        JMenu selectionMenu = new JMenu("Edit Selection");
        menuBar.add(selectionMenu);
        selectionComponents.add(new ColoredComponent(selectionMenu, 5, 0));

        makeMenuItem(selectionMenu, new JMenuItem("Zero Selected"), 5, 0, new ZeroSelectedAction(), selectionComponents);

        makeMenuItem(selectionMenu, new JMenuItem("Zero Non-Selected"), 5, 0, new ZeroNonSelectedAction(), selectionComponents);

        makeMenuItem(selectionMenu, new JMenuItem("Scale Selection Vertically"), 5, 0, new vscaleAction(true), selectionComponents);

        makeMenuItem(selectionMenu, new JMenuItem("Shift Selection Vertically"), 5, 0, new vshiftAction(true), selectionComponents);

        makeMenuItem(selectionMenu, new JMenuItem("Zoom to Selection"), 5, 0, new ZoomToSelectionAction(), selectionComponents);

        makeMenuItem(selectionMenu, new JMenuItem("Boxcar Filter"), 5, 0, new BoxcarFilterAction(true), selectionComponents);

        makeMenuItem(selectionMenu, new JMenuItem("Threshold Filter"), 5, 0, new FilterThresholdAction(true), selectionComponents);

        JMenu playMenu = new JMenu("\t▶");
        menuBar.add(playMenu);
        components.add(new ColoredComponent(playMenu, 5, 0));

        makeMenuItem(playMenu, new JMenuItem("\t▶ File"), 5, 0, new PlayAction(), components);

        makeMenuItem(playMenu, new JMenuItem("\t▶ Selected"), 5, 0, new PlaySelectedAction(), selectionComponents);

        makeMenuItem(playMenu, new JMenuItem("Repeat?"), 5, 0, new RepeatAction(), components);

        makeMenuItem(playMenu, new JMenuItem("Stop"), 5, 0, new StopAction(), components);

        resetColors();
    }

    public void makeMenuItem(JMenu menu, JMenuItem j, int fgColr, int bgColr, AbstractAction action, ArrayList<ColoredComponent> collection){
        j.addActionListener(action);
        collection.add(new ColoredComponent(j, fgColr, bgColr));
        menu.add(j);
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
                File root;
                try {
                    root = new File(AudioDesktop.LinuxPathHead);
                } catch (Exception ex) {
                    root = new File(AudioDesktop.WindowsPathHead);
                }
                JFileChooser fileChooser = new JFileChooser(root);
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

    public class ExitAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            AdaptiveDialog exitDialog = new AdaptiveDialog("Exit?");
            exitDialog.addDoneBinding(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    audioDesktop.removeWindow(audioWindow);
                }
            });
            exitDialog.buildDialog(audioWindow);
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
                    addHistory("vScale: " + textField.getText() + " @ " + Arrays.toString(pane.getSelection()));
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
                    addHistory("vShift: " + textField.getText() + " @ " + Arrays.toString(pane.getSelection()));
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
                    addHistory("hShift: " + textField.getText() + " @ " + Arrays.toString(pane.getSelection()));
                }
            });
            shiftDialog.buildDialog(audioWindow);
        }
    }

    public class pbpAddAction extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e) {
            audioDesktop.checkWindows();
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

            addHistory("pAdd: ");
            addDialog.addDoneBinding(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    for(AudioFileManager fileManager: toAdd) {
                        audioFile.pAdd(fileManager);
                        addHistory("\t" + fileManager.getName());
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
            audioDesktop.checkWindows();
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
            addHistory("pMult: ");
            multDialog.addDoneBinding(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    for(AudioFileManager fileManager: toMult) {
                        audioFile.pMult(fileManager);
                        addHistory("\t" + fileManager.getName());
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
            addHistory("fftF");
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
            addHistory("fftB");
            updatePane();
        }
    }

    public class TrimAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            audioFile.trim();
            addHistory("trim");
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
                    addHistory("bFilter: " + textField.getText() + " @ " + Arrays.toString(pane.getSelection()));
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
            addHistory("zSelect: " + " @ " + Arrays.toString(pane.getSelection()));
            updatePane();
        }
    }

    public class ZeroNonSelectedAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e){
            float[] selection = pane.getSelection();
            audioFile.zeroFrom(0, (int) selection[0]);
            audioFile.zeroFrom((int) selection[1], Integer.MAX_VALUE);
            addHistory("z!Select: " + " @ " + Arrays.toString(pane.getSelection()));
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
        boolean toggle = false;
        @Override
        public void actionPerformed(ActionEvent e){
            toggle = !toggle;
            player.setRepeat(toggle);
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
                    addHistory("thFilter: " + textField.getText() + " @ " + Arrays.toString(pane.getSelection()));
                    updatePane();
                }
            });
            filterDialog.buildDialog(audioWindow);
        }
    }

    public class AddNoiseAction extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            final AdaptiveDialog filterDialog = new AdaptiveDialog("Filter");
            final JTextField noiseStepsBox = new JTextField();
            final JTextField noiseScaleBox = new JTextField();
            filterDialog.addItem(noiseStepsBox, 5, 0, false);
            filterDialog.addItem(noiseScaleBox, 5, 0, false);
            filterDialog.addDoneBinding(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    audioFile.addNoise(Integer.valueOf(noiseStepsBox.getText()), Integer.valueOf(noiseScaleBox.getText()));
                    addHistory("aNoise: steps: " + noiseStepsBox.getText() + " scale: " + noiseScaleBox.getText());
                    updatePane();
                }
            });
            filterDialog.buildDialog(audioWindow);
        }
    }

    public class PrintHistoryAction extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e){
            System.out.println(windowHistory);
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