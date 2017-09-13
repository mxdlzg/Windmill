package android.mxdlzg.com.windmill.ui.transition;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ParallelExecutorCompat;
import android.transition.ChangeBounds;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by 廷江 on 2017/5/9.
 */

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class DetailReturn extends ChangeBounds{
    private static final String PROPNAME_BOUNDS="android:changeBounds:bounds";

    public DetailReturn(Context context, AttributeSet attrs) {
        super(context,attrs);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        super.captureEndValues(transitionValues);
        if (!(transitionValues.view instanceof ImageView)) return;
        ImageView view = (ImageView) transitionValues.view;
        if (view.getTranslationY() == 0){
            return;
        }
        Rect bounds = (Rect) transitionValues.values.get(PROPNAME_BOUNDS);
        bounds.offset(0, (int) view.getTranslationY());
        transitionValues.values.put(PROPNAME_BOUNDS, bounds);
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        Animator changeBounds = super.createAnimator(sceneRoot, startValues, endValues);
        if (startValues == null || endValues == null || !(endValues.view instanceof ImageView)){
            return changeBounds;
        }
        ImageView imageView = (ImageView) endValues.view;
        if (imageView.getTranslationY() == 0){
            return changeBounds;
        }

        Animator animator = ObjectAnimator.ofInt(imageView, new Property<ImageView, Integer>(null,"") {
            @Override
            public Integer get(ImageView object) {
                return (int)object.getTranslationY();
            }

            @Override
            public void set(ImageView object, Integer value) {
                object.setTranslationX((float)value);
            }
        },0);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(changeBounds,animator);
        return animatorSet;
    }
}
