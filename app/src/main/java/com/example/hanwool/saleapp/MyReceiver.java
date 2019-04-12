package com.example.hanwool.saleapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.hanwool.saleapp.CheckConnectionManager;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String status = CheckConnectionManager.getConnectivityStatusString(context);
        if(status.isEmpty()) {
            status="No internet is available";
        }
        Toast.makeText(context, status, Toast.LENGTH_LONG).show();
    }
}