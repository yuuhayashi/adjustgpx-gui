package osm.jp.gpx.matchtime.gui.parameters;

public interface PanelAction {
    void openAction();
    
    /**
     * 入力条件が満たされているかどうか
     * @return 
     */
    boolean isEnable();
}
