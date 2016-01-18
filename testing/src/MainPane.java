import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MainPane extends JPanel implements KeyListener {

    private short[] notes;
    private int lX;
    private int lY;
    private int nX;
    private int nY;
    private double zoom = 1;

    public void setData(short[] notes) {
        this.notes = notes;
    }

    public MainPane(){
        super();
    }

    /* pretty graphical window for the music --m */
    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        g2.setBackground(AudioDesktop.bgColor);
        g2.setStroke(new BasicStroke(4));
        g2.setColor(AudioDesktop.lnColor);
        if(zoom <= 1) {
            lX = 0;
            lY = this.getHeight() / 2;
            nX = 32;
            nY = this.getHeight() / 2;
            for (int i = 0; i < notes.length; i += zoom) {
                if (notes[i] != 0) {
                    g2.drawLine(lX, lY, nX, nY);
                    lX = nX;
                    nX += zoom;
                }
                lY = nY;
                nY = notes[i] / 4 + this.getHeight() / 2;
            }
        } else {
            lX = 0;
            lY = this.getHeight() / 2;
            nX = 32;
            nY = this.getHeight() / 2;
            g2.drawLine(lX, lY, nX, nY);
            for (int i = 0; i < notes.length - zoom; i += zoom){
                g2.drawLine(nX, findMin(notes, i, (short) (i + zoom)), nX, findMax(notes, i, (short) (i + zoom)));
            }
        }

    }

    public short findMax(short[] arr, int stIndex, int endIndex){
        short max = Short.MIN_VALUE;
        for(int i = stIndex; i < endIndex; i++){
            if(arr[i] > max) max = arr[i];
        }
        return max;
    }

    public short findMin(short[] arr, int stIndex, int endIndex){
        short min = Short.MAX_VALUE;
        for(int i = stIndex; i < endIndex; i++){
            if(arr[i] < min) min = arr[i];
        }
        return min;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        System.out.println("typed");
        if(e.getKeyChar() == 'Z'){
            zoom /= 2;
            System.out.println("Z!");
        }
        if(e.getKeyChar() == 'z'){
            zoom *= 2;
            System.out.println("z");
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
