/**
 * 
 * I declare that this code was written by me, xavier. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Xavier
 * Student ID: 21020683
 * Class: E63C
 * Date created: 2023-Feb-09 10:50:50 am 
 * 
 */

package e63c.xavier.FYP;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CartItemController {

	@Autowired
	private CartItemRepository cartItemRepo;

	@Autowired
	private ItemRepository itemRepo;

	@Autowired
	private MemberRepository memberRepo;

	@Autowired
	private OrderItemRepository orderRepo;

	@Autowired
	private JavaMailSender javaMailSender;

	@GetMapping("/cart")
	public String showCart(Model model, Principal principal) {

		// Get currently logged in user
		MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInMemberId = loggedInMember.getMember().getId();

		// Get shopping cart items added by this user
		// *Hint: You will need to use the method we added in the CartItemRepository
		List<CartItem> cartItemList = cartItemRepo.findByMemberId(loggedInMemberId);

		// Add the shopping cart items to the model
		model.addAttribute("cartItemList", cartItemList);

		// Calculate the total cost of all items in the shopping cart
		double cartTotal = 0.0;

		for (int i = 0; i < cartItemList.size(); i++) {

			CartItem currentCartItem = cartItemList.get(i);
			int itemQuantityInCart = currentCartItem.getQuantity();

			Item item = currentCartItem.getItem();
			double itemPrice = item.getPrice();

			cartTotal += itemPrice * itemQuantityInCart;

		}

		// Add the shopping cart total to the model
		model.addAttribute("cartTotal", cartTotal);
		model.addAttribute("memberId", loggedInMemberId);

		return "cart";
	}

	@PostMapping("/cart/add/{itemId}")
	public String addToCart(@PathVariable("itemId") int itemId, @RequestParam("quantity") int quantity,
			Principal principal) {

		// Get currently logged in user
		MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInMemberId = loggedInMember.getMember().getId();

		// Check in the cartItemRepo if item was previously added by user.
		// *Hint: we will need to write a new method in the CartItemRepository
		CartItem cartItem = cartItemRepo.findByMemberIdAndItemId(loggedInMemberId, itemId);

		// if the item was previously added, then we get the quantity that was
		// previously added and increase that
		// Save the CartItem object back to the repository
		if (cartItem != null) {
			int currentQuantity = cartItem.getQuantity();
			cartItem.setQuantity(quantity + currentQuantity);
			cartItemRepo.save(cartItem);
		} else {

			// if the item was NOT previously added,
			// then prepare the item and member objects
			Item item = itemRepo.getById(itemId);
			Member member = memberRepo.getById(loggedInMemberId);

			// Create a new CartItem object
			CartItem newCartItem = new CartItem();

			// Set the item and member as well as the new quantity in the new CartItem
			// object
			newCartItem.setItem(item);
			newCartItem.setMember(member);
			newCartItem.setQuantity(quantity);

			// Save the new CartItem object to the repository
			cartItemRepo.save(newCartItem);

		}

		return "redirect:/cart";
	}

	@PostMapping("/cart/update/{id}")
	public String updateCart(@PathVariable("id") int cartItemId, @RequestParam("qty") int qty) {

// Get cartItem object by cartItem's id
		CartItem cartItem = cartItemRepo.getById(cartItemId);

		// Set the quantity of the carItem object retrieved
		cartItem.setQuantity(qty);

		// Save the cartItem back to the cartItemRepo
		cartItemRepo.save(cartItem);

		return "redirect:/cart";
	}

	@GetMapping("/cart/remove/{id}")
	public String removeFromCart(@PathVariable("id") int cartItemId) {

		// Remove item from cartItemRepo
		cartItemRepo.deleteById(cartItemId);

		return "redirect:/cart";
	}

	@PostMapping("/cart/process_order")
	  public String processOrder(Model model, @RequestParam("cartTotal") double cartTotal,
	    @RequestParam("memberId") int memberId, @RequestParam("orderId") String orderId,
	    @RequestParam("transactionId") String transactionId) {
	       // Retrieve cart items purchased  
	    List<CartItem> cartItemList = cartItemRepo.findByMemberId(memberId);
	    
	       // Get member object  
	    Member member = memberRepo.getById(memberId);
	       // Loop to iterate through all cart items
	    for (int i = 0; i < cartItemList.size(); i++) {
	      // Retrieve details about current cart item  
	      CartItem currentCartItem = cartItemList.get(i);
	      Item itemToUpdate = currentCartItem.getItem();
	      int quantityOfItemPurchased = currentCartItem.getQuantity();
	      int itemToUpdateId = itemToUpdate.getId();
	      
	      System.out.println("Item: " + itemToUpdate.getDescription());
	      
	      // Update item table
	      Item inventoryItem = itemRepo.getById(itemToUpdateId);
	      int currentInventoryQuantity = inventoryItem.getQuantity();
	      inventoryItem.setQuantity(currentInventoryQuantity - quantityOfItemPurchased);
	      itemRepo.save(inventoryItem);
	      
	      // Add item to order table  
	      OrderItem orderItem = new OrderItem();
	      orderItem.setOrderId(orderId);
	      orderItem.setTransactionId(transactionId);
	      orderItem.setItem(itemToUpdate);
	      orderItem.setMember(member);
	      orderItem.setQuantity(quantityOfItemPurchased);
	      orderRepo.save(orderItem);
	      
	      // clear cart items belonging to member    
	      cartItemRepo.deleteById(currentCartItem.getId());
	    }
	    // Pass info to view to display success page
	    model.addAttribute("cartTotal", cartTotal);
	    model.addAttribute("cartItemList", cartItemList);
	    model.addAttribute("member", member);
	    model.addAttribute("orderId", orderId);
	    model.addAttribute("transactionId", transactionId);

	    // Send email
	    String subject = "XK ELECTRONICS order is confirmed!";
	    String body = "Thank you for your order!\n" + "Order ID: " + orderId + "\n" + "Transaction ID: "
	    + transactionId;
	    String to = member.getEmail();
	    sendEmail(to, subject, body);

	    return "success";
	    
	  }

	@GetMapping("/purchase_history")
	public String showPurchaseHistory(Model model) {
	    // Get currently logged in user
	    MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    int loggedInMemberId = loggedInMember.getMember().getId();

	    // Get all orders for the logged in member
	    List<OrderItem> orderList = orderRepo.findByMemberId(loggedInMemberId);

	    // Calculate total price for each seller
	    Map<String, Double> totalPriceBySeller = new HashMap<>();
	    double overallTotalPrice = 0.0;

	    for (OrderItem orderItem : orderList) {
	        String sellerName = orderItem.getItem().getSeller();
	        double itemPrice = orderItem.getItem().getPrice();
	        double totalPrice = totalPriceBySeller.getOrDefault(sellerName, 0.0);
	        totalPrice += itemPrice * orderItem.getQuantity();
	        totalPriceBySeller.put(sellerName, totalPrice);
	        overallTotalPrice += itemPrice * orderItem.getQuantity();
	    }

	    // Add the order list, total price, and split total price to the model
	    model.addAttribute("orderList", orderList);
	    model.addAttribute("totalPrice", overallTotalPrice);
	    model.addAttribute("totalPriceBySeller", totalPriceBySeller);

	    return "purchase_history";
	}


	

	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		// Get currently logged in user
        MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loggedInMemberId = loggedInMember.getMember().getId();
        
        // Get all orders for the logged in member
        List<OrderItem> orderList = orderRepo.findByMemberId(loggedInMemberId);
        
        // Add the order list to the model
        model.addAttribute("orderList", orderList);
        
        return "dashboard";
	}
	
	public void sendEmail(String to, String subject, String body) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(to);
		msg.setSubject(subject);
		msg.setText(body);
		System.out.println("Sending");
		javaMailSender.send(msg);
		System.out.println("Sent");
	}

}
