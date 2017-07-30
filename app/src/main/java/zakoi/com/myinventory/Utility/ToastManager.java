package zakoi.com.myinventory.Utility;

import android.content.Context;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;

/**
 * Created by zakoi on 7/20/17.
 */

public class ToastManager {

    private enum Type {
        error,
        sync,
        submit
    }

    private Type _type;
    private String _message;
    private Context _context;
    private static ToastManager instance = null;

    public SuperActivityToast current;


    public ToastManager() {
        _type = Type.error;
        _message = "";
        _context = null;
    }

    public static ToastManager getInstance() {
        if (instance != null) {
            return instance;
        }

        instance = new ToastManager();
        return instance;
    }

    public SuperActivityToast ShowSyncToast(Context context) {
        // For syncing purposes
        if (isSameToast(context, Type.sync, _message)) {
            return current;
        }

        DismissToast();

        current = new SuperActivityToast(context, Style.TYPE_PROGRESS_CIRCLE);
        current.setText("Syncing With Server");
        //current.setFrame(Style.FRAME_KITKAT);
        current.setIndeterminate(true);
        current.setDuration(Style.DURATION_SHORT);
        current.setProgressIndeterminate(true);
        current.setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_PINK));
        current.show();
        return current;
    }

    public SuperActivityToast SendSMS(Context context, String msg) {
        // For syncing purposes
        if (isSameToast(context, Type.submit, msg)) {
            return current;
        }

        DismissToast();
        current = new SuperActivityToast(context, Style.TYPE_STANDARD);
        current.setText(msg);
        current.setDuration(Style.DURATION_SHORT);
        current.setFrame(Style.FRAME_KITKAT);
        current.setAnimations(Style.ANIMATIONS_POP);
        current.setIconResource(android.R.drawable.ic_menu_send);
        current.setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN));
        current.show();
        return current;
    }

    public SuperActivityToast ShowSubmitted(Context context, String msg) {
        // For syncing purposes
        if (isSameToast(context, Type.submit, msg)) {
            return current;
        }

        DismissToast();
        current = new SuperActivityToast(context, Style.TYPE_STANDARD);
        current.setText(msg);
        current.setDuration(Style.DURATION_SHORT);
        current.setFrame(Style.FRAME_KITKAT);
        current.setAnimations(Style.ANIMATIONS_POP);
        current.setIconResource(android.R.drawable.ic_menu_upload);
        current.setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN));
        current.show();

        return current;
    }

    public SuperActivityToast ShowError(final Context context, final String message) {
        return ShowError(context, message, Style.TYPE_STANDARD, Style.FRAME_LOLLIPOP);
    }

    public SuperActivityToast ShowError(final Context context, final String message, final int frameType) {
        return ShowError(context, message, Style.TYPE_STANDARD, frameType);
    }

    public SuperActivityToast ShowError(final Context context, final String message, final int type, final int frameType) {
        // For syncing purposes
        if (isSameToast(context, Type.error, message)) {
            return current;
        }

        DismissToast();
        current = new SuperActivityToast(context, type);
        current.setText("   " + message);
        current.setIconResource(android.R.drawable.stat_notify_error);
        current.setAnimations(Style.ANIMATIONS_FADE);
        current.setDuration(Style.DURATION_SHORT);
        current.setFrame(frameType);
        current.setAnimations(Style.ANIMATIONS_FADE);
        current.setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_RED));
        current.show();
        return current;
    }

    private Boolean isSameToast(Context context, Type type, String message) {

        if (type == _type && message.equals(_message) && _context == context) {
            return false;
        }

        _context = context;
        _type = type;
        _message = message;
        return false;
    }

    public void Reset() {
        _context = null;
    }

    public void DismissToast() {
        if(current != null)
            current.dismiss();
    }




}
