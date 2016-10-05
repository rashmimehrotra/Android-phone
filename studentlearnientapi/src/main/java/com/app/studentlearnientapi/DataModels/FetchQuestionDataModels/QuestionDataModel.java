package com.app.studentlearnientapi.DataModels.FetchQuestionDataModels;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by macbookpro on 20/09/2016.
 */
@Root(name = "Root")
public class QuestionDataModel {
    @Element(name = "SunStone")
    private SunStone sunStone;

    public SunStone getSunStone(){
        return sunStone;
    }
}
