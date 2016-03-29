package com.app.studentlearnientapi.DataModels.SessionInfoDataModels;

import org.simpleframework.xml.ElementList;

import java.util.List;

/**
 * Created by macbookpro on 12/02/16.
 */
public class Students {

    @ElementList (name = "Student")
    private List<Student> student;

    public List<Student> getStudentsList(){
        return student;
    }
}
