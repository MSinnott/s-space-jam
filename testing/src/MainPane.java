import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MainPane extends JPanel implements KeyListener {

    private float[] leftNotes;
    private float[] rightNotes;
    private float MinNote, MaxNote;
    private int lX, lY, nX, nY;
    private double zoom = 1;
    private int pan = 0;
    int windowHeight = 0;

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

        windowHeight = this.getHeight();

        Graphics2D g2 = (Graphics2D) g;
        g2.setBackground(AudioDesktop.theme[2]);
        g2.setColor(AudioDesktop.theme[2]);
        g2.fillRect(0 , 0 , this.getWidth(), this.getHeight());

        g2.setStroke(new BasicStroke(4));
        g2.setColor(AudioDesktop.theme[3]);
        if(zoom >= 1) {
            lX = 0;
            lY = getYfromVal((int) leftNotes[pan]);
            for (int i = pan - pan % 2; i < leftNotes.length; i += 2) {
                nX = lX + (int) zoom;
                if( nX > this.getWidth() ) break;
                nY = getYfromVal(leftNotes[i]);
                g2.drawLine(lX, lY, nX, nY);
                lY = nY;
                lX = nX;
            }
        } else {
            int sampsPerPixel = (int) (1.0/zoom);
            lX = 0;
            for (int i = pan - pan % 2; i < leftNotes.length; i += 2 * sampsPerPixel){
                lY = getYfromVal(findMin(leftNotes, i, i + 2*sampsPerPixel + 2));
                nY = getYfromVal(findMax(leftNotes, i, i + 2*sampsPerPixel + 2));
                g2.drawLine(lX, lY, lX, nY );
                if( lX > this.getWidth() ) break;
                lX += 1;
            }
        }

        g2.setColor(AudioDesktop.theme[4]);
        if(zoom >= 1) {
            lX = 0;
            lY = getYfromVal((int) rightNotes[pan]);
            for (int i = pan - pan % 2; i < rightNotes.length; i += 2) {
                nX = lX + (int) zoom;
                if( nX > this.getWidth() ) break;
                nY = getYfromVal(rightNotes[i]);
                g2.drawLine(lX, lY, nX, nY);
                lY = nY;
                lX = nX;
            }
        } else {
            int sampsPerPixel = (int) (1.0/zoom);
            lX = 0;
            for (int i = pan - pan % 2; i < rightNotes.length; i += 2 * sampsPerPixel){
                lY = getYfromVal(findMin(rightNotes, i, i + 2*sampsPerPixel + 2));
                nY = getYfromVal(findMax(rightNotes, i, i + 2*sampsPerPixel + 2));
                g2.drawLine(lX, lY, lX, nY );
                if( lX > this.getWidth() ) break;
                lX += 1;
            }
        }

        g2.setColor(AudioDesktop.theme[0]);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(0, getYfromVal(0), this.getWidth(), getYfromVal(0));
        g2.drawString("" + 0, 16, getYfromVal(0) + 16);

        int numSteps = 5;
        for(int i = 0; i < numSteps; i++){
            float nextVal = MinNote + (MaxNote - MinNote) / (numSteps) * i;
            int nextY = getYfromVal(nextVal);
            if(Math.abs(nextY - getYfromVal(0)) > 50) {
                g2.drawLine(0, (nextY), this.getWidth(), (nextY));
                g2.drawString("" + (nextVal), 16, (nextY) + 16);
            }
        }
    }

    private int getNumPixelsOnscreen(){
        return (int) (2 * this.getWidth() / zoom);
    }

    private int getYfromVal(float val){
        return (int) (windowHeight - (windowHeight * (val - MinNote)) / (MaxNote - MinNote ));
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
            if(arr[i] > max && arr[i] != Float.POSITIVE_INFINITY) max = arr[i];
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
            if(arr[i] < min && arr[i] != Float.NEGATIVE_INFINITY) min = arr[i];
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
        float lmin = findMin(leftNotes, 0, leftNotes.length);
        float lmax =  findMax(leftNotes, 0, leftNotes.length);
        if(MinNote > lmin) MinNote = lmin;
        if(MaxNote < lmax) MaxNote = lmax;
        float rmin = findMin(rightNotes, 0, rightNotes.length);
        float rmax = findMax(rightNotes, 0, rightNotes.length);
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