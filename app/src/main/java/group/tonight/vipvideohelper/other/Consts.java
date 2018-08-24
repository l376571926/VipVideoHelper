package group.tonight.vipvideohelper.other;

import java.util.HashMap;
import java.util.Map;

public class Consts {
    public static final String KEY_LAST_VERSION_ID = "pref_last_version_id";
    public static final String KEY_LAST_DOWNLOAD_ID = "pref_last_download_id";

    public static final String[] xianlu = {
            "http://jx.598110.com/index.php?url="
            , "http://jx.598110.com/duo/index.php?url="
            , "http://jx.598110.com/zuida.php?url="
            , "http://jx.aeidu.cn/index.php?url="
    };
    public static final String[] xianluming = {
            "华南线路①"
            , "华南线路②"
            , "华北线路③"
            , "华中线路④"};

    private static Map<String, String> paserUrlMap = new HashMap<>();

    static {
        paserUrlMap.put("qq.com", "http://vipjixiq.duapp.com/fvsp/index.php?url=");
        paserUrlMap.put("iqiyi.com", "http://yanllcn.duapp.com/daidai/index.php?url=");
        paserUrlMap.put("tudou.com", "http://jx.598110.com/wu/?url=");
        paserUrlMap.put("pptv.com", "http://jx.598110.com/666/index.php?url=");
        paserUrlMap.put("youku.com", "http://vipjixiq.duapp.com/napian/index.php?url=");
        paserUrlMap.put("mgtv.com", "http://yanllcn.duapp.com/damang/index.php?url=");
        paserUrlMap.put("le.com", "http://yanllcn.duapp.com/47/index.php?url=");
        paserUrlMap.put("fun.tv", "http://vip.jlsprh.com/jiexi/92/?url=");
        paserUrlMap.put("sohu.com", "http://jx.598110.com/168/?url=");
        paserUrlMap.put("default", "http://yanllcn.duapp.com/dade/index.php?url=");
    }

    public static String getParseUrlHost(String host) {
        if (paserUrlMap.containsKey(host)) {
            return paserUrlMap.get(host);
        }
        return paserUrlMap.get("default");
    }
}
