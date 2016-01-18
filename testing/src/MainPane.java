import javax.swing.*;
import java.awt.*;

public class MainPane extends JPanel {

    public void setNotes(short[] notes) {
        this.notes = notes;
    }

    short[] notes;
    int lX;
    int lY;
    int nX;
    int nY;

    public MainPane(){
        super();
    }

    /* pretty graphical window for the music --m */
    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        /*for(int i = 0; i < 30; i+=5){
            g2.setColor(new Color(254, 5*i + 75, 3));
            g2.fillRect(i , i , this.getWidth() - 2*i, this.getHeight()- 2*i); //this works on my fedora system, need to test on others --m
        }*/
        g2.setColor(AudioDesktop.accColor);
        g2.setStroke(new BasicStroke(4));
        g2.setColor(new Color(29, 46, 255));
        lX = 0;
        lY = this.getHeight()/2;
        nX = 32;
        nY = this.getHeight()/2;
        for(int i = 0; i < 1024* 256; i+=256){
            if(notes[i]!=0) {
                g2.drawLine(lX, lY, nX, nY);
                lX = nX;
                nX += 4;
            }
            lY = nY;
            nY = notes[i] / 4 + this.getHeight() / 2;
        }

    }
}
