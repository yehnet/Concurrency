package bgu.spl.mics.application.passiveObjects;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory  implements Serializable {


	//----------------------------------------------------------Fields----------------------------------------//
	private static Inventory instance=null;
	private BookInventoryInfo[] bookInventory;
	private HashMap<String,BookInventoryInfo> books=new HashMap<>();



	//----------------------------------------------------------Constructor----------------------------------------//
	private Inventory(){
		this.bookInventory=new BookInventoryInfo[0];
	}

	/**
     * Retrieves the single instance of this class.
     */
	public static Inventory getInstance() {
		if(instance==null){
			instance=new Inventory();
		}
		return instance;
	}
	
	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (BookInventoryInfo[] inventory ) {
		bookInventory=new BookInventoryInfo[inventory.length];
		for(int i=0;i<bookInventory.length;i++){
			bookInventory[i]=inventory[i];
		}
	}
	
	/**
     * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
     */

	//Made here synchronized in order to be sure that customer will not take book that does not exist
	public synchronized OrderResult take (String book) {
		boolean found=false;
		for(int i=0;i<bookInventory.length;i++){
			if(bookInventory[i].getBookTitle().equals(book) & bookInventory[i].getAmountInInventory()>0){
				found=true;
				bookInventory[i].takeBook();
			}
		}
		if(found)
			return OrderResult.SUCCESSFULLY_TAKEN;
		else
			return OrderResult.NOT_IN_STOCK;
	}
	
	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
	public int checkAvailabiltyAndGetPrice(String book) {
		int ans=-1;
		for(int i=0;i<bookInventory.length;i++){
			if(bookInventory[i].getBookTitle().equals(book) & bookInventory[i].getAmountInInventory()>0){
				ans=bookInventory[i].getPrice();
			}
		}
		return ans;
	}
	
	/**
     * 
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */
	public void printInventoryToFile(String filename){
		for(int i=0;i<bookInventory.length;i++){
			books.put(bookInventory[i].getBookTitle(), bookInventory[i]);
		}
		filename=filename;
		HashMap<String,Integer> books_new =new HashMap<>();
		Iterator<String> it=books.keySet().iterator();
		while(it.hasNext()){
			String cur= it.next();
			books_new.put(cur,books.get(cur).getAmountInInventory());
		}
		try(FileOutputStream fos= new FileOutputStream(filename); ObjectOutputStream oos= new ObjectOutputStream(fos)){
			oos.writeObject(books_new);
			oos.close();
			fos.close();
		} catch (IOException e) {
			System.out.println("Cant Create File: Inventory");
		}
	}
}
