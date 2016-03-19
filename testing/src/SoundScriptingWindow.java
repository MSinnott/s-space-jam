import javax.swing.*;
import java.util.ArrayList;

public class SoundScriptingWindow extends JInternalFrame {

    private ArrayList<ColoredComponent> components = new ArrayList<ColoredComponent>();
    private JTextPane textPane = new JTextPane();

    public SoundScriptingWindow(){
        setTitle("Scripting Window");
        components.add(new ColoredComponent(this, "bgColor", "fgColor"));
        JScrollPane scrollPane = new JScrollPane(textPane);
        components.add(new ColoredComponent(textPane, "llnColor", "accColor"));
        components.add(new ColoredComponent(scrollPane, "llnColor", "accColor"));
        add(scrollPane);

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        this.setSize(480, 360);
        this.setVisible(true);
    }

    public void resetColors(){
        components.forEach(ColoredComponent::resetColors);

        this.invalidate();
        this.repaint();
        textPane.invalidate();
        textPane.repaint();
    }
}
