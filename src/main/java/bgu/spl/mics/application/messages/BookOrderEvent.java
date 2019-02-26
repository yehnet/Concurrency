package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

public class BookOrderEvent implements Event {

    //Event that sends on order to Order A book

    //----------------------------------------------------------Fields----------------------------------------//
    private int orderTicks;
    private Customer customer;
    private String bookFromStore;

    //----------------------------------------------------------Constructor----------------------------------------//
    public BookOrderEvent(String bookFromStore, Customer customer, int orderTicks){
        this.orderTicks=orderTicks;
        this.customer=customer;
        this.bookFromStore=bookFromStore;
    }


    //----------------------------------------------------------Methods----------------------------------------//
    public Customer getCustomer(){ return customer;}

    public String getBookFromStore(){ return bookFromStore;}

    public int getOrderTicks(){return orderTicks;}

}
