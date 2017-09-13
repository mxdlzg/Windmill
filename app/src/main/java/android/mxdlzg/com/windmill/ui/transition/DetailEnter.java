package android.mxdlzg.com.windmill.ui.transition;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.transition.ChangeBounds;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 廷江 on 2017/5/9.
 */

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class DetailEnter extends ChangeBounds {
    private static final String PROPNAME_BOUNDS = "android:changeBounds:bounds";
    private static final String PROPNAME_PARENT = "android:changeBounds:parent";

    public DetailEnter(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        super.captureEndValues(transitionValues);
        int width = ((View)transitionValues.values.get(PROPNAME_PARENT)).getWidth();
        Rect bounds = (Rect) transitionValues.values.get(PROPNAME_BOUNDS);
        bounds.right = width;
        bounds.bottom = width*3/4;
        transitionValues.values.put(PROPNAME_BOUNDS,bounds);
    }
}
