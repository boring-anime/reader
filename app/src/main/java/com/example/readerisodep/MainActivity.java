package com.example.readerisodep;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.tech.IsoDep;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import com.example.readerisodep.Utils;

public class MainActivity extends AppCompatActivity {
    public static final String Error_Detected = "No NFC Tag Detected";

    NfcAdapter nfcAdapter;
    Context context;
    TextView nfc_contents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nfc_contents = (TextView) findViewById(R.id.nfc_contents);
        context = this;

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter == null){
            Toast.makeText(this, "This device does not support NFC", Toast.LENGTH_LONG).show();
            finish();
        }else{
            Toast.makeText(this, "Nfc found", Toast.LENGTH_LONG).show();
            Log.i("On create", "Nfc found");
            try {
                readFromIntent(getIntent());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void readFromIntent(Intent intent) throws IOException {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                ){
            Log.i("From Intent", "Tag discovered");
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            IsoDep tag = IsoDep.get(tagFromIntent);
            tag.connect();
            if(tag.isConnected()){
                Toast.makeText(this, "tag is connected", Toast.LENGTH_LONG).show();
                int maxLength = tag.getMaxTransceiveLength();
                Toast.makeText(this, "Max length: " + maxLength, Toast.LENGTH_LONG).show();
                Log.d("Length", Integer.toString(maxLength));
            }
            byte[] SELECT = new byte[] { (byte)0xF0, (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x05, (byte)0x06 };
            byte[] readrecord_apdu = new byte[] { (byte) 0x94, (byte)0xB2, 0x01, 0x04, 0x00 };
            byte[] result =  new byte[0];
            byte[] result1 =  new byte[0];
            result1 = tag.transceive(SELECT);
            result = tag.transceive(readrecord_apdu);
            Log.d("Length", Integer.toString(result.length));
            ArrayList <Byte> b_result = new ArrayList<>();

           /* for (int i = 0; i < result.length; i++) {
                Log.d("result","data at index " + i + " = " + String.valueOf(result[i]));

            }*/
            Log.d("result","data at index "  + " = " + Utils.toHexString(result));
            Log.d("result1","data at index "  + " = " + Utils.toHexString(result1));

            Log.d("Length 0", Byte.toString(result[0]));
            Log.d("Length 1", Byte.toString(result[1]));
            /*byte[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for(int i=0; i< rawMsgs.length; i++){
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            buildTagViews(msgs);*/
        }
        else{
            Log.i("from Intent", "Tag not discovered");
        }
    }
    /*private void buildTagViews(NdefMessage[] msgs){
        if(msgs == null || msgs.length == 0 ) {
            Toast.makeText(this, "msg is blank", Toast.LENGTH_LONG).show();
            return;
        } else {
            Toast.makeText(this, "Able to read message", Toast.LENGTH_LONG).show();
        String text = "";
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0 ) ? "UTF-8" : "UTF-16";  // Get the text Encoding
        int languageCodeLength = payload[0] & 0063; // Get the; Language Code, e.g. "en"

        try {
            // Get the Text
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e){
            Log.e("UnsupportedEncoding", "buildTagViews: "+ e.toString()  );
        }
        nfc_contents.setText("NFC content: " + text);
        }
    }*/
}