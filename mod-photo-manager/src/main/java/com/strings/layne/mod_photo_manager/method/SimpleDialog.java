package com.strings.layne.mod_photo_manager.method;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.strings.layne.mod_photo_manager.R;
import com.strings.layne.mod_photo_manager.camera.LocalDisplay;

import java.util.ArrayList;

/**
 * SimpleDialog
 * Created by laynehuang on 2017/4/18.
 */

@Deprecated
public class SimpleDialog extends Dialog {

    private LinearLayout _layout;
    private TextView _title;
    private TextView _content;
    private ArrayList<TextView> buttons = new ArrayList<>();
    private OnButtonClickListener onButtonClickListener = null;
    private OnConfirmListener onConfirmListener = null;

    public SimpleDialog(Context context) {
        super(context, R.style.My_Dialog);
        initBaseView();
    }

    public SimpleDialog(Context context, String... buttons) {
        super(context, R.style.My_Dialog);
        initBaseView();
        for (int i = 0; i < buttons.length; i++) {
            addButton(buttons[i], ContextCompat.getColor(context, R.color.dialog_button_default_color));
        }
    }

    private void initBaseView() {
        Window window = getWindow();
        @SuppressLint("InflateParams")
        View view = window.getLayoutInflater().inflate(R.layout.simple_dialog, null);
        setContentView(view);
        _layout = (LinearLayout) view.findViewById(R.id.simple_dialog_linear_layout);
        _title = (TextView) view.findViewById(R.id.simple_dialog_title);
        _content = (TextView) view.findViewById(R.id.simple_dialog_content);

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = LocalDisplay.SCREEN_WIDTH_PIXELS;
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER);
        view.setScaleX(0.f);
        view.setScaleY(0.f);
        new Handler().postDelayed(() -> new AnimatorSetUtil().setDuration(500).refresh()
                .setTimeInterpolator(new DecelerateInterpolator())
                .play(ObjectAnimator.ofFloat(view, "scaleX", 0.f, 1.f))
                .play(ObjectAnimator.ofFloat(view, "scaleY", 0.f, 1.f))
                .start(), 300);
    }

    public void addButton(String text, @ColorInt int color) {
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMarginStart(LocalDisplay.dp2px(20));
        textView.setLayoutParams(layoutParams);
        textView.setText(text);
        int padding = LocalDisplay.dp2px(10);
        textView.setPadding(padding, padding, padding, padding);
        textView.setTextColor(color);
        textView.setTextSize(14);
        int index = buttons.size();
        textView.setOnClickListener(view -> {
            if (onButtonClickListener != null) {
                onButtonClickListener.onClick(SimpleDialog.this, index, text);
            }
        });
        buttons.add(textView);
        _layout.addView(textView);
    }

    public void setButtonColor(int buttonIndex, @ColorInt int color) {
        buttons.get(buttonIndex).setTextColor(color);
    }

    public void setButtonColors(@ColorInt int... colors) {
        for (int i = 0; i < colors.length; i++) {
            if (buttons.size() > i) {
                buttons.get(i).setTextColor(colors[i]);
            }
        }
    }

    public void setTitle(String title) {
        _title.setText(title);
        _title.setVisibility(title.isEmpty() ? View.GONE : View.VISIBLE);
    }

    public SimpleDialog setContent(String content) {
        _content.setText(content);
        _content.setVisibility(content.isEmpty() ? View.GONE : View.VISIBLE);
        return this;
    }

    public void setContentColor(int color) {
        _content.setTextColor(color);
    }

    public void setTitleGravity(int gri) {
        _title.setGravity(gri);
    }

    public void setTitleTextSize(int size) {
        _title.setTextSize(size);
    }

    public void setContentTextSize(int size) {
        _content.setTextSize(size);
    }

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
    }

    public interface OnButtonClickListener {
        void onClick(SimpleDialog self, int buttonIndex, String buttonString);
    }

    public interface OnConfirmListener {
        void onConfirm(SimpleDialog self);
    }

    //------------------------------
    public static SimpleDialog getCancelAndConfirmDialog(Context context, OnConfirmListener confirmListener) {
        SimpleDialog dialog = new SimpleDialog(context,
                context.getString(R.string.cancel),
                context.getString(R.string.confirm));
        dialog.setButtonColor(0, ContextCompat.getColor(context, R.color.dialog_button_cancel_color));
        dialog.setOnButtonClickListener((self, buttonIndex, buttonString) -> {
            switch (buttonIndex) {
                case 0://取消
                    self.dismiss();
                    break;
                case 1://确认
                    if (confirmListener != null) {
                        confirmListener.onConfirm(self);
                    }
                    break;
            }
        });
        return dialog;
    }

    public static SimpleDialog getConfirmAndCancelDialog(Context context, OnConfirmListener confirmListener) {
        SimpleDialog dialog = new SimpleDialog(context,
                context.getString(R.string.confirm),
                context.getString(R.string.cancel));
        dialog.setButtonColor(1, ContextCompat.getColor(context, R.color.dialog_button_cancel_color));
        dialog.setOnButtonClickListener((self, buttonIndex, buttonString) -> {
            switch (buttonIndex) {
                case 0://确认
                    if (confirmListener != null) {
                        confirmListener.onConfirm(self);
                    }
                    break;
                case 1://取消
                    self.dismiss();
                    break;
            }
        });
        return dialog;
    }

    public static SimpleDialog getOkayDialog(Context context) {
        SimpleDialog dialog = new SimpleDialog(context, context.getString(R.string.okay));
        dialog.setOnButtonClickListener((self, buttonIndex, buttonString) -> {
            self.dismiss();
        });
        return dialog;
    }

    public static SimpleDialog getNetWorkProblemDialog(Context context) {
        SimpleDialog dialog = new SimpleDialog(context, context.getString(R.string.confirm));
        dialog.setOnButtonClickListener((self, buttonIndex, buttonString) -> {
            self.dismiss();
        });
        dialog.setContent(context.getString(R.string.error_network_2));
        return dialog;
    }

    public static SimpleDialog getConfirmDialog(Context context) {
        SimpleDialog dialog = new SimpleDialog(context, context.getString(R.string.confirm));
        dialog.setOnButtonClickListener((self, buttonIndex, buttonString) -> {
            self.dismiss();
        });
        return dialog;
    }

    public static SimpleDialog getOneButtonDialog(Context context, String buttonContent, OnConfirmListener confirmListener) {
        SimpleDialog dialog = new SimpleDialog(context, buttonContent);
        dialog.setOnButtonClickListener((self, buttonIndex, buttonString) -> {
            if (confirmListener != null) {
                confirmListener.onConfirm(self);
            } else {
                self.dismiss();
            }
        });
        return dialog;
    }
}
