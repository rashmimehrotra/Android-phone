package com.app.studentlearnientapi.DataModels.LoginDataModels;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by macbookpro on 22/12/2015.
 */
@Root(name = "Root")
public class UserLoginDataModel {

    @Element(name = "SunStone")
    private SunStone sunStone;

    public SunStone getSunStone() {
        return sunStone;
    }
}