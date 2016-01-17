import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;

public class baseRunner {
    
    public static void main(String[] args) throws IOException {
        AudioFileManager audioFile = new AudioFileManager("testing/resc/space oddity.wav");
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

        byte[] newSamples = getStereoTone(800, 500, 22050 * 4*2);
        newSamples = concat(newSamples, getStereoTone(1600, 600, 22050*4*3));
        AudioFileManager newFile = new AudioFileManager(newSamples);
        newFile.buildFile("testing/resc/new.wav");

        Clip audioClip = audioFile.getClip();
        audioClip.start();

    }

    public static byte[] getStereoTone(int freqLeft, int freqRight, int numSamples){
        byte[] tone = new byte[numSamples];
        int rightSide;
        int leftSide;
        for(int i = 0; i < numSamples; i+= 4){
            leftSide = (int) (8000 * Math.sin(2 * Math.PI * i / freqLeft));
            rightSide = (int) (8000 * Math.sin(2 * Math.PI * i / freqRight));
            tone[i] = (byte) (leftSide & 255);
            tone[i+1] = (byte) ((leftSide / 256) & 255);
            tone[i+2] = (byte) (rightSide & 255);
            tone[i+3] = (byte) ((rightSide / 256) & 255);
        }
        return tone;
    }

    public static byte[] concat(byte[] arr1, byte[] arr2){
        byte[] arrConcat = new byte[arr1.length + arr2.length];
        for(int i = 0; i < arr1.length; i++){
            arrConcat[i] = arr1[i];
        }
        for(int i = 0; i < arr2.length; i++){
            arrConcat[i+arr1.length] = arr2[i];
        }
        return arrConcat;
    }
        
}
