//21041531 Badi code & function

package e63c.xavier.FYP;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Item {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@NotNull
	@NotEmpty(message = "Backend - Item name cannot be empty!")
	@Size(min = 5, max = 50, message = "Name length must be between 5 and 50 characters!")
	private String name;

	@NotNull
	@NotEmpty(message = "Backend - Item description cannot be empty!")
	@Size(min = 5, max = 100, message = "Description length must be between 5 and 100 characters!")
	private String description;

	@Min(value = 0, message = "Backend - Price must be positive!")
	private double price;

	@Min(value = 0, message = "Backend - Quantity must be positive!")
	private int quantity;

	private String imgName;

	@ManyToOne
	@JoinColumn(name = "category_id", nullable = false)
	@NotNull(message = "Backend - Category cannot be null!")
	private Category category;

	private boolean banned;

	private boolean onSale; // New property for indicating if item is on sale

	private String seller;

	private double originalPrice;

	/**
	 * @return the seller
	 */
	public String getSeller() {
		return seller;
	}

	/**
	 * @param seller the seller to set
	 */
	public void setSeller(String seller) {
		this.seller = seller;
	}

	public String getImgName() {
		return imgName;
	}

	public void setImgName(String imgName) {
		this.imgName = imgName;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public boolean isBanned() {
		return banned;
	}

	public void setBanned(boolean banned) {
		this.banned = banned;
	}

	public boolean isOnSale() {
		return onSale;
	}

	public void setOnSale(boolean onSale) {
		this.onSale = onSale;
	}

	/**
	 * @return the originalPrice
	 */
	public double isOriginalPrice() {
		return originalPrice;
	}

	/**
	 * @param originalPrice the originalPrice to set
	 */
	public void setOriginalPrice(double originalPrice) {
		this.originalPrice = originalPrice;
	}

}
