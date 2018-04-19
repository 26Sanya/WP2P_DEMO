package com.example.lenovo.wp2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.wp2p.utils.WifiDirect;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button mConnection , sh;
    private WifiManager manager;
    private WifiP2pManager man;
    private WifiP2pManager.Channel ch;
    BroadcastReceiver receiver;
    IntentFilter intentFilter;
    ListView list;
    List<WifiP2pDevice> peer=new ArrayList<WifiP2pDevice>();
    String devicesname[];
    WifiP2pDevice devices[];
    public TextView status;
    static final int msg_read=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        connectionListener();
    }
  /*  Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch(message.what){
                case msg_read:
                    byte []readbuff= (byte[]) message.obj;
                    String tempmsg=new String(readbuff,0,message.arg1);
                    msg_box.setText(tempmsg);
                    break;
            }
            return true;
        }
    });*/
    private void init() {

        /**
         * Widgets Initialization
         */
        mConnection = (Button) findViewById(R.id.connect);
        sh = (Button) findViewById(R.id.share);
        status=(TextView)findViewById(R.id.text);


        /**
         * Wifi Manager Initialization
         */
        manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        man = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);

        ch = man.initialize(this, getMainLooper(), null);

        list= (ListView) findViewById(R.id.peerListView);
        receiver = new WifiDirect(man, ch, this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);


        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);


        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    private void connectionListener() {
        sh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (manager.isWifiEnabled()) {
                    manager.setWifiEnabled(false);
                    sh.setText("Start");
                    status.setText("");
                } else {
                    manager.setWifiEnabled(true);
                    sh.setText("Stop");

                }
            }
        });

        mConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                man.discoverPeers(ch, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {

                        status.setText("Discovery Started");
                    }

                    @Override
                    public void onFailure(int i) {
                        status.setText("Discovery Starting Failed");
                    }
                });
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               final WifiP2pDevice dev=devices[i];
                WifiP2pConfig config =new WifiP2pConfig();
                config.deviceAddress= dev.deviceAddress;

                man.connect(ch, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(),"Connected to"+dev.deviceName,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i) {
                        Toast.makeText(getApplicationContext(),"Connection Failure",Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }
    public WifiP2pManager.PeerListListener peerlistListener= new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerlist) {
        if(!peerlist.getDeviceList().equals(peer)){
            peer.clear();
            peer.addAll(peerlist.getDeviceList());
            devicesname=new String[peerlist.getDeviceList().size()];
            devices =new WifiP2pDevice[peerlist.getDeviceList().size()];
            int index=0;
            for(WifiP2pDevice device :peerlist.getDeviceList()){
                devicesname[index]=device.deviceName;
                devices[index]=device;
                index++;
            }
            ArrayAdapter<String> adapter= new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,devicesname){
                @Override
                public View getView(int position, View convertView, ViewGroup parent){

                    View view = super.getView(position, convertView, parent);
                    TextView tv = (TextView) view.findViewById(android.R.id.text1);
                    tv.setTextColor(Color.WHITE);
                    return view;
                }
            };

            list.setAdapter(adapter);
        }
            if(peer.size()==0){
                Toast.makeText(getApplicationContext(),"No device found",Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };


    public WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupowner= wifiP2pInfo.groupOwnerAddress;
            if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner){
                status.setText("Host");
            }
            else if(wifiP2pInfo.groupFormed){
                status.setText("Client");
            }
        }
    };
    @Override
    public void onResume () {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause () {
        super.onPause();
        unregisterReceiver(receiver);
    }
 /*   private class SendReceive extends Thread{
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;
        public SendReceive(Socket skt){
            socket=skt;
            try {
                inputStream=socket.getInputStream();
                outputStream=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run(){

        }
    }*/


}

