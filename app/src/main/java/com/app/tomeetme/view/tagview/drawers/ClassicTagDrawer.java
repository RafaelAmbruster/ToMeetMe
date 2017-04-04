package com.app.tomeetme.view.tagview.drawers;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import com.app.tomeetme.view.tagview.TagView;
import com.app.tomeetme.view.tagview.Utils;

public class ClassicTagDrawer implements TagDrawer {
    @Override
    public void drawTag(Rect bounds, Canvas canvas, TagView.TagViewData data) {
        RectF rect = Utils.toRectF(bounds);
        canvas.drawRoundRect(rect, data.tagBorderRadius, data.tagBorderRadius, data.backgroundPaint);
    }
}
