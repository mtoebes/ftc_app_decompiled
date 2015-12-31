/*
 * Copyright (c) 2014, 2015 Qualcomm Technologies Inc
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * (subject to the limitations in the disclaimer below) provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of Qualcomm Technologies Inc nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS LICENSE. THIS
 * SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
import android.util.SparseArray;
import android.view.View;

public class MapView extends View {
    MapView mapView;
    private int width;
    private int height;
    private int lineIntervalX;
    private int lineIntervalY;
    private Paint linePaint;
    private Canvas canvas;
    private Bitmap bitmap;
    private boolean isSetup;
    private boolean isVisible;
    private int nextId;
    private float scalerX;
    private float scalerY;
    private int robotX;
    private int robotY;
    private int robotTh;
    private boolean robotExists;
    private SparseArray<Marker> markers;
    private Bitmap robotIcon;

    private class Marker {
        public int id;
        public int x;
        public int y;
        public int resource;
        public boolean isCircle;
        final MapView mapView;

        public Marker(MapView mapView, int id, int x, int y, int resource, boolean isCircle) {
            this.mapView = mapView;
            this.id = id;
            this.x = x;
            this.y = y;
            this.resource = resource;
            this.isCircle = isCircle;
        }
    }

    protected void onSizeChanged(int x, int y, int oldX, int oldY) {
        this.scalerX = (float) getWidth() / (float) this.width;
        this.scalerY = (float) getHeight() / (float) this.height;
        this.isVisible = true;
        redraw();
        Log.e("MapView", "Size changed");
    }

    public MapView(Context context) {
        super(context);
        this.isSetup = false;
        this.isVisible = false;
        this.nextId = 1;
        this.robotExists = false;
        init();
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isSetup = false;
        this.isVisible = false;
        this.nextId = 1;
        this.robotExists = false;
        init();
    }

    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.isSetup = false;
        this.isVisible = false;
        this.nextId = 1;
        this.robotExists = false;
        init();
    }

    private void init() {
        this.linePaint = new Paint();
        this.linePaint.setColor(Color.BLACK);
        this.linePaint.setStrokeWidth(Dimmer.MAXIMUM_BRIGHTNESS);
        this.linePaint.setAntiAlias(true);
        this.mapView = this;
        this.markers = new SparseArray<Marker>();
    }

    private int toEvenInt(int i) {
        return ((i % 2) == 0) ? i : (i + 1);
    }

    public void setup(int xMax, int yMax, int numLinesX, int numLinesY, Bitmap robotIcon) {
        this.width = xMax * 2;
        this.height = yMax * 2;
        this.lineIntervalX = this.width / toEvenInt(numLinesX);
        this.lineIntervalY = this.height / toEvenInt(numLinesY);
        this.robotIcon = robotIcon;
        this.isSetup = true;
    }

    private void setup() {
        this.bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
        this.canvas = new Canvas(this.bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        this.canvas.drawRect(0.0f, 0.0f, (float) this.canvas.getWidth(), (float) this.canvas.getHeight(), paint);
        int index = 0;
        while (index < this.height) {
            float f = this.scalerY * (float) index;
            this.canvas.drawLine(0.0f, f, (float) this.canvas.getWidth(), f, this.linePaint);
            index = this.lineIntervalY + index;
        }
        index = 0;
        while (index < this.width) {
            float f2 = this.scalerX * (float) index;
            this.canvas.drawLine(f2, 0.0f, f2, (float) this.canvas.getHeight(), this.linePaint);
            index += this.lineIntervalX;
        }
    }

    private float scaleX(int x) {
        return (x * this.scalerX) + (getWidth() / 2.0f);
    }

    private float scaleY(int y) {
        return (getHeight() / 2.0f) - (y * this.scalerY);
    }

    private int scaleTh(int th) {
        return 360 - th;
    }

    public void setRobotLocation(int x, int y, int angle) {
        this.robotX = -x;
        this.robotY = y;
        this.robotTh = angle;
        this.robotExists = true;
    }

    public int addMarker(int x, int y, int color) {
        int id = this.nextId;
        this.nextId = id + 1;
        this.markers.put(id, new Marker(this, id, -x, y, color, true));
        return id;
    }

    public boolean removeMarker(int id) {
        this.markers.removeAt(id);
        return true;
    }

    public int addDrawable(int x, int y, int resource) {
        int id = this.nextId;
        this.nextId = id + 1;
        this.markers.put(id, new Marker(this, id, -x, y, resource, false));
        return id;
    }

    private void drawMarkers() {
        for (int i = 0; i < this.markers.size(); i++) {
            int key = this.markers.keyAt(i);
            Marker marker = this.markers.get(key);

            float x = scaleX(marker.x);
            float y = scaleY(marker.y);
            if (marker.isCircle) {
                Paint paint = new Paint();
                paint.setColor(marker.resource);
                this.canvas.drawCircle(x, y, 5.0f, paint);
            } else {
                Bitmap decodeResource = BitmapFactory.decodeResource(getResources(), marker.resource);
                this.canvas.drawBitmap(decodeResource, x - (float) (decodeResource.getWidth() / 2), y - (float) (decodeResource.getHeight() / 2), new Paint());
            }
        }
    }

    private void drawRobot() {
        float x = scaleX(this.robotX);
        float y = scaleY(this.robotY);
        int th = scaleTh(this.robotTh);
        Matrix matrix = new Matrix();
        matrix.postRotate((float) th);
        matrix.postScale(0.2f, 0.2f);
        Bitmap bitmap = this.robotIcon;
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        this.canvas.drawBitmap(bitmap, x - (float) (bitmap.getWidth() / 2), y - (float) (bitmap.getHeight() / 2), new Paint());
    }

    public void redraw() {
        if (this.isSetup && this.isVisible) {
            setup();
            drawMarkers();
            if (this.robotExists) {
                drawRobot();
            }
        }
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), this.bitmap);
        this.mapView.setBackground(bitmapDrawable);
    }
}
