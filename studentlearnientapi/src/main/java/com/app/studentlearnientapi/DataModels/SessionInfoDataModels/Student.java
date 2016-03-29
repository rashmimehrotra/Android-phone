package com.app.studentlearnientapi.DataModels.SessionInfoDataModels;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by macbookpro on 12/02/16.
 */
@Root(name = "Student")
public class Student {

    @Element
    private String SeatId;

    @Element
    private String SeatLabel;

    @Element
    private String StudentId;

    @Element
    private String StudentName;

    @Element
    private String StudentState;

    @Element
    private String SeatState;

    public String getSeatId() {
        return SeatId;
    }

    public void setSeatId(String seatId) {
        SeatId = seatId;
    }

    public String getSeatLabel() {
        return SeatLabel;
    }

    public void setSeatLabel(String seatLabel) {
        SeatLabel = seatLabel;
    }

    public String getStudentId() {
        return StudentId;
    }

    public void setStudentId(String studentId) {
        StudentId = studentId;
    }

    public String getStudentName() {
        return StudentName;
    }

    public void setStudentName(String studentName) {
        StudentName = studentName;
    }

    public String getStudentState() {
        return StudentState;
    }

    public void setStudentState(String studentState) {
        StudentState = studentState;
    }

    public String getSeatState() {
        return SeatState;
    }

    public void setSeatState(String seatState) {
        SeatState = seatState;
    }

}
