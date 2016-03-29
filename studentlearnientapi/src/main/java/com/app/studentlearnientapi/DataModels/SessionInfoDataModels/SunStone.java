package com.app.studentlearnientapi.DataModels.SessionInfoDataModels;

import org.simpleframework.xml.Element;

/**
 * Created by macbookpro on 10/02/16.
 */
public class SunStone {
    @Element(name = "Action")
    private Action action;

    public Action getAction(){
        return action;
    }
}
