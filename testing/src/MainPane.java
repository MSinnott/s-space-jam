import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MainPane extends JPanel implements KeyListener {

    private int[] notes;
    private int MinNote, MaxNote;
    private int lX, lY, nX, nY;
    private double zoom = .03125;
    private int pan = 0;
    int TopWindow = 0;

    public MainPane(){
        super();
        addKeyListener( this);
    }

    /* pretty graphical window for the music --m */
    @Override
    public void paintComponent(Graphics g){
        if( notes == null ) return;
        if( notes.length < 2 ) return;
        if(pan < 0) pan = 0;
        TopWindow = this.getHeight();
        Graphics2D g2 = (Graphics2D) g;
        g2.setBackground(AudioDesktop.accColor);
        g2.setColor(AudioDesktop.accColor);
        g2.fillRect(0 , 0 , this.getWidth(), this.getHeight());
        g2.setStroke(new BasicStroke(4));
        g2.setColor( Color.red);
        lY = getY(0);
        g2.drawLine(0, lY, this.getWidth(), lY);
        g2.setColor(AudioDesktop.lnColor);
        if(zoom >= 1) {
            lX = 0;
            lY = getY(notes[pan]);
            for (int i = (2*pan); i < notes.length; i += 4) {
                nX = lX + (int) zoom;
                if( nX > this.getWidth() ) break;
                nY = getY(notes[i]);
                g2.drawLine(lX, lY, nX, nY);
                lY = nY;
                lX = nX;
            }
        } else {
            int sampsPerPixel = (int) (1.0/zoom);
            lX = 0;
            for (int i = pan; i < notes.length; i += 2 * sampsPerPixel){
                lY = getY(findMin( notes, i, i + 2*sampsPerPixel + 1));
                nY = getY(findMax( notes, i, i + 2*sampsPerPixel + 1));
                g2.drawLine(lX, lY, lX, nY );
                if( lX > this.getWidth() ) break;
                lX += 1;
            }
        }

    }

    private int getY(int val){
        return TopWindow - (TopWindow * ( val - MinNote )) / (( MaxNote - MinNote ));
    }

    public int findMax(int[] arr, int stIndex, int endIndex){
        int max = Integer.MIN_VALUE;
        if(stIndex < 0){
            stIndex = 0;
        }
        if(stIndex > arr.length){
            return 0;
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

    public int findMin(int[] arr, int stIndex, int endIndex){
        int min = Integer.MAX_VALUE;
        if(stIndex < 0){
            stIndex = 0;
        }
        if(stIndex > arr.length){
            return 0;
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

    public void pan(int panDirection) {
        if(zoom < 1){
            pan += panDirection * Math.ceil(1.0/zoom);
        } else {
            pan += panDirection * zoom;
        }
    }

    public int getDataLength(){
        return notes.length;
    }

    public void setData(short[] notesIn) {
        notes = new int[notesIn.length];

        for(int i = 0; i < notesIn.length; i++){
            notes[i] = notesIn[i];
        }

        /*
        for(int i = 0; i < notesIn.length; i+= 1) {
            notes[i] = (int) (6000 * Math.sin(2 * Math.PI * i / 500));
        }*/
        MinNote = findMin(notes, 0, notes.length);
        MaxNote = findMax( notes, 0, notes.length);
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
            this.pan(1);
            this.invalidate();
            this.repaint();
        }
        if(e.getKeyCode() == 39){
            this.pan(-1);
            this.invalidate();
            this.repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void setPan(int pan) {
        this.pan = pan;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    public int getPan() {
        return pan;
    }

    public double getZoom() {
        return zoom;
    }

}
