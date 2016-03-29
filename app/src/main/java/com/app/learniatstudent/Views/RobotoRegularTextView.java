package com.app.learniatstudent.Views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.app.learniatstudent.Constants.SLConstants;

/**
 * Created by macbookpro on 23/02/16.
 */
public class RobotoRegularTextView extends TextView {

    public RobotoRegularTextView(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public RobotoRegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public RobotoRegularTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface(SLConstants.kFontRobotoRegular, context);
        setTypeface(customFont);
    }
}