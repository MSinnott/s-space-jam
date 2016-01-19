import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MainPane extends JPanel implements KeyListener {

    private short[] notes;
    private short MinNote, MaxNote;
    private int lX;
    private int lY;
    private int nX;
    private int nY;
    private double zoom = .03125;
    private int pan = 0;
    int TopWindow;

    public void pan(int amountToPan){
        pan += amountToPan;
    }

    public int getDataLength(){
        return notes.length;
    }

    public void setData(short[] notes) {
        this.notes = notes;
        MinNote = findMin(notes, 0, notes.length);
        MaxNote = findMax( notes, 0, notes.length);
        System.out.println("Got "+MinNote+" and "+MaxNote+" size "+notes.length);
    }

    public MainPane(){
        super();
        addKeyListener( this);
    }

    /* pretty graphical window for the music --m */
    @Override
    public void paintComponent(Graphics g){
        if( notes == null ) return;
        if( notes.length < 2 ) return;
        TopWindow = this.getHeight();
        Graphics2D g2 = (Graphics2D) g;
        g2.setBackground(AudioDesktop.accColor);
        g2.setColor(AudioDesktop.accColor);
        g2.fillRect(0 , 0 , this.getWidth(), this.getHeight());
        g2.setStroke(new BasicStroke(4));
        g2.setColor( Color.red);
        lY = TopWindow - (TopWindow * ( 0 - MinNote )) / (2* ( MaxNote - MinNote ) );
        g2.drawLine(0, lY, this.getWidth(), lY);
        g2.setColor(AudioDesktop.lnColor);
        int BottomWindow = MinNote;
        System.out.println("Printing data "+MinNote+" to "+MaxNote);
        if(zoom >= 1) {
            lX = 0;
            lY = TopWindow - (TopWindow * ( notes[pan] - MinNote )) / (2* ( MaxNote - MinNote ) );
            for (int i = pan +1; i < notes.length; i += 1) {
                nX = lX + (int) zoom;
                if( nX > this.getWidth() ) break;
                nY = getY(notes[i]);
                g2.drawLine(lX, lY, nX, nY);
                lY = nY;
                lX = nX;
            }
        } else {
            int SampsPerPixel = (int) (1.0/zoom);
            lX = 0;
            for (int i = pan; i < notes.length; i += SampsPerPixel){
                lY = getY(findMin( notes, pan +i*SampsPerPixel, pan +i*SampsPerPixel+SampsPerPixel));
                nY = getY(findMax( notes, pan +i*SampsPerPixel, pan +i*SampsPerPixel+SampsPerPixel));
                g2.drawLine(lX, lY, lX, nY );
                if( lX > this.getWidth() ) break;
                lX += 1;
            }
        }

    }

    private int getY(short val){
        return TopWindow - (TopWindow * ( val - MinNote )) / (2* ( MaxNote - MinNote ) );
    }

    public short findMax(short[] arr, int stIndex, int endIndex){
        short max = Short.MIN_VALUE;
        if(stIndex < 0){
            stIndex = 0;
        }
        if(endIndex < 0){
            return 0;
        }
        if(endIndex > arr.length){
            endIndex = arr.length;
        }
        for(int i = stIndex; i < endIndex; i++){
            if(arr[i] > max) max = arr[i];
        }
        return max;
    }

    public short findMin(short[] arr, int stIndex, int endIndex){
        short min = Short.MAX_VALUE;
        if(stIndex < 0){
            stIndex = 0;
        }
        if(endIndex < 0){
            return 0;
        }
        if(endIndex > arr.length){
            endIndex = arr.length;
        }
        for(int i = stIndex; i < endIndex; i++){
            if(arr[i] < min) min = arr[i];
        }
        return min;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if(e.getKeyChar() == 'Z'){
            zoom /= 2;
            this.invalidate();
            this.repaint();
        }
        if(e.getKeyChar() == 'z'){
            zoom *= 2;
            this.invalidate();
            this.repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == 37){
            this.pan(this.getWidth() / 2);
            this.invalidate();
            this.repaint();
        }
        if(e.getKeyCode() == 39){
            this.pan(-1*this.getWidth() / 2);
            this.invalidate();
            this.repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
