import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MainPane extends JPanel implements KeyListener {

    private AudioFileManager audioFile;
    private float MinNote, MaxNote;
    private int lX, lY, nX, nY;
    private double zoom = 1;
    private int pan = 0;
    private int windowHeight = 0;
    private int windowWidth = 0;
    private float samplesPerPixel;

    public MainPane(){
        super();
        addKeyListener( this);
    }

    /* pretty graphical window for the music --m */
    @Override
    public void paintComponent(Graphics g){
        for(float[] channel: audioFile.getChannels()){
            if (channel == null || channel.length < 2) return;
        }

        if(pan < 0) pan = 0;

        setMaxMinNotes();

        windowHeight = this.getHeight();
        windowWidth = this.getWidth();

        Graphics2D g2 = (Graphics2D) g;
        g2.setBackground(AudioDesktop.theme[2]);
        g2.setColor(AudioDesktop.theme[2]);
        g2.fillRect(0 , 0 , this.getWidth(), this.getHeight());

        g2.setStroke(new BasicStroke(4));

        samplesPerPixel = (float) (1 / zoom);
        int colorNum = 3;
        for (float[] channel : audioFile.getChannels()) {
            g2.setColor(AudioDesktop.theme[colorNum++]);
            lX = 0;
            lY = getYfromVal((int) channel[pan]);
            int lastI = 0;
            for (int i = pan - pan % 2; i < channel.length; i += (samplesPerPixel < 1) ? 2 : 2 * samplesPerPixel) {
                nX = lX + ((zoom > 1) ? (int) zoom : 1);
                lY = getYfromVal(findMin(channel, lastI, i));
                nY = getYfromVal(findMax(channel, i, i + (int) ((samplesPerPixel < 1) ? 2 : 2 * samplesPerPixel)));
                g2.drawLine(lX, lY, nX, nY);
                lX = nX;
                lastI = i;
                if (lX > windowWidth || nX > windowWidth) break;
            }
        }
        /*
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
        */

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

    public void setAudioFile(AudioFileManager fileManager){
        audioFile = fileManager;
    }

    private void setMaxMinNotes(){
        MaxNote = Integer.MIN_VALUE;
        MinNote = Integer.MAX_VALUE;
        for (float[] channel : audioFile.getChannels()) {
            if (channel == null) return;
            float lmin = findMin(channel, 0, channel.length);
            float lmax = findMax(channel, 0, channel.length);
            if (MinNote > lmin) MinNote = lmin;
            if (MaxNote < lmax) MaxNote = lmax;
        }
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