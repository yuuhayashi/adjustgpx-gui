package osm.jp.gpx.matchtime.gui;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

@SuppressWarnings("serial")
public class ParameterPanelImageFile extends ParameterPanel implements PropertyChangeListener {
    JFileChooser fc;
    public JButton openButton;
    public ParameterPanelSourceFolder paramDir;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public ParameterPanelImageFile(
            String name, String label, String text, 
            ParameterPanelSourceFolder paramDir
    ) {
        super(name, label, text);

        // "選択..."
        SelectButtonAction buttonAction = new SelectButtonAction();
        openButton = new JButton(i18n.getString("button.select"));
        openButton.addActionListener(buttonAction);
        this.add(openButton);
        
        //Create a file chooser
        this.paramDir = paramDir;
        this.paramDir.addPropertyChangeListener(new SourceFolderChangeListener());
        
        // 'argField' ’が変更されたら、「update イベントを発火させる
        this.argField.getDocument().addDocumentListener(
            new SimpleDocumentListener() {
                @Override
                public void update(DocumentEvent e) {
                	pcs.firePropertyChange(getName(), "", argField.getText());
                }
            }
        );
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
			argField.setText("");
            fc = new JFileChooser();
            fc.setSelectedFile(null);
		}    	
    }
    
    class SelectButtonAction implements java.awt.event.ActionListener
    {
        public void actionPerformed(ActionEvent e) {
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
                argField.setText(file.getName());
            }
            else {
                fc.setSelectedFile(null);
            }
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
		this.pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
		// TODO Nothing to do.
		
		Object eventTriggerObject = evt.getSource();
        String propertyName = evt.getPropertyName();
        if (JTextField.class.isInstance(eventTriggerObject)) {
            if (propertyName.equals(getName())) {
                String newValue = (String) evt.getNewValue();
                System.out.println("["+ propertyName +"] propertyChanged() newValue:" + newValue);
            }
        }
	}
}