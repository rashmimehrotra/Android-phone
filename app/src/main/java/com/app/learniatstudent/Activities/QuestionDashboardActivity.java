package com.app.learniatstudent.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.learniatstudent.Constants.SLConstants;
import com.app.learniatstudent.R;
import com.app.learniatstudent.Utils.CommonUtils;
import com.app.learniatstudent.Utils.ObservingService;
import com.app.learniatstudent.XMPPClient.XMPPController;
import com.app.studentlearnientapi.DataModels.TodaySessionModels.Session;

import org.jivesoftware.smack.XMPPException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by macbookpro on 15/02/16.
 */
public class QuestionDashboardActivity extends BaseActivity implements Observer {

    RelativeLayout gettingItLayout, notGettingItLayout, topBannerLayout, graspIndexLayout;
    LinearLayout questionTopicLayout, waitingMessageLayout;
    TextView courseNameTextView, classStartTimerTextView, questionTopicTextView, topicStatementTextView, questionStatementTextView;
    ImageView activityIndicationImage, graspIndicatorImage;

    Observer _mObserver = this;
    Session currentSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.question_dashboard_layout);
        ViewGroup content = (ViewGroup) findViewById(R.id.content_frame1);
        View inflator = getLayoutInflater().inflate(R.layout.question_dashboard_layout, content, true);
        showQuestionHelpLayout();
        signOutTextView.setText("Leave Session");
        hideRefreshLayout();
        CommonUtils.currentActivity = this;
        findComponents();
        currentSession = CommonUtils.getInstance(this).getCurrentSession();
        if(currentSession !=null){
            // Join the XMPP conference room
            final String roomName = "question_"+currentSession.getSessionId()+"@conference.52.76.85.25";
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        XMPPController.getInstance(QuestionDashboardActivity.this).join(roomName,5000);
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            if(currentSession.getSessionState().equalsIgnoreCase("Live")){

            }
            courseNameTextView.setText(currentSession.getClassName());
        }
        ObservingService.sharedManager(this).addObserver(SLConstants.kXMPPMsgSeatChanged, _mObserver);
        ObservingService.sharedManager(this).addObserver(SLConstants.kXMPPMsgTimeExtended, _mObserver);
        ObservingService.sharedManager(this).addObserver(SLConstants.kXMPPMsgAllowVoting, _mObserver);
        ObservingService.sharedManager(this).addObserver(SLConstants.kXMPPMsgTeacherQnASubmitted, _mObserver);
    }

    private void findComponents(){
        gettingItLayout = (RelativeLayout) findViewById(R.id.rl_getting);
        notGettingItLayout = (RelativeLayout) findViewById(R.id.rl_notGetting);
        topBannerLayout = (RelativeLayout) findViewById(R.id.top_layout);
        questionTopicLayout = (LinearLayout) findViewById(R.id.questionStatement_layout);
        waitingMessageLayout = (LinearLayout) findViewById(R.id.waitingMessageLayout);
        graspIndexLayout = (RelativeLayout) findViewById(R.id.rl_activityAndGrasp);
        classStartTimerTextView = (TextView) findViewById(R.id.startsinabout_timer);
        questionTopicTextView = (TextView) findViewById(R.id.topic_textview);
        topicStatementTextView = (TextView) findViewById(R.id.topicStatement_textview);
        questionStatementTextView = (TextView) findViewById(R.id.questionStatement_textview);
        activityIndicationImage = (ImageView) findViewById(R.id.activity_indicator_img);
        graspIndicatorImage = (ImageView) findViewById(R.id.graspIndex_indicator_img);
        courseNameTextView = (TextView) findViewById(R.id.courseName_text);

        gettingItLayout.setOnClickListener(this);
    }

    private void startQuestionsActivity(){
        Intent intent = new Intent(this,QuestionsActivity.class);
        startActivity(intent);
//        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(this, RoomSeatsActivity.class);
//        startActivity(intent);
//        finish();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v == gettingItLayout){
            startQuestionsActivity();
        }
        else if(v == signOutTextView){
            if (CommonUtils.getInstance(this).isOnline(this)) {
                CommonUtils.getInstance(this).updateUserState("7");
                Intent intent = new Intent(this, TodayScheduleActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                Toast.makeText(this, "Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
            }
        }
        else if (v == xmppReConnectTextView){
            if (CommonUtils.getInstance(this).isOnline(this)) {
                if(XMPPController.getInstance(this).getXMPPConnection() !=null  && XMPPController.getInstance(this).getXMPPConnection().isConnected()){
                    closeMenuDrawer();
                    Toast.makeText(this, "Already connected to xmpp server", Toast.LENGTH_SHORT).show();
                }
                else {
                    closeMenuDrawer();
                    String userId = CommonUtils.getInstance(this).loadStringPreferences("userid");
                    String userPwd = CommonUtils.getInstance(this).loadStringPreferences("userpwd");
                    if(userId !=null && !userId.equalsIgnoreCase("Empty") && userPwd !=null && !userPwd.equalsIgnoreCase("Empty")){
                        CommonUtils.getInstance(this).showProgressDialog(this, "Learniat Student", "Reconnecting to xmpp server....");
//                    ObservingService.sharedManager(QuestionDashboardActivity.this).addObserver(SLConstants.kXMPPReConnectedSuccessfully, _mObserver);
//                    XMPPController.getInstance(this).loginToXMPPServer(userId,userPwd,SLConstants.kXMPPReConnectedSuccessfully);
                    }
                }
            }
            else {
                Toast.makeText(this, "Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        ObservingService.sharedManager(this);
        String str = ObservingService.getKey();
        if (str.equalsIgnoreCase(SLConstants.kXMPPMsgSeatChanged)) {
            QuestionDashboardActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getBaseContext(), "Seating arrangement changed", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if (str.equalsIgnoreCase(SLConstants.kXMPPMsgTimeExtended)){
            final String msg = CommonUtils.getInstance(this).getClassTimeExtendMessage();
            QuestionDashboardActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    if(msg.equalsIgnoreCase("Class has begun")){
                        Toast.makeText(getBaseContext(), "Class has begun", Toast.LENGTH_SHORT).show();
                        showClassBegunScreen();
                    }
                    else {
                    }
                }
            });
        }
        else if (str.equalsIgnoreCase(SLConstants.kXMPPMsgAllowVoting)){
            try {
                JSONObject bodyObject = (JSONObject)data;
                String votingValue = bodyObject.getString("VotingValue");
                if(votingValue.equalsIgnoreCase("true")){
                    showQuestionTopic(bodyObject.getString("SubTopicName"), votingValue);
                }
                else {
                    showQuestionTopic(bodyObject.getString("SubTopicName"), votingValue);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        else if (str.equalsIgnoreCase(SLConstants.kXMPPMsgTeacherQnASubmitted)){
            try {
                JSONObject bodyObject = (JSONObject)data;
                String qType = bodyObject.getString("QuestionType");
                String questionLogId = bodyObject.getString("QuestionLogId");
                if(qType.equalsIgnoreCase("Text")){
                    showQuestionAlert("Received a question","Please type out your response",Integer.parseInt(questionLogId));
                }
                else if (qType.equalsIgnoreCase("Multiple Choice")){
                    showQuestionAlert("Received a question","Please Select correct Response (Just one)",Integer.parseInt(questionLogId));
                }
                else if (qType.equalsIgnoreCase("Multiple Response")){
                    showQuestionAlert("Received a question","Please Select correct Responses (More than one or just one)",Integer.parseInt(questionLogId));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onDestroy() {
        ObservingService.sharedManager(this).removeObserver(SLConstants.kXMPPMsgSeatChanged, _mObserver);
        ObservingService.sharedManager(this).removeObserver(SLConstants.kXMPPMsgTimeExtended, _mObserver);
        ObservingService.sharedManager(this).removeObserver(SLConstants.kXMPPMsgAllowVoting, _mObserver);
        ObservingService.sharedManager(this).removeObserver(SLConstants.kXMPPMsgTeacherQnASubmitted, _mObserver);
        super.onDestroy();
    }

    private void showClassBegunScreen(){
        questionTopicLayout.setVisibility(View.VISIBLE);
        graspIndexLayout.setVisibility(View.VISIBLE);
        topBannerLayout.setBackgroundColor(Color.parseColor("#4CD964"));
    }

    private void showQuestionTopic(String topicName, String votingValue){
        topicStatementTextView.setVisibility(View.VISIBLE);
        topicStatementTextView.setText(topicName);
        if(votingValue.equalsIgnoreCase("false")){
            questionStatementTextView.setText("No Active Question");
        }
    }

    private void showQuestionAlert(String title, String msg, int qLogId){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setTitle(title);

        alertDialogBuilder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
            }
        });

        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
