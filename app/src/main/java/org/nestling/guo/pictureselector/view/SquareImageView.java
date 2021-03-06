package org.nestling.guo.pictureselector.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by guo on 2016/8/22.
 */
public class SquareImageView extends ImageView{

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //int wd = getMeasuredWidth() > getMeasuredHeight() ? getMeasuredHeight() : getMeasuredWidth();
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}
