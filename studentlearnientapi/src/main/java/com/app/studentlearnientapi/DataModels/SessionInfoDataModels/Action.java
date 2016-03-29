package com.app.studentlearnientapi.DataModels.SessionInfoDataModels;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.List;

/**
 * Created by macbookpro on 10/02/16.
 */
public class Action {
    @Element
    private String Status;

    @ElementList(required=false)
    private List<Student> Students;

    public String getStatus(){
        return Status;
    }

    public List  getStudentsList(){
        return Students;
    }
}
