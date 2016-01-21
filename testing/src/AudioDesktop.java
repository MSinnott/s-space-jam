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
    public static Color lnColor = new Color(29, 46, 255);
    public static Color txtColor = new Color(255, 255, 255);

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
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
            final JColorChooser lnColorChooser = new JColorChooser();
            final JColorChooser txtColorChooser = new JColorChooser();
            tabbedPane.addTab("Background", bgColorChooser);
            tabbedPane.addTab("Foreground", fgColorChooser);
            tabbedPane.addTab("Accent", accColorChooser);
            tabbedPane.addTab("Line", lnColorChooser);
            tabbedPane.addTab("Text", txtColorChooser);
            JButton exitButton = new JButton("Done?");
            JButton cancelButton = new JButton("Cancel!");
            exitButton.setBackground(AudioDesktop.bgColor);
            exitButton.setForeground(AudioDesktop.txtColor);
            cancelButton.setBackground(AudioDesktop.bgColor);
            cancelButton.setForeground(AudioDesktop.txtColor);
            bgColorChooser.setColor(bgColor);
            fgColorChooser.setColor(fgColor);
            accColorChooser.setColor(accColor);
            lnColorChooser.setColor(lnColor);
            txtColorChooser.setColor(txtColor);
            exitButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    bgColor = bgColorChooser.getColor();
                    fgColor = fgColorChooser.getColor();
                    accColor = accColorChooser.getColor();
                    lnColor = lnColorChooser.getColor();
                    txtColor = txtColorChooser.getColor();
                    try {
                        saveProperties();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    resetColors();
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
            colorDialog.setSize(480, 320);
            colorDialog.setVisible(true);
        }
    }

    //prob should autogen these...
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

        properties.setProperty("lnColor:R", String.valueOf(lnColor.getRed()));
        properties.setProperty("lnColor:G", String.valueOf(lnColor.getGreen()));
        properties.setProperty("lnColor:B", String.valueOf(lnColor.getBlue()));

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

    public void resetColors(){
        bgColor = new Color(Integer.valueOf(properties.getProperty("bgColor:R")), Integer.valueOf(properties.getProperty("bgColor:G")), Integer.valueOf(properties.getProperty("bgColor:B")));
        fgColor = new Color(Integer.valueOf(properties.getProperty("fgColor:R")), Integer.valueOf(properties.getProperty("fgColor:G")), Integer.valueOf(properties.getProperty("fgColor:B")));
        accColor = new Color(Integer.valueOf(properties.getProperty("accColor:R")), Integer.valueOf(properties.getProperty("accColor:G")), Integer.valueOf(properties.getProperty("accColor:B")));
        lnColor = new Color(Integer.valueOf(properties.getProperty("lnColor:R")), Integer.valueOf(properties.getProperty("lnColor:G")), Integer.valueOf(properties.getProperty("lnColor:B")));
        txtColor = new Color(Integer.valueOf(properties.getProperty("txtColor:R")), Integer.valueOf(properties.getProperty("txtColor:G")), Integer.valueOf(properties.getProperty("txtColor:B")));

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
                System.out.println(selection.getAbsolutePath() + " " +selection.length());
                AudioWindow newAW = new AudioWindow(selection.getName(), 200, 100, new AudioFileManager(selection), desktop, audioWindows);
                audioWindows.add(newAW);
                desktop.add(newAW);
            }
        }
    }


}
