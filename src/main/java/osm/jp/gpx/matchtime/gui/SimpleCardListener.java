package osm.jp.gpx.matchtime.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JTabbedPane;

import osm.jp.gpx.AppParameters;
import osm.jp.gpx.matchtime.gui.parameters.ParameterPanel;

public class SimpleCardListener implements PropertyChangeListener {
	int cardNo;
	ParameterPanel param;
    JTabbedPane cardPanel;       // ウィザード形式パネル（タブ型）
	Card[] cards;
	
	SimpleCardListener(Card[] cards, JTabbedPane cardPanel, int cardNo, ParameterPanel param) {
		this.cards = cards;
		this.cardPanel = cardPanel;
		this.cardNo = cardNo;
		this.param = param;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
        if (propertyName.equals(AppParameters.IMG_SOURCE_FOLDER)) {
            toEnable(cardNo, param.isEnable());
        }
        else {
            toEnable(cardNo, param.isEnable());
        }
	}
	
    void toEnable(final int cardNo, final boolean enable) {
        if ((cardNo >= 0) && (cardNo < cards.length)) {
            cardPanel.setEnabledAt(cardNo, enable);
            if ((cardNo -1) >= 0) {
                cards[cardNo -1].nextButton.setEnabled(enable);
            }
            if ((cardNo +1) < cards.length) {
                cardPanel.setEnabledAt(cardNo+1, enable);
                cards[cardNo +1].backButton.setEnabled(enable);
                cards[cardNo].nextButton.setEnabled(enable);
            }
        }
    }
}

