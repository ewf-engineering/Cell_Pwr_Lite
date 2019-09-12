package com.example.cell_pwr_lite;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;

import android.telephony.CellSignalStrengthLte;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private Button button;
    private Button StartButton;
    private TextView textView;
    private TextView textViewPLMN;
    private TextView textViewPCI;
    private TextView textViewTAC;
    private TextView textViewCI;
    private TextView textViewEARFCN;
    private TextView textViewBW;
    private TextView textViewRSRP;
    private TextView textViewRSRQ;
    private TextView textViewRSSI;
    private TextView textViewPRB;
    private TextView textViewIMSI;
    private TextView textViewIMEI;

    private Boolean isRunning;

    private LocationManager locationManager;
    private LocationListener locationListener;
    //private GnssStatus.Callback gnssListener;
    private Location location;

    private SignalStrength signalStrength;
    private TelephonyManager telephonyManager;

    private CellSignalStrengthLte cellSignalStrengthLte;

    public String Time;
    public String Latitude;
    public String Longitude;
    public String Altitude;
    public String dBm;
    public int rssi;
    public String logData;

    public int port = 5555;

    private final String APPTAG = "Cell_Pwr_Lite";
    //private CellSignalStrengthLte cellSignalStrengthLte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addListenerOnButton();
        addListenerOnStartButton();
        populateLog();

        isRunning = Boolean.FALSE;

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //YOUR CODE:
                try {
                    DatagramSocket s = new DatagramSocket();
                    byte buf[] = logData.getBytes();
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName("192.168.225.1"), port);
                    //DatagramSocket socketClient = new DatagramSocket();

                    s.send(packet);


                } catch (SocketException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        thread.start();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //double lat = location.getLatitude();
                Time = Calendar.getInstance().getTime().toString();
                Latitude = String.valueOf(location.getLatitude());
                Longitude = String.valueOf(location.getLongitude());
                Altitude = String.valueOf(location.getAltitude());
                Log.d(APPTAG, "Lat " + String.valueOf(location.getLatitude()));

                if (isRunning) {
                    //the start button was pressed we need to start auto populating the log file.
                    populateLog();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        } catch (SecurityException e) {
            Log.wtf(APPTAG, "You aint got no permissions");
        } catch (Exception e1) {
            Log.e(APPTAG, "Other exception: " + e1.toString());
        }

    }

    /**
     * Called when the user taps the Send button
     */

    public void addListenerOnStartButton() {

        //editText = (EditText) findViewById(R.id.editText);
        StartButton = (Button) findViewById(R.id.startbutton);
        StartButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (!isRunning) {
                    //isRunning = false, We will start...
                    isRunning = Boolean.TRUE;
                    StartButton.setText("STOP");
                } else {
                    isRunning = Boolean.FALSE;
                    StartButton.setText("START");
                }
            }
        });
    }


    public void addListenerOnButton() {
        // Do something in response to button
        //locationManager = (LocationManager);


        //editText = (EditText) findViewById(R.id.editText);
        textView = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!isRunning) {
                    populateLog();
                } else {
                    return;
                }
            }

        });
    }

    public void populateLog() {
        textViewPLMN = (TextView) findViewById(R.id.textDestIP);
        textViewPCI = (TextView) findViewById(R.id.textViewPCI);
        textViewTAC = (TextView) findViewById(R.id.textViewTAC);
        textViewCI = (TextView) findViewById(R.id.textViewCI);
        textViewEARFCN = (TextView) findViewById(R.id.textViewEARFCN);
        textViewBW = (TextView) findViewById(R.id.editTextBW);
        textViewRSRP = (TextView) findViewById(R.id.textViewRSRP);
        textViewRSRQ = (TextView) findViewById(R.id.textViewRSRQ);
        textViewRSSI = (TextView) findViewById(R.id.textViewRSSI);
        textViewPRB = (TextView) findViewById(R.id.textViewPRB);
        textViewIMSI = (TextView) findViewById(R.id.textViewIMSI);
        textViewIMEI = (TextView) findViewById(R.id.textViewIMEI);

        Log.d(APPTAG, "is LTE = 13? " + String.valueOf(telephonyManager.NETWORK_TYPE_LTE));
        try {
            List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();
            for (int i = 0; i < cellInfoList.size(); i++) {
                if (cellInfoList.get(i) instanceof CellInfoLte) {
                    Log.d(APPTAG, "LTEEEEEEEEE!!!!!");
                    Log.d(APPTAG, "RSRP dBm: " + String.valueOf(((CellInfoLte) cellInfoList.get(i)).getCellSignalStrength().getDbm()));
                    Log.d(APPTAG, "RSRQ dBm: " + String.valueOf(((CellInfoLte) cellInfoList.get(i)).getCellSignalStrength().getRsrq()));
                    Log.d(APPTAG, "MCC: " + String.valueOf(((CellInfoLte) cellInfoList.get(i)).getCellIdentity().getMcc()));
                    Log.d(APPTAG, "MNC: " + String.valueOf(((CellInfoLte) cellInfoList.get(i)).getCellIdentity().getMnc()));
                    textViewIMSI.setText("IMSI: " + telephonyManager.getSubscriberId());
                    textViewIMEI.setText("IMEI: " + telephonyManager.getImei());
                    textViewPLMN.setText("PLMN: " + String.valueOf(((CellInfoLte) cellInfoList.get(i)).getCellIdentity().getMcc()) + String.valueOf(((CellInfoLte) cellInfoList.get(i)).getCellIdentity().getMnc()));
                    textViewPCI.setText("PCI: " + String.valueOf(((CellInfoLte) cellInfoList.get(i)).getCellIdentity().getPci()));
                    textViewTAC.setText("TAC: " + String.valueOf(((CellInfoLte) cellInfoList.get(i)).getCellIdentity().getTac()));
                    textViewCI.setText("CID: " + String.valueOf(((CellInfoLte) cellInfoList.get(i)).getCellIdentity().getCi()));
                    textViewEARFCN.setText("EARFCN: " + String.valueOf(((CellInfoLte) cellInfoList.get(i)).getCellIdentity().getEarfcn()));
                    int bw = 10;
                    textViewBW.setText("BW: " + String.valueOf(bw));
                    textViewRSRP.setText("RSRP: " + String.valueOf(((CellInfoLte) cellInfoList.get(i)).getCellSignalStrength().getRsrp()) + " dBm");
                    textViewRSRQ.setText("RSRQ: " + String.valueOf(((CellInfoLte) cellInfoList.get(i)).getCellSignalStrength().getRsrq()) + " dB");
                    double bw_value = java.lang.Math.log(bw * 5);
                    int bw_int = (int) bw_value;
                    rssi = Integer.valueOf(10 * bw_int + Integer.valueOf(((CellInfoLte) cellInfoList.get(i)).getCellSignalStrength().getRsrp()) - Integer.valueOf(((CellInfoLte) cellInfoList.get(i)).getCellSignalStrength().getRsrq()));
                    textViewRSSI.setText("RSSI: " + String.valueOf(rssi) + " dBm");
                    dBm = String.valueOf(((CellInfoLte) cellInfoList.get(i)).getCellSignalStrength().getDbm());

                }
            }
        } catch (SecurityException e) {
            Log.wtf(APPTAG, "You aint got no permissions");
        } catch (Exception e1) {
            Log.e(APPTAG, "Other exception: " + e1.toString());
        }

        //Double latitude = location.getLatitude();
        //String text = String.valueOf(location.getLatitude());
        //String text = (editText.getText().toString() + location.getLatitude() + "\n" );
        //textView.setText(editText.getText().toString() + textView.getText());
        logData =
                        "LTE_pwr_lite" +
                        rssi + "," +
                        //Time + "," +
                        Latitude + "," +
                        Longitude + "," +
                        Altitude + "," +
                        "\n";
        textView.setText(logData + textView.getText());
        //textView.setText("test");


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //YOUR CODE:
                try {
                    DatagramSocket s = new DatagramSocket();
                    byte buf[] = logData.getBytes();
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName("10.8.1.88"), port);
                    //DatagramSocket socketClient = new DatagramSocket();

                    s.send(packet);


                } catch (SocketException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        thread.start();


    }


}

