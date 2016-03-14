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
    MapView f391a;
    private int f392b;
    private int f393c;
    private int f394d;
    private int f395e;
    private Paint f396f;
    private Canvas f397g;
    private Bitmap f398h;
    private boolean f399i;
    private boolean f400j;
    private int f401k;
    private float f402l;
    private float f403m;
    private BitmapDrawable f404n;
    private int f405o;
    private int f406p;
    private int f407q;
    private boolean f408r;
    private HashMap<Integer, C0050a> f409s;
    private Bitmap f410t;

    /* renamed from: com.qualcomm.robotcore.util.MapView.a */
    private class C0050a {
        public int f385a;
        public int f386b;
        public int f387c;
        public int f388d;
        public boolean f389e;
        final /* synthetic */ MapView f390f;

        public C0050a(MapView mapView, int i, int i2, int i3, int i4, boolean z) {
            this.f390f = mapView;
            this.f385a = i;
            this.f386b = i2;
            this.f387c = i3;
            this.f388d = i4;
            this.f389e = z;
        }
    }

    protected void onSizeChanged(int x, int y, int oldx, int oldy) {
        this.f402l = ((float) getWidth()) / ((float) this.f392b);
        this.f403m = ((float) getHeight()) / ((float) this.f393c);
        this.f400j = true;
        redraw();
        Log.e("MapView", "Size changed");
    }

    public MapView(Context context) {
        super(context);
        this.f399i = false;
        this.f400j = false;
        this.f401k = 1;
        this.f408r = false;
        m222a();
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.f399i = false;
        this.f400j = false;
        this.f401k = 1;
        this.f408r = false;
        m222a();
    }

    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.f399i = false;
        this.f400j = false;
        this.f401k = 1;
        this.f408r = false;
        m222a();
    }

    private void m222a() {
        this.f396f = new Paint();
        this.f396f.setColor(-16777216);
        this.f396f.setStrokeWidth(Dimmer.MAXIMUM_BRIGHTNESS);
        this.f396f.setAntiAlias(true);
        this.f391a = this;
        this.f409s = new HashMap();
    }

    private int m221a(int i) {
        return i % 2 == 0 ? i : i + 1;
    }

    public void setup(int xMax, int yMax, int numLinesX, int numLinesY, Bitmap robotIcon) {
        this.f392b = xMax * 2;
        this.f393c = yMax * 2;
        this.f394d = this.f392b / m221a(numLinesX);
        this.f395e = this.f393c / m221a(numLinesY);
        this.f410t = robotIcon;
        this.f399i = true;
    }

    private void m224b() {
        this.f398h = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
        this.f397g = new Canvas(this.f398h);
        Paint paint = new Paint();
        paint.setColor(-1);
        paint.setAntiAlias(true);
        this.f397g.drawRect(0.0f, 0.0f, (float) this.f397g.getWidth(), (float) this.f397g.getHeight(), paint);
        int i = 0;
        while (i < this.f393c) {
            float f = this.f403m * ((float) i);
            this.f397g.drawLine(0.0f, f, (float) this.f397g.getWidth(), f, this.f396f);
            i = this.f395e + i;
        }
        int i2 = 0;
        while (i2 < this.f392b) {
            float f2 = this.f402l * ((float) i2);
            this.f397g.drawLine(f2, 0.0f, f2, (float) this.f397g.getHeight(), this.f396f);
            i2 += this.f394d;
        }
    }

    private float m223b(int i) {
        return (((float) i) * this.f402l) + ((float) (getWidth() / 2));
    }

    private float m225c(int i) {
        return ((float) (getHeight() / 2)) - (((float) i) * this.f403m);
    }

    private int m227d(int i) {
        return 360 - i;
    }

    public void setRobotLocation(int x, int y, int angle) {
        this.f405o = -x;
        this.f406p = y;
        this.f407q = angle;
        this.f408r = true;
    }

    public int addMarker(int x, int y, int color) {
        int i = this.f401k;
        this.f401k = i + 1;
        this.f409s.put(Integer.valueOf(i), new C0050a(this, i, -x, y, color, true));
        return i;
    }

    public boolean removeMarker(int id) {
        if (this.f409s.remove(Integer.valueOf(id)) == null) {
            return false;
        }
        return true;
    }

    public int addDrawable(int x, int y, int resource) {
        int i = this.f401k;
        this.f401k = i + 1;
        this.f409s.put(Integer.valueOf(i), new C0050a(this, i, -x, y, resource, false));
        return i;
    }

    private void m226c() {
        for (C0050a c0050a : this.f409s.values()) {
            float b = m223b(c0050a.f386b);
            float c = m225c(c0050a.f387c);
            if (c0050a.f389e) {
                Paint paint = new Paint();
                paint.setColor(c0050a.f388d);
                this.f397g.drawCircle(b, c, 5.0f, paint);
            } else {
                Bitmap decodeResource = BitmapFactory.decodeResource(getResources(), c0050a.f388d);
                this.f397g.drawBitmap(decodeResource, b - ((float) (decodeResource.getWidth() / 2)), c - ((float) (decodeResource.getHeight() / 2)), new Paint());
            }
        }
    }

    private void m228d() {
        float b = m223b(this.f405o);
        float c = m225c(this.f406p);
        int d = m227d(this.f407q);
        Matrix matrix = new Matrix();
        matrix.postRotate((float) d);
        matrix.postScale(0.2f, 0.2f);
        Bitmap bitmap = this.f410t;
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        this.f397g.drawBitmap(bitmap, b - ((float) (bitmap.getWidth() / 2)), c - ((float) (bitmap.getHeight() / 2)), new Paint());
    }

    public void redraw() {
        if (this.f399i && this.f400j) {
            m224b();
            m226c();
            if (this.f408r) {
                m228d();
            }
        }
        this.f404n = new BitmapDrawable(getResources(), this.f398h);
        this.f391a.setBackgroundDrawable(this.f404n);
    }
}
