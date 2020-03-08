package osm.jp.gpx.matchtime.gui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.JButton;
import javax.swing.JFileChooser;

@SuppressWarnings("serial")
public class ParameterPanelImageFile extends ParameterPanel {
    JFileChooser fc;
    public JButton openButton;
    public ParameterPanelFolder paramDir;

    public ParameterPanelImageFile(
            String label, String text, 
            ParameterPanelFolder paramDir
    ) {
        super(label, text);

        // "選択..."
        SelectButtonAction buttonAction = new SelectButtonAction();
        openButton = new JButton(i18n.getString("button.select"));
        openButton.addActionListener(buttonAction);
        this.add(openButton);
        
        //Create a file chooser
        this.paramDir = paramDir;
    }
    
    class SelectButtonAction implements java.awt.event.ActionListener
    {
        public void actionPerformed(ActionEvent e) {
            selectImage_Action(e);
        }
    }

    public void selectImage_Action(ActionEvent ev) {
        File sdir = new File(paramDir.getText());
        System.out.println(sdir.toPath());
        if (sdir.isDirectory()) {
            fc = new JFileChooser(sdir);
        }
        else {
            fc = new JFileChooser();
        }

        fc.addChoosableFileFilter(new ImageFilter());
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileView(new ImageFileView());
        fc.setAccessory(new ImagePreview(fc));

        //Show it.　"選択"
        int returnVal = fc.showDialog(ParameterPanelImageFile.this, i18n.getString("dialog.select"));
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            this.argField.setText(file.getName());
        }
        fc.setSelectedFile(null);
    }
    
    public File getImageFile() {
        if (this.paramDir.isEnable()) {
            String text = this.argField.getText();
            if (text != null) {
                try {
                    File dir = this.paramDir.getDirectory();
                    File file = new File(dir, text);
                    if (file.exists() && file.isFile()) {
                        return file;
                    }
                }
                catch (FileNotFoundException e) {
                    return null;
                }
            }
        }
        return null;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public boolean isEnable() {
        if (this.paramDir.isEnable()) {
            String text = this.argField.getText();
            if (text != null) {
                try {
                    File dir = this.paramDir.getDirectory();
                    File file = new File(dir, text);
                    if (file.exists() && file.isFile()) {
                        String name = file.getName().toUpperCase();
                        if (name.endsWith(".JPG") || name.endsWith(".JPEG")) {
                            return true;
                        }
                    }
                }
                catch (FileNotFoundException e) {
                    return false;
                }
            }
        }
        return false;
    }
}