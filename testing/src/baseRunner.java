import java.io.*;

public class baseRunner {

    public static void main(String[] args) throws IOException, InterruptedException {
        //making and initializing window --m
        AudioDesktop mainWindow = new AudioDesktop("sSpace -- Music Creator!", 600, 500);

        AudioFileManager oneTone = new AudioFileManager(getStereoTone(20000, 500, 8*44100));
        oneTone.buildFile("testing/music/singleTone.wav");
        mainWindow.buildWindow(oneTone);

        AudioFileManager twoTone = new AudioFileManager("testing/music/singleTone.wav");
        mainWindow.buildWindow(twoTone);
    }

    //just for testing
    public static float[] getStereoTone(double freqLeft, double freqRight, int numSamples){
        float[] tone = new float[numSamples*=2];
        float rightSide;
        float leftSide;
        for(int i = 0; i < numSamples; i+= 2){
            leftSide = (float) (6000 * Math.sin(2 * Math.PI * i / freqLeft));
            rightSide = (float) (6000 * Math.sin(2 * Math.PI * i / freqRight));
            tone[i] = leftSide;
            tone[i+1] = rightSide;
        }
        return tone;
    }

}
