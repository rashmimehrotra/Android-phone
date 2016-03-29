package com.app.learniatstudent.Activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.learniatstudent.R;
import com.app.learniatstudent.Utils.CommonUtils;
import com.app.learniatstudent.Views.RobotoRegularTextView;
import com.app.learniatstudent.Views.RoundedImageView;
import com.app.learniatstudent.XMPPClient.XMPPController;
import com.app.studentlearnientapi.Convertors.XMLConvertor;
import com.app.studentlearnientapi.DataModels.LoginDataModels.UserLoginDataModel;
import com.app.studentlearnientapi.DataModels.TodaySessionModels.Session;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by macbookpro on 18/11/2015.
 */
public class BaseActivity extends Activity implements View.OnClickListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    ImageView questionAnswerIcon, questionHelpIcon;
    CircleImageView userImageView;
    public CircleImageView courseIndicatorImage;
    private Button menuButton, menuBackButton, menuButton1;
    private RobotoRegularTextView actionBarTitleText;
    public TextView signOutTextView, userNameTextView, xmppReConnectTextView, setUpSeatingTextView;
    ActionBar mActionBar;
    LinearLayout mainLayout;
    private View dummyview1;
    RelativeLayout questionsHelpLayout;
    private boolean mSlideState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decor);

        findViews();
    }

    /**
     * findViews from layout
     */
    private void findViews(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
//                getActionBar().show();
            }
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
//                getActionBar().hide();
            }
        };
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        userImageView = (CircleImageView) findViewById(R.id.userPhoto_iv);
        signOutTextView = (TextView) findViewById(R.id.signOut_tv);
        userNameTextView = (TextView) findViewById(R.id.username_tv);
        xmppReConnectTextView = (TextView) findViewById(R.id.xmppReconnect_tv);
        setUpSeatingTextView = (TextView) findViewById(R.id.setUpSeating_tv);
        dummyview1 = (View) findViewById(R.id.dummyview1);
        signOutTextView.setOnClickListener(this);
        xmppReConnectTextView.setOnClickListener(this);
        setUpSeatingTextView.setOnClickListener(this);

        populateCustomActionBarLayout();
        setUserInfo();
    }

    // When user is already signed in, update its info
    private void setUserInfo(){
            String userId = CommonUtils.getInstance(this).loadStringPreferences("userid");
            if(userId !=null && !userId.equalsIgnoreCase("Empty")){
                String url ="http://54.251.104.13/images/sunprofile/"+userId+"_79px.jpg";
                Picasso.with(this)
                        .load(url)
                        .placeholder(R.drawable.seat)
                        .error(R.drawable.seat)
                        .into(userImageView);
            }
        String username = CommonUtils.getInstance(this).loadStringPreferences("username");
        if(username !=null && !username.equalsIgnoreCase("Empty")){
            userNameTextView.setText(username);
        }
    }

    /**
     * Custom ActionBar
     */
    private void populateCustomActionBarLayout(){
        actionBarSettings();

        LayoutInflater mInflater = LayoutInflater.from(this);

        View customActionBarView;
        customActionBarView = mInflater.inflate(R.layout.custom_actionbar_layout,null);
        menuButton = (Button) customActionBarView.findViewById(R.id.menuButton);
        menuButton1 = (Button) customActionBarView.findViewById(R.id.menuButton1);
        menuBackButton = (Button) customActionBarView.findViewById(R.id.menuBackButton);
        actionBarTitleText = (RobotoRegularTextView) customActionBarView.findViewById(R.id.tob_bar_title);
        questionsHelpLayout = (RelativeLayout) customActionBarView.findViewById(R.id.question_dashboard_menuitems);
        questionAnswerIcon = (ImageView) customActionBarView.findViewById(R.id.question_answer_icon);
        questionHelpIcon = (ImageView) customActionBarView.findViewById(R.id.question_help_icon);
        courseIndicatorImage = (CircleImageView) customActionBarView.findViewById(R.id.course_icon_img1);

        menuButton.setOnClickListener(this);
        menuButton1.setOnClickListener(this);

        mActionBar.setCustomView(customActionBarView,
                new ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

    }

    /**
     * ActionBar Title
     */
    public void setActionBarTitle(String title){
        if(actionBarTitleText !=null && title !=null){
            actionBarTitleText.setText(title);
        }
    }

    /**
     * Show Menu button
     */
    public void showActionBarMenuButton(){
        if(View.INVISIBLE == menuButton.getVisibility()){
            menuButton.setVisibility(View.VISIBLE);
            menuButton1.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hide Action Bar Menu Button
     */

    public void hideActionBarMenuButton(){
        menuButton.setVisibility(View.INVISIBLE);
        menuButton1.setVisibility(View.INVISIBLE);
    }

    public void showQuestionHelpLayout(){
        if(View.INVISIBLE == questionsHelpLayout.getVisibility()){
            questionsHelpLayout.setVisibility(View.VISIBLE);
        }
    }

    public void hideQuestionHelpLayout(){
        questionsHelpLayout.setVisibility(View.INVISIBLE);
    }

    /**
     * Show ActionBar Back Button
     */

    public void showActionBarBackButton(){
        if(View.GONE == menuBackButton.getVisibility()){
            menuBackButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hide Action Bar Back Button
     */

    public void hideActionBarBackButton(){
        menuBackButton.setVisibility(View.GONE);
    }

    public Button getMenuBackButton(){
        return menuBackButton;
    }

    /**
     * ActionBar Settings
     */

    private void actionBarSettings(){
        mActionBar = getActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);

        mActionBar.setDisplayOptions(
                ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_TITLE);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(false);
    }

    public void menuButtonClicked(){
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if(mSlideState){
                    mDrawerLayout.closeDrawer(mainLayout);
//            getActionBar().show();
                    mSlideState = false;
                }
                else{
                    mSlideState = true;
//            getActionBar().hide();
                    mDrawerLayout.openDrawer(mainLayout);
                }
            }
        },200);

    }

    /**
     * Hide refresh text in the menu
     */
    public void hideRefreshLayout(){
        dummyview1.setVisibility(View.GONE);
        setUpSeatingTextView.setVisibility(View.GONE);

    }

    public TextView getSignOutButton(){
        return signOutTextView;
    }

    public TextView getXmppReConnectTextView(){
        return xmppReConnectTextView;
    }

    /**
     * Close menu drawer
     */
    public void closeMenuDrawer(){
        mDrawerLayout.closeDrawer(mainLayout);
    }

    /**
     *
     * @param v
     */

    @Override
    public void onClick(View v) {
        if(v == menuButton || v == menuButton1){
            menuButtonClicked();
        }
    }
}
