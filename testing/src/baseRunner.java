import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioSystem;


/**
 * Created by Administratuh on 1/14/2016.
 */
public class baseRunner {
    
    public static void main(String[] args){
        File file = new File("Music/loc.wav"); //grabbing music file
        byte[] samples;
        //see if this works. I pulled it off of stack overflow
        AudioInputSteam is = AudioSystem.getAudioInputStream(myFile);
        DataInputStream dis = new DataInputStream(is);
        try {
            AudioFormat format = is.getFormat();
            samples = new byte[(int)(is.getFrameLength() * format.getFrameSize())];
            dis.readFully(samples);
        } finally {
            dis.close();
        }
        // So this should work by loading the contents of the file into byte using getAudioInputStream
        //Notice how this wont get header information and metadata, but that probably isn't a problem.
        //this works for .au audio files
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        
}
