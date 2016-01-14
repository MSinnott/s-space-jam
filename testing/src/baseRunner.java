import java.io.File;
import java.io.IOException;

/**
 * Created by Administratuh on 1/14/2016.
 */
public class baseRunner {
    
    public static void main(String[] args){
        File file = new File("Music/loc.wav"); //grabbing music file
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        
}
