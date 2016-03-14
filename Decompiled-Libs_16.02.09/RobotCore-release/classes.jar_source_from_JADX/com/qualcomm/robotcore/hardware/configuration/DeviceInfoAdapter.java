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
    private Map<SerialNumber, ControllerConfiguration> f257a;
    private SerialNumber[] f258b;
    private Context f259c;
    private int f260d;
    private int f261e;

    public DeviceInfoAdapter(Activity context, int list_id, Map<SerialNumber, ControllerConfiguration> deviceControllers) {
        this.f257a = new HashMap();
        this.f259c = context;
        this.f257a = deviceControllers;
        this.f258b = (SerialNumber[]) deviceControllers.keySet().toArray(new SerialNumber[deviceControllers.size()]);
        this.f260d = list_id;
        this.f261e = this.f261e;
    }

    public int getCount() {
        return this.f257a.size();
    }

    public Object getItem(int arg0) {
        return this.f257a.get(this.f258b[arg0]);
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(int pos, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) this.f259c).getLayoutInflater().inflate(this.f260d, parent, false);
        }
        ((TextView) convertView.findViewById(16908309)).setText(this.f258b[pos].toString());
        ((TextView) convertView.findViewById(16908308)).setText(((ControllerConfiguration) this.f257a.get(this.f258b[pos])).getName());
        return convertView;
    }
}
