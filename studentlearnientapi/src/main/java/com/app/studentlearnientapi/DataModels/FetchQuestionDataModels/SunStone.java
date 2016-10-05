package com.app.studentlearnientapi.DataModels.FetchQuestionDataModels;

import org.simpleframework.xml.Element;

/**
 * Created by macbookpro on 20/09/2016.
 */

public class SunStone {
    @Element(name = "Action")
    private Action action;

    public Action getAction(){
        return action;
    }
}
