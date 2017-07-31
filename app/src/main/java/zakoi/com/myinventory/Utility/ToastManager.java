package zakoi.com.myinventory.Utility;

import android.content.Context;
import android.util.Log;

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
        if (_context == null || !_context.toString().equals(context.toString())) {
            if(_context != null)
                current.dismiss();
            current  = new SuperActivityToast(context);
            Log.i("Toast","Toast context changed");
            _context = context;
        }

        current.setText("Syncing With Server");
        current.setDuration(Style.DURATION_SHORT);
        current.setIconResource(android.R.drawable.stat_notify_sync);
        current.setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_PINK));
        if(!current.isShowing())
            current.show();
        return current;
    }

    public SuperActivityToast SendSMS(Context context, String msg) {
        // For syncing purposes
        if (_context == null || !_context.toString().equals(context.toString())) {
            if(_context != null)
                current.dismiss();
            current  = new SuperActivityToast(context);
            _context = context;
            Log.i("Toast","Toast context changed");
        }
        current.setText(msg);
        current.setDuration(Style.DURATION_SHORT);
        current.setAnimations(Style.ANIMATIONS_POP);
        current.setIconResource(android.R.drawable.ic_menu_send);
        current.setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN));
        current.show();
        return current;
    }

    public SuperActivityToast ShowSubmitted(Context context, String msg) {
        // For syncing purposes

        if (_context == null || !_context.toString().equals(context.toString())) {
            if(_context != null)
                current.dismiss();
            current  = new SuperActivityToast(context);
            _context = context;
            Log.i("Toast","Toast context changed");
        }

        current.setText(msg);
        current.setDuration(Style.DURATION_VERY_SHORT);
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
        if (_context == null || !_context.toString().equals(context.toString())) {
            if(_context != null)
                current.dismiss();
            current  = new SuperActivityToast(context);
            Log.i("Toast","Toast context changed");
            _context = context;
        }

        current.setText("   " + message);
        current.setIconResource(android.R.drawable.stat_notify_error);
        current.setAnimations(Style.ANIMATIONS_FADE);
        current.setDuration(Style.DURATION_VERY_SHORT);
        current.setFrame(frameType);
        current.setAnimations(Style.ANIMATIONS_FADE);
        current.setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_RED));
        if(!current.isShowing())
            current.show();
        return current;
    }

    public void Reset() {
        _context = null;
    }

    public void DismissToast() {
        if(current != null)
            current.dismiss();
    }




}
