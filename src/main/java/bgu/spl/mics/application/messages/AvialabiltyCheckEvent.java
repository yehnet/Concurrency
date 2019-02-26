package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class AvialabiltyCheckEvent implements Event {

    //Event that send in order to check if the book is available at store

    //----------------------------------------------------------Fields----------------------------------------//
    private String book;

    //----------------------------------------------------------Constructor----------------------------------------//
    public AvialabiltyCheckEvent(String book){
        this.book=book;
    }


    //----------------------------------------------------------Methods----------------------------------------//
    public String getBook(){return book;}

}

