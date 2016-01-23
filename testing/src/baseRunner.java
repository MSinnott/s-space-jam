import java.io.*;

public class baseRunner {

    public static void main(String[] args) throws IOException, InterruptedException {
        //making and initializing window --m
        AudioDesktop mainWindow = new AudioDesktop("sSpace -- Music Creator!", 600, 500);

        AudioFileManager oneTone = new AudioFileManager(getStereoTone(500, 600, 8*44100));
        mainWindow.buildWindow(oneTone);

        oneTone.buildFile("testing/music/singleTone.wav");
    }

    //just for testing
    public static short[] getStereoTone(double freqLeft, double freqRight, int numSamples){
        short[] tone = new short[numSamples*=2];
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

}
