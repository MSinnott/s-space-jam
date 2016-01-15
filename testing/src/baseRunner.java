import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;


/**
 * Created by Administratuh on 1/14/2016.
 */
public class baseRunner {
    
    public static void main(String[] args) throws IOException {
        File file = new File("testing/resc/space oddity.au"); //grabbing music file
        byte[] samples;
        
        //see if this works. I pulled it off of stack overflow
        AudioInputStream is = null;
        try {
            is = AudioSystem.getAudioInputStream(file);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DataInputStream dis = new DataInputStream(is);
        try {
            AudioFormat format = is.getFormat();
            samples = new byte[(int)(is.getFrameLength() * format.getFrameSize())];
//            for (int i = 0; i < samples.length; i++) {
                    System.out.println(samples.length);
//            } SO problem. All of the 58286592 bits of samples are zeroes
            //please note, that with audio, zeroes indicate silence. So maybe something up with file? It plays properly.
            dis.readFully(samples);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            dis.close();
        }
        //matt's stuff
//        try {
//            file.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
        
}
