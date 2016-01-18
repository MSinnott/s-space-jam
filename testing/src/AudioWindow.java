import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

public class AudioWindow extends JInternalFrame{


    private MainPane pane;
    private short[] data;
    private AudioFileManager audioFile;
    private AudioWindow audioWindow = this;

    public AudioWindow(String name, int width, int height, AudioFileManager fman){
        super(name);

        this.setSize(width, height);
        this.setResizable(true);
        this.setVisible(true);
        this.setLayout(new BorderLayout());

        this.setBackground(AudioDesktop.accColor);

        buildMenus();

        pane = new MainPane();
        audioFile = fman;
        data = audioFile.getAudioData();
        pane.setData(data);

        this.add(pane);
        this.addKeyListener(pane);
    }

    public void loadFile(File f){
        audioFile = new AudioFileManager(f);
        data = audioFile.getAudioData();
        pane.setData(data);
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

        JMenuItem cloneButton = new JMenuItem("Clone");
        cloneButton.setBackground(AudioDesktop.bgColor);
        cloneButton.setForeground(AudioDesktop.txtColor);
        fileMenu.add(cloneButton);
        cloneButton.addActionListener(new CloneAction(this));

        JMenuItem exitButton = new JMenuItem("Exit");
        exitButton.setBackground(AudioDesktop.bgColor);
        exitButton.setForeground(AudioDesktop.txtColor);
        fileMenu.add(exitButton);
        exitButton.addActionListener(new CloseAction(this));

        JMenu opMenu = new JMenu("Operations");
        opMenu.setBackground(AudioDesktop.bgColor);
        opMenu.setForeground(AudioDesktop.txtColor);
        menuBar.add(opMenu);

        JMenuItem ftransformButton = new JMenuItem("Forward FFT");
        ftransformButton.setBackground(AudioDesktop.bgColor);
        ftransformButton.setForeground(AudioDesktop.txtColor);
        opMenu.add(ftransformButton);
        ftransformButton.addActionListener(new forwardFFtAction());

        JMenuItem scaleButton = new JMenuItem("Scale Vertically");
        scaleButton.setBackground(AudioDesktop.bgColor);
        scaleButton.setForeground(AudioDesktop.txtColor);
        opMenu.add(scaleButton);
        scaleButton.addActionListener(new scaleAction());
    }

    public class CloneAction extends AbstractAction {
        private AudioWindow audioWindow;
        public CloneAction(AudioWindow window){
            audioWindow = window;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            AudioWindow clone = new AudioWindow(audioWindow.getName(), audioWindow.getWidth(), audioWindow.getHeight(), audioWindow.audioFile);
            audioWindow.getParent().add(clone);
            clone.moveToFront();
        }
    }

    public class scaleAction extends AbstractAction {
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
                        pane.setData(audioFile.scale(scale[0]));
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
    }

    public class forwardFFtAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            pane.setData(audioFile.ftransform());
            pane.invalidate();
            pane.repaint();
            audioWindow.invalidate();
            audioWindow.repaint();
        }
    }
}
