import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

public class AudioDesktop extends JFrame{

    public static Color[] theme = new Color[]
            { new Color(252, 53, 0), new Color(252, 127, 3), new Color(255, 201, 8),
              new Color(55, 236, 255), new Color(29, 46, 255), new Color(255, 255, 255),
              new Color(0, 0, 0)};

    /*
    public static Color bgColor = new Color(252, 53, 0);
    public static Color fgColor = new Color(252, 127, 3);
    public static Color accColor = new Color(255, 201, 8);
    public static Color llnColor = new Color(55, 236, 255);
    public static Color rlnColor = new Color(29, 46, 255);
    public static Color txtColor = new Color(255, 255, 255);
    public static Color black = new Color(0, 0, 0);
*/

    private AdaptiveDialog themeDialog;

    private JDesktopPane desktop;

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem newButton;
    private JMenuItem openButton;
    private JMenuItem exitButton;
    private JMenu optionMenu;
    private JMenuItem themeButton;

    private ArrayList<AudioWindow> audioWindows;

    private String propertiesPathname = "testing/resc/properties.txt";
    private String iconPath = "testing/resc/s-Space-Jam-Logo.jpg";

    private Properties properties;

    //Standard constructor -- builds window desktop, preps for user input
    public AudioDesktop(String name, int width, int height){
        super(name);

        audioWindows = new ArrayList<AudioWindow>();

        desktop = new JDesktopPane();
        this.setLayout(new BorderLayout());
        this.add(desktop, BorderLayout.CENTER);
        this.setSize(width, height);

        this.setResizable(true);
        this.setIconImage(new ImageIcon(iconPath).getImage());
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        menuBar = new JMenuBar();
        this.add(menuBar, BorderLayout.NORTH);

        fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        newButton = new JMenuItem("New");
        fileMenu.add(newButton);

        openButton = new JMenuItem("Open");
        fileMenu.add(openButton);
        openButton.addActionListener(new OpenFileAction());

        exitButton = new JMenuItem("Exit");
        fileMenu.add(exitButton);
        exitButton.addActionListener(new CloseAction(this));

        optionMenu = new JMenu("Options");
        menuBar.add(optionMenu);

        themeButton = new JMenuItem("Theme");
        optionMenu.add(themeButton);
        themeButton.addActionListener(new ThemeSelectorAction());

        properties = new Properties();

        try {
            updateProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
        resetColors(true);
        this.setVisible(true);
        this.invalidate();
        this.repaint();
    }

    //prob should autogen these... ah well!
    public void saveProperties() throws IOException {
        properties.setProperty("bgColor:R", String.valueOf(theme[0].getRed()));
        properties.setProperty("bgColor:G", String.valueOf(theme[0].getGreen()));
        properties.setProperty("bgColor:B", String.valueOf(theme[0].getBlue()));

        properties.setProperty("fgColor:R", String.valueOf(theme[1].getRed()));
        properties.setProperty("fgColor:G", String.valueOf(theme[1].getGreen()));
        properties.setProperty("fgColor:B", String.valueOf(theme[1].getBlue()));

        properties.setProperty("accColor:R", String.valueOf(theme[2].getRed()));
        properties.setProperty("accColor:G", String.valueOf(theme[2].getGreen()));
        properties.setProperty("accColor:B", String.valueOf(theme[2].getBlue()));

        properties.setProperty("llnColor:R", String.valueOf(theme[3].getRed()));
        properties.setProperty("llnColor:G", String.valueOf(theme[3].getGreen()));
        properties.setProperty("llnColor:B", String.valueOf(theme[3].getBlue()));

        properties.setProperty("rlnColor:R", String.valueOf(theme[4].getRed()));
        properties.setProperty("rlnColor:G", String.valueOf(theme[4].getGreen()));
        properties.setProperty("rlnColor:B", String.valueOf(theme[4].getBlue()));

        properties.setProperty("txtColor:R", String.valueOf(theme[5].getRed()));
        properties.setProperty("txtColor:G", String.valueOf(theme[5].getGreen()));
        properties.setProperty("txtColor:B", String.valueOf(theme[5].getBlue()));

        File propFile = new File(propertiesPathname);
        BufferedWriter propOut = new BufferedWriter(new FileWriter(propFile));
        properties.store(propOut, "Colors");
    }

    public void updateProperties() throws IOException {
        File propFile = new File(propertiesPathname);
        FileInputStream propReader = new FileInputStream(propFile);
        properties.load(propReader);
    }

    public void resetColors(boolean loadFromFile) {
        if(properties.isEmpty()) {
            try {
                saveProperties();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        boolean configOk = true;
        if(loadFromFile) {
            if (properties.containsKey("bgColor:R") && properties.containsKey("bgColor:G") && properties.containsKey("bgColor:B")) {
                theme[0] = new Color(Integer.valueOf(properties.getProperty("bgColor:R")), Integer.valueOf(properties.getProperty("bgColor:G")), Integer.valueOf(properties.getProperty("bgColor:B")));
            } else {
                configOk = false;
            }
            if (properties.containsKey("fgColor:R") && properties.containsKey("fgColor:G") && properties.containsKey("fgColor:B")) {
                theme[1] = new Color(Integer.valueOf(properties.getProperty("fgColor:R")), Integer.valueOf(properties.getProperty("fgColor:G")), Integer.valueOf(properties.getProperty("fgColor:B")));
            } else {
                configOk = false;
            }
            if (properties.containsKey("accColor:R") && properties.containsKey("accColor:G") && properties.containsKey("accColor:B")) {
                theme[2] = new Color(Integer.valueOf(properties.getProperty("accColor:R")), Integer.valueOf(properties.getProperty("accColor:G")), Integer.valueOf(properties.getProperty("accColor:B")));
            } else {
                configOk = false;
            }
            if (properties.containsKey("llnColor:R") && properties.containsKey("llnColor:G") && properties.containsKey("llnColor:B")) {
                theme[3] = new Color(Integer.valueOf(properties.getProperty("llnColor:R")), Integer.valueOf(properties.getProperty("llnColor:G")), Integer.valueOf(properties.getProperty("llnColor:B")));
            } else {
                configOk = false;
            }
            if (properties.containsKey("rlnColor:R") && properties.containsKey("rlnColor:G") && properties.containsKey("rlnColor:B")) {
                theme[4] = new Color(Integer.valueOf(properties.getProperty("rlnColor:R")), Integer.valueOf(properties.getProperty("rlnColor:G")), Integer.valueOf(properties.getProperty("rlnColor:B")));
            } else {
                configOk = false;
            }
            if (properties.containsKey("txtColor:R") && properties.containsKey("txtColor:G") && properties.containsKey("txtColor:B")) {
                theme[5] = new Color(Integer.valueOf(properties.getProperty("txtColor:R")), Integer.valueOf(properties.getProperty("txtColor:G")), Integer.valueOf(properties.getProperty("txtColor:B")));
            } else {
                configOk = false;
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
        this.setBackground(theme[0]);
        this.setForeground(theme[5]);
        menuBar.setBackground(theme[0]);
        menuBar.setForeground(theme[5]);
        fileMenu.setBackground(theme[0]);
        fileMenu.setForeground(theme[5]);
        newButton.setBackground(theme[0]);
        newButton.setForeground(theme[5]);
        openButton.setBackground(theme[0]);
        openButton.setForeground(theme[5]);
        exitButton.setBackground(theme[0]);
        exitButton.setForeground(theme[5]);
        optionMenu.setBackground(theme[0]);
        optionMenu.setForeground(theme[5]);
        themeButton.setBackground(theme[0]);
        themeButton.setForeground(theme[5]);

        if(themeDialog != null) themeDialog.resetColors();

        for(AudioWindow aw: audioWindows){
            aw.resetColors();
        }
    }

    public void removeWindow(AudioWindow aw){
        audioWindows.remove(aw);
        aw.dispose();
    }

    public void addWindow(AudioWindow aw){
        audioWindows.remove(aw);
        desktop.add(aw);
    }

    public void buildWindow(AudioFileManager fileManager){
        AudioWindow newAW = new AudioWindow(200, 100, fileManager, this);
        addWindow(newAW);
    }

    public class OpenFileAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setBackground(theme[0]);
            fileChooser.setForeground(theme[5]);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "WAV Files", "wav");
            fileChooser.setFileFilter(filter);
            int returnVal = fileChooser.showOpenDialog(desktop);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                File selection = fileChooser.getSelectedFile();
                buildWindow(new AudioFileManager(selection));
            }
        }
    }

    //Allows for recoloring of the window + runtime changes
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

            themeDialog.buildDialog(600, 320);
        }
    }

    private void addColorChoice(AdaptiveDialog dialog, int color, String colorName){
        JPanel p = new JPanel();
        dialog.addItem(p, 6, color, false);

        JButton b = new JButton(colorName);
        b.addActionListener(new ColorAction(color, colorName));
        dialog.addItem(b, 6, color, true);
    }

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
            colorDialog.buildDialog();
        }
    }
}
