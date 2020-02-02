package osm.jp.gpx.matchtime.gui;

import java.util.Observable;

public class ParameterData extends Observable {
    String content = "";
    
    String getContent() {
        return content;
    }

    void setContent(String content) {
        this.content = content;
        setChanged();
        super.notifyObservers(content);
        clearChanged();
    }

    @Override
    public void notifyObservers(Object arg) {
        setContent(arg.toString());
    }
}
