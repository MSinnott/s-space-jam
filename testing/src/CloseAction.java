import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CloseAction extends AbstractAction {

    private Window windowToClose;
    private AudioWindow frameToClose;
    private AudioDesktop audioDesktop;

    public CloseAction(Window w){
        windowToClose = w;
    }

    public CloseAction(AudioWindow f, AudioDesktop audioDesktop){
        frameToClose = f;
        this.audioDesktop = audioDesktop;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final JDialog closeDialog = new JDialog();
        closeDialog.setTitle("Close?");
        closeDialog.setLayout(new BorderLayout());

        final JButton closeButton = new JButton("Exit?");
        closeButton.setBackground(AudioDesktop.theme[0]);
        closeButton.setForeground(AudioDesktop.theme[5]);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeItem();
                closeDialog.dispose();
            }
        });

        final JButton cancelButton = new JButton("Cancel!");
        cancelButton.setBackground(AudioDesktop.theme[0]);
        cancelButton.setForeground(AudioDesktop.theme[5]);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeDialog.dispose();
            }
        });

        closeDialog.add(closeButton, BorderLayout.CENTER);
        closeDialog.add(cancelButton, BorderLayout.EAST);

        if(windowToClose != null){
            closeDialog.setLocation(windowToClose.getX() + 240, windowToClose.getY() + 240);
        }
        if(frameToClose != null){
            closeDialog.setLocation(frameToClose.getX() + 240, frameToClose.getY() + 240);
        }

        closeDialog.setSize(200, 64);
        closeDialog.setResizable(false);
        closeDialog.setVisible(true);
    }

    private void closeItem(){
        if(windowToClose != null) windowToClose.dispose();
        if(frameToClose != null) audioDesktop.removeWindow(frameToClose);
    }

}