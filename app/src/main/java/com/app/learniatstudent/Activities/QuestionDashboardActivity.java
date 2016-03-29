package com.app.learniatstudent.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.learniatstudent.Constants.SLConstants;
import com.app.learniatstudent.R;
import com.app.learniatstudent.Utils.CommonUtils;
import com.app.learniatstudent.Utils.ObservingService;
import com.app.learniatstudent.XMPPClient.XMPPController;
import com.app.studentlearnientapi.DataModels.TodaySessionModels.Session;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by macbookpro on 15/02/16.
 */
public class QuestionDashboardActivity extends BaseActivity implements Observer {

    RelativeLayout gettingItLayout, notGettingItLayout;
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
            if(currentSession.getSessionState().equalsIgnoreCase("Live")){

            }
        }
        ObservingService.sharedManager(this).addObserver(SLConstants.kXMPPMsgSeatChanged, _mObserver);
    }

    private void findComponents(){
        gettingItLayout = (RelativeLayout) findViewById(R.id.rl_getting);
        notGettingItLayout = (RelativeLayout) findViewById(R.id.rl_notGetting);

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
    }

    @Override
    protected void onDestroy() {
        ObservingService.sharedManager(this).removeObserver(SLConstants.kXMPPMsgSeatChanged, _mObserver);
        super.onDestroy();
    }
}
