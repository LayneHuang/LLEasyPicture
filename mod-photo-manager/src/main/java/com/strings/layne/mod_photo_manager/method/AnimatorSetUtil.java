package com.strings.layne.mod_photo_manager.method;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.text.Spanned;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import java.util.HashMap;
import java.util.HashSet;

/**
 * AnimatorSetUtil
 * Created by FinkyS on 15/9/14.
 */
public class AnimatorSetUtil {

    private OnAnimationEndListener _listener;
    private AnimatorSet _animatorSet;
    private View _touchIsolation;

    public AnimatorSet getAnimatorSet() {
        return _animatorSet;
    }

    public AnimatorSetUtil setDuration(long duration) {
        _animatorSet.setDuration(duration);
        return this;
    }

    public AnimatorSetUtil setTimeInterpolator(TimeInterpolator timeInterpolator) {
        _animatorSet.setInterpolator(timeInterpolator);
        return this;
    }

    public AnimatorSetUtil refresh() {
        if (_animatorSet != null && _animatorSet.isRunning()) {
            _animatorSet.end();
        }
        _animatorSet = new AnimatorSet();
        addListener();
        _touchIsolation = null;
        _listener = null;
        return this;
    }

    private void addListener() {
        _animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (_listener != null) {
                    _listener.onAnimationEnd();
                }
                if (_touchIsolation != null) {
                    _touchIsolation.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                if (_touchIsolation != null) {
                    _touchIsolation.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public AnimatorSetUtil() {
        _animatorSet = new AnimatorSet();
    }


    private ObjectAnimator lastestAnim;

    public AnimatorSetUtil play(ObjectAnimator anim) {
        _animatorSet.play(anim);
        lastestAnim = anim;
        return this;
    }


    public AnimatorSetUtil playWith(ObjectAnimator anim1, ObjectAnimator anim2) {
        _animatorSet.play(anim1).with(anim2);
        lastestAnim = anim1;
        return this;
    }

    public AnimatorSetUtil playWithAfterTheLast(ObjectAnimator anim1, ObjectAnimator anim2) {
        if (lastestAnim == null) {
            _animatorSet.play(anim1).with(anim2);
            lastestAnim = anim1;
        } else {
            _animatorSet.play(anim1).with(anim2).after(lastestAnim);
            lastestAnim = anim1;
        }
        return this;
    }

    public AnimatorSetUtil playWith3AfterTheLast(ObjectAnimator anim1, ObjectAnimator anim2, ObjectAnimator anim3) {
        if (lastestAnim == null) {
            _animatorSet.play(anim1).with(anim2).with(anim3);
            lastestAnim = anim1;
        } else {
            _animatorSet.play(anim1).with(anim2).with(anim3).after(lastestAnim);
            lastestAnim = anim1;
        }
        return this;
    }

    public AnimatorSetUtil playAfterTheLast(ObjectAnimator anim1) {
        if (lastestAnim == null) {
            _animatorSet.play(anim1);
            lastestAnim = anim1;
        } else {
            _animatorSet.play(anim1).after(lastestAnim);
            lastestAnim = anim1;
        }
        return this;
    }

    public void start() {
        if (_touchIsolation != null) {
            _touchIsolation.setVisibility(View.VISIBLE);
        }
        _animatorSet.start();
    }

    public AnimatorSetUtil setTouchIsolation(View touchIsolation) {
        _touchIsolation = touchIsolation;
        return this;
    }

    public void end() {
        _animatorSet.end();
    }

    public AnimatorSetUtil setOnAnimationEndListener(OnAnimationEndListener l) {
        _listener = l;
        return this;
    }

    public interface OnAnimationEndListener {
        void onAnimationEnd();
    }

    public boolean isRunning() {
        return _animatorSet.isRunning();
    }


    /**
     * 常用动画部分
     */
    private static HashSet<View> hidingViews = new HashSet<>();

    public static void alphaShow(View view, long duration) {
        if (view == null) return;
        if (view.getAlpha() == 0.f || view.getVisibility() != View.VISIBLE || (view.getVisibility() == View.VISIBLE && hidingViews.contains(view))) {
            view.setAlpha(0.f);
            view.setVisibility(View.VISIBLE);
            new AnimatorSetUtil().refresh().setDuration(duration)
                    .play(ObjectAnimator.ofFloat(view, "alpha", 1.f))
                    .start();
        }
    }

    public static void alphaHide(final View view, long duration) {
        if (view == null) return;
        view.setAlpha(1.f);
        new AnimatorSetUtil().refresh().setDuration(duration)
                .play(ObjectAnimator.ofFloat(view, "alpha", 0.f))
                .setOnAnimationEndListener(new OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd() {
                        view.setVisibility(View.INVISIBLE);
                    }
                })
                .start();
    }

    public static void alphaHideAlphaOnly(final View view, long duration) {
        if (view == null) return;
        if (view.getAlpha() == 1.f) {
            view.setAlpha(1.f);
            hidingViews.add(view);
            new AnimatorSetUtil().setDuration(duration).refresh()
                    .play(ObjectAnimator.ofFloat(view, "alpha", 0.f))
                    .setOnAnimationEndListener(new OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd() {
                            hidingViews.remove(view);
                        }
                    }).start();
        }
    }

    public static void alphaDismiss(final View view, long duration) {
        if (view == null) return;
        view.setAlpha(1.f);
        new AnimatorSetUtil().setDuration(duration).refresh()
                .play(ObjectAnimator.ofFloat(view, "alpha", 0.f))
                .setOnAnimationEndListener(new OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd() {
                        view.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    public static void alphaChangeText(TextView textView, String text, long duration) {
        if (textView == null) return;
        if (!text.equals(textView.getText().toString())) {
            textView.setText(text);
            textView.setAlpha(0.f);
            new AnimatorSetUtil().setDuration(duration).refresh()
                    .play(ObjectAnimator.ofFloat(textView, "alpha", 1.f))
                    .start();
        }
    }

    public static void alphaChangeText(TextView textView, Spanned text, long duration) {
        if (textView == null) return;
        if (!text.toString().equals(textView.getText().toString())) {
            textView.setText(text);
            textView.setAlpha(0.f);
            new AnimatorSetUtil().setDuration(duration).refresh()
                    .play(ObjectAnimator.ofFloat(textView, "alpha", 1.f))
                    .start();
        }
    }

    /**
     * 放大缩小型抖动（发送yo,表情）
     */
    public static void shake(View v, float scaleMax, float scaleMin, long duration, OnAnimationEndListener endListener) {
        new AnimatorSetUtil().setDuration(duration).refresh()
                .playWith(ObjectAnimator.ofFloat(v, "scaleX", scaleMax).setDuration(duration / 4), ObjectAnimator.ofFloat(v, "scaleY", scaleMax).setDuration(duration / 4))
                .playWithAfterTheLast(ObjectAnimator.ofFloat(v, "scaleX", scaleMin).setDuration(duration / 4), ObjectAnimator.ofFloat(v, "scaleY", scaleMin).setDuration(duration / 4))
                .playWithAfterTheLast(ObjectAnimator.ofFloat(v, "scaleX", 1.f + (scaleMax - 1.f) * 0.6f).setDuration(duration / 4), ObjectAnimator.ofFloat(v, "scaleY", 1.f + (scaleMax - 1.f) * 0.6f).setDuration(duration / 4))
                .playWithAfterTheLast(ObjectAnimator.ofFloat(v, "scaleX", 1.f).setDuration(duration / 4), ObjectAnimator.ofFloat(v, "scaleY", 1.f).setDuration(duration / 4))
                .setOnAnimationEndListener(endListener)
                .start();
    }


    private static HashMap<View, ObjectAnimator> shakingViews = new HashMap<>();

    /**
     * 开始抖动 （照片墙拖拽）
     */
    public static void startShaking(View v) {
        startShaking(v, 2, 180);
    }

    public static void startShaking(View v, int rotation, long duration) {
        if (shakingViews.containsKey(v)) {
            return;
        }
        ObjectAnimator shaking = ObjectAnimator.ofFloat(v, "rotation", -rotation, rotation).setDuration(duration);
        shaking.setRepeatCount(ValueAnimator.INFINITE);
        shaking.setRepeatMode(ValueAnimator.REVERSE);
        shaking.start();
        shakingViews.put(v, shaking);
    }

    /**
     * 停止抖动 （照片墙拖拽）
     */
    public static void endShaking(View v) {
        if (shakingViews.containsKey(v)) {
            v.setRotation(0);
            shakingViews.get(v).end();
            shakingViews.remove(v);
        }
    }


    public static void startFloatingAround(View v, int startY, int endY, long duration) {
//        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, -startY, endY);
//        translateAnimation.setInterpolator(new DecelerateInterpolator());
//        translateAnimation.setRepeatCount(Animation.INFINITE);
//        translateAnimation.setRepeatMode(Animation.REVERSE);
//        translateAnimation.setDuration(duration);
//        v.startAnimation(translateAnimation);
        ObjectAnimator floating = ObjectAnimator.ofFloat(v, "translationX", -startY, endY).setDuration(duration);
        floating.setInterpolator(new DecelerateInterpolator());
        floating.setRepeatCount(ValueAnimator.INFINITE);
        floating.setRepeatMode(ValueAnimator.REVERSE);
        floating.start();
    }

    public static void startFloatingUpDown(View v, int up, int down, long duration) {
//        TranslateAnimation translateAnimation = new TranslateAnimation(-up, down, 0, 0);
//        translateAnimation.setInterpolator(new DecelerateInterpolator());
//        translateAnimation.setRepeatCount(Animation.INFINITE);
//        translateAnimation.setRepeatMode(Animation.REVERSE);
//        translateAnimation.setDuration(duration);
//        v.setAnimation(translateAnimation);
        ObjectAnimator floating = ObjectAnimator.ofFloat(v, "translationY", -up, down).setDuration(duration);
        floating.setInterpolator(new DecelerateInterpolator());
        floating.setRepeatCount(ValueAnimator.INFINITE);
        floating.setRepeatMode(ValueAnimator.REVERSE);
        floating.start();
    }

}
