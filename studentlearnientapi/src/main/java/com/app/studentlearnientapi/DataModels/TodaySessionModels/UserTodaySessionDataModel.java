package com.app.studentlearnientapi.DataModels.TodaySessionModels;

import com.app.studentlearnientapi.DataModels.TodaySessionModels.SunStone;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by macbookpro on 09/02/16.
 */
@Root(name = "Root")
public class UserTodaySessionDataModel {
    @Element(name = "SunStone")
    private SunStone sunStone;

    public SunStone getSunStone() {
        return sunStone;
    }
}