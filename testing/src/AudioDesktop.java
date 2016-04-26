import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;

/**
 * Main JFrame class -- provides multi-document interface capability
 */
public class AudioDesktop extends JFrame{

    private AdaptiveDialog themeDialog;
    private AudioDesktop audioDesktop = this;

    private JDesktopPane desktop;

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem newButton;
    private JMenuItem openButton;
    private JMenuItem exitButton;
    private JMenu optionMenu;
    private JMenuItem themeButton;

    private ArrayList<InternalWindow> audioFileWindows;
    private ArrayList<Component> components = new ArrayList<Component>();

    public static final String propPath = "resc/";

    private Properties properties;

    /**Standard constructor -- builds window desktop, preps for user input
     *
     * @param name name of the main window
     * @param width width of the window
     * @param height height of the window
     */
    public AudioDesktop(String name, int width, int height){
        super(name);

        components.add(this);
        audioFileWindows = new ArrayList<InternalWindow>();

        desktop = new JDesktopPane();
        this.setLayout(new BorderLayout());
        this.add(desktop, BorderLayout.CENTER);
        this.setSize(width, height);

        this.setResizable(true);
        ImageIcon icon  =  new ImageIcon(propPath + "/s-Space-Jam-Logo.jpg");
        if(icon.getImage() == null) icon = new ImageIcon(propPath + "/s-Space-Jam-Logo.jpg");
        this.setIconImage(icon.getImage());
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        buildMenus();

        properties = new Properties();

        updateProperties();

        resetColors(true);
        this.setVisible(true);
        this.invalidate();
        this.repaint();
    }

    public void buildMenus(){
        menuBar = new JMenuBar();
        this.add(menuBar, BorderLayout.NORTH);
        components.add(menuBar);

        fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        components.add(fileMenu);

        newButton = new JMenuItem("New");
        fileMenu.add(newButton);
        newButton.addActionListener(new GenerateAction());
        components.add(newButton);

        openButton = new JMenuItem("Open");
        fileMenu.add(openButton);
        openButton.addActionListener(new OpenFileAction());
        components.add(openButton);

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
                exitDialog.buildDialog(audioDesktop);
            }
        });
        components.add(exitButton);

        optionMenu = new JMenu("Options");
        menuBar.add(optionMenu);
        components.add(optionMenu);

        themeButton = new JMenuItem("Theme");
        optionMenu.add(themeButton);
        themeButton.addActionListener(new ThemeSelectorAction());
        components.add(themeButton);
    }

    /**
     * Saves the theme to the properties file
     * @throws IOException
     */
    public void saveProperties() {
        for(int i = 0; i < Theme.theme.length; i++){
            properties.setProperty("" + Theme.themeKeys[i] + ":R", String.valueOf(Theme.theme[i].getRed()));
            properties.setProperty("" + Theme.themeKeys[i] + ":G", String.valueOf(Theme.theme[i].getGreen()));
            properties.setProperty("" + Theme.themeKeys[i] + ":B", String.valueOf(Theme.theme[i].getBlue()));
        }

        try {
            File propFile = new File(propPath + "properties.txt");
            BufferedWriter propOut = new BufferedWriter(new FileWriter(propFile));
            properties.store(propOut, "Colors");
        } catch (Exception e0){
        }
    }

    /**
     * Updates the properties file
     */
    public void updateProperties() {
        try {
            File propFile = new File(propPath + "properties.txt");
            if(!propFile.exists()) propFile.createNewFile();
            FileInputStream propReader = new FileInputStream(propFile);
            properties.load(propReader);
        } catch (Exception e0){
        }

    }

    /**
     * Resets the colors for this component and its children
     * @param loadFromFile whether or not to load the theme from the properties file
     */
    public void resetColors(boolean loadFromFile) {
        if(properties.isEmpty() || !loadFromFile) {
            saveProperties();
        }

        boolean configOk = true;
        if(loadFromFile) {
            for(int i = 0; i < Theme.theme.length; i++){
                if(properties.containsKey("" + Theme.themeKeys[i] + ":R") && properties.containsKey("" + Theme.themeKeys[i] + ":G") && properties.containsKey("" + Theme.themeKeys[i] + ":B")){
                    Theme.theme[i] = new Color(Integer.valueOf(properties.getProperty("" + Theme.themeKeys[i] + ":R")),
                            Integer.valueOf(properties.getProperty("" + Theme.themeKeys[i] + ":G")),
                            Integer.valueOf(properties.getProperty("" + Theme.themeKeys[i] + ":B")));
                } else {
                    configOk = false;
                }
            }

            if (!configOk) {
                saveProperties();
            }
        }

        desktop.setBackground(Theme.getThemeColor("fgColor"));
        desktop.setForeground(Theme.getThemeColor("bgColor"));
        for(Component c: components){
            c.setForeground(Theme.getThemeColor("txtColor"));
            c.setBackground(Theme.getThemeColor("bgColor"));
        }

        if(themeDialog != null) themeDialog.resetColors();

        checkWindows();
    }

    /**
     * Basic sanity check for windows
     */
    public void checkWindows() {
        for (InternalWindow iw: audioFileWindows) {
            if(iw.isClosed()) removeWindow(iw);
            iw.resetColors();
        }
    }

    /**
     * Removes the passed AudioFileWindow
     * @param iw audioWindow to remove
     */
    public void removeWindow(InternalWindow iw){
        audioFileWindows.remove(iw);
        iw.dispose();
    }

    /**
     * Adds the passed AudioFileWindow
     * @param iw audioWindow to add
     */
    public void addWindow(InternalWindow iw){
        audioFileWindows.add(iw);
        desktop.add(iw);
    }

    /**
     * @return ArrayList of AudioWindows owned by this component
     */
    public ArrayList<InternalWindow> getAudioFileWindows(){
        return audioFileWindows;
    }

    /**
     * Builds an audioWindow from an AudioFileManager
     * @param fileManager source to build window
     */
    public void buildWindow(AudioFileManager fileManager){
        fileManager.setDefaultName(fileManager.getName() + " | " + audioFileWindows.size());
        AudioFileWindow newAW = new AudioFileWindow(200, 100, fileManager, this);
        addWindow(newAW);
    }

    /**
     *  Action that generates a song
     */
    public class GenerateAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            final AdaptiveDialog generateDialog = new AdaptiveDialog("Generate Sound File");

            JTextField lEqnLabel = new JTextField("Left Eqn:   ");
            lEqnLabel.setEditable(false);
            final AdaptiveTextField lExprField = new AdaptiveTextField("");
            JPanel lEqnPane = new JPanel();
            lEqnPane.setLayout(new BorderLayout());
            lEqnPane.add(lEqnLabel, BorderLayout.WEST);
            lEqnPane.add(lExprField, BorderLayout.CENTER);
            generateDialog.addItem(lEqnPane, "txtColor", "bgColor", false);

            JTextField rEqnLabel = new JTextField("Right Eqn: ");
            rEqnLabel.setEditable(false);
            final AdaptiveTextField rExprField = new AdaptiveTextField("");
            JPanel rEqnPane = new JPanel();
            rEqnPane.setLayout(new BorderLayout());
            rEqnPane.add(rEqnLabel, BorderLayout.WEST);
            rEqnPane.add(rExprField, BorderLayout.CENTER);
            generateDialog.addItem(rEqnPane, "txtColor", "bgColor", false);

            JTextField sizeLabel = new JTextField("Size:");
            sizeLabel.setEditable(false);
            final AdaptiveTextField sizeField = new AdaptiveTextField("");
            JPanel sizePane = new JPanel();
            sizePane.setLayout(new BorderLayout());
            sizePane.add(sizeLabel, BorderLayout.WEST);
            sizePane.add(sizeField, BorderLayout.CENTER);
            generateDialog.addItem(sizePane, "txtColor", "bgColor", false);

            final JButton randButton = new JButton("Generate Random Song!");
            generateDialog.addItem(randButton, "txtColor", "bgColor", false);
            randButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    MusicGenerator generator = new MusicGenerator(AudioFileManager.DEFAULT_SAMPLE_RATE);

                    AudioFileManager song = generator.genNewComplexSong();

                    buildWindow(song);
                    generateDialog.dispose();
                }
            });

            final JButton randSButton = new JButton("Generate Music Stream!");
            generateDialog.addItem(randSButton, "txtColor", "bgColor", false);
            randSButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    AudioStreamWindow streamWindow = new AudioStreamWindow(480, 360, audioDesktop);
                    addWindow(streamWindow);
                    generateDialog.dispose();
                    streamWindow.beginStream();
                }
            });

            generateDialog.addDoneBinding(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    buildWindow(new AudioFileManager(lExprField.generateSamples(Integer.valueOf(sizeField.getText())), rExprField.generateSamples(Integer.valueOf(sizeField.getText()))));
                }
            });

            lEqnLabel.setBackground(Theme.getThemeColor("bgColor"));
            lEqnLabel.setForeground(Theme.getThemeColor("txtColor"));
            rEqnLabel.setBackground(Theme.getThemeColor("bgColor"));
            rEqnLabel.setForeground(Theme.getThemeColor("txtColor"));
            sizeLabel.setBackground(Theme.getThemeColor("bgColor"));
            sizeLabel.setForeground(Theme.getThemeColor("txtColor"));

            generateDialog.buildDialog(320, 256, audioDesktop);
        }
    }

    /**
     *  Action that opens a WAV file
     */
    public class OpenFileAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            File root = new File("");

            JFileChooser fileChooser = new JFileChooser(root);
            fileChooser.setBackground(Theme.getThemeColor("bgColor"));
            fileChooser.setForeground(Theme.getThemeColor("txtColor"));
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "WAV Files", "wav", "mp3 Files", "mp3");
            //need to throw an if mp3 file, call decode function. Use Jlayer / MP3SPI library
            //looks like .au and .aiff files are already supported.
            fileChooser.setFileFilter(filter);
            int returnVal = fileChooser.showOpenDialog(desktop);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                File selection = fileChooser.getSelectedFile();
                buildWindow(new AudioFileManager(selection));
            }
        }
    }

    /**
     *  Action that allows for theme changes
     */
    public class ThemeSelectorAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            themeDialog = new AdaptiveDialog("Theme");

            for(String colorKey : Theme.themeKeys){
                addColorChoice(themeDialog, colorKey);
            }

            themeDialog.addDoneBinding(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    resetColors(false);
                    audioDesktop.invalidate();
                    audioDesktop.repaint();
                }
            });

            themeDialog.buildDialog(600, 320, audioDesktop);
        }
    }

    /**
     * Adds a color choice to the theme dialog
     * @param dialog dialog to add color choice to
     * @param colorName color
     */
    private void addColorChoice(AdaptiveDialog dialog, String colorName){
        JPanel p = new JPanel();
        dialog.addItem(p, "black", colorName, false);

        JButton b = new JButton(colorName);
        b.addActionListener(new ColorAction(colorName));
        dialog.addItem(b, "black", colorName, true);
    }

    /**
     *  Action that allows changing colors
     */
    public class ColorAction extends AbstractAction {

        private String colorName;

        public ColorAction(String colorName){
            this.colorName = colorName;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final AdaptiveDialog colorDialog = new AdaptiveDialog(colorName);
            final JColorChooser colorChooser = new JColorChooser();
            colorChooser.setColor(Theme.getThemeColor(colorName));
            colorDialog.addItem(colorChooser, colorName, "txtColor", false);
            colorDialog.addDoneBinding(new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    Theme.setThemeColor(colorName, colorChooser.getColor());
                    resetColors(false);
                }
            });
            colorDialog.buildDialog(audioDesktop);
        }
    }

}