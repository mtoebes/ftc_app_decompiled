package com.qualcomm.robotcore.hardware.configuration;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.qualcomm.robotcore.util.SerialNumber;
import java.util.HashMap;
import java.util.Map;

public class DeviceInfoAdapter extends BaseAdapter implements ListAdapter {
    private Map<SerialNumber, ControllerConfiguration> f263a;
    private SerialNumber[] f264b;
    private Context f265c;
    private int f266d;
    private int f267e;

    public DeviceInfoAdapter(Activity context, int list_id, Map<SerialNumber, ControllerConfiguration> deviceControllers) {
        this.f263a = new HashMap();
        this.f265c = context;
        this.f263a = deviceControllers;
        this.f264b = (SerialNumber[]) deviceControllers.keySet().toArray(new SerialNumber[deviceControllers.size()]);
        this.f266d = list_id;
        this.f267e = this.f267e;
    }

    public int getCount() {
        return this.f263a.size();
    }

    public Object getItem(int arg0) {
        return this.f263a.get(this.f264b[arg0]);
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(int pos, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) this.f265c).getLayoutInflater().inflate(this.f266d, parent, false);
        }
        ((TextView) convertView.findViewById(16908309)).setText(this.f264b[pos].toString());
        ((TextView) convertView.findViewById(16908308)).setText(((ControllerConfiguration) this.f263a.get(this.f264b[pos])).getName());
        return convertView;
    }
}
