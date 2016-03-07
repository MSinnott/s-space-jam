import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Main JFrame class -- provides multi-document interface capability + theme
 */
public class AudioDesktop extends JFrame{

    public static Color[] theme = new Color[] { new Color(252, 53, 0), new Color(252, 127, 3), new Color(255, 201, 8), new Color(55, 236, 255), new Color(29, 46, 255), new Color(255, 255, 255),
                                                new Color(0, 0, 0)  };
    //bgColor = new Color(252, 53, 0);
    //fgColor = new Color(252, 127, 3);
    //accColor = new Color(255, 201, 8);
    //llnColor = new Color(55, 236, 255);
    //rlnColor = new Color(29, 46, 255);
    //txtColor = new Color(255, 255, 255);
    //black = new Color(0, 0, 0);

    private AdaptiveDialog themeDialog;
    private AudioDesktop audioDesktop = this;

    private JDesktopPane desktop;

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem newButton;
    private JMenuItem openButton;
    private JMenuItem exitButton;
    private JMenuItem convertButton;
    private JMenu optionMenu;
    private JMenuItem themeButton;

    private ArrayList<AudioWindow> audioWindows;
    private ArrayList<Component> components = new ArrayList<Component>();

    public static final String LinuxPathHead = "s-space-jam/testing/";
    public static final String WindowsPathHead = "testing/";
    //private String propertiesPathnameLinux = "s-space-jam/testing/resc/properties.txt";
    //private String propertiesPathnameWindows = "testing/resc/properties.txt";
    //private String iconPathLinux = "s-space-jam/testing/resc/s-Space-Jam-Logo.jpg";
    //private String iconPathWindows = "testing/resc/s-Space-Jam-Logo.jpg";

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
        audioWindows = new ArrayList<AudioWindow>();

        desktop = new JDesktopPane();
        this.setLayout(new BorderLayout());
        this.add(desktop, BorderLayout.CENTER);
        this.setSize(width, height);

        this.setResizable(true);
        ImageIcon icon  =  new ImageIcon(WindowsPathHead + "resc/s-Space-Jam-Logo.jpg");
        if(icon.getImage() == null) icon = new ImageIcon(LinuxPathHead + "resc/s-Space-Jam-Logo.jpg");
        this.setIconImage(icon.getImage());
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

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

        convertButton = new JMenuItem("File Convert");
        fileMenu.add(convertButton);
        convertButton.addActionListener(new ConverterOpenAction());
        components.add(convertButton);

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


        properties = new Properties();

        updateProperties();

        resetColors(true);
        this.setVisible(true);
        this.invalidate();
        this.repaint();
    }

    /**
     * Saves the theme to the properties file
     * @throws IOException
     */
    public void saveProperties() throws IOException {
        for(int i = 0; i < theme.length; i++){
            properties.setProperty("" + i + ":R", String.valueOf(theme[i].getRed()));
            properties.setProperty("" + i + ":G", String.valueOf(theme[i].getGreen()));
            properties.setProperty("" + i + ":B", String.valueOf(theme[i].getBlue()));
        }

        try {
            File propFile = new File(LinuxPathHead + "resc/properties.txt");
            BufferedWriter propOut = new BufferedWriter(new FileWriter(propFile));
            properties.store(propOut, "Colors");
        } catch (Exception e0){
            try {
                File propFile = new File(WindowsPathHead + "resc/properties.txt");
                BufferedWriter propOut = new BufferedWriter(new FileWriter(propFile));
                properties.store(propOut, "Colors");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Updates the properties file
     * @throws IOException
     */
    public void updateProperties() {
        try {
            File propFile = new File(LinuxPathHead + "resc/properties.txt");
            FileInputStream propReader = new FileInputStream(propFile);
            properties.load(propReader);
        } catch (Exception e0){
            try {
                File propFile = new File(WindowsPathHead + "resc/properties.txt");
                FileInputStream propReader = new FileInputStream(propFile);
                properties.load(propReader);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

    }

    /**
     * Resets the colors for this component and its children
     * @param loadFromFile whether or not to load the theme from the properties file
     */
    public void resetColors(boolean loadFromFile) {
        if(properties.isEmpty() || !loadFromFile) {
            try {
                saveProperties();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        boolean configOk = true;
        if(loadFromFile) {
            for(int i = 0; i < theme.length; i++){
                if(properties.containsKey("" + i + ":R") && properties.containsKey("" + i + ":G") && properties.containsKey("" + i + ":B")){
                    theme[i] = new Color(Integer.valueOf(properties.getProperty("" + i + ":R")), Integer.valueOf(properties.getProperty("" + i + ":G")), Integer.valueOf(properties.getProperty("" + i + ":B")));
                } else {
                    configOk = false;
                }
            }

            if (!configOk) {
                try {
                    saveProperties();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        desktop.setBackground(theme[1]);
        desktop.setForeground(theme[5]);
        for(Component c: components){
            c.setForeground(AudioDesktop.theme[5]);
            c.setBackground(AudioDesktop.theme[0]);
        }

        if(themeDialog != null) themeDialog.resetColors();

        checkWindows();
    }

    public void checkWindows() {
        for (AudioWindow aw: audioWindows) {
            if(aw.isClosed()) removeWindow(aw);
            aw.resetColors();
        }
    }

    /**
     * Removes the passed AudioWindow
     * @param aw audioWindo to remove
     */
    public void removeWindow(AudioWindow aw){
        audioWindows.remove(aw);
        aw.dispose();
    }

    /**
     * Adds the passed AudioWindow
     * @param aw audioWindow to add
     */
    public void addWindow(AudioWindow aw){
        audioWindows.add(aw);
        desktop.add(aw);
    }

    /**
     * @return ArrayList of AudioWindows owned by this component
     */
    public ArrayList<AudioWindow> getAudioWindows(){
        return audioWindows;
    }

    /**
     * Builds an audioWindow from an AudioFileManager
     * @param fileManager source to build window
     */
    public void buildWindow(AudioFileManager fileManager){
        fileManager.setDefaultName(fileManager.getName() + " | " + audioWindows.size());
        AudioWindow newAW = new AudioWindow(200, 100, fileManager, this);
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
            generateDialog.addItem(lEqnPane, 5, 0, false);

            JTextField rEqnLabel = new JTextField("Right Eqn: ");
            rEqnLabel.setEditable(false);
            final AdaptiveTextField rExprField = new AdaptiveTextField("");
            JPanel rEqnPane = new JPanel();
            rEqnPane.setLayout(new BorderLayout());
            rEqnPane.add(rEqnLabel, BorderLayout.WEST);
            rEqnPane.add(rExprField, BorderLayout.CENTER);
            generateDialog.addItem(rEqnPane, 5, 0, false);

            JTextField sizeLabel = new JTextField("Size:");
            sizeLabel.setEditable(false);
            final AdaptiveTextField sizeField = new AdaptiveTextField("");
            JPanel sizePane = new JPanel();
            sizePane.setLayout(new BorderLayout());
            sizePane.add(sizeLabel, BorderLayout.WEST);
            sizePane.add(sizeField, BorderLayout.CENTER);
            generateDialog.addItem(sizePane, 5, 0, false);

            final JButton randButton = new JButton("Generate Random Song!");
            generateDialog.addItem(randButton, 0, 5, false);
            randButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    MusicGenerator generator = new MusicGenerator(AudioFileManager.DEFAULT_SAMPLE_RATE);

                    float[] song0 = generator.generateSongV1(4, 1);
                    AudioFileManager rSong0 = new AudioFileManager(song0, song0);

                    float[] song1 = generator.generateSongV1(4, 1);
                    AudioFileManager rSong1 = new AudioFileManager(song1, song1);

                    float[] song2 = generator.generateSongV1(4, 1);
                    AudioFileManager rSong2 = new AudioFileManager(song2, song2);

                    rSong0.pAdd(rSong1.getChannels());
                    rSong0.pAdd(rSong2.getChannels());

                    buildWindow(rSong0);
                    generateDialog.dispose();
                }
            });

            generateDialog.addDoneBinding(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    buildWindow(new AudioFileManager(lExprField.generateSamples(Integer.valueOf(sizeField.getText())), rExprField.generateSamples(Integer.valueOf(sizeField.getText()))));
                }
            });

            lEqnLabel.setBackground(theme[0]);
            lEqnLabel.setForeground(theme[5]);
            rEqnLabel.setBackground(theme[0]);
            rEqnLabel.setForeground(theme[5]);
            sizeLabel.setBackground(theme[0]);
            sizeLabel.setForeground(theme[5]);

            generateDialog.buildDialog(320, 256, audioDesktop);
        }
    }

    /**
     *  Action that opens a WAV file
     */
    public class OpenFileAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            File root;
            try {
                root = new File(LinuxPathHead);
            } catch (Exception ex) {
                root = new File(WindowsPathHead);
            }
            JFileChooser fileChooser = new JFileChooser(root);
            fileChooser.setBackground(theme[0]);
            fileChooser.setForeground(theme[5]);
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
     *  Action that opens a nonWAV file -- unfinished
     */
    // TODO: 2/28/16
    public class ConverterOpenAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setBackground(theme[0]);
            fileChooser.setForeground(theme[5]);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "mp3 Files", "mp3");
            //need to throw an if mp3 file, call decode function. Use Jlayer / MP3SPI library
            //looks like .au and .aiff files are already supported.
            fileChooser.setFileFilter(filter);
            int returnVal = fileChooser.showOpenDialog(desktop);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                File selection = fileChooser.getSelectedFile();
                //code for conversion goes here
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

            addColorChoice(themeDialog, 0, "bgColor");
            addColorChoice(themeDialog, 1, "fgColor");
            addColorChoice(themeDialog, 2, "accColor");
            addColorChoice(themeDialog, 3, "llnColor");
            addColorChoice(themeDialog, 4, "rlnColor");
            addColorChoice(themeDialog, 5, "txtColor");

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
     * @param color color to set sample
     * @param colorName name of the color
     */
    private void addColorChoice(AdaptiveDialog dialog, int color, String colorName){
        JPanel p = new JPanel();
        dialog.addItem(p, 6, color, false);

        JButton b = new JButton(colorName);
        b.addActionListener(new ColorAction(color, colorName));
        dialog.addItem(b, 6, color, true);
    }

    /**
     *  Action that allows changing colors
     */
    public class ColorAction extends AbstractAction {

        private int myColor;
        private String colorName;

        public ColorAction(int toChange, String colorName){
            myColor = toChange;
            this.colorName = colorName;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final AdaptiveDialog colorDialog = new AdaptiveDialog(colorName);
            final JColorChooser colorChooser = new JColorChooser();
            colorChooser.setColor(theme[myColor]);
            colorDialog.addItem(colorChooser, myColor, 5, false);
            colorDialog.addDoneBinding(new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    theme[myColor] = colorChooser.getColor();
                    resetColors(false);
                }
            });
            colorDialog.buildDialog(audioDesktop);
        }
    }

}