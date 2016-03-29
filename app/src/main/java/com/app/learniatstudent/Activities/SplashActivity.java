package com.app.learniatstudent.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.learniatstudent.R;
import com.app.learniatstudent.Utils.CommonUtils;
import com.app.learniatstudent.XMPPClient.XMPPController;
import com.app.studentlearnientapi.Convertors.XMLConvertor;
import com.app.studentlearnientapi.DataModels.TodaySessionModels.UserTodaySessionDataModel;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by macbookpro on 10/02/16.
 */
public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getActionBar().hide();
        setContentView(R.layout.splash_layout);
//        logOutUser("526");
        new Handler().postDelayed(new Runnable() {
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
//                startAppropriateActivity();
                openMainActivity();
            }
        }, 3000);
    }

    private void openMainActivity(){
        String isLoggedIn = CommonUtils.getInstance(this).loadStringPreferences("isLoggedIn");
        if(isLoggedIn !=null && !isLoggedIn.equalsIgnoreCase("Empty") && isLoggedIn.equalsIgnoreCase("true")){
            startTodayScheduleActivity();
        }
        else {
            startSignInActivity();
        }
    }

    private void startSignInActivity(){
        Intent intent = new Intent(this,SignInActivity.class);
        startActivity(intent);
//        overridePendingTransition(R.anim.enter,R.anim.exit);
        finish();
    }

    private void startTodayScheduleActivity(){
        if (CommonUtils.getInstance(this).isOnline(this)) {
            String userId = CommonUtils.getInstance(this).loadStringPreferences("userid");
            if(userId !=null && !userId.equalsIgnoreCase("Empty")){
                getUserTodaySession(userId);
            }
        }
        else {
            showInternetPopUp("Internet Error!", "Please check your internet connection and try again.");
        }
    }

    private void getUserTodaySession(String userId){
        String url = "<Sunstone><Action><Service>GetThisStudentSessions</Service><UserId>"+userId+"</UserId></Action></Sunstone>";
        XMLConvertor.getInstance().StudentWebInterface().getUserTodaysSession(url, new Callback<UserTodaySessionDataModel>() {
            @Override
            public void success(UserTodaySessionDataModel userTodaySessionDataModel, Response response) {
//                CommonUtils.getInstance(TodayScheduleActivity.this).hideHud();
                if (userTodaySessionDataModel.getSunStone().getAction().getStatus().equalsIgnoreCase("Success")) {
                    CommonUtils.getInstance(SplashActivity.this).setSessionsList(userTodaySessionDataModel.getSunStone().getAction().getUserSessionsList());
//                    Toast.makeText(SplashActivity.this, "Success", Toast.LENGTH_SHORT).show();

                } else {
                    CommonUtils.getInstance(SplashActivity.this).setSessionsList(null);
                    Toast.makeText(SplashActivity.this, userTodaySessionDataModel.getSunStone().getAction().getStatus(), Toast.LENGTH_SHORT).show();
                }
                String userId = CommonUtils.getInstance(SplashActivity.this).loadStringPreferences("userid");
                String userPwd = CommonUtils.getInstance(SplashActivity.this).loadStringPreferences("userpwd");
                XMPPController.getInstance(SplashActivity.this).loginToXMPPServer(userId, userPwd, "");
                CommonUtils.getInstance(SplashActivity.this).updateUserState("7");
//                XMPPController.getInstance(SplashActivity.this).sendMessage("7");
                Intent intent = new Intent(SplashActivity.this, TodayScheduleActivity.class);
                startActivity(intent);
//                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();

            }

            @Override
            public void failure(RetrofitError error) {
//                CommonUtils.getInstance(TodayScheduleActivity.this).hideHud();
                Toast.makeText(SplashActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showInternetPopUp(String title, String message)
    {
        try {
            LayoutInflater li = LayoutInflater.from(this);
            View customAlertView = li.inflate(R.layout.custom_alert_layout, null);


            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder.setView(customAlertView);

            //final EditText userInput = (EditText) customAlertView.findViewById(R.id.editTextPromptName);
            final TextView userTextViewTitle = (TextView) customAlertView.findViewById(R.id.tv_alertBox_Title);
            final TextView userTextViewMessage = (TextView) customAlertView.findViewById(R.id.tv_alertBox_message);
            userTextViewTitle.setText(title);
            userTextViewTitle.setGravity(Gravity.CENTER);
            userTextViewMessage.setText(message);
            userTextViewMessage.setGravity(Gravity.CENTER);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            startTodayScheduleActivity();
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            final AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();


        } catch (Exception e) {

        }
    }
}
