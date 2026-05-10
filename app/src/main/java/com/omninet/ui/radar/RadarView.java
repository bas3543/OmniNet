package com.omninet.ui.radar;

import android.content.Context;
import android.graphics.*;
import android.view.View;

public class RadarView extends View {

    private final Paint ringPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint sweepPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dotPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint centerPaint= new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint linePaint  = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float sweepAngle = 0f;
    private final android.os.Handler handler =
        new android.os.Handler(android.os.Looper.getMainLooper());

    // Düğümler: [angle, radius_ratio, color, label]
    private final Object[][] nodes = {
        {45f,  0.35f, 0xFF2EA043, "@kerem"},
        {130f, 0.32f, 0xFF2EA043, "@merve"},
        {220f, 0.62f, 0xFF58A6FF, "@ugur"},
        {300f, 0.58f, 0xFF58A6FF, "@selin"},
        {10f,  0.28f, 0xFFF85149, "@ali🌐"},
        {170f, 0.75f, 0xFFD29922, "@?4F2A"},
    };

    public RadarView(Context context) {
        super(context);
        setBackgroundColor(0xFF161B22);

        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setColor(0x3038A43B);
        ringPaint.setStrokeWidth(2f);

        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(0x1838A43B);
        linePaint.setStrokeWidth(1f);

        textPaint.setColor(0xFFC9D1D9);
        textPaint.setTextSize(28f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        centerPaint.setColor(0xFF2EA043);
        centerPaint.setStyle(Paint.Style.FILL);

        startAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth(), h = getHeight();
        int cx = w / 2, cy = h / 2;
        int r = Math.min(cx, cy) - 20;

        canvas.drawColor(0xFF0D1117);

        // Halkalar
        for (int i = 1; i <= 3; i++) {
            canvas.drawCircle(cx, cy, r * i / 3f, ringPaint);
        }

        // Çapraz çizgiler
        canvas.drawLine(cx, cy - r, cx, cy + r, linePaint);
        canvas.drawLine(cx - r, cy, cx + r, cy, linePaint);
        canvas.drawLine(cx - r * 0.7f, cy - r * 0.7f,
                        cx + r * 0.7f, cy + r * 0.7f, linePaint);
        canvas.drawLine(cx + r * 0.7f, cy - r * 0.7f,
                        cx - r * 0.7f, cy + r * 0.7f, linePaint);

        // Yeşil süpürme
        canvas.save();
        canvas.translate(cx, cy);
        canvas.rotate(sweepAngle);
        sweepPaint.setShader(new SweepGradient(0, 0,
            new int[]{0x0038A43B, 0x7038A43B, 0x0038A43B},
            new float[]{0f, 0.12f, 0.25f}));
        canvas.drawCircle(0, 0, r, sweepPaint);
        canvas.restore();

        // Merkez nokta
        canvas.drawCircle(cx, cy, 14f, centerPaint);
        textPaint.setTextSize(22f);
        textPaint.setColor(0xFF0D1117);
        canvas.drawText("◉", cx, cy + 8f, textPaint);

        // Düğümler
        dotPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(0xFFC9D1D9);
        textPaint.setTextSize(26f);

        for (Object[] node : nodes) {
            float angle   = (float) node[0];
            float ratio   = (float) node[1];
            int   color   = (int)   node[2];
            String label  = (String) node[3];

            float rad = (float) Math.toRadians(angle);
            float nx = cx + r * ratio * (float) Math.cos(rad);
            float ny = cy + r * ratio * (float) Math.sin(rad);

            dotPaint.setColor(color);
            canvas.drawCircle(nx, ny, 14f, dotPaint);

            // Halka
            dotPaint.setStyle(Paint.Style.STROKE);
            dotPaint.setStrokeWidth(2f);
            dotPaint.setAlpha(100);
            canvas.drawCircle(nx, ny, 22f, dotPaint);
            dotPaint.setStyle(Paint.Style.FILL);
            dotPaint.setAlpha(255);

            canvas.drawText(label, nx, ny + 40f, textPaint);
        }

        // "Ben" yazısı
        textPaint.setColor(0xFF2EA043);
        textPaint.setTextSize(24f);
        canvas.drawText("Ben", cx, cy + 36f, textPaint);
    }

    private void startAnimation() {
        handler.post(new Runnable() {
            @Override public void run() {
                sweepAngle = (sweepAngle + 2f) % 360f;
                invalidate();
                handler.postDelayed(this, 16);
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
    }
}
