import javax.swing.*;
import java.util.ArrayList;

public abstract class InternalWindow extends JInternalFrame{

    protected MainPane pane;
    protected AudioDesktop audioDesktop;
    protected InternalWindow window = this;
    protected ArrayList<ColoredComponent> selectionComponents = new ArrayList<ColoredComponent>();
    protected ArrayList<ColoredComponent> components = new ArrayList<ColoredComponent>();
    protected boolean saved = false;
    protected String savePath = null;

    protected SoundPlayer player;

    public InternalWindow(int width, int height, AudioDesktop aDesk){
        audioDesktop = aDesk;
        this.setSize(width, height);
        this.setResizable(true);
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        this.setVisible(true);

        buildMenus();
    }

    public void makeMenuItem(JMenu menu, JMenuItem j, String fgColr, String bgColr, AbstractAction action, ArrayList<ColoredComponent> collection){
        j.addActionListener(action);
        collection.add(new ColoredComponent(j, fgColr, bgColr));
        menu.add(j);
    }

    public void resetColors(){
        components.forEach(ColoredComponent::resetColors);
        selectionComponents.forEach(ColoredComponent::resetColors);

        this.setBackground(Theme.getThemeColor("accColor"));
        this.setForeground(Theme.getThemeColor("txtColor"));

        this.invalidate();
        this.repaint();
        if(pane != null) {
            pane.invalidate();
            pane.repaint();
        }
    }

    public void updatePane(){
        pane.invalidate();
        pane.repaint();
        this.invalidate();
        this.repaint();
    }

    public abstract AudioFileManager getFileManager();

    public abstract void buildMenus();

}
