import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 *  Builds dialogs -- packages dialog creation into a quick, easy process
 *  to save lines and time in main project
 */

public class AdaptiveDialog extends JDialog {

    private ArrayList<ArrayList<ColoredComponent>> components = new ArrayList<ArrayList<ColoredComponent>>();
    private int numArrays = 0;
    private JButton doneButton;
    private JButton cancelButton;

    /**
     * Constructor
     * @param name name of te dialog, displayed int title bar
     */
    public AdaptiveDialog(String name){
        super();
        doneButton = new JButton("Done!");
        cancelButton = new JButton("Cancel!");
        setTitle(name);
        this.setBackground(Theme.getThemeColor("bgColor"));
    }

    /**
     * Adds a component to this dialog (call only before dialog built)
     * @param c component to add
     * @param bg background color in theme
     * @param fg foreground color in theme
     * @param inLine whether or not to create the component in the current line
     */
    public void addItem(Component c, String bg, String fg, boolean inLine){
        addItem(new ColoredComponent(c, bg, fg), inLine);
    }

    /**
     * Adds a colored component to this dialog (call only before dialog built)
     * @param c colored component to add
     * @param inLine whether or not to create the component in the current line
     */
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

    /**
     * Builds the dialog into a dialog visible onscreen
     * @param width width of finished dialog
     * @param height height of finished dialog
     * @param parent parent component for this dialog
     */
    public void buildDialog(int width, int height, Component parent){
        buildDialog(parent);
        setSize(width, height);
    }

    /**
     * Builds the dialog into a dialog visible onscreen
     * @param parent parent component for this dialog
     */
    public void buildDialog(Component parent){
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new GridLayout(numArrays, 1, 0, 1));
        setLayout(new BorderLayout());
        add(contentPane, BorderLayout.CENTER);
        for(ArrayList<ColoredComponent> comp: components){
            JPanel nextPane = new JPanel();
            contentPane.add(nextPane);
            nextPane.setBackground(Theme.getThemeColor("bgColor"));
            nextPane.setLayout(new GridLayout(1, comp.size(), 1, 0));
            for(ColoredComponent c: comp){
                nextPane.add(c.getC());
            }
        }

        JPanel nextPane = new JPanel();
        add(nextPane, BorderLayout.SOUTH);
        nextPane.setBackground(Theme.getThemeColor("bgColor"));
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

        nextPane.add(doneButton);
        nextPane.add(cancelButton);

        pack();
        setLocation(parent.getX() + 64, parent.getY() + 64);
        setVisible(true);

        resetColors();
    }

    /**
     * Resets this dialogs colors, along with all its children
     */
    public void resetColors(){
        for (ArrayList<ColoredComponent> comps : components){
            comps.forEach(ColoredComponent::resetColors);
        }
        doneButton.setForeground(Theme.getThemeColor("txtColor"));
        doneButton.setBackground(Theme.getThemeColor("bgColor"));
        cancelButton.setForeground(Theme.getThemeColor("txtColor"));
        cancelButton.setBackground(Theme.getThemeColor("bgColor"));
        invalidate();
        repaint();
    }

    /**
     * Adds an action to the done button for this dialog
     * @param action action to add to the done button
     */
    public void addDoneBinding(AbstractAction action){
        doneButton.addActionListener(action);
    }

}