package group.tonight.vipvideohelper;

import java.util.HashMap;
import java.util.Map;

public class Consts {
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
