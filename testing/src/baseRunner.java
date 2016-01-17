import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;

public class baseRunner {
    
    public static void main(String[] args) throws IOException {
        AudioFileManager audioFile = new AudioFileManager("testing/resc/space oddity.au");
        //grabbing music audioFile --a
        byte[] samples;

        //making and initializing window --m
        JFrame mainWindow = new JFrame("sSpace");
        MainPane panel = new MainPane();

        mainWindow.setIconImage(new ImageIcon("testing/s-Space-Jam-Logo.jpg").getImage());
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setVisible(true);
        mainWindow.setResizable(false);
        mainWindow.pack();
        mainWindow.setSize(480, 480);
        mainWindow.add(panel);

        samples = audioFile.getAudioBytes();
        System.out.println(samples.length);

        Clip audioClip = audioFile.getClip();
        audioClip.start();

    }
        
}
