package com.app.learniatstudent.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.learniatstudent.Constants.SLConstants;
import com.app.learniatstudent.R;
import com.app.learniatstudent.Utils.CommonUtils;
import com.app.learniatstudent.Utils.ObservingService;
import com.app.learniatstudent.XMPPClient.XMPPController;
import com.app.studentlearnientapi.Convertors.XMLConvertor;
import com.app.studentlearnientapi.DataModels.LoginDataModels.UserLoginDataModel;
import com.app.studentlearnientapi.DataModels.TodaySessionModels.UserTodaySessionDataModel;

import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.Observable;
import java.util.Observer;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by macbookpro on 21/12/2015.
 */
public class SignInActivity extends Activity implements View.OnClickListener, Observer{

    EditText userNameEditText, passwordEditText;
    RelativeLayout parentLayout;
    Button btnLogin, btnSignUp;
    Observer _mObserver = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_layout);

        findComponents();
    }

    /**
     * Find components from the layout
     */

    private void findComponents(){
        parentLayout = (RelativeLayout) findViewById(R.id.parent_layout);
        userNameEditText = (EditText) findViewById(R.id.username_edittext);
        passwordEditText = (EditText) findViewById(R.id.password_edittext);
        btnLogin = (Button) findViewById(R.id.btn_signin); // Sign in button
        btnSignUp = (Button) findViewById(R.id.btn_JFS); // Sign up button

        btnLogin.setOnClickListener(this);

        setupUI(parentLayout);

        /*
        parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                Rect r = new Rect();
                parentLayout.getWindowVisibleDisplayFrame(r);

                int screenHeight = parentLayout.getRootView().getHeight();
                int heightDifference = screenHeight - (r.bottom - r.top);
                Log.d("Keyboard Size", "Size: " + heightDifference);

                //boolean visible = heightDiff > screenHeight / 3;
            }
        });
        */
    }

    /**
     * Check wether values entered are valid or not
     */
    private void checkForValidValues() {
        if(userNameEditText.getText().toString().equals("") || passwordEditText.getText().toString().equals("")){
            if (userNameEditText.getText().equals("")) {
                Toast.makeText(this, "Username should not be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            // check for password
            else if(passwordEditText.getText().equals("")){
                Toast.makeText(this, "Password should not be empty", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else {
            // check for internet availability
            if (CommonUtils.getInstance(this).isOnline(this)) {
                registerUser();
            } else {
                Toast.makeText(this, "Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Register user to learniat server
     */

    private void registerUser() {
        CommonUtils.getInstance(this).showProgressDialog(this,"Learniat Student" ,SLConstants.kConstantLogin);
        String userName = userNameEditText.getText().toString();//"shane";
        String password = passwordEditText.getText().toString();//"sh";
        String appVersion = "1.6";
        String deviceId = CommonUtils.getInstance(this).getDeviceId(this);
        int isTeacher = 0;
        // Url to be called
        String signUpURL = "<Sunstone><Action><Service>Login</Service><UserName>"+userName+"</UserName><UserPassword>"+password+"</UserPassword><AppVersion>"+appVersion+"</AppVersion><DeviceId>"+deviceId+"</DeviceId><IsTeacher>0</IsTeacher></Action></Sunstone>";
        // Server call using retrofit
        XMLConvertor.getInstance().StudentWebInterface().signInUser(signUpURL, new Callback<UserLoginDataModel>() {
            @Override
            public void success(UserLoginDataModel userLoginDataModel, Response response) {
                if (userLoginDataModel.getSunStone().getAction().getStatus().equalsIgnoreCase("Success")) {
                    String userId = userLoginDataModel.getSunStone().getAction().getUserId();
                    CommonUtils.getInstance(SignInActivity.this).saveStringPreferences("userid", userId); // userid saved in user prefs
                    CommonUtils.getInstance(SignInActivity.this).saveStringPreferences("username", userNameEditText.getText().toString()); // username saved in user prefs
                    CommonUtils.getInstance(SignInActivity.this).saveStringPreferences("userpwd", passwordEditText.getText().toString()); // password saved in user prefs

                    CommonUtils.getInstance(SignInActivity.this).saveStringPreferences("isLoggedIn", "true"); // isLoggedin boolean saved in user prefs
                    CommonUtils.getInstance(SignInActivity.this).hideProgressDialog();
                    CommonUtils.getInstance(SignInActivity.this).showProgressDialog(SignInActivity.this, "Learniat Student", SLConstants.kConstantAuthSuccessful1 + userId + SLConstants.kConstantAuthSuccessful2);
                    CommonUtils.getInstance(SignInActivity.this).hideProgressDialog();
                    CommonUtils.getInstance(SignInActivity.this).showProgressDialog(SignInActivity.this, "Learniat Student", SLConstants.kConstantXMPPConnected);
                    // Call for XMPP server connection
                    XMPPController.getInstance(SignInActivity.this).loginToXMPPServer(userId, passwordEditText.getText().toString(), SLConstants.kXMPPConnectedSuccessfully);
                    // Adding observer for XMPP server callback
                    ObservingService.sharedManager(SignInActivity.this).addObserver(SLConstants.kXMPPConnectedSuccessfully, _mObserver);
                } else {
                    CommonUtils.getInstance(SignInActivity.this).hideProgressDialog();
                    Toast.makeText(SignInActivity.this, userLoginDataModel.getSunStone().getAction().getStatus(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                CommonUtils.getInstance(SignInActivity.this).saveStringPreferences("isLoggedIn", "false");
                CommonUtils.getInstance(SignInActivity.this).hideProgressDialog();
            }
        });
    }

    /**
     *
     * Learniat server call to fetch the today's session of user
     * @param userId
     */
    private void getUserTodaySession(String userId){
        String url = "<Sunstone><Action><Service>GetThisStudentSessions</Service><UserId>"+userId+"</UserId></Action></Sunstone>";
        XMLConvertor.getInstance().StudentWebInterface().getUserTodaysSession(url, new Callback<UserTodaySessionDataModel>() {
            @Override
            public void success(UserTodaySessionDataModel userTodaySessionDataModel, Response response) {
                CommonUtils.getInstance(SignInActivity.this).hideProgressDialog();
                // Check for status success
                if (userTodaySessionDataModel.getSunStone().getAction().getStatus().equalsIgnoreCase("Success")) {
                    CommonUtils.getInstance(SignInActivity.this).setSessionsList(userTodaySessionDataModel.getSunStone().getAction().getUserSessionsList());
                } else {
                    CommonUtils.getInstance(SignInActivity.this).setSessionsList(null);
                    Toast.makeText(SignInActivity.this, userTodaySessionDataModel.getSunStone().getAction().getStatus(), Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(SignInActivity.this, TodayScheduleActivity.class);
                startActivity(intent);
                finish();

            }

            @Override
            public void failure(RetrofitError error) {
                CommonUtils.getInstance(SignInActivity.this).hideProgressDialog();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == btnLogin){
            checkForValidValues();
        }
        else if(v == btnSignUp){
            Intent intent = new Intent(this,SignUp.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Observer callback listner method
     * @param observable
     * @param data
     */
    @Override
    public void update(Observable observable, Object data) {
        ObservingService.sharedManager(this);
        String str = ObservingService.getKey();
        // When XMPP connected successfully
        if((boolean)data == true && str.equalsIgnoreCase(SLConstants.kXMPPConnectedSuccessfully)){
            String userId = CommonUtils.getInstance(SignInActivity.this).loadStringPreferences("userid");
            if(userId !=null && !userId.equalsIgnoreCase("Empty")){
                CommonUtils.getInstance(this).hideProgressDialog();
                CommonUtils.getInstance(this).showProgressDialog(this, "Learniat Student", "Authentication successfully completed.");
                // Get user session
                getUserTodaySession(userId);
                // send message using teacher id
                XMPPController.getInstance(SignInActivity.this).sendMessage("7");
                // update state to free
                CommonUtils.getInstance(this).updateUserState("7");
            }
        }
        // When XMPP not connected successfully
        else if((boolean)data == false && str.equalsIgnoreCase(SLConstants.kXMPPConnectedSuccessfully)){
            CommonUtils.getInstance(this).hideProgressDialog();
            Toast.makeText(SignInActivity.this, "Either username or password is incorrect.", Toast.LENGTH_SHORT).show();
            CommonUtils.getInstance(SignInActivity.this).saveStringPreferences("isLoggedIn", "false");
        }

    }

    // Hide keyboard
    private void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    /**
     * Hide keyboard when touched on layout
     * @param view
     */
    private void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(SignInActivity.this);
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }

    @Override
    protected void onDestroy() {
        ObservingService.sharedManager(this).removeObserver(SLConstants.kXMPPConnectedSuccessfully, _mObserver);
        super.onDestroy();
    }
}
