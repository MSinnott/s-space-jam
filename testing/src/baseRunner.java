import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;

public class baseRunner {

    static short[] samples;

    public static void main(String[] args) throws IOException, InterruptedException {
        AudioFileManager audioFile = new AudioFileManager("testing/resc/space oddity.wav");

        //grabbing music audioFile --a
        samples = audioFile.getAudioData();

        //making and initializing window --m
        makeWindow(1280, 1000);


        short[] newSamples = getStereoTone(60., 50., 22050 * 4*3);

        AudioFileManager newFile = new AudioFileManager(newSamples);
        newFile.buildFile("testing/resc/new.wav");

        System.out.println("reading clip");
        Clip audioClip = newFile.getClip();
        audioClip.start();
    }

    public static short[] getStereoTone(double freqLeft, double freqRight, int numSamples){
        short[] tone = new short[numSamples];
        short rightSide;
        short leftSide;
        for(int i = 0; i < numSamples; i+= 2){
            leftSide = (short) (6000 * Math.sin(2 * Math.PI * i / freqLeft));
            rightSide = (short) (6000 * Math.sin(2 * Math.PI * i / freqRight));
            tone[i] = leftSide;
            tone[i+1] = rightSide;
        }
        return tone;
    }

    public static short[] concat(short[] arr1, short[] arr2){
        short[] arrConcat = new short[arr1.length + arr2.length];
        for(int i = 0; i < arr1.length; i++){
            arrConcat[i] = arr1[i];
        }
        for(int i = 0; i < arr2.length; i++){
            arrConcat[i+arr1.length] = arr2[i];
        }
        return arrConcat;
    }

    public static void makeWindow(int windowWidth, int windowHeight){
        JFrame mainWindow = new JFrame("sSpace -- Music Creator!");

        MainPane panel = new MainPane();
        panel.notes = samples;
        mainWindow.setIconImage(new ImageIcon("testing/s-Space-Jam-Logo.jpg").getImage());
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setVisible(true);
        mainWindow.setResizable(false);

        JMenuBar menu = new JMenuBar();
        menu.setBackground(new Color(252, 53, 0));
        menu.setForeground(new Color(255, 255, 255));
        mainWindow.setJMenuBar(menu);

            JMenu fileMenu = new JMenu("File");
            fileMenu.setBackground(new Color(252, 53, 0));
            fileMenu.setForeground(new Color(255, 255, 255));

                JMenuItem newFile = new JMenuItem("New");
                newFile.setBackground(new Color(252, 53, 0));
                newFile.setForeground(new Color(255, 255, 255));
                fileMenu.add(newFile);

                JMenuItem open = new JMenuItem("Open");
                open.setBackground(new Color(252, 53, 0));
                open.setForeground(new Color(255, 255, 255));
                fileMenu.add(open);

                JMenuItem save = new JMenuItem("Save");
                save.setBackground(new Color(252, 53, 0));
                save.setForeground(new Color(255, 255, 255));
                fileMenu.add(save);
                save.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                    }
                });

                JMenuItem exit = new JMenuItem("Exit");
                exit.setBackground(new Color(252, 53, 0));
                exit.setForeground(new Color(255, 255, 255));
                fileMenu.add(exit);
                exit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        final JDialog exitDialog = new JDialog();
                        exitDialog.setLayout(new GridBagLayout());
                        exitDialog.setResizable(false);
                        exitDialog.setVisible(true);
                        exitDialog.setTitle("Exit?");
                        JButton exitButton = new JButton("Exit?");
                        exitButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                System.exit(0);
                            }
                        });
                        exitButton.setBackground(new Color(252, 53, 0));
                        exitButton.setForeground(new Color(255, 255, 255));
                        JButton cancelButton = new JButton("Cancel!");
                        cancelButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                exitDialog.dispose();
                            }
                        });
                        cancelButton.setBackground(new Color(252, 53, 0));
                        cancelButton.setForeground(new Color(255, 255, 255));
                        exitDialog.add(exitButton);
                        exitDialog.add(cancelButton);
                        exitDialog.setSize(240, 240);
                        exitDialog.pack();
                        exitDialog.setLocation(480, 480);
                        exitDialog.setBackground(new Color(252, 53, 0));
                        exitDialog.setForeground(new Color(255, 255, 255));
                    }
                });



        menu.add(fileMenu);

        mainWindow.add(panel);
        mainWindow.pack();
        mainWindow.setSize(windowWidth, windowHeight);

    }
}
