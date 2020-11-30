package osm.jp.gpx.matchtime.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Comparator;

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
        this.paramDir.argField.addActionListener(new BaseTimeImgUpdateAction());
        this.paramDir.addPropertyChangeListener(new SourceFolderChangeListener());
    }
    
    /**
     * Action : Update 'arg2_baseTimeImg'
     * 
     */
    class SourceFolderChangeListener implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent arg0) {
			if (paramDir.isEnable()) {
				try {
					File dir = paramDir.getDirectory();
					File[] files = dir.listFiles(new ImageFileFilter());
					if (files != null) {
						Arrays.sort(files, new Comparator<File>() {
							public int compare(File file1, File file2){
							    return file1.getName().compareTo(file2.getName());
							}
					    });
						if (files.length > 0) {
				            argField.setText(files[0].getName());
				            fc = new JFileChooser(dir);
				            fc.setSelectedFile(files[0]);
				            return;
						}
					}
				} catch (FileNotFoundException e) {}
			}
            fc = new JFileChooser();
            fc.setSelectedFile(null);
		}    	
    }
    
    /**
     * Action : Update 'arg2_baseTimeImg'
     * 
     */
    class BaseTimeImgUpdateAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (paramDir.isEnable()) {
				try {
					File dir = paramDir.getDirectory();
					File[] files = dir.listFiles(new ImageFileFilter());
					if (files != null) {
						Arrays.sort(files, new Comparator<File>() {
							public int compare(File file1, File file2){
							    return file1.getName().compareTo(file2.getName());
							}
					    });
						if (files.length > 0) {
				            argField.setText(files[0].getName());
				            fc = new JFileChooser(dir);
				            fc.setSelectedFile(files[0]);
				            return;
						}
					}
				} catch (FileNotFoundException e) {}
			}
            fc = new JFileChooser();
            fc.setSelectedFile(null);
		}    	
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
        else {
            fc.setSelectedFile(null);
        }
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

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub
		
	}
}