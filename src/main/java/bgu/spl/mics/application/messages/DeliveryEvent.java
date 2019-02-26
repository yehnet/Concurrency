package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class DeliveryEvent implements Event {

    //Event thats send and order to start Delivery


    //----------------------------------------------------------Fields----------------------------------------//
    private String address;
    private int distance;


    //----------------------------------------------------------Constructor----------------------------------------//
    public DeliveryEvent(String address,int distance){
        this.address=address;
        this.distance=distance;
    }


    //----------------------------------------------------------Methods----------------------------------------//
    public String getAddress(){return address;}

    public  int getDistance(){return distance;}


}
