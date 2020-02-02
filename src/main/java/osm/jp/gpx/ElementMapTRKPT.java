package osm.jp.gpx;

import java.text.ParseException;
import java.util.Date;
import java.util.TreeMap;

import org.w3c.dom.DOMException;

@SuppressWarnings("serial")
public class ElementMapTRKPT extends TreeMap<Date, TagTrkpt> {
    public static final long DIFF_MAE_TIME = 3000L;	// before 3 secound

    public ElementMapTRKPT() {
        super(new TimeComparator());
    }

    /**
     * 拡張put value:ElementをputするとElement内のtimeを読み取ってkeyとしてthis.put(key,value)する。
     * @param tag
     * @return 
     * @throws java.text.ParseException
     * @code{
     * <trkpt lat="36.4260153752" lon="138.0117778201">
     *   <ele>614.90</ele>
     *   <time>2017-05-21T23:02:16Z</time>
     *   <hdop>0.5</hdop>
     * </trkpt>
     * }
     * @return	keyとして登録したtime:Date
     * @throws ParseException 
     * @throws DOMException 
     */
    public Date put(TagTrkpt tag) throws DOMException, ParseException {
        this.put(tag.time, tag);
    	return tag.time;
    }

    /**
     * 指定時刻(jptime)のTRKPTエレメントを取り出す。
     * 
     * @param jptime	指定する日時
     * @return	エレメントTRKPT。指定時刻に対応するノードがないときはnullを返す。
     * @throws ParseException
     */
    public TagTrkpt getValue(Date jptime) throws ParseException {
    	TagTrkpt imaE = getTrkpt(jptime);
        if (imaE != null) {
            TagTrkpt maeE = getMaeTrkpt(imaE.time);
            if (maeE != null) {
            	Complementation comp = new Complementation(imaE, maeE);

                // <MAGVAR>がなければ、
                // 直前の位置と、現在地から進行方向を求める
            	// 経度(longitude)と経度から進行方向を求める
                if (Complementation.param_GpxOverwriteMagvar) {
                    comp.complementationMagvar();
                }

                // 緯度・経度と時間差から速度(km/h)を求める
                if (Complementation.param_GpxOutputSpeed) {
                    comp.complementationSpeed();
                }
                //return (TagTrkpt)(comp.imaTag.trkpt.cloneNode(true));
                return (TagTrkpt)(comp.imaTag);
            }
            return imaE;
        }
        return null;
    }
    
    /**
     * [map]から指定した時刻の<trkpt>エレメントを取り出す。
     * 取り出すエレメントは、指定した時刻と同一時刻、もしくは、直近・直前の時刻のエレメントとする。
     * 指定した時刻以前のエレメントが存在しない場合は null を返す。
     * 指定した時刻と直近・直前のエレメントの時刻との乖離が プロパティ[OVER_TIME_LIMIT=3000(ミリ秒)]より大きい場合には null を返す。
     * 
     * @param jptime
     * @return	<trkpt>エレメント。対象のエレメントが存在しなかった場合には null。
     * @throws ParseException
     */
    private TagTrkpt getTrkpt(Date jptime) throws ParseException {
    	Date keyTime = null;
    	for (Date key : this.keySet()) {
            int flag = jptime.compareTo(key);
            if (flag < 0) {
                if (keyTime != null) {
                    return this.get(keyTime);
                }
                return null;
            }
            else if (flag == 0) {
                return this.get(key);
            }
            else if (flag > 0) {
                keyTime = new Date(key.getTime());
            }
        }
        if (keyTime != null) {
            if (Math.abs(keyTime.getTime() - jptime.getTime()) <= OVER_TIME_LIMIT) {
                return this.get(keyTime);
            }
        }
        return null;
    }
    
    /**
     * ロガーの最終取得時刻を超えた場合、どこまでを有効とするかを設定する。
     * この設定がないと、最終取得時刻を超えたものは全て有効になってしまう。
     * OVER_TIME_LIMITは、GPSロガーの位置取得間隔（）よりも長くする必要がある。長すぎても良くない。
     */
    public static long OVER_TIME_LIMIT = 3000;	// ミリ秒(msec)
    
    private TagTrkpt getMaeTrkpt(Date time) throws ParseException {
    	Date maeTime = null;
        for (Date key : this.keySet()) {
            int flag = time.compareTo(key);
            if (flag > 0) {
                maeTime = new Date(key.getTime());
            }
            else if (flag == 0) {
                if (maeTime == null) {
                    return null;
                }
                return this.get(maeTime);
            }
            else {
                // time は key より古い
                if (maeTime == null) {
                    return null;
                }
                if (Math.abs(maeTime.getTime() - time.getTime()) > OVER_TIME_LIMIT) {
                    return null;
                }
                return this.get(maeTime);
            }
        }
        return null;
    }
    
    public void printinfo() {
    	Date firstTime = null;
    	Date lastTime = null;
        for (Date key : this.keySet()) {
            if (firstTime == null) {
                firstTime = new Date(key.getTime());
            }
            lastTime = new Date(key.getTime());
        }
        System.out.println(String.format("|                      <trkseg/> |%20s|%20s|", ImportPicture.toUTCString(firstTime), ImportPicture.toUTCString(lastTime)));
    }
}
