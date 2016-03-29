package com.app.learniatstudent.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.learniatstudent.Constants.SLConstants;
import com.app.learniatstudent.R;
import com.app.learniatstudent.Utils.CommonUtils;
import com.app.learniatstudent.Utils.ObservingService;
import com.app.learniatstudent.Views.MyGridView;
import com.app.learniatstudent.Views.RoundedImageView;
import com.app.learniatstudent.XMPPClient.XMPPController;
import com.app.studentlearnientapi.Convertors.XMLConvertor;
import com.app.studentlearnientapi.DataModels.GridDesignDataModels.GridDesignDataModel;
import com.app.studentlearnientapi.DataModels.SessionInfoDataModels.SessionInfoDataModel;
import com.app.studentlearnientapi.DataModels.SessionInfoDataModels.Student;
import com.app.studentlearnientapi.DataModels.TodaySessionModels.Session;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Join Class screen
 * Created by macbookpro on 01/01/2016.
 */
public class RoomSeatsActivity extends BaseActivity implements Observer {
    GridView gridView;
    TextView timerTextView, noStudentTextView;
    ArrayList<String> receivedMessagesList = new ArrayList<>();
    HashMap<String,Student> hMap = new HashMap<>();
    ImageAdapter adapter;
    private List<Student> studentSeatsList;
    Button btnJoinClass;
    ArrayList<String> seatLabelList, removedSeatsList;
    private String currentUserSeatNumber = "";
    Session currentSession;
    Observer _mObserver = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup content = (ViewGroup) findViewById(R.id.content_frame1);
        View inflator = getLayoutInflater().inflate(R.layout.room_seats_layout, content, true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        signOutTextView.setText("Leave Session"); // Action bar title
        hideRefreshLayout(); // Hide menu refresh button in this screen
        CommonUtils.getInstance(this).saveStringPreferences("className", this.getClass().getName());
        CommonUtils.currentActivity = this;
        // Get current session info saved
        currentSession = CommonUtils.getInstance(this).getCurrentSession();
        if(currentSession !=null){
            setActionBarTitle(currentSession.getClassName());
            courseIndicatorImage.setVisibility(View.VISIBLE);
            if(currentSession.getSessionState().equalsIgnoreCase("Live")){
                courseIndicatorImage.setBackground(getResources().getDrawable(R.drawable.green_circle_bg));
            }
            else {
                courseIndicatorImage.setBackground(getResources().getDrawable(R.drawable.yellow_circle_bg));
            }
            getGridDesign(currentSession.getRoomId());
        }
        findComponents();

        // Registering observers for messages
        ObservingService.sharedManager(this).addObserver(SLConstants.kXMPPMsgSeatChanged, _mObserver);
        ObservingService.sharedManager(this).addObserver(SLConstants.kXMPPMsgTimeExtended, _mObserver);
        ObservingService.sharedManager(this).addObserver(SLConstants.kXMPPMsgTeacherEndsSessions, _mObserver);
    }

    /**
     * Get grid design from learniat server
     * @param roomId
     */
    private void getGridDesign(String roomId){
        String url = "<Sunstone><Action><Service>RetrieveGridDesign</Service><RoomId>"+roomId+"</RoomId></Action></Sunstone>";
        XMLConvertor.getInstance().StudentWebInterface().retreiveGridDesign(url, new Callback<GridDesignDataModel>() {
            @Override
            public void success(GridDesignDataModel gridDesignDataModel, Response response) {
                if (gridDesignDataModel.getSunStone().getAction().getStatus().equalsIgnoreCase("Success")) {
                    String columns = gridDesignDataModel.getSunStone().getAction().getColumns();
                    gridView.setNumColumns(Integer.parseInt(columns));
                    if(Integer.parseInt(gridDesignDataModel.getSunStone().getAction().getRows()) > 5 ){
                        gridView.setVerticalSpacing(10);
                    }
                    else {
                        gridView.setVerticalSpacing(18);
                    }
                    // Removed seats list
                    String removedList = gridDesignDataModel.getSunStone().getAction().getSeatsRemoved();
                    String[] removedListToken = new String[0];
                    if (removedList != null && !removedList.equalsIgnoreCase("")) {
                        removedListToken = removedList.split(",");
                    }
                    removedSeatsList = new ArrayList<String>(Arrays.asList(removedListToken));
                    String seatsLabelList = gridDesignDataModel.getSunStone().getAction().getSeatLabelList();
                    String[] token = seatsLabelList.split(",");
                    seatLabelList = new ArrayList<String>();
                    ArrayList<String> stringsArray = new ArrayList<String>(Arrays.asList(token));
                    ArrayList<Integer> sortArray = new ArrayList<Integer>();
                    for (String item : removedSeatsList) {
                        stringsArray.add(item);
                    }
                    // Sorting seats labels
                    for (int j = 0; j < stringsArray.size(); j++) {
                        if (stringsArray.get(j).charAt(0) == 'A') {
                            sortArray.add(Integer.parseInt(stringsArray.get(j).substring(1)));
                        }
                    }
                    Collections.sort(sortArray);
                    for (Integer item : sortArray) {
                        seatLabelList.add("A" + item);
                    }
                    // Get Seat assignments from learniat server
                    if (currentSession != null) {
                        getSeatAssignments(currentSession.getSessionId());
                    }

                } else {
                    Toast.makeText(RoomSeatsActivity.this, gridDesignDataModel.getSunStone().getAction().getStatus(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(RoomSeatsActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Get Seat assignments from learniat server
     * @param sessionId
     */
    private void getSeatAssignments(String sessionId){
        String url = "<Sunstone><Action><Service>RetrieveSeatAssignments</Service><SessionId>"+sessionId+"</SessionId></Action></Sunstone>";
        XMLConvertor.getInstance().StudentWebInterface().retreiveSeatsAssignment(url, new Callback<SessionInfoDataModel>() {
            @Override
            public void success(SessionInfoDataModel sessionInfoDataModel, Response response) {
                if (sessionInfoDataModel.getSunStone().getAction().getStatus().equalsIgnoreCase("Success")) {
                    noStudentTextView.setVisibility(View.INVISIBLE);
                    gridView.setVisibility(View.VISIBLE);
                    btnJoinClass.setVisibility(View.VISIBLE);
                    btnJoinClass.setEnabled(true);
                    btnJoinClass.setClickable(true);
                    if (studentSeatsList != null && studentSeatsList.size() > 0) {
                        studentSeatsList.clear();
                    }
                    // Students seats list
                    studentSeatsList = sessionInfoDataModel.getSunStone().getAction().getStudentsList();
                    String userId = CommonUtils.getInstance(RoomSeatsActivity.this).loadStringPreferences("userid");
                    if (userId != null && !userId.equalsIgnoreCase("Empty")) {
                        if (hMap != null && hMap.size() > 0) {
                            hMap.clear();
                        }
                        for (Student model : studentSeatsList) {
                            hMap.put(model.getSeatLabel(), model);
                            if (model.getStudentId().equalsIgnoreCase(userId)) {
                                currentUserSeatNumber = model.getSeatLabel();
                            }
                        }
                    }
                    if (seatLabelList != null && seatLabelList.size() > 0) {
                        adapter = new ImageAdapter(seatLabelList, RoomSeatsActivity.this);
                        gridView.setAdapter(adapter);
                    }

                } else {
                    if(hMap !=null && hMap.size() > 0){
                        hMap.clear();
                    }

                    if (studentSeatsList != null && studentSeatsList.size() > 0) {
                        studentSeatsList.clear();
                    }
                    noStudentTextView.setVisibility(View.VISIBLE);
                    noStudentTextView.setText(sessionInfoDataModel.getSunStone().getAction().getStatus());
                    gridView.setVisibility(View.INVISIBLE);
                    btnJoinClass.setEnabled(false);
                    btnJoinClass.setClickable(false);
                    btnJoinClass.setVisibility(View.INVISIBLE);
                    seatLabelList = new ArrayList<String>();
                    adapter = new ImageAdapter(seatLabelList, RoomSeatsActivity.this);
                    gridView.setAdapter(adapter);
                }
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    /**
     * Find components from the layout
     */

    private void findComponents(){
        timerTextView = (TextView) findViewById(R.id.startsinabout_timer);
        noStudentTextView = (TextView) findViewById(R.id.noStudent_tv);
        btnJoinClass = (Button) findViewById(R.id.btnJoinClass);
        gridView = (GridView) findViewById(R.id.grid_view);

        btnJoinClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSession != null) {
                    if (currentSession.getSessionState().equalsIgnoreCase("Opened")) {
                        CommonUtils.getInstance(RoomSeatsActivity.this).updateUserState("10");
                    } else if (currentSession.getSessionState().equalsIgnoreCase("Live")) {
                        CommonUtils.getInstance(RoomSeatsActivity.this).updateUserState("1");
                    }
                }
                startQuestionDashboardActivity();
            }
        });

    }

    private void startQuestionDashboardActivity(){
        Intent intent = new Intent(this,QuestionDashboardActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void update(Observable observable, Object data) {
        ObservingService.sharedManager(this);
        String str = ObservingService.getKey();
        if ((boolean)data == true && str.equalsIgnoreCase(SLConstants.kXMPPMsgSeatChanged)) {
            if(currentSession !=null){
                getGridDesign(currentSession.getRoomId());
            }
            else {

            }
        }
        else if((boolean)data == true && str.equalsIgnoreCase(SLConstants.kXMPPMsgTimeExtended)){
            final String msg = CommonUtils.getInstance(this).getClassTimeExtendMessage();
            RoomSeatsActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    if(!receivedMessagesList.contains(msg)){
                        receivedMessagesList.add(msg);
                        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                    if (courseIndicatorImage == null) {
                        return;
                    }
                    courseIndicatorImage.setVisibility(View.VISIBLE);
                    if (msg.equalsIgnoreCase("Class has begun")) {
                        courseIndicatorImage.setBackground(getResources().getDrawable(R.drawable.green_circle_bg));
                    }
                }
            });
        }
        else if ((boolean)data == true && str.equalsIgnoreCase(SLConstants.kXMPPReConnectedSuccessfully)) {
            CommonUtils.getInstance(this).hideProgressDialog();
            CommonUtils.getInstance(this).showProgressDialog(this, "Learniat Student", "Reconnected to xmpp server successfully.");
            CommonUtils.getInstance(this).hideProgressDialog();

        }
        else if ((boolean)data == true && str.equalsIgnoreCase(SLConstants.kXMPPMsgTeacherEndsSessions)){
            CommonUtils.getInstance(this).updateUserState("7");
            Intent intent = new Intent(this, TodayScheduleActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Adapter for students grid
     */
    class ImageAdapter extends BaseAdapter {
        ArrayList<String> studentList = new ArrayList<>();
        private LayoutInflater inflater=null;
        Context ctx;
        public ImageAdapter(ArrayList<String> list, Context context)
        {
            studentList = list;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return studentList.size();
        }

        @Override
        public Object getItem(int position) {
            return studentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder = null;
            if (row == null) {
                row = inflater.inflate(R.layout.grid_item_layout, parent, false);
                holder = new ViewHolder();
                holder.imageItem = (RoundedImageView)row.findViewById(R.id.full_image_view1);
                row.setTag(holder);
            }
            else {
                holder = (ViewHolder) row.getTag();
            }
            // Current user
            if(studentList.get(position).equalsIgnoreCase(currentUserSeatNumber)){
                holder.imageItem.setImageResource(R.drawable.allocated_seat);
            }
            // Empty seats
            else if(removedSeatsList.contains(studentList.get(position))){
                holder.imageItem.setImageResource(R.drawable.empty_seat);
            }
            // allocated seats
            else {
                if(hMap !=null && studentList !=null && studentList.size() > 0 && studentList.get(position) !=null){
                    Student st = (Student)hMap.get(studentList.get(position));
                    if(st == null || hMap.get(studentList.get(position)) == null){
                        holder.imageItem.setImageResource(R.drawable.seat);
                    }
                    else {
                        // Setting up students images
                        String url ="http://54.251.104.13/images/sunprofile/"+st.getStudentId()+"_79px.jpg";
                        Picasso.with(RoomSeatsActivity.this)
                                .load(url)
                                .placeholder(R.drawable.seat)
                                .error(R.drawable.seat)
                                .into(holder.imageItem);
                    }
                }
            }

            holder.imageItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(studentList.get(position).equalsIgnoreCase(currentUserSeatNumber)){
                        if(currentSession !=null){
                            if(currentSession.getSessionState().equalsIgnoreCase("Opened")){
                                XMPPController.getInstance(RoomSeatsActivity.this).sendMessage("10");
                                CommonUtils.getInstance(RoomSeatsActivity.this).updateUserState("10");
                            }
                            else if(currentSession.getSessionState().equalsIgnoreCase("Live")){
                                XMPPController.getInstance(RoomSeatsActivity.this).sendMessage("1");
                                CommonUtils.getInstance(RoomSeatsActivity.this).updateUserState("1");
                            }
                        }
                        startQuestionDashboardActivity();
                    }
                }
            });
            return row;
        }
    }

    public class ViewHolder {
        RoundedImageView imageItem;
    }


    @Override
    protected void onDestroy() {
        ObservingService.sharedManager(this).removeObserver(SLConstants.kXMPPMsgSeatChanged, _mObserver);
        ObservingService.sharedManager(this).removeObserver(SLConstants.kXMPPMsgTimeExtended, _mObserver);
        ObservingService.sharedManager(this).removeObserver(SLConstants.kXMPPReConnectedSuccessfully, _mObserver);
        ObservingService.sharedManager(this).removeObserver(SLConstants.kXMPPMsgTeacherEndsSessions, _mObserver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v == signOutTextView){
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
                    Toast.makeText(this,"Already connected to xmpp server",Toast.LENGTH_SHORT).show();
                }
                else {
                    closeMenuDrawer();
                    String userId = CommonUtils.getInstance(this).loadStringPreferences("userid");
                    String userPwd = CommonUtils.getInstance(this).loadStringPreferences("userpwd");
                    if(userId !=null && !userId.equalsIgnoreCase("Empty") && userPwd !=null && !userPwd.equalsIgnoreCase("Empty")){
                        CommonUtils.getInstance(this).showProgressDialog(this, "Learniat Student", "Reconnecting to xmpp server....");
                        ObservingService.sharedManager(RoomSeatsActivity.this).addObserver(SLConstants.kXMPPReConnectedSuccessfully, _mObserver);
                        XMPPController.getInstance(this).loginToXMPPServer(userId,userPwd,SLConstants.kXMPPReConnectedSuccessfully);
                    }
                }
            }
            else {
                Toast.makeText(this, "Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
