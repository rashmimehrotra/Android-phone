package com.app.studentlearnientapi.DataModels.UpdateUserStateDataModels;

import org.simpleframework.xml.Element;

/**
 * Created by macbookpro on 12/02/16.
 */
public class SunStone {

    @Element (name = "Action")
    private Action action;

    public Action getAction(){
        return action;
    }
}
