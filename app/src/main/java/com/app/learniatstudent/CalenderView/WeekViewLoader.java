package com.app.learniatstudent.CalenderView;

import java.util.Calendar;
import java.util.List;

/**
 * Created by macbookpro on 28/01/16.
 */
public interface WeekViewLoader {
    /**
     * Convert a date into a double that will be used to reference when you're loading data.
     *
     * All periods that have the same integer part, define one period. Dates that are later in time should have a greater return value.
     *
     * @param instance the date
     * @return The period index in which the date falls (floating point number).
     */
    public double toWeekViewPeriodIndex(Calendar instance);

    /**
     * Load the events within the period
     * @param periodIndex the period to load
     * @return A list with the events of this period
     */
    public List<WeekViewEvent> onLoad(int periodIndex);
}
