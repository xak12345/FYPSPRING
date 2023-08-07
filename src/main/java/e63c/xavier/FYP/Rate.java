/**
 * 
 * I declare that this code was written by me, 21022015. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: zhen hong
 * Student ID: 21022015
 * Class: ABC1
 * Date created: 2023-May-27 5:01:30 pm 
 * 
 */

package e63c.xavier.FYP;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author 21022015
 *
 */
@Entity
public class Rate {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private int rating;
	private String message;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne
	@JoinColumn(name = "item_id")
	private Item item;

	private String imgName;

	public Rate(int rating, String message, Member member, Item item, String imgName) {
		this.rating = rating;
		this.message = message;
		this.member = member;
		this.item = item;
		this.imgName=imgName;
	}
    public Rate() {
        // Default constructor with no arguments
    }
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the rating
	 */
	public int getRating() {
		return rating;
	}

	/**
	 * @param rating the rating to set
	 */
	public void setRating(int rating) {
		this.rating = rating;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the member
	 */
	public Member getMember() {
		return member;
	}

	/**
	 * @param member the member to set
	 */
	public void setMember(Member member) {
		this.member = member;
	}

	/**
	 * @return the item
	 */
	public Item getitem() {
		return item;
	}

	/**
	 * @param item the item to set
	 */
	public void setitem(Item item) {
		this.item = item;
	}

	public String getImgName() {
		return imgName;
	}

	public void setImgName(String imgName) {
		this.imgName = imgName;
	}
}
