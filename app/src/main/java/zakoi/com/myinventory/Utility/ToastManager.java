package zakoi.com.myinventory.Utility;

import android.content.Context;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;

/**
 * Created by zakoi on 7/20/17.
 */

public class ToastManager {

    private static ToastManager instance = null;

    private ToastManager() {}
    public SuperActivityToast current;

    public static ToastManager getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = new ToastManager();
        return instance;
    }

    public SuperActivityToast ShowSyncToast(Context context) {

            current = new SuperActivityToast(context, Style.TYPE_PROGRESS_CIRCLE);
            current.setText("Syncing With Server");
            current.setIndeterminate(true);
            current.setProgressIndeterminate(true);
            current.setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_PINK));
            current.show();

        return current;
    }

    public SuperActivityToast ShowSubmitted(Context context, String msg) {

        current = new SuperActivityToast(context, Style.TYPE_PROGRESS_CIRCLE);
        current.setText(msg);
        current.setDuration(Style.DURATION_MEDIUM);
        current.setFrame(Style.FRAME_KITKAT);
        current.setAnimations(Style.ANIMATIONS_FADE);
        current.setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN));
        current.show();

        return current;
    }

    public void DismissToast() {
        current.dismiss();
    }


}
