import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 *  Builds a super slick dialog -- super easy to use!
 */

public class AdaptiveDialog extends JDialog {

    private ArrayList<ArrayList<ColoredComponent>> components = new ArrayList<ArrayList<ColoredComponent>>();
    private int numArrays = 0;
    private JButton doneButton;
    private JButton cancelButton;

    public AdaptiveDialog(String name){
        super();
        doneButton = new JButton("Done!");
        cancelButton = new JButton("Cancel!");
        setTitle(name);
        this.setBackground(AudioDesktop.theme[2]);
    }

    public void addItem(Component c, int bg, int fg, boolean inLine){
        addItem(new ColoredComponent(c, bg, fg), inLine);
    }

    public void addItem(ColoredComponent c, boolean inLine){
        if(inLine && components.size() > 0){
            components.get(components.size() - 1).add(c);
            return;
        }
        ArrayList<ColoredComponent> nextLine = new ArrayList<ColoredComponent>();
        nextLine.add(c);
        components.add(nextLine);
        numArrays++;
    }

    public void buildDialog(int width, int height, Component parent){
        buildDialog(parent);
        setSize(width, height);
    }

    public void buildDialog(Component parent){
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new GridLayout(numArrays, 1, 0, 1));
        setLayout(new BorderLayout());
        add(contentPane, BorderLayout.CENTER);
        for(ArrayList<ColoredComponent> comp: components){
            JPanel nextPane = new JPanel();
            contentPane.add(nextPane);
            nextPane.setBackground(AudioDesktop.theme[2]);
            nextPane.setLayout(new GridLayout(1, comp.size(), 1, 0));
            for(ColoredComponent c: comp){
                nextPane.add(c.getC());
            }
        }

        JPanel nextPane = new JPanel();
        add(nextPane, BorderLayout.SOUTH);
        nextPane.setBackground(AudioDesktop.theme[2]);
        nextPane.setLayout(new GridLayout(1, 2, 1, 0));

        doneButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        cancelButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        doneButton.setForeground(AudioDesktop.theme[5]);
        doneButton.setBackground(AudioDesktop.theme[0]);
        cancelButton.setForeground(AudioDesktop.theme[5]);
        cancelButton.setBackground(AudioDesktop.theme[0]);

        nextPane.add(doneButton);
        nextPane.add(cancelButton);

        pack();
        setLocation(parent.getX() + 64, parent.getY() + 64);
        setVisible(true);
    }

    public void resetColors(){
        for (ArrayList<ColoredComponent> comps : components){
            comps.forEach(ColoredComponent::resetColors);
        }
        doneButton.setBackground(AudioDesktop.theme[0]);
        doneButton.setForeground(AudioDesktop.theme[5]);
        cancelButton.setBackground(AudioDesktop.theme[0]);
        cancelButton.setForeground(AudioDesktop.theme[5]);
        invalidate();
        repaint();
    }

    public void addDoneBinding(AbstractAction action){
        doneButton.addActionListener(action);
    }

}