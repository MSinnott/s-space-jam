import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;

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

    public AudioDesktop(String name, int width, int height){
        super(name);

        audioWindows = new ArrayList<AudioWindow>();

        desktop = new JDesktopPane();
        this.setLayout(new BorderLayout());
        this.add(desktop, BorderLayout.CENTER);
        this.setSize(width, height);

        this.setResizable(true);
        this.setIconImage(new ImageIcon("testing/s-Space-Jam-Logo.jpg").getImage());
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

        resetColors();
        this.setVisible(true);
        this.invalidate();
        this.repaint();

    }

    public class ThemeSelectorAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            final JDialog colorDialog = new JDialog();
            final JColorChooser bgColorChooser = new JColorChooser();
            final JColorChooser fgColorChooser = new JColorChooser();
            final JColorChooser accColorChooser = new JColorChooser();
            final JColorChooser lnColorChooser = new JColorChooser();
            final JColorChooser txtColorChooser = new JColorChooser();
            colorDialog.setLayout(new GridLayout());
            colorDialog.add(bgColorChooser);
            colorDialog.add(fgColorChooser);
            colorDialog.add(accColorChooser);
            colorDialog.add(lnColorChooser);
            colorDialog.add(txtColorChooser);
            JButton exitButton = new JButton("Done?");
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
                    resetColors();
                    colorDialog.dispose();
                }
            });
            colorDialog.add(exitButton);
            colorDialog.setSize(480, 480);
            colorDialog.setVisible(true);
        }
    }

    public void resetColors(){
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
                System.out.println(selection.getAbsolutePath());
                AudioWindow newAW = new AudioWindow(fileChooser.getSelectedFile().getName(), 200, 100, new AudioFileManager(selection), desktop, audioWindows);
                audioWindows.add(newAW);
                desktop.add(newAW);
            }
        }
    }


}
