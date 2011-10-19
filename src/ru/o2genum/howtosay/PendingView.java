package ru.o2genum.howtosay;

/**
 * Progress indicator view
 *
 * @author Andrey Moiseev
 */

import android.content.Context;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.util.AttributeSet;

public class PendingView extends ImageView {

    public PendingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setImageResource(R.drawable.preloader);
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.rotation);
        startAnimation(animation);
    }
}
