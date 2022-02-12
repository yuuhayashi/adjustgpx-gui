package osm.jp.gpx.matchtime.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

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
            toEnable(cardNo, checkImgSource(param.argField.getText()));
        }
	}
	
	/**
	 * "IMG_SOURCE_FOLDER"の設定内容が有効かどうかを判別する
	 * @param str
	 * @return
	 */
	boolean checkImgSource(String str) {
		if (str != null) {
			Path p = Paths.get(str);
			if (p != null) {
				if (Files.exists(p)) {
					if (Files.isDirectory(p)) {
						List<Path> entries;
						try {
							entries = Files.list(p).collect(Collectors.toList());
							for (Path file : entries) {
								if (file.toString().toLowerCase().endsWith(".jpeg") || file.toString().toLowerCase().endsWith(".jpg")) {
									return true;
								}
							}
						} catch (IOException e) {
							return false;
						}
					}
				}
			}
		}
		return false;
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

