package com.app.studentlearnientapi.DataModels.FetchQuestionDataModels;

import org.simpleframework.xml.Element;

/**
 * Created by macbookpro on 20/09/2016.
 */

public class Action {
    @Element
    private String Status;

    @Element(required=false)
    private Question question;

    public String getStatus(){
        return Status;
    }

    public Question getQuestion(){
        return question;
    }

}
