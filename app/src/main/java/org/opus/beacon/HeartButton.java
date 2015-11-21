package org.opus.beacon;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class HeartButton extends ImageButton {
    public HeartButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        _hearted = false;
    }

    private boolean _hearted;

    public boolean isHearted() { return _hearted; }

    public void heart() {
        _hearted = true;
        setColorFilter(Color.rgb(255, 106, 106));
    }

    public void unheart() {
        _hearted = false;
        clearColorFilter();
    }
}
