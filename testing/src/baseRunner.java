import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;
import javax.swing.*;

public class baseRunner {
    
    public static void main(String[] args) throws IOException {
        File audioFile = new File("testing/resc/space oddity.au"); //grabbing music audioFile --a
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

        
        //see if this works. I pulled it off of stack overflow --a
        AudioInputStream audioIn = null;
        try {
            audioIn = AudioSystem.getAudioInputStream(audioFile);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DataInputStream dataIn = new DataInputStream(audioIn);
        try {
            AudioFormat format = audioIn.getFormat();
            samples = new byte[(int)(audioIn.getFrameLength() * format.getFrameSize())];
            System.out.println(samples.length);
            dataIn.readFully(samples);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            dataIn.close();
        }
    }
        
}
