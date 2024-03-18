package translate;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseListener;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import org.apache.http.util.TextUtils;

public class GoTransSelected implements EditorMouseListener {

    public void mouseReleased(EditorMouseEvent e){
        Editor editor=e.getEditor();
        String selectedWord=editor.getSelectionModel().getSelectedText();
        if(TextUtils.isEmpty(selectedWord)) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                translate(selectedWord,editor);
            }
        }).start();
    }

    public void translate(String text, Editor editor) {
        if (TextUtils.isEmpty(text)) return;
        if (text.contains("\n")) return;
        String json = String.format("{\"text\": \"%s\"}", text);
        String message = common.CommonUtils.request(json,"http://127.0.0.1:8080/translate");
        if(!message.isEmpty()){
            showMessage(message,editor);
        }
    }

    private Balloon waitBalloon;
    private Balloon balloon;
    private Balloon lastBalloon;

    private void showMessage(String message, Editor editor) {
        ApplicationManager.getApplication().invokeLater(() -> {
            if(waitBalloon!=null)
            {
                waitBalloon.hide();
            }
            JBPopupFactory jbPopupFactory = JBPopupFactory.getInstance();
            BalloonBuilder balloonBuilder = jbPopupFactory.createHtmlTextBalloonBuilder(message, null, new JBColor(
                    Gray._222
                    , Gray._77), null);
            balloonBuilder.setFadeoutTime(20000);
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