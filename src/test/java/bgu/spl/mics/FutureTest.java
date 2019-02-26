package bgu.spl.mics;

import org.junit.Before;
import org.junit.Test;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;



public class FutureTest {

    private Future<Integer> future;

    public FutureTest(){}

    @Before
    public void setUp(){
        this.future=new Future<>();
    }


    @Test
    public void resolve() {
        future.resolve(3);
        assert future.get()==3;
        assert future.get()!=2;
        future.resolve(2);
        assert future.get()==3;
        assert future.get()!=2;
    }

	
    @Test
    public void isDone() {
        assert !future.isDone();
        future.resolve(2);
        try {
           assert future.isDone();
        }
        catch (NoSuchElementException e){
            assert false;
        }
    }


    @Test
    public void get1() {
        long timeThatPast=System.currentTimeMillis();
        future.get(1000, TimeUnit.MILLISECONDS);
        assert (System.currentTimeMillis()-timeThatPast>=1000);
    }


    @Test
    public void get() {
        try{
            Thread.sleep(300);
        }
        catch (InterruptedException e){
        }
        future.resolve(2);
        assert future.get()==2;
        assert future.get()!=1;
    }



}