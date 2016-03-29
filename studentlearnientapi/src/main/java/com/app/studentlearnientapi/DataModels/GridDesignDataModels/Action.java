package com.app.studentlearnientapi.DataModels.GridDesignDataModels;

import org.simpleframework.xml.Element;

/**
 * Created by macbookpro on 12/02/16.
 */
public class Action {
    @Element
    private String Status;

    @Element(required=false)
    private String Rows;

    @Element(required=false)
    private String Columns;

    @Element(required=false)
    private String SeatsRemoved;

    @Element(required=false)
    private String SeatIdList;

    @Element(required=false)
    private String SeatLabelList;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getRows() {
        return Rows;
    }

    public void setRows(String rows) {
        Rows = rows;
    }

    public String getColumns() {
        return Columns;
    }

    public void setColumns(String columns) {
        Columns = columns;
    }

    public String getSeatsRemoved() {
        return SeatsRemoved;
    }

    public void setSeatsRemoved(String seatsRemoved) {
        SeatsRemoved = seatsRemoved;
    }

    public String getSeatIdList() {
        return SeatIdList;
    }

    public void setSeatIdList(String seatIdList) {
        SeatIdList = seatIdList;
    }

    public String getSeatLabelList() {
        return SeatLabelList;
    }

    public void setSeatLabelList(String seatLabelList) {
        SeatLabelList = seatLabelList;
    }

}
