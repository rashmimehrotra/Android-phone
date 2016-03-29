package com.app.learniatstudent.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.learniatstudent.R;
import com.app.learniatstudent.Utils.CommonUtils;
import com.app.studentlearnientapi.Convertors.XMLConvertor;
import com.app.studentlearnientapi.DataModels.LoginDataModels.UserLoginDataModel;
import com.app.studentlearnientapi.DataModels.TodaySessionModels.UserTodaySessionDataModel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by macbookpro on 22/12/2015.
 */
public class SignUp extends BaseActivity implements View.OnClickListener {
    EditText userNameEditText, fullNameEditText, emailAddressEditText, passwordEditText, confirmEditText;
    Button btnSubmit, btnMenuBack;
    private DefaultHttpClient client = new DefaultHttpClient();
    private Handler handler = new Handler();
    private ArrayList<String> messages = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup content = (ViewGroup) findViewById(R.id.content_frame1);
        View inflator = getLayoutInflater().inflate(R.layout.signup_layout, content, true);
        showActionBarBackButton();
        hideActionBarMenuButton();
        btnMenuBack = getMenuBackButton();
        btnMenuBack.setOnClickListener(this);
        findViews();
    }

    private void findViews() {
        userNameEditText = (EditText) findViewById(R.id.username_edittext);
        fullNameEditText = (EditText) findViewById(R.id.fullname_edittext);
        emailAddressEditText = (EditText) findViewById(R.id.emailaddress_edittext);
        passwordEditText = (EditText) findViewById(R.id.password_edittext);
        confirmEditText = (EditText) findViewById(R.id.confirmpassword_edittext);
        btnSubmit = (Button) findViewById(R.id.btn_Submit);

        btnSubmit.setOnClickListener(this);

    }

    private void checkForValidValues() {
        if (userNameEditText.getText().equals("") || fullNameEditText.getText().equals("") || emailAddressEditText.getText().equals("")
                || passwordEditText.getText().equals("") || confirmEditText.getText().equals("")) {
            if (userNameEditText.getText().equals("")) {
                Toast.makeText(this, "Username should not be empty", Toast.LENGTH_SHORT).show();
                return;
            } else if (fullNameEditText.getText().equals("")) {
                Toast.makeText(this, "Fullname should not be empty", Toast.LENGTH_SHORT).show();
                return;
            } else if (emailAddressEditText.getText().equals("")) {
                Toast.makeText(this, "Email id should not be empty", Toast.LENGTH_SHORT).show();
                return;
            } else if (passwordEditText.getText().equals("")) {
                Toast.makeText(this, "Password should not be empty", Toast.LENGTH_SHORT).show();
                return;
            } else if (confirmEditText.getText().equals("")) {
                Toast.makeText(this, "Confirm password should not be empty", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            if (CommonUtils.getInstance(this).isEmailValid(emailAddressEditText.getText().toString())) {
                if (!passwordEditText.getText().toString().equals(confirmEditText.getText().toString())) {
                    Toast.makeText(this, "Password doesn't match.", Toast.LENGTH_SHORT).show();
                } else {
                    if (CommonUtils.getInstance(this).isOnline(this)) {
                        registerUser();
                    } else {
                        Toast.makeText(this, "Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void registerUser() {
        CommonUtils.getInstance(this).showHud(false,this);
        final String userName = userNameEditText.getText().toString();//"shane";//
        final String password = passwordEditText.getText().toString();//"sh";//
        String appVersion = "1.6";
        String deviceId = CommonUtils.getInstance(this).getDeviceId(this);
        int isTeacher = 0;
        String signUpURL = "<Sunstone><Action><Service>Login</Service><UserName>"+userName+"</UserName><UserPassword>"+password+"</UserPassword><AppVersion>"+appVersion+"</AppVersion><DeviceId>"+deviceId+"</DeviceId><IsTeacher>0</IsTeacher></Action></Sunstone>";
        XMLConvertor.getInstance().StudentWebInterface().signInUser(signUpURL, new Callback<UserLoginDataModel>() {
            @Override
            public void success(UserLoginDataModel userLoginDataModel, Response response) {
                CommonUtils.getInstance(SignUp.this).hideHud();
                userLoginDataModel.getSunStone().getAction().getUserId();
                CommonUtils.getInstance(SignUp.this).saveStringPreferences("userid", userLoginDataModel.getSunStone().getAction().getUserId());
                Toast.makeText(SignUp.this, "Success", Toast.LENGTH_SHORT).show();
                getUserTodaySession(userLoginDataModel.getSunStone().getAction().getUserId());
                CommonUtils.getInstance(SignUp.this).saveStringPreferences("isLoggedIn", "true");
            }

            @Override
            public void failure(RetrofitError error) {
                CommonUtils.getInstance(SignUp.this).hideHud();
                Toast.makeText(SignUp.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserTodaySession(String userId){
        String url = "<Sunstone><Action><Service>GetThisStudentSessions</Service><UserId>"+userId+"</UserId></Action></Sunstone>";
        XMLConvertor.getInstance().StudentWebInterface().getUserTodaysSession(url, new Callback<UserTodaySessionDataModel>() {
            @Override
            public void success(UserTodaySessionDataModel userTodaySessionDataModel, Response response) {
//                CommonUtils.getInstance(TodayScheduleActivity.this).hideHud();
                if (userTodaySessionDataModel.getSunStone().getAction().getStatus().equalsIgnoreCase("Success")) {
                    CommonUtils.getInstance(SignUp.this).setSessionsList(userTodaySessionDataModel.getSunStone().getAction().getUserSessionsList());
                    Toast.makeText(SignUp.this, "Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUp.this,TodayScheduleActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    finish();
                } else {
                    Toast.makeText(SignUp.this, "Error in data fetching", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(SignUp.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openTodayScheduleActivity(){
        Intent intent = new Intent(this,TodayScheduleActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        finish();
    }

    public String retrieve(String url) {

        HttpGet getRequest = new HttpGet(url);

        try {

            HttpResponse getResponse = client.execute(getRequest);
            final int statusCode = getResponse.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }

            HttpEntity getResponseEntity = getResponse.getEntity();

            if (getResponseEntity != null) {
                return EntityUtils.toString(getResponseEntity);
            }

        }
        catch (IOException e) {
            getRequest.abort();
            Log.w(getClass().getSimpleName(), "Error for URL " + url, e);
        }

        return null;

    }

    /*
    private void createXMPPConnection(final String userName, final String pwd){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // Create connection
        AsyncTask<Void, Void, Void> connectionThread = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... arg0) {

                try {
//                    XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
//                            .setUsernameAndPassword(userName, pwd)
//                            .setXmppDomain("52.76.85.25")
//                            .setHost("52.76.85.25")
//                            .setPort("8280")
//                            .build();

//                    AbstractXMPPConnection conn1 = new XMPPTCPConnection(config);
//                    conn1.connect();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }
        };
        connectionThread.execute();
    }

    private void createConnection(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // Create connection
        AsyncTask<Void, Void, Void> connectionThread = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... arg0) {

                try {
                    XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
                    config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
//                    config.setSocketFactory(SSLSocketFactory.getDefault());
                    config.setResource("Android");
                    config.setHost("52.76.85.25");
                    config.setPort(5222);
                    config.setServiceName("52.76.85.25");
                    config.setSendPresence(true);
                    config.setDebuggerEnabled(true);

                    connection1 = new XMPPTCPConnection(config.build());
                    connection1.setPacketReplyTimeout(10000);
                    try {
                        connection1.connect();
                        connection1.login("zaman", "za");

                        // Set status to online / available
                        Presence presence = new Presence(Presence.Type.available);
                        presence.setStatus("I'm available");
                        connection1.sendPacket(presence);

                        if(connection1.isConnected()) {


//                            Roster roster = connection1.getRoster();
                        }

                    } catch (SmackException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (XMPPException ex) {
                    ex.printStackTrace();
                }
            return null;
            }
        };
        connectionThread.execute();
    }
    public void setConnection (XMPPConnection connection) {
        this.connection = connection;
        if (connection != null) {
            //Packet listener to get messages sent to logged in user
            PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
            connection.addPacketListener(new PacketListener() {
                public void processPacket(Packet packet) {
                    Message message = (Message) packet;
                    if (message.getBody() != null) {
                        String fromName = StringUtils.parseBareAddress(message.getFrom());
                        messages.add(fromName + ":");
                        messages.add(message.getBody());
                    }
                }
            }, filter);
        }
    }

    public void sendMsg() {
        if (connection1.isConnected()== true) {
            // Assume we've created an XMPPConnection name "connection"._
            chatmanager = ChatManager.getInstanceFor(connection1);
            newChat = chatmanager.createChat("concurer@nimbuzz.com");

            try {
                newChat.sendMessage("Howdy!");
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }
    */

    @Override
    public void onClick(View v) {
        if (v == btnSubmit) {
            checkForValidValues();
        }
        else if(v == btnMenuBack){
            Intent intent = new Intent(this,SignInActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.exit,R.anim.enter);
            finish();
        }
    }
}
