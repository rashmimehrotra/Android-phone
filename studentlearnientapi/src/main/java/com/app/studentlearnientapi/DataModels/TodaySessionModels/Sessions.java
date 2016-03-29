package com.app.studentlearnientapi.DataModels.TodaySessionModels;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by macbookpro on 10/02/16.
 */

public class Sessions {

    @ElementList (name = "Session")
    private List<Session> session;

    public List<Session> getSession(){
        return session;
    }

}
