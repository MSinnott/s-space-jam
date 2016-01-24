import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

public class AudioDesktop extends JFrame{

    public static Color bgColor = new Color(252, 53, 0);
    public static Color fgColor = new Color(252, 127, 3);
    public static Color accColor = new Color(255, 201, 8);
    public static Color llnColor = new Color(55, 236, 255);
    public static Color rlnColor = new Color(29, 46, 255);
    public static Color txtColor = new Color(255, 255, 255);

    public static Color defbgColor = new Color(252, 53, 0);
    public static Color deffgColor = new Color(252, 127, 3);
    public static Color defaccColor = new Color(255, 201, 8);
    public static Color defllnColor = new Color(55, 236, 255);
    public static Color defrlnColor = new Color(29, 46, 255);
    public static Color deftxtColor = new Color(255, 255, 255);

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
        newButton.addActionListener(new OpenFile());

        openButton = new JMenuItem("Open");
        fileMenu.add(openButton);
        openButton.addActionListener(new OpenFile());

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
        resetColors();
        this.setVisible(true);
        this.invalidate();
        this.repaint();
    }

    //Allows for recoloring of the window + runtime changes
    public class ThemeSelectorAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            final JDialog colorDialog = new JDialog();
            JTabbedPane tabbedPane = new JTabbedPane();
            Container conPane = colorDialog.getContentPane();
            conPane.setLayout(new BorderLayout());
            conPane.add(tabbedPane, BorderLayout.CENTER);

            final JColorChooser bgColorChooser = new JColorChooser();
            final JColorChooser fgColorChooser = new JColorChooser();
            final JColorChooser accColorChooser = new JColorChooser();
            final JColorChooser leftlnColorChooser = new JColorChooser();
            final JColorChooser rightlnColorChooser = new JColorChooser();
            final JColorChooser txtColorChooser = new JColorChooser();

            tabbedPane.addTab("Background", bgColorChooser);
            tabbedPane.addTab("Foreground", fgColorChooser);
            tabbedPane.addTab("Accent", accColorChooser);
            tabbedPane.addTab("Left Channel Line", leftlnColorChooser);
            tabbedPane.addTab("Right Channel Line", rightlnColorChooser);
            tabbedPane.addTab("Text", txtColorChooser);

            bgColorChooser.setColor(bgColor);
            fgColorChooser.setColor(fgColor);
            accColorChooser.setColor(accColor);
            leftlnColorChooser.setColor(llnColor);
            rightlnColorChooser.setColor(rlnColor);
            txtColorChooser.setColor(txtColor);

            JButton exitButton = new JButton("Done?");
            JButton cancelButton = new JButton("Cancel!");
            JButton resetTheme = new JButton("Reset?");

            exitButton.setBackground(AudioDesktop.bgColor);
            exitButton.setForeground(AudioDesktop.txtColor);
            cancelButton.setBackground(AudioDesktop.bgColor);
            cancelButton.setForeground(AudioDesktop.txtColor);
            resetTheme.setBackground(bgColor);
            resetTheme.setForeground(txtColor);

            resetTheme.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    bgColor = defbgColor;
                    fgColor = deffgColor;
                    accColor = defaccColor;
                    llnColor = defllnColor;
                    rlnColor = defrlnColor;
                    txtColor = deftxtColor;
                    try {
                        saveProperties();
                        resetColors();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    colorDialog.dispose();
                }
            });

            exitButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    bgColor = bgColorChooser.getColor();
                    fgColor = fgColorChooser.getColor();
                    accColor = accColorChooser.getColor();
                    llnColor = leftlnColorChooser.getColor();
                    rlnColor = rightlnColorChooser.getColor();
                    txtColor = txtColorChooser.getColor();
                    try {
                        saveProperties();
                        resetColors();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    colorDialog.dispose();
                }
            });

            cancelButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    colorDialog.dispose();
                }
            });

            JPanel buttonPane = new JPanel();
            conPane.add(buttonPane, BorderLayout.SOUTH);
            buttonPane.setLayout(new BorderLayout());
            buttonPane.add(exitButton, BorderLayout.WEST);
            buttonPane.add(cancelButton, BorderLayout.EAST);
            buttonPane.add(resetTheme, BorderLayout.CENTER);

            colorDialog.setSize(480, 320);
            colorDialog.setVisible(true);
        }
    }

    //prob should autogen these... ah well!
    public void saveProperties() throws IOException {
        properties.setProperty("bgColor:R", String.valueOf(bgColor.getRed()));
        properties.setProperty("bgColor:G", String.valueOf(bgColor.getGreen()));
        properties.setProperty("bgColor:B", String.valueOf(bgColor.getBlue()));

        properties.setProperty("fgColor:R", String.valueOf(fgColor.getRed()));
        properties.setProperty("fgColor:G", String.valueOf(fgColor.getGreen()));
        properties.setProperty("fgColor:B", String.valueOf(fgColor.getBlue()));

        properties.setProperty("accColor:R", String.valueOf(accColor.getRed()));
        properties.setProperty("accColor:G", String.valueOf(accColor.getGreen()));
        properties.setProperty("accColor:B", String.valueOf(accColor.getBlue()));

        properties.setProperty("llnColor:R", String.valueOf(llnColor.getRed()));
        properties.setProperty("llnColor:G", String.valueOf(llnColor.getGreen()));
        properties.setProperty("llnColor:B", String.valueOf(llnColor.getBlue()));

        properties.setProperty("rlnColor:R", String.valueOf(rlnColor.getRed()));
        properties.setProperty("rlnColor:G", String.valueOf(rlnColor.getGreen()));
        properties.setProperty("rlnColor:B", String.valueOf(rlnColor.getBlue()));

        properties.setProperty("txtColor:R", String.valueOf(txtColor.getRed()));
        properties.setProperty("txtColor:G", String.valueOf(txtColor.getGreen()));
        properties.setProperty("txtColor:B", String.valueOf(txtColor.getBlue()));

        File propFile = new File(propertiesPathname);
        BufferedWriter propOut = new BufferedWriter(new FileWriter(propFile));
        properties.store(propOut, "Colors");
    }

    public void updateProperties() throws IOException {
        File propFile = new File(propertiesPathname);
        FileInputStream propReader = new FileInputStream(propFile);
        properties.load(propReader);
    }

    public void resetColors() {
        if(properties.isEmpty()) {
            try {
                saveProperties();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        boolean configOk = true;

        if(properties.containsKey("bgColor:R") && properties.containsKey("bgColor:G") && properties.containsKey("bgColor:B")) {
            bgColor = new Color(Integer.valueOf(properties.getProperty("bgColor:R")), Integer.valueOf(properties.getProperty("bgColor:G")), Integer.valueOf(properties.getProperty("bgColor:B")));
        } else {
            configOk = false;
        }
        if(properties.containsKey("fgColor:R") && properties.containsKey("fgColor:G") && properties.containsKey("fgColor:B")) {
            fgColor = new Color(Integer.valueOf(properties.getProperty("fgColor:R")), Integer.valueOf(properties.getProperty("fgColor:G")), Integer.valueOf(properties.getProperty("fgColor:B")));
        } else {
            configOk = false;
        }
        if(properties.containsKey("accColor:R") && properties.containsKey("accColor:G") && properties.containsKey("accColor:B")) {
            accColor = new Color(Integer.valueOf(properties.getProperty("accColor:R")), Integer.valueOf(properties.getProperty("accColor:G")), Integer.valueOf(properties.getProperty("accColor:B")));
        } else {
            configOk = false;
        }
        if(properties.containsKey("llnColor:R") && properties.containsKey("llnColor:G") && properties.containsKey("llnColor:B")) {
            llnColor = new Color(Integer.valueOf(properties.getProperty("llnColor:R")), Integer.valueOf(properties.getProperty("llnColor:G")), Integer.valueOf(properties.getProperty("llnColor:B")));
        } else {
            configOk = false;
        }
        if(properties.containsKey("rlnColor:R") && properties.containsKey("rlnColor:G") && properties.containsKey("rlnColor:B")) {
            rlnColor = new Color(Integer.valueOf(properties.getProperty("rlnColor:R")), Integer.valueOf(properties.getProperty("rlnColor:G")), Integer.valueOf(properties.getProperty("rlnColor:B")));
        } else {
            configOk = false;
        }
        if(properties.containsKey("txtColor:R") && properties.containsKey("txtColor:G") && properties.containsKey("txtColor:B")) {
            txtColor = new Color(Integer.valueOf(properties.getProperty("txtColor:R")), Integer.valueOf(properties.getProperty("txtColor:G")), Integer.valueOf(properties.getProperty("txtColor:B")));
        } else {
            configOk = false;
        }

        if(!configOk){
            try {
                saveProperties();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        desktop.setBackground(fgColor);
        desktop.setForeground(txtColor);
        this.setBackground(bgColor);
        this.setForeground(txtColor);
        menuBar.setBackground(bgColor);
        menuBar.setForeground(txtColor);
        fileMenu.setBackground(bgColor);
        fileMenu.setForeground(txtColor);
        newButton.setBackground(bgColor);
        newButton.setForeground(txtColor);
        openButton.setBackground(bgColor);
        openButton.setForeground(txtColor);
        exitButton.setBackground(bgColor);
        exitButton.setForeground(txtColor);
        optionMenu.setBackground(bgColor);
        optionMenu.setForeground(txtColor);
        themeButton.setBackground(bgColor);
        themeButton.setForeground(txtColor);

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
        AudioWindow newAW = new AudioWindow(fileManager.getName(), 200, 100, fileManager, this);
        addWindow(newAW);
    }

    public class OpenFile extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setBackground(bgColor);
            fileChooser.setForeground(txtColor);
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


}
