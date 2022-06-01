package org.techtown.cap3;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {


    private BluetoothAdapter BTAdapter;

    private final static int REQUEST_ENABLE_BT = 1;

    private ListView listView;
    private Set<BluetoothDevice> pairedDevices;
    private ArrayAdapter<String> btArrayAdapter;
    private ArrayList<String> deviceAddressArray;
    private BluetoothSocket btSocket = null;
    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //final static UUID ESP32_cam_UUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    //https://blog.naver.com/oh4zzang/40111957130 UUID 참고용
    //현재 소스코드는 아두이노 - 스마트폰의 연결을 위해 작성된것.
    //만약 스마트폰 - 스마트폰 블루투스 연결을 하려면
    //uuid값은 8CE255C0-200A-11E0-AC64-0800200C9A66 으로 변경해야됨.
    private ConnectedThread mConnectedThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_pair);

        listView = (ListView) findViewById(R.id.listview);
        btArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        deviceAddressArray = new ArrayList<>();
        listView.setAdapter(btArrayAdapter);

        listView.setOnItemClickListener(new myOnItemClickListner());


        //Manifest에서 설정한 권한을 요청하기

        String[] permission_list = {
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        ActivityCompat.requestPermissions(BluetoothActivity.this, permission_list, 1);

        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!BTAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

    }

    public void onClickButtonPaired(View view) {
        btArrayAdapter.clear();
        if (deviceAddressArray != null && !deviceAddressArray.isEmpty()) {
            deviceAddressArray.clear();
        }
        pairedDevices = BTAdapter.getBondedDevices();//페어링된 기존기기들을 불러오는 함수
        if (BTAdapter.isEnabled()) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                btArrayAdapter.add(deviceName);
                deviceAddressArray.add(deviceHardwareAddress);
            }

        }
    }

    public void onClickButtonSearch(View view) {
        btArrayAdapter.clear();
        if (BTAdapter.isDiscovering())//주변의 기기를 찾는 함수
        {
            BTAdapter.cancelDiscovery();
            Toast.makeText(getApplicationContext(), "Discovery stopped", Toast.LENGTH_SHORT).show();
        } else {
            if (BTAdapter.enable()) {
                BTAdapter.startDiscovery();
                Toast.makeText(getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();
                registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
                Toast.makeText(getApplicationContext(), "블루투스 검색 완료", Toast.LENGTH_SHORT).show();
            }
        }
        /*if(BTAdapter.isDiscovering()){
            BTAdapter.cancelDiscovery();
        }else{
            if(BTAdapter.isEnabled()){
                btArrayAdapter.clear();
                BTAdapter.startDiscovery();

                if(deviceAddressArray != null && !deviceAddressArray.isEmpty()){
                    deviceAddressArray.clear();
                }
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(receiver, filter);
                Toast.makeText(getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), "bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }*/
    }

    public void onClickButtonConnectWiFi(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        /*final EditText ID_text = new EditText(this);
        final EditText PW_text = new EditText(this);

        builder.setView(ID_text);
        builder.setView(PW_text);
        builder.setTitle("WiFi");

        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "확인" , Toast.LENGTH_SHORT).show();
                    }
                });
        builder.setNeutralButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "취소" , Toast.LENGTH_SHORT).show();
                    }
                });*/
        LayoutInflater inflater = getLayoutInflater();
        View view1 = inflater.inflate(R.layout.dialog_login, null);
        builder.setView(view1);
        final Button submit = (Button) view1.findViewById(R.id.buttonSubmit);
        final EditText ID = (EditText) view1.findViewById(R.id.edittextID);
        final EditText PW = (EditText) view1.findViewById(R.id.edittextPassword);

        final AlertDialog dialog = builder.create();

        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String strID = ID.getText().toString();
                String strPW = PW.getText().toString();
                Toast.makeText(getApplicationContext(), strID + "/" + strPW, Toast.LENGTH_LONG).show();

                if (mConnectedThread != null) {
                    mConnectedThread.write(strID + "\n");
                    mConnectedThread.write(strPW + "\n");
                }

                dialog.dismiss();
            }
        });


        builder.show();

    }

    final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                if (deviceName != null) {
                    btArrayAdapter.add(deviceName);

                    deviceAddressArray.add(deviceHardwareAddress);
                }
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);

    }

    public class myOnItemClickListner implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?>parent, View view, int position, long id){

            final String name = btArrayAdapter.getItem(position);
            final String address = deviceAddressArray.get(position);
            boolean flag = true;

            BluetoothDevice device = BTAdapter.getRemoteDevice(address);

            try{
                btSocket = device.createRfcommSocketToServiceRecord(BT_UUID);
                Toast.makeText(getBaseContext(), "소켓 연결 시도", Toast.LENGTH_SHORT).show();
                btSocket.connect();

            } catch (IOException e) {
                flag = false;
                Toast.makeText(getBaseContext(), "소켓 생성 실패", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            if(flag)
            {
                Toast.makeText(getBaseContext(), "연결합니다.", Toast.LENGTH_SHORT).show();
                mConnectedThread = new ConnectedThread(btSocket);
                mConnectedThread.start();
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        buffer = new byte[1024];
                        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }

    }
}
