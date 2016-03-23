import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class SoundScriptingWindow extends JInternalFrame {

    private SoundScriptingWindow scriptingWindow = this;
    private ArrayList<ColoredComponent> components = new ArrayList<ColoredComponent>();
    private JTextPane textPane = new JTextPane();
    private AudioDesktop audioDesktop;

    public SoundScriptingWindow(AudioDesktop aDesk){
        audioDesktop = aDesk;

        setTitle("Scripting Window");
        components.add(new ColoredComponent(this, "bgColor", "fgColor"));
        JScrollPane scrollPane = new JScrollPane(textPane);
        components.add(new ColoredComponent(textPane, "llnColor", "accColor"));
        components.add(new ColoredComponent(scrollPane, "llnColor", "accColor"));
        add(scrollPane);

        buildMenus();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        this.setSize(480, 360);
        this.setVisible(true);
    }

    public void buildMenus(){
        JMenuBar menuBar = new JMenuBar();
        this.add(menuBar, BorderLayout.NORTH);
        components.add(new ColoredComponent(menuBar, "txtColor", "bgColor"));

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        components.add(new ColoredComponent(fileMenu,"txtColor", "bgColor"));

        makeMenuItem(fileMenu, new JMenuItem("Save"), "txtColor", "bgColor", new SaveAction("Save"), components);

        makeMenuItem(fileMenu, new JMenuItem("Save As ..."), "txtColor", "bgColor", new SaveAction("SaveAs"), components);

        makeMenuItem(fileMenu, new JMenuItem("Exit"), "txtColor", "bgColor", new ExitAction(), components);
    }

    public void makeMenuItem(JMenu menu, JMenuItem j, String fgColr, String bgColr, AbstractAction action, ArrayList<ColoredComponent> collection){
        j.addActionListener(action);
        collection.add(new ColoredComponent(j, fgColr, bgColr));
        menu.add(j);
    }


    public void resetColors(){
        components.forEach(ColoredComponent::resetColors);

        this.invalidate();
        this.repaint();
        textPane.invalidate();
        textPane.repaint();
    }

    public class SaveAction extends AbstractAction{

        private String type;
        private String savePath;
        private File saveFile;
        private boolean saved;

        public SaveAction(String type){
            this.type = type;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(!saved || "SaveAs".equals(type)){
                File root;
                try {
                    root = new File(AudioDesktop.LinuxPathHead);
                } catch (Exception ex) {
                    root = new File(AudioDesktop.WindowsPathHead);
                }
                JFileChooser fileChooser = new JFileChooser(root);
                if (fileChooser.showSaveDialog(scriptingWindow) == JFileChooser.APPROVE_OPTION) {
                    try {
                        saveFile = fileChooser.getSelectedFile();
                        savePath = fileChooser.getSelectedFile().getPath();
                        BufferedOutputStream writeStream = new BufferedOutputStream(new FileOutputStream(saveFile));
                        writeStream.write(textPane.getText().getBytes());
                        scriptingWindow.setTitle(saveFile.getName());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            } else {
                try {
                    saveFile = new File(savePath);
                    BufferedOutputStream writeStream = new BufferedOutputStream(new FileOutputStream(saveFile));
                    writeStream.write(textPane.getText().getBytes());
                    scriptingWindow.setTitle(saveFile.getName());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            saved = true;
        }
    }

    public class ExitAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            AdaptiveDialog exitDialog = new AdaptiveDialog("Exit?");
            exitDialog.addDoneBinding(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    audioDesktop.removeWindow(scriptingWindow);
                }
            });
            exitDialog.buildDialog(scriptingWindow);
        }
    }
}
