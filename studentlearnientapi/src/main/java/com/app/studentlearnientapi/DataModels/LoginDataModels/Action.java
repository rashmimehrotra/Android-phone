package com.app.studentlearnientapi.DataModels.LoginDataModels;

import org.simpleframework.xml.Element;

/**
 * Created by macbookpro on 29/12/2015.
 */
public class Action {
    @Element
    private String Status;

    @Element(required=false)
    private String UserId;

    @Element(required=false)
    private String SchoolId;

    public Action() {
    }

    public String getStatus(){
        return Status;
    }

    public String getUserId(){
        return UserId;
    }

    public String getSchoolId(){
        return SchoolId;
    }
}
