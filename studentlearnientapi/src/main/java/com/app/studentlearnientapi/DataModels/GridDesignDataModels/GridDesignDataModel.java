package com.app.studentlearnientapi.DataModels.GridDesignDataModels;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by macbookpro on 12/02/16.
 */
@Root (name = "Root")
public class GridDesignDataModel {

    @Element (name = "SunStone")
    private SunStone sunStone;

    public SunStone getSunStone(){
        return sunStone;
    }

}
