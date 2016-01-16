import javax.swing.*;
import java.awt.*;

public class MainPane extends JPanel {

    public MainPane(){
        super();
    }

    /* pretty graphical window for the music --m */
    @Override
    public void paintComponent(Graphics g){
        for(int i = 0; i < 30; i+=5){
            g.setColor(new Color(254, 5*i + 75, 3));
            g.fillRect(i , i , this.getWidth() - 2*i, this.getHeight()- 2*i); //this works on my fedora system, need to test on others --m
        }
        /* Will be adding visualization code here once we get the fourier transform working --m */
    }
}
