import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;

public class MainPane extends JPanel implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    private Component parent;
    private AudioFileManager audioFile;
    private float MinNote, MaxNote;
    private int lX, lY, nX, nY;
    private double zoom = 1;
    private int pan = 0;
    private int windowHeight = 0;
    private int windowWidth = 0;
    private float samplesPerPixel;

    private int playLoc = 0;

    public MainPane(Component parent){
        super();
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        this.parent = parent;
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

        g2.setColor(AudioDesktop.theme[2].darker());
        g2.fillRect((int) getXfromIndex(selection[0]), 0, (int)(getXfromIndex(selection[1]) - getXfromIndex(selection[0])), windowHeight);

        g2.setStroke(new BasicStroke(4));

        samplesPerPixel = (float) (1 / zoom);
        int colorNum = 3;
        for (float[] channel : audioFile.getChannels()) {
            if(pan > channel.length) break;
            g2.setColor(AudioDesktop.theme[colorNum++]);
            lX = 0;
            lY = getYfromVal(channel[pan - pan % 2]);
            int lastI = pan - pan % 2;
            for (int i = pan - pan % 2; i < channel.length; i += (samplesPerPixel <= 1) ? 2 : 2 * samplesPerPixel) {
                nX = lX + ((zoom > 1) ? (int) zoom : 1);
                if (samplesPerPixel > 1) lY = getYfromVal(findMin(channel, lastI, i));
                nY = getYfromVal(findMax(channel, lastI, i));
                g2.drawLine(lX, lY, nX, nY);
                lastI = i;
                lX = nX;
                lY = nY;
                if (lX > windowWidth || nX > windowWidth) break;
            }
        }

        g2.setColor(AudioDesktop.theme[0]);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(0, getYfromVal(0), this.getWidth(), getYfromVal(0));
        g2.drawString("" + 0, 16, getYfromVal(0) + 16);

        int numSteps = 8;
        for(int i = 0; i < numSteps; i++){
            float nextVal = MinNote + (MaxNote - MinNote) / (numSteps) * i;
            int nextY = getYfromVal(nextVal);
            if(Math.abs(nextY - getYfromVal(0)) > 50) {
                g2.drawLine(0, (nextY), this.getWidth(), (nextY));
                g2.drawString("" + (nextVal), 16, (nextY) + 16);
            }
        }

        float nextI = pan, lastI = pan;
        for (int i = 0; i < numSteps; i++) {
            nextI += getScreenIndexRange() / numSteps;
            if(Math.abs(getXfromIndex(nextI) - getXfromIndex(lastI)) > 50) {
                g2.drawLine((int) getXfromIndex(nextI), getYfromVal(0) + 8, (int) getXfromIndex(nextI), getYfromVal(0) - 8);
                g2.drawString("" + HumanReadable.neatenFloat(nextI), (int) getXfromIndex(nextI), getYfromVal(0) + 16);
                lastI = nextI;
            }
        }

        g2.setColor(AudioDesktop.theme[0]);
        g2.drawLine((int) getXfromIndex(playLoc), 0 , (int) getXfromIndex(playLoc), windowHeight);

        Font oldFont = g2.getFont();
        g2.setFont(oldFont.deriveFont(windowWidth / 50f));
        if(g2.getFont().getSize() > 24) g2.setFont(oldFont.deriveFont(20f));

        g2.setColor(AudioDesktop.theme[0]);
        g2.fillRect(0, windowHeight - 20, windowWidth, 20);
        g2.setColor(AudioDesktop.theme[5]);
        g2.drawString("Zoom: " + HumanReadable.neatenFloat((float) zoom) + " Selection: " + HumanReadable.neatenFloat(selection[0]) + " to " + HumanReadable.neatenFloat(selection[1]) + " Playing @: " + playLoc + " - (" +getIndexFromX(mouseLoc.x) + "," + getValfromY(mouseLoc.y)+ ")", 0, windowHeight);


    }

    private int getYfromVal(float val){
        return (int) (windowHeight - (windowHeight * (val - MinNote)) / (MaxNote - MinNote ));
    }

    private float getValfromY(int y){
        return (y + windowHeight) * (MaxNote - MinNote) / windowHeight + MinNote;
    }

    private float getXfromIndex(float index){
        return (float) (zoom * (index - pan) / 2);
    }

    private float getIndexFromX(float x){
        return (float) (2 * x / zoom + pan);
    }

    private float getScreenIndexRange(){
        return (getIndexFromX(windowWidth) - getIndexFromX(0));
    }

    public void rescale(int startIndex, int endIndex){
        pan = startIndex;
        zoom = 2f * getWidth() / (endIndex - startIndex);
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
        for(int i = stIndex - stIndex % 2; i < endIndex; i+=2){
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
        for(int i = stIndex - stIndex % 2; i < endIndex; i+=2){
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

    public void updateLoc(int loc){
        playLoc = loc;
        invalidate();
        repaint();
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

    private Point mouseClick = new Point();
    private float[] selection = new float[] {0, 0};
    private boolean[] mouseButtonState = new boolean[4]; //Allows access by MouseEvent value

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseButtonState[e.getButton()] = true;
        if(e.getButton() == MouseEvent.BUTTON1){
            mouseClick = e.getPoint();
        } else if(e.getButton() == MouseEvent.BUTTON3){
            selection[0] = 0;
            selection[1] = 0;
        }
        invalidate();
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(mouseButtonState[MouseEvent.BUTTON1] &&
                !(mouseButtonState[MouseEvent.BUTTON2] || mouseButtonState[MouseEvent.BUTTON3])) {
            if (mouseClick.x < e.getPoint().x) {
                selection[0] = getIndexFromX(mouseClick.x);
                selection[1] = getIndexFromX(e.getPoint().x);
            } else {
                selection[0] = getIndexFromX(e.getPoint().x);
                selection[1] = getIndexFromX(mouseClick.x);
            }
            mouseClick = new Point();
        } else {
            selection[0] = 0;
            selection[1] = 0;
        }
        mouseButtonState[e.getButton()] = false;
        invalidate();
        repaint();
        parent.invalidate();
        parent.repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public float[] getSelection(){
        return selection;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(mouseButtonState[MouseEvent.BUTTON1] &&
                !(mouseButtonState[MouseEvent.BUTTON2] || mouseButtonState[MouseEvent.BUTTON3])) {
            if (mouseClick.x < e.getPoint().x) {
                selection[0] = getIndexFromX(mouseClick.x);
                selection[1] = getIndexFromX(e.getPoint().x);
            } else {
                selection[0] = getIndexFromX(e.getPoint().x);
                selection[1] = getIndexFromX(mouseClick.x);
            }
            invalidate();
            repaint();
            parent.invalidate();
            parent.repaint();
        }
    }

    private Point mouseLoc = new Point();
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseLoc = e.getPoint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double rot = e.getPreciseWheelRotation();
        if(!e.isShiftDown()) {
            pan += 32 * rot / zoom;
        } else {
            if (rot > 0) {
                zoom /= (rot * 2);
            } else {
                zoom *= -(rot * 2);
            }
        }
        invalidate();
        repaint();
    }
}