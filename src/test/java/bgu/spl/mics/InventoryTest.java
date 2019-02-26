package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import org.junit.Before;
import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;


public class InventoryTest {
    BookInventoryInfo[] toLoad;

    @Before
    public void setUp(){
        toLoad=new BookInventoryInfo[4];
        toLoad[0]=new BookInventoryInfo("nitzan", 1 , 30);
        toLoad[1]=new BookInventoryInfo("netanel", 1 , 10);
        toLoad[2]=new BookInventoryInfo("netanel&nitzan", 1 , 20);
        toLoad[3]=new BookInventoryInfo("nitzan&netanel", 1 , 40);
    }

    @Test
    public void load(){
        try {
            Inventory.getInstance().take("moshe");
        }
        catch (NullPointerException e){
            assert true;
        }

        try {
            assert Inventory.getInstance() != null;
            Inventory.getInstance().load(toLoad);
        }
        catch (NullPointerException e){
        }
        try {
            assertEquals(Inventory.getInstance().take("nitzan"), OrderResult.SUCCESSFULLY_TAKEN);
            assertEquals(Inventory.getInstance().take("netanel"), OrderResult.SUCCESSFULLY_TAKEN);
            assertEquals(Inventory.getInstance().take("netanel&nitzan"), OrderResult.SUCCESSFULLY_TAKEN);
            assertEquals(Inventory.getInstance().take("nitzan&netanel"), OrderResult.SUCCESSFULLY_TAKEN);
        }
        catch (NoSuchElementException e){
            assert false;
        }
        try {
            assertEquals(Inventory.getInstance().take("nitzan"), OrderResult.NOT_IN_STOCK);
            assertEquals(Inventory.getInstance().take("netanel"), OrderResult.NOT_IN_STOCK);
            assertEquals(Inventory.getInstance().take("netanel&nitzan"), OrderResult.NOT_IN_STOCK);
            assertEquals(Inventory.getInstance().take("nitzan&netanel"), OrderResult.NOT_IN_STOCK);
        }
        catch(NoSuchElementException e){
            assert true;
        }
    }

    @Test
    public void take(){
        try {
            assert Inventory.getInstance() != null;
            Inventory.getInstance().load(toLoad);
        }
        catch (NullPointerException e){
        }
        try {
            assertEquals(Inventory.getInstance().take("nitzan"), OrderResult.SUCCESSFULLY_TAKEN);
            assertEquals(Inventory.getInstance().take("netanel"), OrderResult.SUCCESSFULLY_TAKEN);
            assertEquals(Inventory.getInstance().take("netanel"), OrderResult.SUCCESSFULLY_TAKEN);
            assertEquals(Inventory.getInstance().take("netanel&nitzan"), OrderResult.SUCCESSFULLY_TAKEN);
            assertEquals(Inventory.getInstance().take("nitzan&netanel"), OrderResult.SUCCESSFULLY_TAKEN);
        }
        catch (NoSuchElementException e) {
            assert false;
        }
        // test if you try to take books that the amount is 0
        try {
            assertEquals(Inventory.getInstance().take("netanel"), OrderResult.NOT_IN_STOCK);
            assertEquals(Inventory.getInstance().take("nitzan"), OrderResult.NOT_IN_STOCK);
        }
        catch (NoSuchElementException e){
            assert true;
        }
        // test if you try to take book that does not exist
        try {
            assertEquals(Inventory.getInstance().take("The Hobbit"), OrderResult.NOT_IN_STOCK);
            assertEquals(Inventory.getInstance().take("Harry Potter"), OrderResult.NOT_IN_STOCK);
        }
        catch(NullPointerException e){
            assert true;
        }
    }

    @Test
    public void checkAvailabiltyAndGetPrice(){
        try {
            assert Inventory.getInstance() != null;
            Inventory.getInstance().load(toLoad);
        }
        catch (NullPointerException e){
        }
        // normal tests
        try {
            assertEquals(Inventory.getInstance().checkAvailabiltyAndGetPrice("nitzan"), 30);
            assertEquals(Inventory.getInstance().checkAvailabiltyAndGetPrice("netanel"), 10);
            assertEquals(Inventory.getInstance().checkAvailabiltyAndGetPrice("netanel&nitzan"), 20);
            assertEquals(Inventory.getInstance().checkAvailabiltyAndGetPrice("nitzan&netanel"), 40);
        }
        catch (NullPointerException e){
            assert false;
        }
        //test if there is 0 books in the inventory
        try {
            Inventory.getInstance().take("nitzan");
            assertEquals(Inventory.getInstance().checkAvailabiltyAndGetPrice("nitzan"), -1);
        }
        catch (NoSuchElementException e){
            assert false;
        }
        //test book that does not exist
        try {
            assertEquals(Inventory.getInstance().checkAvailabiltyAndGetPrice("Harry Potter"), -1);
            assertEquals(Inventory.getInstance().checkAvailabiltyAndGetPrice("The Hobbit"), -1);
        }
        catch(NoSuchElementException e){
            assert true;
        }
    }

    @Test
    public void printInventoryToFile(){

    }

}