package com.app.learniatstudent.Activities;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.app.learniatstudent.R;

/**
 * Created by macbookpro on 15/02/16.
 */
public class QuestionsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.questions_layout);
        ViewGroup content = (ViewGroup) findViewById(R.id.content_frame1);
        View inflator = getLayoutInflater().inflate(R.layout.questions_layout, content, true);
        showActionBarBackButton();
        hideActionBarMenuButton();
        showQuestionHelpLayout();
        setActionBarTitle("Questions");

    }
}
