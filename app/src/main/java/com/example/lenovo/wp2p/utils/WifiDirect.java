package com.example.lenovo.wp2p.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import com.example.lenovo.wp2p.MainActivity;

/**
 * Created by lenovo on 06-04-2018.
 */

public class WifiDirect extends BroadcastReceiver {
    private WifiP2pManager man;
    private WifiP2pManager.Channel ch;
    private MainActivity mactivity;


    public WifiDirect(WifiP2pManager man, WifiP2pManager.Channel ch, MainActivity activity){
        this.ch=ch;
        this.man=man;
        this.mactivity=activity;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Toast.makeText(context,"Wifi is ON",Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(context,"Wifi is OFF",Toast.LENGTH_SHORT).show();

            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                if(man!=null){
                    man.requestPeers(ch,mactivity.peerlistListener);
                }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if(man==null){
                return;
            }
                NetworkInfo networkInfo= intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if(networkInfo.isConnected()){
                man.requestConnectionInfo(ch,mactivity.connectionInfoListener);
            }
            else
            {

                mactivity.status.setText("Device Disconnected");
            }


        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

        }
    }

}
