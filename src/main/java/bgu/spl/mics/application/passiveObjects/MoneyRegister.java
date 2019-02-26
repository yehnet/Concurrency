package bgu.spl.mics.application.passiveObjects;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable {


	//----------------------------------------------------------Fields----------------------------------------//
	private static MoneyRegister moneyRegister=null;
	private ConcurrentLinkedQueue<OrderReceipt> orderReceipts;
	private int totalEarning=0;


	//----------------------------------------------------------Constructor----------------------------------------//
	private MoneyRegister(){
		this.orderReceipts=new ConcurrentLinkedQueue<>();
	}
	
	/**
     * Retrieves the single instance of this class.
     */
	public static MoneyRegister getInstance() {
		if(moneyRegister==null){
			moneyRegister=new MoneyRegister();
		}
		return moneyRegister;
	}
	
	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file (OrderReceipt r) {
		orderReceipts.add(r);
		totalEarning=totalEarning+r.getPrice();
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {
		return totalEarning;
	}
	
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	//Made here synchronized in order to make sure that customer not try to charge him self when he dont have money
	public synchronized void chargeCreditCard(Customer c, int amount) {
		if(c.getAvailableCreditAmount()>=amount){
			c.setCreditAmount(amount);
		}
	}
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.. 
     */
	public void printOrderReceipts(String filename) {
		LinkedList<OrderReceipt> tmp=new LinkedList<>();
		for(OrderReceipt d: orderReceipts){
			tmp.add(d);
		}
		try{
			FileOutputStream fos= new FileOutputStream(filename);
			ObjectOutputStream oos= new ObjectOutputStream(fos);
			oos.writeObject(tmp);
			oos.close();
			fos.close();
		}
		catch (IOException e){
			System.out.println("Cant Create File: Money Register");
		}
	}
}
