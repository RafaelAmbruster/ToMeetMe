package com.app.tomeetme.view.tagview.drawers;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

import com.app.tomeetme.view.tagview.TagView;
import com.app.tomeetme.view.tagview.Tags;
import com.app.tomeetme.view.tagview.Utils;

public class SharpTagDrawer implements TagDrawer {
    @Override
    public void drawTag(Rect bounds, Canvas canvas, TagView.TagViewData data) {
        RectF rect = Utils.toRectF(bounds);
        rect.right -= data.tagRightPadding * Tags.SHARP_TAG_MULTIPLIER;
        float halfOfRectHeight = (rect.bottom - rect.top) / 2;
        canvas.drawRect(rect, data.backgroundPaint);
        Path trianglePath = createTrianglePath(data, rect, halfOfRectHeight);
        canvas.drawPath(trianglePath, data.trianglePaint);
    }

    /**
     * Returns filled colored triangle that is used for drawing "sharp" tags
     */
    protected Path createTrianglePath(TagView.TagViewData data, RectF rect, float halfOfRectHeight) {
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(rect.right, rect.top);
        path.lineTo(rect.right + data.tagRightPadding * Tags.SHARP_TAG_MULTIPLIER, halfOfRectHeight);
        path.lineTo(rect.right, rect.bottom);
        path.lineTo(rect.right, rect.top);
        return path;
    }
}
