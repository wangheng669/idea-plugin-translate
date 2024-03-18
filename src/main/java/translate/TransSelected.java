package translate;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseListener;
import common.CommonUtils;
import org.apache.http.util.TextUtils;
import java.util.regex.Pattern;
import translate.youdao.Translate;

public class TransSelected implements EditorMouseListener {


    public void mouseReleased(EditorMouseEvent e){
        Editor editor=e.getEditor();
        String selectedWord=editor.getSelectionModel().getSelectedText();
        if(TextUtils.isEmpty(selectedWord)) return; // 空限制
        if(selectedWord.length() > 5000) return; // 字符限制
        if(selectedWord.contains("\n")) return; // 换行符限制
        new Thread(new Runnable() {
            @Override
            public void run() {
                translate(selectedWord,editor);
            }
        }).start();
    }

    public static String camelToUnderscore(String camelCase) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < camelCase.length(); i++) {
            char currentChar = camelCase.charAt(i);
            if (Character.isUpperCase(currentChar) && i > 0) {
                result.append('_');
            }
            result.append(Character.toLowerCase(currentChar));
        }
        return result.toString();
    }

    public void translate(String selectedWord, Editor editor) {

        if(selectedWord.matches(".*[a-z].*")){
            selectedWord = camelToUnderscore(selectedWord); // 大写处理
        }else{
            selectedWord = selectedWord.toLowerCase();
        }
        String to = containsChinese(selectedWord) ? "en" : "zh";
        if(!containsChinese(selectedWord)){ // 非中文处理
            selectedWord = selectedWord.replaceAll("[^a-z^A-Z^0-9^ ^_^(^)]", "");
            if (TextUtils.isBlank(selectedWord) || selectedWord.length() < 2) return;
            selectedWord = selectedWord.replaceAll("_", " ");
            selectedWord = parseWord(selectedWord); // 驼峰
        }

        String result = "";
        result = LocalData.read(selectedWord); // 优先读取本地
        if(TextUtils.isEmpty(result)){
            result = Translate.TransByYoudao(selectedWord,to);
            LocalData.store(selectedWord,result);
        }
        CommonUtils.showMessage(result,editor);
    }

    /**
     * 在大写字母前自动加入空格
     */
    private String parseWord(String text) {
        StringBuilder builder = new StringBuilder(text);
        for (int i = text.length() - 1; i > 0; i--) {
            if (Character.isUpperCase(text.charAt(i))) {
                builder.insert(i, " ");
            }
        }
        return builder.toString();
    }

    public static boolean containsChinese(String str) {
        String regex = "[\\u4e00-\\u9fa5]";
        return Pattern.compile(regex).matcher(str).find();
    }



}