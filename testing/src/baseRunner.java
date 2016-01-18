import java.io.*;
import javax.sound.sampled.*;

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
        AudioDesktop mainWindow = new AudioDesktop("sSpace -- Music Creator!", windowWidth, windowHeight);
    }
}
