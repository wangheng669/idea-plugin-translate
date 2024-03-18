package common;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import okhttp3.*;
import translate.TransConfig;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class CommonUtils {
    public static String getBaiduMD5(String p, String randomString) {
        String flexString = TransConfig.BD_APP_ID + p + randomString + TransConfig.BD_APP_SEC;
        return md5(flexString);
    }
    public static String getYouDaoMD5(String p, String randomString) {
        String flexString = TransConfig.YD_APP_ID + p + randomString + TransConfig.YD_APP_KEY;
        return md5(flexString);
    }

    //默认不需要samples
    public static String getBingUrl(String word) {
        return getBingUrl(word, false);
    }

    public static String getBingUrl(String word, boolean needSample) {
        if (needSample) {
            return TransConfig.BING_URL + "?Word=" + word;
        } else {
            return TransConfig.BING_URL + "?Word=" + word + "&Samples=false";
        }
    }

    public static String md5(String str) {
        byte[] digest = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            digest = md5.digest(str.getBytes("utf-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new BigInteger(1, digest).toString(16);
    }
    

    public static String request(String json,String url) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(Objects.requireNonNull(MediaType.parse("application/json; charset=utf-8")), json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void SkipFile(Project project, String filePath,int lineNumber) {
        // 根据文件路径获取VirtualFile对象
        VirtualFile file = LocalFileSystem.getInstance().findFileByIoFile(new File(filePath));
        if (file != null) {
            // 获取编辑器
            Editor editor = FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, file, lineNumber - 1, 0), true);
            if (editor != null) {
                // 设置光标位置
                editor.getCaretModel().moveToLogicalPosition(editor.offsetToLogicalPosition(lineNumber - 1));
            }
        }
    }


    private static Balloon balloon;
    private static Balloon lastBalloon;
    private static Balloon waitBalloon;

    public static void showMessage(String message, Editor editor) {
        ApplicationManager.getApplication().invokeLater(() -> {
            if(waitBalloon!=null)
            {
                waitBalloon.hide();
            }
            JBPopupFactory jbPopupFactory = JBPopupFactory.getInstance();
            BalloonBuilder balloonBuilder = jbPopupFactory.createHtmlTextBalloonBuilder(message, null, new JBColor(
                    Gray._222
                    , Gray._77), null);
            balloonBuilder.setFadeoutTime(30000);
            balloon = balloonBuilder.createBalloon();

            if (lastBalloon != null) {
                lastBalloon.hide();
            }
            lastBalloon = balloon;
            balloon.setAnimationEnabled(false);
            balloon.show(jbPopupFactory.guessBestPopupLocation(editor), Balloon.Position.below);
        });
    }


}
