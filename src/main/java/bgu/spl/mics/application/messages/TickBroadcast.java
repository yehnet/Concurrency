package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {

    //Event that sends Tick Time to all micro services

    //----------------------------------------------------------Fields----------------------------------------//
    private int currentTick;
    private int duration;
    private boolean isDone;


    //----------------------------------------------------------Constructor----------------------------------------//
    public TickBroadcast(int currentTick,int duration, boolean isDone){
        this.duration=duration;
        this.currentTick=currentTick;
        this.isDone=isDone;
    }

    //----------------------------------------------------------Methods----------------------------------------//
    public int getCurrentTick(){return currentTick;}

    public int getDuration(){return duration;}

    public boolean getIsDone(){return isDone;}
}
