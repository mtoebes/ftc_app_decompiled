package com.qualcomm.robotcore.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import java.util.HashMap;

public class MapView extends View {
    MapView f387a;
    private int f388b;
    private int f389c;
    private int f390d;
    private int f391e;
    private Paint f392f;
    private Canvas f393g;
    private Bitmap f394h;
    private boolean f395i;
    private boolean f396j;
    private int f397k;
    private float f398l;
    private float f399m;
    private BitmapDrawable f400n;
    private int f401o;
    private int f402p;
    private int f403q;
    private boolean f404r;
    private HashMap<Integer, C0055a> f405s;
    private Bitmap f406t;

    /* renamed from: com.qualcomm.robotcore.util.MapView.a */
    private class C0055a {
        public int f381a;
        public int f382b;
        public int f383c;
        public int f384d;
        public boolean f385e;
        final /* synthetic */ MapView f386f;

        public C0055a(MapView mapView, int i, int i2, int i3, int i4, boolean z) {
            this.f386f = mapView;
            this.f381a = i;
            this.f382b = i2;
            this.f383c = i3;
            this.f384d = i4;
            this.f385e = z;
        }
    }

    protected void onSizeChanged(int x, int y, int oldx, int oldy) {
        this.f398l = ((float) getWidth()) / ((float) this.f388b);
        this.f399m = ((float) getHeight()) / ((float) this.f389c);
        this.f396j = true;
        redraw();
        Log.e("MapView", "Size changed");
    }

    public MapView(Context context) {
        super(context);
        this.f395i = false;
        this.f396j = false;
        this.f397k = 1;
        this.f404r = false;
        m240a();
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.f395i = false;
        this.f396j = false;
        this.f397k = 1;
        this.f404r = false;
        m240a();
    }

    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.f395i = false;
        this.f396j = false;
        this.f397k = 1;
        this.f404r = false;
        m240a();
    }

    private void m240a() {
        this.f392f = new Paint();
        this.f392f.setColor(-16777216);
        this.f392f.setStrokeWidth(Dimmer.MAXIMUM_BRIGHTNESS);
        this.f392f.setAntiAlias(true);
        this.f387a = this;
        this.f405s = new HashMap();
    }

    private int m239a(int i) {
        return i % 2 == 0 ? i : i + 1;
    }

    public void setup(int xMax, int yMax, int numLinesX, int numLinesY, Bitmap robotIcon) {
        this.f388b = xMax * 2;
        this.f389c = yMax * 2;
        this.f390d = this.f388b / m239a(numLinesX);
        this.f391e = this.f389c / m239a(numLinesY);
        this.f406t = robotIcon;
        this.f395i = true;
    }

    private void m242b() {
        this.f394h = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
        this.f393g = new Canvas(this.f394h);
        Paint paint = new Paint();
        paint.setColor(-1);
        paint.setAntiAlias(true);
        this.f393g.drawRect(0.0f, 0.0f, (float) this.f393g.getWidth(), (float) this.f393g.getHeight(), paint);
        int i = 0;
        while (i < this.f389c) {
            float f = this.f399m * ((float) i);
            this.f393g.drawLine(0.0f, f, (float) this.f393g.getWidth(), f, this.f392f);
            i = this.f391e + i;
        }
        int i2 = 0;
        while (i2 < this.f388b) {
            float f2 = this.f398l * ((float) i2);
            this.f393g.drawLine(f2, 0.0f, f2, (float) this.f393g.getHeight(), this.f392f);
            i2 += this.f390d;
        }
    }

    private float m241b(int i) {
        return (((float) i) * this.f398l) + ((float) (getWidth() / 2));
    }

    private float m243c(int i) {
        return ((float) (getHeight() / 2)) - (((float) i) * this.f399m);
    }

    private int m245d(int i) {
        return 360 - i;
    }

    public void setRobotLocation(int x, int y, int angle) {
        this.f401o = -x;
        this.f402p = y;
        this.f403q = angle;
        this.f404r = true;
    }

    public int addMarker(int x, int y, int color) {
        int i = this.f397k;
        this.f397k = i + 1;
        this.f405s.put(Integer.valueOf(i), new C0055a(this, i, -x, y, color, true));
        return i;
    }

    public boolean removeMarker(int id) {
        if (this.f405s.remove(Integer.valueOf(id)) == null) {
            return false;
        }
        return true;
    }

    public int addDrawable(int x, int y, int resource) {
        int i = this.f397k;
        this.f397k = i + 1;
        this.f405s.put(Integer.valueOf(i), new C0055a(this, i, -x, y, resource, false));
        return i;
    }

    private void m244c() {
        for (C0055a c0055a : this.f405s.values()) {
            float b = m241b(c0055a.f382b);
            float c = m243c(c0055a.f383c);
            if (c0055a.f385e) {
                Paint paint = new Paint();
                paint.setColor(c0055a.f384d);
                this.f393g.drawCircle(b, c, 5.0f, paint);
            } else {
                Bitmap decodeResource = BitmapFactory.decodeResource(getResources(), c0055a.f384d);
                this.f393g.drawBitmap(decodeResource, b - ((float) (decodeResource.getWidth() / 2)), c - ((float) (decodeResource.getHeight() / 2)), new Paint());
            }
        }
    }

    private void m246d() {
        float b = m241b(this.f401o);
        float c = m243c(this.f402p);
        int d = m245d(this.f403q);
        Matrix matrix = new Matrix();
        matrix.postRotate((float) d);
        matrix.postScale(0.2f, 0.2f);
        Bitmap bitmap = this.f406t;
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        this.f393g.drawBitmap(bitmap, b - ((float) (bitmap.getWidth() / 2)), c - ((float) (bitmap.getHeight() / 2)), new Paint());
    }

    public void redraw() {
        if (this.f395i && this.f396j) {
            m242b();
            m244c();
            if (this.f404r) {
                m246d();
            }
        }
        this.f400n = new BitmapDrawable(getResources(), this.f394h);
        this.f387a.setBackgroundDrawable(this.f400n);
    }
}
