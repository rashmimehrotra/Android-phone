package com.app.studentlearnientapi.DataModels.LoginDataModels;

import org.simpleframework.xml.Element;

/**
 * Created by macbookpro on 29/12/2015.
 */
public class SunStone {
    @Element(name = "Action")
    private Action action;

    public Action getAction(){
        return action;
    }
}
