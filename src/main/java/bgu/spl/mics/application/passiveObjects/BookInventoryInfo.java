package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class BookInventoryInfo implements Serializable {

	//----------------------------------------------------------Fields----------------------------------------//
	private String BookName;
	private int amountInInventory;
	private int bookPrice;


	//----------------------------------------------------------Private Constructor----------------------------------------//
	public BookInventoryInfo(String bookName, int amountInInventory, int bookPrice) {
    	this.BookName=bookName;
    	this.amountInInventory=amountInInventory;
    	this.bookPrice=bookPrice;
    }


	//----------------------------------------------------------Methods----------------------------------------//

	/**
     * Retrieves the title of this book.
     * <p>
     * @return The title of this book.   
     */
	public String getBookTitle() {
		return BookName;
	}

	/**
     * Retrieves the amount of books of this type in the inventory.
     * <p>
     * @return amount of available books.      
     */
	public int getAmountInInventory() {
		return amountInInventory;
	}

	/**
     * Retrieves the price for  book.
     * <p>
     * @return the price of the book.
     */
	public int getPrice() {
		return bookPrice;
	}


	//take a book and decrease the amount
	public void takeBook(){
		if(this.amountInInventory>0){
			this.amountInInventory=this.amountInInventory-1;
		}
	}
	
}
