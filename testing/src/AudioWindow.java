import javax.swing.*;
import java.awt.*;
import java.io.File;

public class AudioWindow extends JInternalFrame{

    private JScrollPane scroll;
    private MainPane pane;

    public AudioWindow(String name, int width, int height, File f){
        super(name);
        this.setSize(width, height);
        this.setResizable(true);
        this.setVisible(true);
        this.setLayout(new BorderLayout());

        this.setBackground(AudioDesktop.accColor);

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(AudioDesktop.bgColor);
        menuBar.setForeground(AudioDesktop.txtColor);
        this.add(menuBar, BorderLayout.NORTH);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setBackground(AudioDesktop.bgColor);
        fileMenu.setForeground(AudioDesktop.txtColor);
        menuBar.add(fileMenu);

        JMenuItem exitButton = new JMenuItem("Exit");
        exitButton.setBackground(AudioDesktop.bgColor);
        exitButton.setForeground(AudioDesktop.txtColor);
        fileMenu.add(exitButton);
        exitButton.addActionListener(new CloseAction(this));

        scroll = new JScrollPane();
        pane = new MainPane();

        this.add(scroll);
        scroll.add(pane);

        loadFile(f);
    }

    public void loadFile(File f){
        AudioFileManager audioFile = new AudioFileManager(f);
        short[] data = audioFile.getAudioData();
        pane.setNotes(data);
    }
}
