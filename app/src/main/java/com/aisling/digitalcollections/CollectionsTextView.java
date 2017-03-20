package com.aisling.digitalcollections;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Ais on 19/03/2017.
 */

public class CollectionsTextView extends TextView {

    public CollectionsTextView(Context context)
    {
        super(context);
        applyFont(context);
    }

    public CollectionsTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyFont(context);
    }

    public CollectionsTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyFont(context);
    }

    private void applyFont(Context context) {
        Typeface customFont = FontCache.getTypeface("OpenSans-Regular.ttf", context);
        setTypeface(customFont);
    }
}
