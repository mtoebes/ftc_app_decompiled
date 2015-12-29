package com.qualcomm.robotcore.hardware.configuration;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.qualcomm.ftcrobotcontroller.R;
import com.qualcomm.robotcore.util.SerialNumber;
import java.util.HashMap;
import java.util.Map;

public class DeviceInfoAdapter extends BaseAdapter implements ListAdapter {
    private Map<SerialNumber, ControllerConfiguration> controllerConfigs;
    private SerialNumber[] serialNumbers;
    private Context context;
    private int list_id;

    public DeviceInfoAdapter(Activity context, int list_id, Map<SerialNumber, ControllerConfiguration> deviceControllers) {
        this.controllerConfigs = new HashMap<SerialNumber, ControllerConfiguration>();
        this.context = context;
        this.controllerConfigs = deviceControllers;
        this.serialNumbers = deviceControllers.keySet().toArray(new SerialNumber[deviceControllers.size()]);
        this.list_id = list_id;
    }

    public int getCount() {
        return this.controllerConfigs.size();
    }

    public Object getItem(int arg0) {
        return this.controllerConfigs.get(this.serialNumbers[arg0]);
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(int pos, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) this.context).getLayoutInflater().inflate(this.list_id, parent, false);
        }
        ((TextView) convertView.findViewById(android.R.id.text2)).setText(this.serialNumbers[pos].toString());
        ((TextView) convertView.findViewById(android.R.id.text1)).setText((this.controllerConfigs.get(this.serialNumbers[pos])).getName());
        return convertView;
    }
}
