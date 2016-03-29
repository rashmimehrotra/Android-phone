package com.app.learniatstudent.Views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.app.learniatstudent.Constants.SLConstants;

/**
 * Created by macbookpro on 23/02/16.
 */
public class RobotoRegularEditText extends EditText {


    private Context context;
    private AttributeSet attrs;
    private int defStyle;

    public RobotoRegularEditText(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public RobotoRegularEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        init();
    }

    public RobotoRegularEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        this.attrs = attrs;
        this.defStyle = defStyle;
        init();
    }

    private void init() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), SLConstants.kFontRobotoRegular);
        this.setTypeface(font);
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        tf = Typeface.createFromAsset(getContext().getAssets(), SLConstants.kFontRobotoRegular);
        super.setTypeface(tf, style);
    }

    @Override
    public void setTypeface(Typeface tf) {
        tf = Typeface.createFromAsset(getContext().getAssets(), SLConstants.kFontRobotoRegular);
        super.setTypeface(tf);
    }
}