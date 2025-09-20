package com.quickcopy.fucksociety.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class EmojiIcon {
    public static Bitmap renderEmoji(Context ctx, String emoji, int sizePx) {
        Bitmap bmp = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setTextAlign(Paint.Align.CENTER);
        p.setColor(0xFFFFFFFF);
        // I dont like extrs space...
        p.setTextSize(sizePx * 0.75f);
        Rect r = new Rect();
        p.getTextBounds(emoji, 0, emoji.length(), r);
        float x = sizePx / 2f;
        float y = sizePx / 2f - r.exactCenterY();
        c.drawText(emoji, x, y, p);
        return bmp;
    }
}