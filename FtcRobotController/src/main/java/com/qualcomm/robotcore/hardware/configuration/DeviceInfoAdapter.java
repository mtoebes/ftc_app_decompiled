package com.qualcomm.robotcore.hardware.configuration;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.qualcomm.robotcore.util.SerialNumber;

import java.util.Map;

public class DeviceInfoAdapter extends BaseAdapter implements ListAdapter {
    private Map<SerialNumber, ControllerConfiguration> controllers;
    private SerialNumber[] serialNumbers;
    private Context context;
    private int list_id;

    public DeviceInfoAdapter(Activity context, int list_id, Map<SerialNumber, ControllerConfiguration> deviceControllers) {
        this.context = context;
        this.list_id = list_id;
        controllers = deviceControllers;
        serialNumbers = deviceControllers.keySet().toArray(new SerialNumber[deviceControllers.size()]);
    }

    public int getCount() {
        return controllers.size();
    }

    public Object getItem(int arg0) {
        return controllers.get(serialNumbers[arg0]);
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(int pos, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) context).getLayoutInflater().inflate(list_id, parent, false);
        }

        // TODO fix this
        ((TextView) convertView.findViewById(16908309)).setText(serialNumbers[pos].toString());
        ((TextView) convertView.findViewById(16908308)).setText((controllers.get(serialNumbers[pos])).getName());
        return convertView;
    }
}
