import java.io.*;

public class baseRunner {

    static short[] samples;

    public static void main(String[] args) throws IOException, InterruptedException {
        AudioFileManager audioFile = new AudioFileManager("testing/music/space oddity.wav");

        //grabbing music audioFile --a
        samples = audioFile.getAudioData();

        //making and initializing window --m
        AudioDesktop mainWindow = new AudioDesktop("sSpace -- Music Creator!", 600, 500);
        mainWindow.invalidate();
        mainWindow.repaint();


        audioFile.buildFile("testing/music/space oddity OC");

        //audioFile.ftransform();
        //audioFile.btransform();

        audioFile.buildFile("testing/music/space oddity TRANSFORM.wav");

        System.out.println("Done!");
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

}
