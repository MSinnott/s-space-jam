import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class AudioFileWindow extends InternalWindow {

    private AudioFileManager audioFile;

    private String windowHistory = "";

    public AudioFileWindow(int width, int height, AudioFileManager fileManager, AudioDesktop aDesk){
        super(width, height, aDesk);
        pane = new MainPane(this);

        addHistory(fileManager.getName());

        player = new StaticSoundPlayer(fileManager, pane);

        this.setLayout(new BorderLayout());

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
        components.add(new ColoredComponent(menuBar, "txtColor", "bgColor"));

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        components.add(new ColoredComponent(fileMenu,"txtColor", "bgColor"));

        makeMenuItem(fileMenu, new JMenuItem("Clone"), "txtColor", "bgColor", new CloneAction(this), components);

        makeMenuItem(fileMenu, new JMenuItem("Save"), "txtColor", "bgColor", new SaveAction("Save"), components);

        makeMenuItem(fileMenu, new JMenuItem("Save As ..."), "txtColor", "bgColor", new SaveAction("SaveAs"), components);

        makeMenuItem(fileMenu, new JMenuItem("Print History"), "txtColor", "bgColor", new PrintHistoryAction(), components);

        makeMenuItem(fileMenu, new JMenuItem("Exit"), "txtColor", "bgColor", new ExitAction(), components);

        JMenu opMenu = new JMenu("Operations");
        menuBar.add(opMenu);
        components.add(new ColoredComponent(opMenu,"txtColor", "bgColor"));

        makeMenuItem(opMenu, new JMenuItem("Forward FFT"), "txtColor", "bgColor", new forwardFFtAction(), components);

        makeMenuItem(opMenu, new JMenuItem("Step Forward FFT"), "txtColor", "bgColor", new stepforwardFFtAction(), components);

        makeMenuItem(opMenu, new JMenuItem("Backward FFT"), "txtColor", "bgColor", new backwardFFtAction(), components);

        makeMenuItem(opMenu, new JMenuItem("Scale Vertically"), "txtColor", "bgColor", new vscaleAction(false), components);

        makeMenuItem(opMenu, new JMenuItem("Shift Vertically"), "txtColor", "bgColor", new vshiftAction(false), components);

        makeMenuItem(opMenu, new JMenuItem("Shift Horizontally"), "txtColor", "bgColor", new hshiftAction(), components);

        makeMenuItem(opMenu, new JMenuItem("Point-by-Point Add"), "txtColor", "bgColor", new pbpAddAction(), components);

        makeMenuItem(opMenu, new JMenuItem("Point-by-Point Multiply"), "txtColor", "bgColor", new pbpMultAction(), components);

        makeMenuItem(opMenu, new JMenuItem("Trim"), "txtColor", "bgColor", new TrimAction(), components);

        makeMenuItem(opMenu, new JMenuItem("Boxcar Filter"), "txtColor", "bgColor", new BoxcarFilterAction(false), components);

        makeMenuItem(opMenu, new JMenuItem("Threshold Filter"), "txtColor", "bgColor", new FilterThresholdAction(false), components);

        makeMenuItem(opMenu, new JMenuItem("Add noise"), "txtColor", "bgColor", new AddNoiseAction(), components);

        makeMenuItem(opMenu, new JMenuItem("Make Audible"), "txtColor", "bgColor", new MakeAudibleAction(), components);

        JMenu selectionMenu = new JMenu("Edit Selection");
        menuBar.add(selectionMenu);
        selectionComponents.add(new ColoredComponent(selectionMenu, "txtColor", "bgColor"));

        makeMenuItem(selectionMenu, new JMenuItem("Zero Selected"), "txtColor", "bgColor", new ZeroSelectedAction(), selectionComponents);

        makeMenuItem(selectionMenu, new JMenuItem("Zero Non-Selected"), "txtColor", "bgColor", new ZeroNonSelectedAction(), selectionComponents);

        makeMenuItem(selectionMenu, new JMenuItem("Scale Selection Vertically"), "txtColor", "bgColor", new vscaleAction(true), selectionComponents);

        makeMenuItem(selectionMenu, new JMenuItem("Shift Selection Vertically"), "txtColor", "bgColor", new vshiftAction(true), selectionComponents);

        makeMenuItem(selectionMenu, new JMenuItem("Zoom to Selection"), "txtColor", "bgColor", new ZoomToSelectionAction(), selectionComponents);

        makeMenuItem(selectionMenu, new JMenuItem("Boxcar Filter"), "txtColor", "bgColor", new BoxcarFilterAction(true), selectionComponents);

        makeMenuItem(selectionMenu, new JMenuItem("Threshold Filter"), "txtColor", "bgColor", new FilterThresholdAction(true), selectionComponents);

        JMenu playMenu = new JMenu("\t▶");
        menuBar.add(playMenu);
        components.add(new ColoredComponent(playMenu,"txtColor", "bgColor"));

        makeMenuItem(playMenu, new JMenuItem("\t▶ File"), "txtColor", "bgColor", new PlayAction(), components);

        makeMenuItem(playMenu, new JMenuItem("\t▶ Selected"), "txtColor", "bgColor", new PlaySelectedAction(), selectionComponents);

        makeMenuItem(playMenu, new JMenuItem("Repeat?"), "txtColor", "bgColor", new RepeatAction(), components);

        makeMenuItem(playMenu, new JMenuItem("Stop"), "txtColor", "bgColor", new StopAction(), components);

        resetColors();
    }

    public void setView(int pan, double zoom){
        pane.setPan(pan);
        pane.setZoom(zoom);
    }

    public void play(){
        player.playFile();
    }

    //Creates + adds a clone of this object to the desktop
    public class vscaleAction extends AbstractAction {
        private boolean toSelection = false;
        public vscaleAction(boolean toSelection){
            this.toSelection = toSelection;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            final AdaptiveDialog scaleDialog = new AdaptiveDialog("Scale Vertically");
            final JTextField textField = new JTextField();
            scaleDialog.addItem(textField, "txtColor", "bgColor", false);
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
            scaleDialog.buildDialog(window);
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
            shiftDialog.addItem(textField, "txtColor", "bgColor", false);
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
            shiftDialog.buildDialog(window);
        }
    }

    public class hshiftAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            final AdaptiveDialog shiftDialog = new AdaptiveDialog("Shift Horizontally");
            final JTextField textField = new JTextField();
            shiftDialog.addItem(textField, "txtColor", "bgColor", false);
            shiftDialog.addDoneBinding(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    audioFile.hshift(Integer.valueOf(textField.getText()));
                    updatePane();
                    addHistory("hShift: " + textField.getText() + " @ " + Arrays.toString(pane.getSelection()));
                }
            });
            shiftDialog.buildDialog(window);
        }
    }

    public class pbpAddAction extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e) {
            audioDesktop.checkWindows();
            final AdaptiveDialog addDialog = new AdaptiveDialog("Scale Vertically");
            final ArrayList<AudioFileManager> toAdd = new ArrayList<AudioFileManager>();
            for(InternalWindow aw : audioDesktop.getAudioFileWindows()){
                JButton jTextButton = new JButton(aw.getFileManager().getName());
                ColoredComponent coloredComp = new ColoredComponent(jTextButton, "txtColor", "bgColor");
                jTextButton.addActionListener(new AbstractAction() {
                    boolean selected = false;
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        if(selected) {
                            coloredComp.setFgColor("txtColor");
                            coloredComp.setBgColor("bgColor");
                            toAdd.remove(aw.getFileManager());
                            selected = false;
                        } else {
                            coloredComp.setFgColor("bgColor");
                            coloredComp.setBgColor("txtColor");
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
                        audioFile.pAdd(fileManager, 0);
                        addHistory("\t" + fileManager.getName());
                    }
                    updatePane();
                }
            });
            addDialog.buildDialog(window);
        }
    }

    public class pbpMultAction extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e) {
            audioDesktop.checkWindows();
            final AdaptiveDialog multDialog = new AdaptiveDialog("Scale Vertically");
            final ArrayList<AudioFileManager> toMult = new ArrayList<AudioFileManager>();
            for(InternalWindow aw : audioDesktop.getAudioFileWindows()){
                JButton jTextButton = new JButton(aw.getFileManager().getName());
                ColoredComponent coloredComp = new ColoredComponent(jTextButton, "txtColor", "bgColor");
                jTextButton.addActionListener(new AbstractAction() {
                    boolean selected = false;
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        if(selected) {
                            coloredComp.setFgColor("txtColor");
                            coloredComp.setBgColor("bgColor");
                            toMult.remove(aw.getFileManager());
                            selected = false;
                        } else {
                            coloredComp.setFgColor("bgColor");
                            coloredComp.setBgColor("txtColor");
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

            multDialog.buildDialog(window);
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
            stepDialog.addItem(textField, "txtColor", "bgColor", false);
            stepDialog.addDoneBinding(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("!");
                    audioFile.smallFFT(Integer.valueOf(textField.getText()));
                    updatePane();
                }
            });
            stepDialog.buildDialog(window);
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
            filterDialog.addItem(textField, "txtColor", "bgColor", false);
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
            filterDialog.buildDialog(window);
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
            player.playFile();
        }
    }

    public class PlaySelectedAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e){
            player.playFile((int) pane.getSelection()[0], (int) pane.getSelection()[1]);
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
            filterDialog.addItem(textField, "txtColor", "bgColor", false);
            filterDialog.addItem(removeBelowBox, "txtColor", "bgColor", false);
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
            filterDialog.buildDialog(window);
        }
    }

    public class AddNoiseAction extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            final AdaptiveDialog filterDialog = new AdaptiveDialog("Filter");
            final JTextField noiseStepsBox = new JTextField();
            final JTextField noiseScaleBox = new JTextField();
            filterDialog.addItem(noiseStepsBox, "txtColor", "bgColor", false);
            filterDialog.addItem(noiseScaleBox, "txtColor", "bgColor", false);
            filterDialog.addDoneBinding(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    audioFile.addNoise(Integer.valueOf(noiseStepsBox.getText()), Integer.valueOf(noiseScaleBox.getText()));
                    addHistory("aNoise: steps: " + noiseStepsBox.getText() + " scale: " + noiseScaleBox.getText());
                    updatePane();
                }
            });
            filterDialog.buildDialog(window);
        }
    }

    public class MakeAudibleAction extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            audioFile.makeAudible();
            updatePane();
        }
    }

    public class PrintHistoryAction extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e){
            System.out.println(windowHistory);
        }
    }

    @Override
    public void paintComponent(Graphics g){
        pane.setAudioFile(audioFile);
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