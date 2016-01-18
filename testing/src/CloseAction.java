import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CloseAction extends AbstractAction {

    private Window windowToClose;
    private JInternalFrame frameToClose;

    public CloseAction(Window w){
        windowToClose = w;
    }

    public CloseAction(JInternalFrame f){
        frameToClose = f;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final JDialog closeDialog = new JDialog();
        closeDialog.setTitle("Close?");
        closeDialog.setLayout(new BorderLayout());
        final JButton closeButton = new JButton("Exit?");
        closeButton.setBackground(AudioDesktop.bgColor);
        closeButton.setForeground(AudioDesktop.txtColor);
        final JButton cancelButton = new JButton("Cancel!");
        cancelButton.setBackground(AudioDesktop.bgColor);
        cancelButton.setForeground(AudioDesktop.txtColor);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(windowToClose != null) windowToClose.dispose();
                if(frameToClose != null) frameToClose.dispose();
                closeDialog.dispose();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeDialog.dispose();
            }
        });
        closeDialog.add(closeButton, BorderLayout.CENTER);
        closeDialog.add(cancelButton, BorderLayout.EAST);
        closeDialog.setVisible(true);
        if(windowToClose != null){
            closeDialog.setLocation(windowToClose.getX() + 240, windowToClose.getY() + 240);
        }
        if(frameToClose != null){
            closeDialog.setLocation(frameToClose.getX() + 240, frameToClose.getY() + 240);
        }
        closeDialog.setSize(200, 64);
        closeDialog.setResizable(false);
    }
}