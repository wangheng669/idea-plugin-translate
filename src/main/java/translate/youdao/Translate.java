package translate.youdao;

import com.google.gson.Gson;
import com.intellij.openapi.editor.Editor;
import common.CommonUtils;
import common.MD5;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import translate.TransConfig;
import translate.entity.BaiduResponseEntity;
import translate.entity.YouDaoResponseEntity;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


public class Translate {

    public static String TransByYoudao(String words,String to) {
        String result = "";
        Map<String, String> params = buildParams(words, "auto", to);
        try {
            Gson gson = new Gson();
            YouDaoResponseEntity youdaoResponse = gson.fromJson(requestForHttp(TransConfig.YD_URL,params), YouDaoResponseEntity.class);
            result = youdaoResponse.translation.get(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private static Map<String, String> buildParams(String query, String from, String to) {
        Map<String,String> params = new HashMap<String,String>();
        String q = query;
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("from", from);
        params.put("to", to);
        params.put("signType", "v3");
        String curtime = String.valueOf(System.currentTimeMillis() / 1000);
        params.put("curtime", curtime);
        String signStr = TransConfig.YD_APP_ID + truncate(q) + salt + curtime + TransConfig.YD_APP_KEY;
        String sign = getDigest(signStr);
        params.put("appKey", TransConfig.YD_APP_ID);
        params.put("q", q);
        params.put("salt", salt);
        params.put("sign", sign);
        return params;
    }

    public static String requestForHttp(String url,Map<String,String> params) throws IOException {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        Iterator<Map.Entry<String,String>> it = params.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String,String> en = it.next();
            String key = en.getKey();
            String value = en.getValue();
            paramsList.add(new BasicNameValuePair(key,value));
        }
        httpPost.setEntity(new UrlEncodedFormEntity(paramsList,"UTF-8"));
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        try{
                HttpEntity httpEntity = httpResponse.getEntity();
                String json = EntityUtils.toString(httpEntity,"UTF-8");
                EntityUtils.consume(httpEntity);
                return json;
        }finally {
            try{
                if(httpResponse!=null){
                    httpResponse.close();
                }
            }catch(IOException e){
                return "";
            }
        }
    }
    // asyncClearWeekGoddessHonorRank
    /**
     * 生成加密字段
     */
    public static String getDigest(String string) {
        if (string == null) {
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        byte[] btInput = string.getBytes(StandardCharsets.UTF_8);
        try {
            MessageDigest mdInst = MessageDigest.getInstance("SHA-256");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String truncate(String q) {
        if (q == null) {
            return null;
        }
        int len = q.length();
        String result;
        return len <= 20 ? q : (q.substring(0, 10) + len + q.substring(len - 10, len));
    }
}