package com.qualcomm.robotcore.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import java.util.HashMap;

public class MapView extends View {
    MapView mv;
    private int width;
    private int height;
    private int lineIntervalX;
    private int lineIntervalY;
    private Paint linePaint;
    private Canvas canvas;
    private Bitmap bitmap;
    private boolean isSetup = false;
    private boolean isVisible = false;
    private int nextId = 1;
    private float scalerX;
    private float scalerY;
    private int robotX;
    private int robotY;
    private int robotAngle;
    private boolean robotExists = false;
    private HashMap<Integer, Marker> markers;

    private Bitmap robotIcon;

    private class Marker {
        public int id;
        public int x;
        public int y;
        public int resource;
        public boolean isCircle;

        public Marker(int id, int x, int y, int resource, boolean isCircle) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.resource = resource;
            this.isCircle = isCircle;
        }
    }

    protected void onSizeChanged(int x, int y, int oldx, int oldy) {
        this.scalerX = ((float) getWidth()) / ((float) this.width);
        this.scalerY = ((float) getHeight()) / ((float) this.height);
        this.isVisible = true;
        redraw();
        Log.e("MapView", "Size changed");
    }

    public MapView(Context context) {
        super(context);
        this.isVisible = false;
        init();
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isVisible = false;
        init();
    }

    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        this.linePaint = new Paint();
        this.linePaint.setColor(Color.BLACK);
        this.linePaint.setStrokeWidth(1);
        this.linePaint.setAntiAlias(true);
        this.mv = this;
        this.markers = new HashMap<Integer, Marker>();
    }

    private int makeEven(int i) {
        return i % 2 == 0 ? i : i + 1;
    }

    public void setup(int xMax, int yMax, int numLinesX, int numLinesY, Bitmap robotIcon) {
        this.width = 2 * xMax;
        this.height = 2 * yMax;
        this.lineIntervalX = this.width / makeEven(numLinesX);
        this.lineIntervalY = this.height / makeEven(numLinesY);
        this.robotIcon = robotIcon;
        this.isSetup = true;
    }

    private void drawGrid() {
        this.bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
        this.canvas = new Canvas(this.bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        this.canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);

        for (int index = 0; index < height; index += lineIntervalY) {
            float position = scalerY * index;
            canvas.drawLine(0, position, canvas.getWidth(), position, linePaint);
        }

        for (int index = 0; index < width; index += lineIntervalX) {
            float position = scalerX * index;
            canvas.drawLine(position, 0, position, canvas.getHeight(), linePaint);
        }
    }

    private float scaleX(int x) {
        return ((float) (getWidth() / 2)) + (x * this.scalerX);
    }

    private float scaleY(int y) {
        return ((float) (getHeight() / 2)) - (y * this.scalerY);
    }

    private int scaleAngle(int angle) {
        return 360 - angle;
    }

    public void setRobotLocation(int x, int y, int angle) {
        this.robotX = -x;
        this.robotY = y;
        this.robotAngle = angle;
        this.robotExists = true;
    }

    public int addMarker(int x, int y, int color) {
        int id = this.nextId++;
        Marker marker = new Marker(id, -x, y, color, true);
        this.markers.put(id, marker);
        return id;
    }

    public boolean removeMarker(int id) {
        return (this.markers.remove(id) != null);
    }

    public int addDrawable(int x, int y, int resource) {
        int id = this.nextId++;
        Marker drawable = new Marker(id, -x, y, resource, false);
        this.markers.put(id, drawable);
        return id;
    }

    private void drawMarkers() {
        for (Marker marker : this.markers.values()) {
            float x = scaleX(marker.x);
            float y = scaleY(marker.y);
            if (marker.isCircle) {
                Paint paint = new Paint();
                paint.setColor(marker.resource);
                this.canvas.drawCircle(x, y, 5, paint);
            } else {
                Bitmap decodeResource = BitmapFactory.decodeResource(getResources(), marker.resource);
                this.canvas.drawBitmap(decodeResource, x - ((float) (decodeResource.getWidth() / 2)), y - ((float) (decodeResource.getHeight() / 2)), new Paint());
            }
        }
    }

    private void drawRobot() {
        float x = scaleX(this.robotX);
        float y = scaleY(this.robotY);
        int angle = scaleAngle(this.robotAngle);
        Matrix matrix = new Matrix();
        matrix.postRotate((float) angle);
        matrix.postScale(0.2f, 0.2f);
        Bitmap bitmap = this.robotIcon;
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        this.canvas.drawBitmap(bitmap, x - ((float) (bitmap.getWidth() / 2)), y - ((float) (bitmap.getHeight() / 2)), new Paint());
    }

    public void redraw() {
        if (this.isSetup && this.isVisible) {
            drawGrid();
            drawMarkers();
            if (this.robotExists) {
                drawRobot();
            }
        }
        BitmapDrawable drawable = new BitmapDrawable(getResources(), this.bitmap);
        this.mv.setBackgroundDrawable(drawable);
    }
}
