package osm.jp.gpx;

import osm.jp.gpx.Expecter;

public class Fixture {
    String comment;				// テスト概要（コメント）
    String tarFilePath;			// TARデータ
    String gpxSourcePath;		// GPXファイル（オリジナル）
    String gpxDestinationPath;	// GPXファイル（配置先）
    String iniFilePath;			// iniファイル
    Expecter[] expecters;

    public Fixture(
        String comment,
        String tarFilePath,
        String gpxSourcePath,
        String gpxDestinationPath,
        String iniFilePath,
        Expecter[] expecters
    ) {
        this.comment = comment;
        this.tarFilePath = tarFilePath;
        this.gpxSourcePath = gpxSourcePath;
        this.gpxDestinationPath = gpxDestinationPath;
        this.iniFilePath = iniFilePath;
        this.expecters = expecters;
    }

    @Override
    public String toString() {
        String msg = "テストパターン : "+ comment + "\n";
        msg += "\ttarFilePath = "+ tarFilePath +"\n";
        msg += "\tgpxSourcePath = "+ gpxSourcePath +"\n";
        msg += "\tgpxDestinationPath = "+ gpxDestinationPath +"\n";
        msg += "\tiniFilePath = "+ iniFilePath;
        return msg;
    }

}
