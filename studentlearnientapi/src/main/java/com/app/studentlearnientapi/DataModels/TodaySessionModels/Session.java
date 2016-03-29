package com.app.studentlearnientapi.DataModels.TodaySessionModels;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by macbookpro on 10/02/16.
 */
@Root (name = "Session")
public class Session {

    @Element (name = "SessionId")
    private String sessionId;

    @Element (name = "StartTime")
    private String sTime;

    @Element (name = "EndTime")
    private String eTime;

    @Element (name = "SessionState")
    private String sessionState;

    @Element (name = "StudentName")
    private String studentName;

    @Element (name = "StudentId")
    private String studentId;

    @Element (name = "ClassId")
    private int classId;

    @Element (name = "ClassName")
    private String cName;

    @Element (name = "RoomId")
    private String roomId;

    @Element (name = "RoomName")
    private String roomName;

    @Element (name = "SubjectId")
    private String subjectId;

    @Element (name = "SubjectName")
    private String subjectName;

    public String getSubjectName() {
        return subjectName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getStartTime() {
        return sTime;
    }

    public String getEndTime() {
        return eTime;
    }

    public String getSessionState() {
        return sessionState;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentId() {
        return studentId;
    }

    public int getClassId() {
        return classId;
    }

    public String getClassName() {
        return cName;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getSubjectId() {
        return subjectId;
    }
}
