package com.aisling.digitalcollections;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import static com.aisling.digitalcollections.R.color.text;

/**
 * Created by Ais on 19/03/2017.
 */

public class CollectionsTextView extends TextView {

    public CollectionsTextView(Context context)
    {
        super(context);
        applyFont(context);
        applyColour();
    }

    public CollectionsTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyFont(context);
        applyColour();
    }

    public CollectionsTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyFont(context);
        applyColour();
    }

    private void applyFont(Context context) {
        Typeface customFont = FontCache.getTypeface("OpenSans-Regular.ttf", context);
        setTypeface(customFont);
    }

    private void applyColour(){
        setTextColor(getResources().getColor(text));
    }


}
