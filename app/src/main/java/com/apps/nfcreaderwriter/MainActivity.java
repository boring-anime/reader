package com.apps.nfcreaderwriter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity
{

    public static final String Error_Detetcted = "No NFC Tag Detetcted";
    public static final String Write_Success = "Text written Successfully";
    public static final String Write_Error = "Error during writing";

    NfcAdapter nfcadApter;
    PendingIntent pendingIntent;
    IntentFilter writingTagFilter[];
    boolean writeMode;
    Tag myTag;
    Context context;
    TextView edit_message;
    TextView nfc_contents;
    Button activated_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edit_message = (TextView) findViewById(R.id.edittext);
        nfc_contents = (TextView) findViewById(R.id.textView);
        activated_button = findViewById(R.id.button);
        context = this;

        activated_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (myTag == null) {
                        Toast.makeText(context, Error_Detetcted, Toast.LENGTH_LONG).show();
                    } else {
                        write("PlainText|" + edit_message.getText().toString(), myTag);
                        Toast.makeText(context, Write_Success, Toast.LENGTH_LONG).show();
                    }

                } catch (IOException e) {
                    Toast.makeText(context, Write_Error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (FormatException e) {
                    Toast.makeText(context, Write_Error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
        nfcadApter = NfcAdapter.getDefaultAdapter(this);
        if (nfcadApter == null) {
            Toast.makeText(this, "This Device does not support NFC", Toast.LENGTH_SHORT).show();
            finish();
        }
        readfromintant(getIntent());
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetetcted = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetetcted.addCategory(Intent.CATEGORY_DEFAULT);
        writingTagFilter = new IntentFilter[]{tagDetetcted};
    }

    private void readfromintant(Intent intent)
    {
        String action = intent.getAction();
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
        || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
        || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
        {
            Parcelable rawMsgs[] = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if(rawMsgs != null)
            {
                msgs = new NdefMessage[rawMsgs.length];
                for(int i =0; i< rawMsgs.length; i++)
                {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            buildTagViews(msgs);
        }
    }
    private void buildTagViews(NdefMessage[] msgs)
    {
        if(msgs == null || msgs.length == 0) return;

        String text = "";
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) ==0 ) ? "UTF-8" : "UTF-16";
        int languageCodeLength = payload[0] & 0063;
        try {
            {
                text = new String(payload, languageCodeLength+1, payload.length - languageCodeLength - 1, textEncoding);

            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        nfc_contents.setText("NFC content" + text);
    }
    private void write(String text, Tag tag) throws IOException, FormatException
    {
        NdefRecord[] records = {createRecord(text)};
        NdefMessage message = new NdefMessage(records);
        Ndef ndef = Ndef.get(tag);
        ndef.connect();
        ndef.writeNdefMessage(message);
        ndef.close();
    }
    private NdefRecord createRecord(String text) throws UnsupportedEncodingException
    {
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        int textLength = textBytes.length;
        byte[] payload = new byte[1 + langLength + textLength];

        payload[0] = (byte)langLength;
        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1+ langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
        return recordNFC;
    }
    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
        readfromintant(intent);
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()))
        {
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }
    @Override
    public void onPause()
    {
        super.onPause();
        WriteModeOff();
    }
    @Override
    public void onResume()
    {
        super.onResume();
        WriteModeOn();
    }
    public void WriteModeOn()
    {
        writeMode = true;
        nfcadApter.enableForegroundDispatch(this,pendingIntent,  writingTagFilter, null);
    }
    public void WriteModeOff()
    {
        writeMode = false;
        nfcadApter.disableForegroundDispatch(this);
    }
}