package osm.jp.gpx.matchtime.gui.card.perform;

import static osm.jp.gpx.matchtime.gui.AdjustTerra.i18n;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;

import osm.jp.gpx.AppParameters;
import osm.jp.gpx.matchtime.gui.AdjustTerra;
import osm.jp.gpx.matchtime.gui.parameters.ParameterPanel;
import osm.jp.hayashi.tools.files.Directory;

@SuppressWarnings("serial")
public class ParameterPanelOutput extends ParameterPanel implements ActionListener
{
    public JCheckBox gpxOverwriteMagvar;	// ソースGPXの<MAGVAR>を無視する
    public JCheckBox gpxOutputSpeed;	// GPXに<SPEED>を書き出す
    public ParameterPanelSimplify simplifyMeters;	// 「単純化(m)」正の整数 TEXT
    
    JFileChooser fc;
    JButton selectButton;
    int chooser;
    
    /**
     * コンストラクタ
     * ディレクトリのみ選択可能なダイアログ
     * @param label
     * @param text 
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    public ParameterPanelOutput(String text) {
        super(AppParameters.IMG_OUTPUT_FOLDER, i18n.getString("label.530") + ": ", text);
        
        // Create a file chooser
        this.chooser = JFileChooser.DIRECTORIES_ONLY;

        // "選択..."
        selectButton = new JButton(
            i18n.getString("button.select"),
            AdjustTerra.createImageIcon("/images/Open16.gif")
        );
        selectButton.addActionListener(this);
        this.add(selectButton);

        //---- CENTER -----
        this.gpxOverwriteMagvar = new JCheckBox(i18n.getString("label.560"), false);
        this.gpxOutputSpeed = new JCheckBox(i18n.getString("label.570"), false);
        this.simplifyMeters = new ParameterPanelSimplify("simplify", i18n.getString("label.580"), "3");

        try {
            AppParameters params = new AppParameters();
            
            // チェックボックス "ソースGPXの<MAGVAR>を無視する"
            if (params.getProperty(AppParameters.GPX_OVERWRITE_MAGVAR).equals("true")) {
            	this.gpxOverwriteMagvar.setEnabled(true);
            }

            // チェックボックス "出力GPXに[SPEED]を上書きする"
            if (params.getProperty(AppParameters.GPX_OUTPUT_SPEED).equals("true")) {
            	this.gpxOutputSpeed.setEnabled(true);
            }

            // TEXTボックス "単純化(m)"
            if (params.getProperty(AppParameters.SIMPLIFY_METERS) != null) {
            	String str = params.getProperty(AppParameters.SIMPLIFY_METERS);
                if (str.isEmpty()) {
                	this.simplifyMeters.setText("3");
                	this.simplifyMeters.setEnabled(true);
                }
                else {
                	this.simplifyMeters.setText(str);
                	this.simplifyMeters.setEnabled(true);
                }
            }
        }
        catch (Exception e) {}
    }
    
    public boolean isUpdateMagvar() {
    	return gpxOverwriteMagvar.isSelected();
    }
    
    public boolean isUpdateSpeed() {
    	return gpxOutputSpeed.isSelected();
    }
    
    public int getSimplify() {
    	try {
    		return this.simplifyMeters.getSimplify();
    	}
    	catch (NumberFormatException e) {
    		return 0;
    	}
    }

	@Override
	public void actionPerformed(ActionEvent e) {
        if (e.getSource() == selectButton){
            File sdir;
            try {
                sdir = getDirectory();
            } catch (FileNotFoundException ex) {
            	Path p;
                try {
                	p = Directory.getCurrentDirectory();
	                this.argField.setText(p.toAbsolutePath().toString());
				} catch (URISyntaxException e1) {
	                this.argField.setText(".");
				}
                sdir = new File(this.argField.getText());
            }
            if (sdir.exists()) {
                this.fc = new JFileChooser(sdir);
            }
            else {
                this.fc = new JFileChooser();
            }
            this.fc.setFileSelectionMode(this.chooser);

            int returnVal = this.fc.showOpenDialog(ParameterPanelOutput.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = this.fc.getSelectedFile();
                if (!file.isDirectory()) {
                	file = file.getParentFile();
                }
                String text = file.getAbsolutePath();
                this.argField.setText(text);
                this.setText(text);
            }
        }
	}
	
    File getDirectory() throws FileNotFoundException {
        String path = this.argField.getText();
        if (path == null) {
            throw new FileNotFoundException("Folder is Not specifiyed yet.");
        }
        File sdir = new File(path);
        if (!sdir.exists()) {
            throw new FileNotFoundException(String.format("Folder '%s' is Not exists.", path));
        }
        if (!sdir.isDirectory()) {
        	sdir = sdir.getParentFile();
        }
        return sdir;
    }

	@Override
	public boolean isEnable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setText(String text) {
        this.argField.setText(text);
	}

	@Override
	public String getText() {
        return this.argField.getText();
	}
}