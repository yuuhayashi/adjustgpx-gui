package osm.jp.gpx.matchtime.gui.parameters;

public interface ParamAction {
    boolean isEnable();
    void setText(String text);
    String getText();
    void setName(String name);
    String getName();
}
