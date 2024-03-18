package translate.baidu;

import com.google.gson.Gson;
import com.intellij.openapi.editor.Editor;
import common.HttpGet;
import common.MD5;
import translate.TransConfig;
import translate.entity.BaiduResponseEntity;

import common.CommonUtils;
import java.util.HashMap;
import java.util.Map;

public class Translate {

    /**
     * 百度翻译，主要翻译句子
     */
    public static String TransByBaidu(String words, String to) {
        String result = "";
        Map<String, String> params = buildParams(words, "auto", to);
        Gson gson = new Gson();
        BaiduResponseEntity baiduResponse = gson.fromJson(HttpGet.get(TransConfig.BD_URL, params), BaiduResponseEntity.class);
        return result;
    }

    private static Map<String, String> buildParams(String query, String from, String to) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);
        params.put("appid", TransConfig.BD_APP_ID);
        String salt = String.valueOf(System.currentTimeMillis()); // 随机数
        params.put("salt", salt);
        String src = TransConfig.BD_APP_ID + query + salt + TransConfig.BD_APP_SEC; // 加密前的原文 // 签名
        params.put("sign", MD5.md5(src));
        return params;
    }
}
