import java.awt.*;

public class ColoredComponent {

    private Component c;
    private String bgColor;
    private String fgColor;

    public Component getC() {
        return c;
    }

    public void setC(Component c) {
        this.c = c;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
        c.setBackground(Theme.getThemeColor(bgColor));
        c.invalidate();
        c.repaint();
    }

    public String getFgColor() {
        return fgColor;
    }

    public void setFgColor(String fgColor) {
        this.fgColor = fgColor;
        c.setForeground(Theme.getThemeColor(fgColor));
        c.invalidate();
        c.repaint();
    }

    public ColoredComponent(Component c, String fgColor, String bgColor){
        this.c = c;
        this.fgColor = fgColor;
        this.bgColor = bgColor;
        resetColors();
    }

    public void resetColors(){
        c.setForeground(Theme.getThemeColor(fgColor));
        c.setBackground(Theme.getThemeColor(bgColor));
        c.invalidate();
        c.repaint();
    }

    public void setVisible(boolean setTo){
        c.setVisible(setTo);
    }

}
