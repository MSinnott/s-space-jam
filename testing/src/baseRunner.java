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

        //makes a file that play 2 tones, one on each channel!!!
        byte[] newSamples = new byte[4 * 22050 * 10];
        int leftSide;
        int rightSide;
        for(int i = 0; i < newSamples.length; i+= 4){
            leftSide = (int) (8000 * Math.sin(2 * Math.PI * i / 500));
            rightSide = (int) (8000 * Math.sin(2 * Math.PI * i / 400));
            newSamples[i] = (byte) (leftSide & 255);
            newSamples[i+1] = (byte) ((leftSide / 256) & 255);
            newSamples[i+2] = (byte) (rightSide & 255);
            newSamples[i+3] = (byte) ((rightSide / 256) & 255);
        }

        AudioFileManager newFile = new AudioFileManager(newSamples);
        newFile.buildFile("testing/resc/new.wav");

        Clip audioClip = audioFile.getClip();
        audioClip.start();

    }
        
}
