package com.app.learniatstudent.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.learniatstudent.R;
import com.app.learniatstudent.Utils.CommonUtils;
import com.app.learniatstudent.Views.RobotoRegularTextView;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by macbookpro on 19/03/16.
 * Working on this class. Will update it soon
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout mDrawerLayout;
    ImageView questionAnswerIcon, questionHelpIcon;
    CircleImageView userImageView;
    public CircleImageView courseIndicatorImage;
    private Button menuButton, menuBackButton, menuButton1;
    private RobotoRegularTextView actionBarTitleText;
    public TextView signOutTextView, userNameTextView, xmppReConnectTextView, setUpSeatingTextView;
    android.app.ActionBar mActionBar;
    LinearLayout mainLayout;
    private View dummyview1;
    RelativeLayout questionsHelpLayout;
    private boolean mSlideState = false;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_decore_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
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



        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
//            setupDrawerContent(navigationView);
        }

        populateCustomActionBarLayout();
        setUserInfo();
    }

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
//        actionBarSettings();

//        LayoutInflater mInflater = LayoutInflater.from(this);
//
//        View customActionBarView;
//        customActionBarView = mInflater.inflate(R.layout.custom_actionbar_layout,null);
        menuButton = (Button) toolbar.findViewById(R.id.menuButton);
        menuButton1 = (Button) toolbar.findViewById(R.id.menuButton1);
        menuBackButton = (Button) toolbar.findViewById(R.id.menuBackButton);
        actionBarTitleText = (RobotoRegularTextView) toolbar.findViewById(R.id.tob_bar_title);
        questionsHelpLayout = (RelativeLayout) toolbar.findViewById(R.id.question_dashboard_menuitems);
        questionAnswerIcon = (ImageView) toolbar.findViewById(R.id.question_answer_icon);
        questionHelpIcon = (ImageView) toolbar.findViewById(R.id.question_help_icon);
        courseIndicatorImage = (CircleImageView) toolbar.findViewById(R.id.course_icon_img1);

        menuButton.setOnClickListener(this);
        menuButton1.setOnClickListener(this);

//        mActionBar.setCustomView(customActionBarView,
//                new android.app.ActionBar.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT));

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
                android.app.ActionBar.DISPLAY_SHOW_CUSTOM,
                android.app.ActionBar.DISPLAY_SHOW_CUSTOM | android.app.ActionBar.DISPLAY_SHOW_TITLE);
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.sample_actions, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                mDrawerLayout.openDrawer(GravityCompat.START);
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

//    private void setupDrawerContent(NavigationView navigationView) {
//        navigationView.setNavigationItemSelectedListener(
//                new NavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(MenuItem menuItem) {
//                        menuItem.setChecked(true);
//                        mDrawerLayout.closeDrawers();
//                        return true;
//                    }
//                });
//    }
}
