package net.rabraffe.lazyalarmclock.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Neo on 2015/11/9 0009.
 */
public class AlarmClock extends View {
    Context context;
    int width, height;
    Paint paint = new Paint();

    public AlarmClock(Context context) {
        super(context);
        this.context = context;
    }

    public AlarmClock(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setARGB(255, 255, 0, 0);
        paint.setStrokeWidth(1.0f);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        if (Build.VERSION.SDK_INT > 20)
            canvas.drawOval(0.0f, 0.0f, 100, 100, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = widthMeasureSpec;
        height = heightMeasureSpec;
    }
}
