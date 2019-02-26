package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseVehicleEvent implements Event {

    //Event that sends in order to release a vehicle

    //----------------------------------------------------------Fields----------------------------------------//
    private DeliveryVehicle deliveryVehicle;

    //----------------------------------------------------------Constructor----------------------------------------//
    public ReleaseVehicleEvent(DeliveryVehicle deliveryVehicle){
        this.deliveryVehicle=deliveryVehicle;
    }



    //----------------------------------------------------------Methods----------------------------------------//
    public DeliveryVehicle getDeliveryVehicle(){return deliveryVehicle;}
}
