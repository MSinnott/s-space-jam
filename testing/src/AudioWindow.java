import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class AudioWindow extends JInternalFrame{

    private MainPane pane;
    private AudioFileManager audioFile;
    private JDesktopPane desktop;
    private AudioWindow audioWindow = this;
    private ArrayList<AudioWindow> audioWindows;

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem cloneButton;
    private JMenuItem exitButton;
    private JMenu opMenu;
    private JMenuItem ftransformButton;
    private JMenuItem btransformButton;
    private JMenuItem scaleButton;

    private String windowName;

    private short[] leftData;

    public AudioWindow(String name, int width, int height, AudioFileManager fileManager, JDesktopPane aDesk, ArrayList<AudioWindow> audioWindows){
        super(name);

        windowName = name;

        desktop = aDesk;
        this.audioWindows = audioWindows;

        this.setSize(width, height);
        this.setResizable(true);

        this.setLayout(new BorderLayout());


        pane = new MainPane();
        loadFile(fileManager);

        Container c = this.getContentPane();
        c.setLayout(new BorderLayout());

        c.add(pane, BorderLayout.CENTER);
        c.addKeyListener(pane);
        pane.addKeyListener(pane);

        buildMenus();

        this.setVisible(true);
    }

    public String getWindowName(){
        return windowName;
    }

    public void loadFile(AudioFileManager fileManager){
        audioFile = fileManager;
        leftData = audioFile.getLeftChannel();
        updatePane();
    }

    public void buildMenus(){
        menuBar = new JMenuBar();

        this.add(menuBar, BorderLayout.NORTH);

        fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        cloneButton = new JMenuItem("Clone");
        fileMenu.add(cloneButton);
        cloneButton.addActionListener(new CloneAction(this));

        exitButton = new JMenuItem("Exit");
        fileMenu.add(exitButton);
        exitButton.addActionListener(new CloseAction(this));

        opMenu = new JMenu("Operations");
        menuBar.add(opMenu);

        ftransformButton = new JMenuItem("Forward FFT");
        opMenu.add(ftransformButton);
        ftransformButton.addActionListener(new forwardFFtAction());

        btransformButton = new JMenuItem("Backward FFT");
        opMenu.add(btransformButton);
        btransformButton.addActionListener(new backwardFFtAction());

        scaleButton = new JMenuItem("Scale Vertically");
        opMenu.add(scaleButton);
        scaleButton.addActionListener(new scaleAction());

        resetColors();
    }

    public void resetColors(){
        menuBar.setBackground(AudioDesktop.bgColor);
        menuBar.setForeground(AudioDesktop.txtColor);
        fileMenu.setBackground(AudioDesktop.bgColor);
        fileMenu.setForeground(AudioDesktop.txtColor);
        cloneButton.setBackground(AudioDesktop.bgColor);
        cloneButton.setForeground(AudioDesktop.txtColor);
        exitButton.setBackground(AudioDesktop.bgColor);
        exitButton.setForeground(AudioDesktop.txtColor);
        opMenu.setBackground(AudioDesktop.bgColor);
        opMenu.setForeground(AudioDesktop.txtColor);
        ftransformButton.setBackground(AudioDesktop.bgColor);
        ftransformButton.setForeground(AudioDesktop.txtColor);
        btransformButton.setBackground(AudioDesktop.bgColor);
        btransformButton.setForeground(AudioDesktop.txtColor);
        scaleButton.setBackground(AudioDesktop.bgColor);
        scaleButton.setForeground(AudioDesktop.txtColor);
        this.setBackground(AudioDesktop.accColor);
        this.setForeground(AudioDesktop.txtColor);

        this.invalidate();
        this.repaint();
        pane.invalidate();
        pane.repaint();
    }

    public void setView(int pan, double zoom){
        pane.setPan(pan);
        pane.setZoom(zoom);
    }

    public void removeFromDesktop(){
        audioWindows.remove(this);
    }

    //Creates + adds a clone of this object to the desktop
    public class CloneAction extends AbstractAction {
        private AudioWindow audioWindow;
        public CloneAction(AudioWindow window){
            audioWindow = window;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            AudioWindow clone = new AudioWindow(audioWindow.getWindowName(), audioWindow.getWidth(), audioWindow.getHeight(), audioWindow.audioFile, desktop, audioWindows);
            desktop.add(clone);
            audioWindows.add(clone);
            clone.moveToFront();
            clone.setLocation(audioWindow.getX() + 32, audioWindow.getY() + 32);
            clone.setView(pane.getPan(), pane.getZoom());
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
                        audioFile.scale(scale[0]);
                        updatePane();
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
            audioFile.ftransform();
            updatePane();
        }
    }

    public class backwardFFtAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            audioFile.btransform();
            updatePane();
        }
    }

    public void updatePane(){
        pane.setLeftData(audioFile.getLeftChannel());
        pane.setRightData(audioFile.getRightChannel());
        pane.invalidate();
        pane.repaint();
        audioWindow.invalidate();
        audioWindow.repaint();
    }

}
