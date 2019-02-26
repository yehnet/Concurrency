package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class TakeBookEvent implements Event {

    //Event that sends in order to take a book from the inventory


    //----------------------------------------------------------Fields----------------------------------------//
    private String bookTitle;


    //----------------------------------------------------------Constructor----------------------------------------//
    public TakeBookEvent(String bookTitle){
        this.bookTitle=bookTitle;
    }


    //----------------------------------------------------------Methods----------------------------------------//
    public String getBookTitle(){return bookTitle;}
}
