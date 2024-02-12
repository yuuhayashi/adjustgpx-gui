package osm.jp.gpx.matchtime.gui.parameters;

import java.awt.Color;

import javax.swing.JLabel;

/**
 * パラメータを設定する為のパネル。
 * この１インスタンスで、１パラメータをあらわす。
 */
public abstract class ParameterPanelWithComment extends ParameterPanel {
	private static final long serialVersionUID = 1L;
	
	protected JLabel comment;

    ParameterPanelWithComment() {
        super();
        this.comment = new JLabel();
        this.comment.setForeground(Color.darkGray);
        this.comment.setText("");
        this.add(this.comment);
    }

    public ParameterPanelWithComment(String propertyName, String label, String text) {
        this();
        this.setName(propertyName);
        this.setLabel(label);
        this.setText(text);
    }
    
    public void setComment(String text, boolean ok) {
    	if (ok) {
            this.comment.setForeground(new Color(0,0,0xcd));		// Color.mediumblue #0000cd
    	}
    	else {
            this.comment.setForeground(new Color(0xdc,0x14,0x3c));	// Color.crimson #dc143c
    	}
        this.comment.setText(text);
    }
}
