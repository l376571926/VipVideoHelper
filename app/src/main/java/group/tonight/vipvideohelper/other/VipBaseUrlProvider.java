package group.tonight.vipvideohelper.other;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VipBaseUrlProvider {
    //闺蜜的战争
    private static String TEST_VIP_VIDEO_URL = "http://www.iqiyi.com/v_19rr1num7g.html";

    public static final String DEFAULT_VIP_URL_0 = "http://yun.mt2t.com/yun?url=";
    public static final String DEFAULT_VIP_URL_1 = "http://jx.618g.com/?url=";
    public static final String DEFAULT_VIP_URL_2 = "http://jx.598110.com/index.php?url=";
    public static final String DEFAULT_VIP_URL_3 = "http://jx.598110.com/zuida.php?url=";
    public static final String DEFAULT_VIP_URL_4 = "http://jx.aeidu.cn/index.php?url=";
    public static final String DEFAULT_VIP_URL_5 = "http://api.wlzhan.com/sudu/?url=";

    private final Map<String, String> map;

    public VipBaseUrlProvider() {
        map = new LinkedHashMap<>();
        init();
    }

    private void init() {
        //618G免费解析http://jx.618g.com/
        map.put(DEFAULT_VIP_URL_1, "4号vip解析接口【小七科技】");

        //熊孩子http://www.598110.com/
        map.put(DEFAULT_VIP_URL_2, "华南线路①");//慢
        map.put(DEFAULT_VIP_URL_3, "华北线路③");//稍慢
        map.put(DEFAULT_VIP_URL_4, "华中线路④");//稍慢

        //http://www.5ifxw.com/vip/
        map.put(DEFAULT_VIP_URL_5, "万能接口1");

        //百域学院http://yun.baiyug.cn/
        map.put("http://yun.baiyug.cn/vip/?url=", "百域学院");
        map.put("http://yun.baiyug.cn/vip/index.php?url=", "1号vip解析接口【小七科技】");
        map.put("http://api.baiyug.cn/vip/index.php?url=", "爱奇艺超清接口2");//http://yun.baiyug.cn/

        //小米视频解析
        map.put("http://api.xiaomil.com/a/index.php?url=", "小米视频解析a接口");//http://api.xiaomil.com/
        map.put("http://api.xiaomil.com/p/index.php?url=", "小米视频解析p接口");

        //爱跟影院-2018年免会员vip全网高清视频-首页
        map.put("http://2gty.com/apiurl/yun.php?url=", "乐视视频接口");

        //ODFLV视频解析
        map.put("http://aikan-tv.com/?url=", "5号vip解析接口【小七科技】");
        //17K云播
        map.put("http://17kyun.com/api.php?url=", "9号vip解析接口【小七科技】");
        //大表哥解析服务http://jx.biaoge.tv/
        map.put("http://jx.biaoge.tv/index.php?url=", "10号vip解析接口【小七科技】");
        map.put("https://jx.biaoge.tv/?url=", "万能接口23");
        //寒曦朦视频智能解析http://jx.hanximeng.com/
        map.put("http://jx.hanximeng.com/api.php?url=", "12号vip解析接口【小七科技】");
        map.put("http://jx.hanximeng.com/vip.php?url=", "接口");
        //思古解析
        map.put("http://api.bbbbbb.me/jx/?url=", "14号vip解析接口【小七科技】");
        map.put("http://api.bbbbbb.me/jiexi/?url=", "万能接口7");

        //减肥影视VIP视频解析
        map.put("http://api.91exp.com/svip/?url=", "减肥影视VIP视频解析");

        //那片免费Api接口
        map.put("http://api.nepian.com/ckparse/?url=", "那片免费Api接口");
        map.put("https://jxapi.nepian.com/ckparse/?url=", "万能接口13");
        //花园影视免费解析http://www.zz22x.com/
        map.put("http://j.zz22x.com/jx/?url=", "花园影视免费解析");

        //18云解析http://api.nobij.top/
        map.put("http://api.nobij.top/jx/?url=", "18云解析");

        //强强视频解析https://000o.cc/
        map.put("http://000o.cc/jx/ty.php?url=", "强强视频解析");
        map.put("https://014670.cn/jx/ty.php?url=", "13号vip解析接口【小七科技】");

        //品优解析http://api.pucms.com/
        map.put("http://api.pucms.com/index.php?url=", "品优解析");
        map.put("http://api.pucms.com/?url=", "爱奇艺超清接口1");

        //vip会员视频解析http://www.wq114.org/
        map.put("http://www.vipjiexi.com/tong.php?url=", "vip会员视频解析");
        map.put("http://www.vipjiexi.com/yun.php?url=", "万能接口12");
        //人人免费云解析http://jx.api.163ren.com/
        map.put("http://jx.api.163ren.com/vod.php?url=", "人人免费云解析");

        //叮当解析http://dingdang-tv.com/index/jx
        map.put("http://dingdang-tv.com/jx?url=", "万能接口18");

        //vip看看https://vip.72du.com
        map.put("http://vip.72du.com/api/?url=", "万能接口10");
        map.put("http://v.72du.com/api/?url=", "万能接口5");

        //云解析-VIP视频在线解析接口http://jiexi.92fz.cn/
        map.put("http://jiexi.92fz.cn/player/vip.php?url=", "线路四");

        //小牛分享网 - 爱奇艺会员账号共享,腾讯视频账号共享,优酷会员账号共享,乐视vip账号共享
        map.put("http://www.xnflv.com/xnflv/index.php?url=", "万能接口24");

        //酷博网视http://tv.x-99.cn/
        map.put("http://tv.x-99.cn/api/wnapi.php?id=", "15号vip解析接口【小七科技】");

        //无名小站
        map.put("http://www.85105052.com/admin.php?url=", "11号vip解析接口【小七科技】");
        map.put("http://www.82190555.com/index/qqvod.php?url=", "优酷超清接口");
        map.put("http://www.82190555.com/video.php?url=", "万能接口20");

        //fastflv全网无广告免费vip免费在线视频解析api-超级稳定-电影资源免费采集api网站view-source:http://api.fastflv.cc/
        map.put("http://api.fastflv.cc/jiexi/?url=", "8号vip解析接口【小七科技】");

        //旋风动漫-最具优势的加速解析服务提供商http://api.xfsub.com/
        map.put("http://api.xfsub.com/index.php?url=", "芒果TV超清接口");//http://api.xfsub.com/

        //金桥解析
        map.put("http://jqaaa.com/jq3/?url=", "万能接口4");
        map.put("http://jqaaa.com/jx.php?url=", "2号通用接口");

        map.put("http://jx.598110.com/duo/index.php?url=", "华南线路②");
        map.put("http://vip.jlsprh.com/index.php?url=", "搜狐视频接口");
        map.put("http://www.xmqbook.com/xnflv/index.php?url=", "7号vip解析接口【小七科技】");
        map.put("http://api.662820.com/xnflv/index.php?url=", "3号通用接口");
        map.put("https://beaacc.com/api.php?url=", "万能接口1");
        map.put("http://y.mt2t.com/lines?url=", "万能接口2");
        map.put("http://xlyy100.com/xlyy.php?url=", "万能接口19");
        map.put("https://jiexi.071811.cc/jx.php?url=", "万能接口21");
        map.put("http://jiexi.071811.cc/jx2.php?url=", "万能接口3");
        map.put("https://jiexi.071811.cc/jx2.php?url=", "万能接口6");
        map.put(DEFAULT_VIP_URL_0, "万能接口6");
        map.put("http://qtv.soshane.com/ko.php?url=", "万能接口14");
        map.put("http://jx.vgoodapi.com/jx.php?url=", "万能接口9");
        map.put("http://www.efunfilm.com/yunparse/index.php?url=", "线路八");
        map.put("http://api.47ks.com/webcloud/?v=", "线路十");
        map.put("http://api.zuilingxian.com/jiexi.php?url=", "万能接口2");
        map.put("http://mt2t.com/yun?url=", "万能接口3");
        map.put("http://7cyd.com/vip/?url=", "万能接口4");
        map.put("https://ckplaer.duapp.com/hai.php?url=", "万能接口20");
        map.put("https://apiv.ga/magnet/", "万能接口22");
        map.put("http://player.jidiaose.com/supapi/iframe.php?v=", "万能接口26");
        map.put("https://api.47ks.com/webcloud/?v=", "万能接口27");
        map.put("http://88wx.pw/duo.php?url=", "请选择接口");
        map.put("http://www.662820.com/xnflv/index.php?url=", "7号vip解析接口【小七科技】");
        map.put("http://api.123zx.net/m3u8/play.html?id=", "2号vip解析接口【小七科技】");
        map.put("https://api.flvsp.com/?url=", "爱奇艺超清接口3");
        map.put("http://65yw.2m.vc/chaojikan.php?url=", "芒果TV手机接口");
    }

    public List<String> getAll() {
        Set<String> strings = map.keySet();
        return new ArrayList<>(strings);
    }

    public String getTestVipVideoUrl() {
        return TEST_VIP_VIDEO_URL;
    }
}
