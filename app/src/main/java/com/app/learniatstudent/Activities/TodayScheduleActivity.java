package com.app.learniatstudent.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.learniatstudent.CalenderView.DateTimeInterpreter;
import com.app.learniatstudent.CalenderView.WeekView;
import com.app.learniatstudent.CalenderView.WeekViewEvent;
import com.app.learniatstudent.Constants.SLConstants;
import com.app.learniatstudent.R;
import com.app.learniatstudent.Utils.CommonUtils;
import com.app.learniatstudent.Utils.ObservingService;
import com.app.learniatstudent.XMPPClient.XMPPController;
import com.app.studentlearnientapi.Convertors.XMLConvertor;
import com.app.studentlearnientapi.DataModels.LoginDataModels.UserLoginDataModel;
import com.app.studentlearnientapi.DataModels.TodaySessionModels.Session;
import com.app.studentlearnientapi.DataModels.TodaySessionModels.UserTodaySessionDataModel;

import org.jivesoftware.smack.XMPPException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by macbookpro on 28/01/16.
 */
public class TodayScheduleActivity extends BaseActivity implements WeekView.MonthChangeListener,
        WeekView.EventClickListener, WeekView.EventLongPressListener, Observer {

    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView mWeekView;
    private List<Session> sessionsList;
    Observer _mObserver = this;
    ArrayList<String> receivedMessagesList = new ArrayList<>();
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewGroup content = (ViewGroup) findViewById(R.id.content_frame1);
        View inflator = getLayoutInflater().inflate(R.layout.activity_main, content, true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setActionBarTitle("Today Schedule"); // Set actionbar title
        setUpSeatingTextView.setText("Refresh"); // Change menu item title
        CommonUtils.getInstance(this).saveStringPreferences("className", this.getClass().getName());
        CommonUtils.currentActivity = this;
        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);
        // Registering observer for Time Extend message
        ObservingService.sharedManager(this).addObserver(SLConstants.kXMPPMsgTimeExtended, _mObserver);
    }

    /**
     * Get user's today session
     * @param userId
     */
    private void getUserTodaySession(String userId){
        String url = "<Sunstone><Action><Service>GetThisStudentSessions</Service><UserId>"+userId+"</UserId></Action></Sunstone>";
        // Learniat Server call using retrofit
        XMLConvertor.getInstance().StudentWebInterface().getUserTodaysSession(url, new Callback<UserTodaySessionDataModel>() {
            @Override
            public void success(UserTodaySessionDataModel userTodaySessionDataModel, Response response) {
                // Check for success status
                if(userTodaySessionDataModel.getSunStone().getAction().getStatus().equalsIgnoreCase("Success")){
                    // Sessions list
                    CommonUtils.getInstance(TodayScheduleActivity.this).setSessionsList(userTodaySessionDataModel.getSunStone().getAction().getUserSessionsList());
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(cal.YEAR);
                    int month = cal.get(cal.MONTH)+1;
                    List<WeekViewEvent> list = onMonthChange(year, month);
                    mWeekView.notifyDatasetChanged();
                    mWeekView.sortAndCacheEvents(list);
//                    mWeekView.invalidate();
                }
                else {
                    Toast.makeText(TodayScheduleActivity.this, userTodaySessionDataModel.getSunStone().getAction().getStatus(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(TodayScheduleActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Draw sessions on the time layout
     * @param newYear: year of the events required by the view.
     * @param newMonth: month of the events required by the view <br/><strong>1 based (not like JAVA API) --> January = 1 and December = 12</strong>.
     * @return
     */

    @Override
    public List<WeekViewEvent> onMonthChange(final int newYear, final int newMonth) {
        final List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        sessionsList = CommonUtils.getInstance(this).getSessionsList();
        if(sessionsList == null){
            return null;
        }
        for(int i = 0; i < sessionsList.size(); i++){
            if(CommonUtils.getInstance(TodayScheduleActivity.this).isDateValid(sessionsList.get(i).getStartTime())){
                String startTimeString = sessionsList.get(i).getStartTime(); // Session start time
                String[] startToken = startTimeString.split(" ");
                String[] startHourToken = startToken[1].split(":");
                String endTimeString = sessionsList.get(i).getEndTime(); // Session end time
                String[] endToken = endTimeString.split(" ");
                String[] endHourToken = endToken[1].split(":");
                String minutesDiff= CommonUtils.getInstance(TodayScheduleActivity.this).getTimeDifference(startTimeString,endTimeString);
                Calendar startTime = Calendar.getInstance();
                startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startHourToken[0]));
                startTime.set(Calendar.MINUTE, Integer.parseInt(startHourToken[1]));
                startTime.set(Calendar.MONTH, newMonth-1);
                startTime.set(Calendar.YEAR, newYear);
                Calendar endTime = (Calendar) startTime.clone();
                endTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endHourToken[0]));
                endTime.set(Calendar.MINUTE, Integer.parseInt(endHourToken[1]));
                endTime.set(Calendar.MONTH, newMonth-1);
//        WeekViewEvent event = new WeekViewEvent(this,1, getEventTitle(startTime),"Started at ", startTime, endTime);
                String sessionState = sessionsList.get(i).getSessionState();
                String startsInString = "";
                // Session is being added as an event
                WeekViewEvent event = new WeekViewEvent(TodayScheduleActivity.this,sessionsList.get(i).getClassId(), sessionsList.get(i).getClassName(),startsInString, startTime, endTime, sessionsList.get(i));

                if(sessionState.equalsIgnoreCase("Ended")){
                    event.setColor(Color.parseColor("#98B4CF"));
                }
                else if(sessionState.equalsIgnoreCase("Cancelled")){
                    event.setColor(Color.parseColor("#FF3B30"));
                }
                else if(sessionState.equalsIgnoreCase("Scheduled")){
                    event.setColor(Color.parseColor("#3A72A8"));
                }
                else if(sessionState.equalsIgnoreCase("Live")){
                    event.setColor(Color.parseColor("#4CD964"));
                }
                else if(sessionState.equalsIgnoreCase("Opened")){
                    event.setColor(Color.parseColor("#00AEEF"));
                }
                else {
//                    event.setColor(getResources().getColor(R.color.event_color_03));
                }
                // Join the XMPP conference room
                final String roomName = "room_"+sessionsList.get(i).getSessionId()+"@conference.52.76.85.25"+"/"+sessionsList.get(i).getStudentId();
//                final String roomName = "testingRoomForXmpp@conference.52.76.85.25"+"/"+sessionsList.get(i).getStudentId();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            XMPPController.getInstance(TodayScheduleActivity.this).join(roomName,5000);
                        } catch (XMPPException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


                events.add(event);
            }
        }


        return events;
    }

    private String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        if(event !=null && event.getTodaySessionModel() !=null &&
                event.getTodaySessionModel().getSessionState().equalsIgnoreCase("Live") ||
                event.getTodaySessionModel().getSessionState().equalsIgnoreCase("Opened")){

            CommonUtils.getInstance(this).setCurrentSession(event.getTodaySessionModel());
            Intent intent = new Intent(this,RoomSeatsActivity.class);
            startActivity(intent);
            finish();

        }
        else if(event.getTodaySessionModel().getSessionState().equalsIgnoreCase("Scheduled")){
            Toast.makeText(TodayScheduleActivity.this, "This class is not opened yet.", Toast.LENGTH_SHORT).show();
        }
        else if(event.getTodaySessionModel().getSessionState().equalsIgnoreCase("Cancelled")){
            Toast.makeText(TodayScheduleActivity.this, "This class has been cancelled.", Toast.LENGTH_SHORT).show();
        }
        else if(event.getTodaySessionModel().getSessionState().equalsIgnoreCase("Ended")){
            Toast.makeText(TodayScheduleActivity.this, "This class has ended.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
    }

    /**
     * Logout user from the app from today session screen
     */
    private void logOutUser(){
        CommonUtils.getInstance(this).showProgressDialog(this, "Learniat Student", "Logging out the user");
        String userId = CommonUtils.getInstance(this).loadStringPreferences("userid");
        if(userId !=null && !userId.equalsIgnoreCase("Empty")){
            String url = "<Sunstone><Action><Service>Logout</Service><UserId>"+userId+"</UserId></Action></Sunstone>";
            XMLConvertor.getInstance().StudentWebInterface().logOutUser(url, new Callback<UserLoginDataModel>() {
                @Override
                public void success(UserLoginDataModel userLoginDataModel, Response response) {
                    CommonUtils.getInstance(TodayScheduleActivity.this).hideProgressDialog();
                    if (userLoginDataModel.getSunStone().getAction().getStatus().equalsIgnoreCase("Success")) {
                        XMPPController.getInstance(TodayScheduleActivity.this).sendMessage("8");
                        CommonUtils.getInstance(TodayScheduleActivity.this).updateUserState("8");
                        disconnectConnection();
                    } else {
                        Toast.makeText(TodayScheduleActivity.this, userLoginDataModel.getSunStone().getAction().getStatus(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    CommonUtils.getInstance(TodayScheduleActivity.this).hideProgressDialog();
                    Toast.makeText(TodayScheduleActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void openSignInActivity(){
        CommonUtils.getInstance(this).saveStringPreferences("isLoggedIn", "Empty");
        CommonUtils.getInstance(this).saveStringPreferences("userid", "Empty");
        CommonUtils.getInstance(this).saveStringPreferences("userpwd", "Empty");
        Intent intent = new Intent(TodayScheduleActivity.this,SignInActivity.class);
        startActivity(intent);
        finish();
    }

    // Disconnect Function
    public void disconnectConnection(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                XMPPController.getInstance(TodayScheduleActivity.this).getXMPPConnection().disconnect();
                openSignInActivity();
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CommonUtils.getInstance(this).isOnline(this)) {
            String userId = CommonUtils.getInstance(this).loadStringPreferences("userid");
            if(userId !=null && !userId.equalsIgnoreCase("Empty")){
                getUserTodaySession(userId);
            }
        }
        else {
            Toast.makeText(this, "Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == signOutTextView){
            if (CommonUtils.getInstance(this).isOnline(this)) {
                logOutUser();
            }
            else {
                Toast.makeText(this, "Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
            }
            closeMenuDrawer();
        }
        else if (v == setUpSeatingTextView){
            if (CommonUtils.getInstance(this).isOnline(this)) {
                String userId = CommonUtils.getInstance(this).loadStringPreferences("userid");
                if(userId !=null && !userId.equalsIgnoreCase("Empty")){
                    getUserTodaySession(userId);
                }
            }
            else {
                Toast.makeText(this, "Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
            }
            closeMenuDrawer();
        }
        else if (v == xmppReConnectTextView){
                if (CommonUtils.getInstance(this).isOnline(this)) {
                    if(XMPPController.getInstance(this).getXMPPConnection() !=null  && XMPPController.getInstance(this).getXMPPConnection().isConnected()){
                        closeMenuDrawer();
                        Toast.makeText(this,"Already connected to xmpp server",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        closeMenuDrawer();
                    String userId = CommonUtils.getInstance(this).loadStringPreferences("userid");
                    String userPwd = CommonUtils.getInstance(this).loadStringPreferences("userpwd");
                    if(userId !=null && !userId.equalsIgnoreCase("Empty") && userPwd !=null && !userPwd.equalsIgnoreCase("Empty")){
                        CommonUtils.getInstance(this).showProgressDialog(this, "Learniat Student", "Reconnecting to xmpp server....");
                        ObservingService.sharedManager(TodayScheduleActivity.this).addObserver(SLConstants.kXMPPReConnectedSuccessfully, _mObserver);
                        XMPPController.getInstance(this).loginToXMPPServer(userId,userPwd,SLConstants.kXMPPReConnectedSuccessfully);
                    }
                }
            }
            else {
                    closeMenuDrawer();
                    Toast.makeText(this, "Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        ObservingService.sharedManager(this);
        String str = ObservingService.getKey();
        if (str.equalsIgnoreCase(SLConstants.kXMPPReConnectedSuccessfully)) {
            CommonUtils.getInstance(this).hideProgressDialog();
            CommonUtils.getInstance(this).showProgressDialog(this, "Learniat Student", "Reconnected to xmpp server successfully.");
            CommonUtils.getInstance(this).hideProgressDialog();

        }
        else if(str.equalsIgnoreCase(SLConstants.kXMPPMsgTimeExtended)){
            final String msg = CommonUtils.getInstance(this).getClassTimeExtendMessage();
            TodayScheduleActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    if (CommonUtils.getInstance(getBaseContext()).isOnline(getBaseContext())) {
                        if(msg.equalsIgnoreCase("Class has been opened")){
                            if(!receivedMessagesList.contains(msg)){
                                receivedMessagesList.add(msg);
                                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                        String userId = CommonUtils.getInstance(TodayScheduleActivity.this).loadStringPreferences("userid");
                        if (userId != null && !userId.equalsIgnoreCase("Empty")) {
                            getUserTodaySession(userId);
                        }
                    }
                    else {
                        Toast.makeText(getBaseContext(), "Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        ObservingService.sharedManager(this).removeObserver(SLConstants.kXMPPReConnectedSuccessfully, _mObserver);
        ObservingService.sharedManager(this).removeObserver(SLConstants.kXMPPMsgTimeExtended, _mObserver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}