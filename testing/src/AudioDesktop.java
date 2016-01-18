import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AudioDesktop extends JFrame{

    public static final Color bgColor = new Color(252, 53, 0);
    public static final Color fgColor = new Color(252, 127, 3);
    public static final Color accColor = new Color(255, 201, 8);
    public static final Color lnColor = new Color(29, 46, 255);
    public static final Color txtColor = new Color(255, 255, 255);

    private JDesktopPane desktop;

    public AudioDesktop(String name, int width, int height){
        super(name);

        desktop = new JDesktopPane();
        this.setLayout(new BorderLayout());
        this.add(desktop, BorderLayout.CENTER);
        this.setSize(width, height);

        this.setResizable(true);
        this.setBackground(bgColor);
        this.setForeground(txtColor);
        this.setIconImage(new ImageIcon("testing/s-Space-Jam-Logo.jpg").getImage());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        desktop.setBackground(fgColor);
        desktop.setForeground(txtColor);

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(bgColor);
        menuBar.setForeground(txtColor);
        this.add(menuBar, BorderLayout.NORTH);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setBackground(bgColor);
        fileMenu.setForeground(txtColor);
        menuBar.add(fileMenu);

        JMenuItem newButton = new JMenuItem("New");
        newButton.setBackground(bgColor);
        newButton.setForeground(txtColor);
        fileMenu.add(newButton);
        newButton.addActionListener(new OpenFile());

        JMenuItem openButton = new JMenuItem("Open");
        openButton.setBackground(bgColor);
        openButton.setForeground(txtColor);
        fileMenu.add(openButton);
        openButton.addActionListener(new OpenFile());

        JMenuItem exitButton = new JMenuItem("Exit");
        exitButton.setBackground(bgColor);
        exitButton.setForeground(txtColor);
        fileMenu.add(exitButton);
        exitButton.addActionListener(new CloseAction(this));


        this.setVisible(true);
        this.invalidate();
        this.repaint();

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
                desktop.add(new AudioWindow(fileChooser.getSelectedFile().getName(), 200, 100, new AudioFileManager(fileChooser.getSelectedFile())));
            }
        }
    }


}
