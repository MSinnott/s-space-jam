import java.awt.*;

public class ColoredComponent {

    private Component c;
    private int bgColor;
    private int fgColor;

    public Component getC() {
        return c;
    }

    public void setC(Component c) {
        this.c = c;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
        c.setBackground(AudioDesktop.theme[bgColor]);
        c.invalidate();
        c.repaint();
    }

    public int getFgColor() {
        return fgColor;
    }

    public void setFgColor(int fgColor) {
        this.fgColor = fgColor;
        c.setForeground(AudioDesktop.theme[fgColor]);
        c.invalidate();
        c.repaint();
    }

    public ColoredComponent(Component c, int fgColor, int bgColor){
        this.c = c;
        this.fgColor = fgColor;
        this.bgColor = bgColor;
        resetColors();
    }

    public void resetColors(){
        c.setForeground(AudioDesktop.theme[fgColor]);
        c.setBackground(AudioDesktop.theme[bgColor]);
        c.invalidate();
        c.repaint();
    }

}
