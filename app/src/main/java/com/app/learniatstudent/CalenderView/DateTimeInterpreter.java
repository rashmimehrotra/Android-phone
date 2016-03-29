package com.app.learniatstudent.CalenderView;

import java.util.Calendar;

/**
 * Created by macbookpro on 28/01/16.
 */
public interface DateTimeInterpreter {
    String interpretDate(Calendar date);
    String interpretTime(int hour);
}
