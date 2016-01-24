import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Arc2D;

public class MainPane extends JPanel implements KeyListener {

    private float[] leftNotes;
    private float[] rightNotes;
    private int MinNote, MaxNote;
    private int lX, lY, nX, nY;
    private double zoom = 1;
    private int pan = 0;
    int TopWindow = 0;

    public MainPane(){
        super();
        addKeyListener( this);
    }

    /* pretty graphical window for the music --m */
    @Override
    public void paintComponent(Graphics g){
        if( leftNotes == null || rightNotes == null) return;
        if( leftNotes.length < 2 || rightNotes.length < 2) return;
        if(pan < 0) pan = 0;
        if(pan + getNumPixelsOnscreen() > leftNotes.length) pan = leftNotes.length - getNumPixelsOnscreen();
        if(getNumPixelsOnscreen() > leftNotes.length) pan = 0;

        setMaxMinNotes();

        TopWindow = this.getHeight();

        Graphics2D g2 = (Graphics2D) g;
        g2.setBackground(AudioDesktop.accColor);
        g2.setColor(AudioDesktop.accColor);
        g2.fillRect(0 , 0 , this.getWidth(), this.getHeight());

        g2.setStroke(new BasicStroke(4));
        g2.setColor(AudioDesktop.llnColor);
        if(zoom >= 1) {
            lX = 0;
            lY = getY((int) leftNotes[pan]);
            for (int i = pan - pan % 2; i < leftNotes.length; i += 2) {
                nX = lX + (int) zoom;
                if( nX > this.getWidth() ) break;
                nY = getY(leftNotes[i]);
                g2.drawLine(lX, lY, nX, nY);
                lY = nY;
                lX = nX;
            }
        } else {
            int sampsPerPixel = (int) (1.0/zoom);
            lX = 0;
            for (int i = pan - pan % 2; i < leftNotes.length; i += 2 * sampsPerPixel){
                lY = getY(findMin(leftNotes, i, i + 2*sampsPerPixel + 2));
                nY = getY(findMax(leftNotes, i, i + 2*sampsPerPixel + 2));
                g2.drawLine(lX, lY, lX, nY );
                if( lX > this.getWidth() ) break;
                lX += 1;
            }
        }

        if( rightNotes == null ) return;
        if( rightNotes.length < 2 ) return;
        g2.setColor(AudioDesktop.rlnColor);
        if(zoom >= 1) {
            lX = 0;
            lY = getY((int) rightNotes[pan]);
            for (int i = pan - pan % 2; i < rightNotes.length; i += 2) {
                nX = lX + (int) zoom;
                if( nX > this.getWidth() ) break;
                nY = getY(rightNotes[i]);
                g2.drawLine(lX, lY, nX, nY);
                lY = nY;
                lX = nX;
            }
        } else {
            int sampsPerPixel = (int) (1.0/zoom);
            lX = 0;
            for (int i = pan - pan % 2; i < rightNotes.length; i += 2 * sampsPerPixel){
                lY = getY(findMin(rightNotes, i, i + 2*sampsPerPixel + 2));
                nY = getY(findMax(rightNotes, i, i + 2*sampsPerPixel + 2));
                g2.drawLine(lX, lY, lX, nY );
                if( lX > this.getWidth() ) break;
                lX += 1;
            }
        }

        g2.setColor(AudioDesktop.bgColor);
        g2.setStroke(new BasicStroke(2));
        lY = getY(0);
        g2.drawLine(0, lY, this.getWidth(), lY);
        g2.drawLine(0, 3*TopWindow/4, this.getWidth(), 3*TopWindow/4);
        g2.drawLine(0, TopWindow/4, this.getWidth(), TopWindow/4);
        g2.drawString("" + (MaxNote - MinNote) / 4, 16, 3*TopWindow/4 + 16);
        g2.drawString("" + (MinNote - MaxNote) / 4, 16, TopWindow/4 + 16);
        g2.drawString("" + 0, 16, TopWindow / 2 + 16);
    }

    private int getNumPixelsOnscreen(){
        return (int) (2 * this.getWidth() / zoom);
    }

    private int getY(float val){
        if(val == 0) {
            return TopWindow / 2;
        }
        return (int) (TopWindow - (TopWindow * (val - MinNote)) / (( MaxNote - MinNote )));
    }

    public float findMax(float[] arr, int stIndex, int endIndex){
        float max = -1 * Float.MAX_VALUE;
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
        for(int i = stIndex; i < endIndex; i+=2){
            if(arr[i] > max) max = arr[i];
        }
        return max;
    }

    public float findMin(float[] arr, int stIndex, int endIndex){
        float min = Float.MAX_VALUE;
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
        for(int i = stIndex; i < endIndex; i+=2){
            if(arr[i] < min) min = arr[i];
        }
        return min;
    }

    public void pan(int panDirection) {
        if(zoom < 1){
            pan += panDirection * Math.ceil(10.0/zoom);
        } else {
            pan += panDirection * 5 * zoom;
        }
    }

    public void setLeftData(float[] notesIn) {
        leftNotes = new float[notesIn.length];
        for(int i = 0; i < notesIn.length; i++){
            leftNotes[i] = notesIn[i];
        }
        setMaxMinNotes();
    }

    private void setMaxMinNotes(){
        MaxNote = Integer.MIN_VALUE;
        MinNote = Integer.MAX_VALUE;
        if(leftNotes == null || rightNotes == null) return;
        int lmin = (int) findMin(leftNotes, 0, leftNotes.length);
        int lmax = (int) findMax(leftNotes, 0, leftNotes.length);
        if(MinNote > lmin) MinNote = lmin;
        if(MaxNote < lmax) MaxNote = lmax;
        int rmin = (int) findMin(rightNotes, 0, rightNotes.length);
        int rmax = (int) findMax(rightNotes, 0, rightNotes.length);
        if(MinNote > rmin) MinNote = rmin;
        if(MaxNote < rmax) MaxNote = rmax;
    }

    public void setRightData(float[] notesIn) {
        rightNotes = new float[notesIn.length];
        for(int i = 0; i < notesIn.length; i++){
            rightNotes[i] = notesIn[i];
        }
        setMaxMinNotes();
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
