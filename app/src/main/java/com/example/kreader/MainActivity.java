package com.example.kreader;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    NfcAdapter nfcAdapter;
    private static final int READER_FLAGS = NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    @Override
    public void onResume(){
        super.onResume();
        if (nfcAdapter != null){
            final Bundle options = new Bundle();
            options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 5000);
            nfcAdapter.enableReaderMode(this,  new NfcAdapter.ReaderCallback() {
                @Override
                public void onTagDiscovered(Tag tag) {
                    Log.d("response", "Tag discovered");
                    IsoDep isoDep = IsoDep.get(tag);
                    if (isoDep != null) {
                        try{
                            isoDep.connect();
                            if (isoDep.isConnected()){
                                Log.d("response", "Isodep Tag connected");
                                byte [] response = isoDep.transceive(AID_ANDROID);
                                Log.d("response", Utils.toHexString(response));
                                isoDep.close();
                            } else {
                                Log.d("response", "Isodep Tag not connected");
                            }

                            ;
                        } catch  (IOException e) {
                            e.printStackTrace();
                        }

                    } else{
                        Log.d("response", "Isodep Tag null");
                    }
                }
            }, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, options);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if (nfcAdapter != null){
            nfcAdapter.disableReaderMode(this);
        }
    }
    private static final byte[] AID_ANDROID = { (byte)0xF0, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 };

}