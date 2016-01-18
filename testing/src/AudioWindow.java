import javax.sound.sampled.AudioSystem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

public class AudioWindow extends JInternalFrame{


    private MainPane pane;
    private short[] data;
    private AudioFileManager audioFile;
    private AudioWindow audioWindow = this;

    public AudioWindow(String name, int width, int height, File f){
        super(name);

        this.setSize(width, height);
        this.setResizable(true);
        this.setVisible(true);
        this.setLayout(new BorderLayout());



        this.setBackground(AudioDesktop.accColor);

        buildMenus();

        pane = new MainPane();

        loadFile(f);
        this.add(pane);
    }

    public void loadFile(File f){
        audioFile = new AudioFileManager(f);
        data = audioFile.getAudioData();
        pane.setNotes(data);
    }

    public void buildMenus(){
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

        JMenu opMenu = new JMenu("Operations");
        opMenu.setBackground(AudioDesktop.bgColor);
        opMenu.setForeground(AudioDesktop.txtColor);
        menuBar.add(opMenu);

        JMenuItem transformButton = new JMenuItem("FFT");
        transformButton.setBackground(AudioDesktop.bgColor);
        transformButton.setForeground(AudioDesktop.txtColor);
        opMenu.add(transformButton);
        transformButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pane.setNotes(audioFile.transform());
                pane.invalidate();
                pane.repaint();
                audioWindow.invalidate();
                audioWindow.repaint();
            }
        });

        JMenuItem scaleButton = new JMenuItem("scale");
        scaleButton.setBackground(AudioDesktop.bgColor);
        scaleButton.setForeground(AudioDesktop.txtColor);
        opMenu.add(scaleButton);
        scaleButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JDialog numberDialog = new JDialog();
                numberDialog.setTitle("Scale By:");
                numberDialog.setLayout(new BorderLayout());
                numberDialog.setLocation(480, 480);
                numberDialog.setSize(200, 64);
                numberDialog.setResizable(false);
                final JTextField numField = new JTextField();
                numberDialog.add(numField, BorderLayout.CENTER);
                final double[] scale = {1};
                numField.addKeyListener(new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {

                    }

                    @Override
                    public void keyReleased(KeyEvent e) {

                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                        if(e.getKeyCode() == 10 && Double.valueOf(numField.getText()) != Double.NaN){
                            scale[0] = Double.valueOf(numField.getText());
                            pane.setNotes(audioFile.scale(scale[0]));
                            pane.invalidate();
                            pane.repaint();
                            audioWindow.invalidate();
                            audioWindow.repaint();
                            numberDialog.dispose();
                        }
                    }
                });
                numberDialog.setVisible(true);

            }
        });
    }

}
