package com.app.studentlearnientapi.DataModels.UpdateUserStateDataModels;

import org.simpleframework.xml.Element;

/**
 * Created by macbookpro on 12/02/16.
 */
public class Action {

    @Element
    private String Status;

    public String getStatus() {
        return Status;
    }
}
