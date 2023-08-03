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
public class CartProductController {

	@Autowired
	private CartProductRepository cartProductRepo;

	@Autowired
	private ProductRepository productRepo;

	@Autowired
	private MemberRepository memberRepo;

	@Autowired
	private OrderProductRepository orderRepo;

	@Autowired
	private JavaMailSender javaMailSender;

	@GetMapping("/cart")
	public String showCart(Model model, Principal principal) {

		// Get currently logged in user
		MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInMemberId = loggedInMember.getMember().getId();

		// Get shopping cart products added by this user
		// *Hint: You will need to use the method we added in the CartProductRepository
		List<CartProduct> cartProductList = cartProductRepo.findByMemberId(loggedInMemberId);

		// Add the shopping cart products to the model
		model.addAttribute("cartProductList", cartProductList);

		// Calculate the total cost of all products in the shopping cart
		double cartTotal = 0.0;

		for (int i = 0; i < cartProductList.size(); i++) {

			CartProduct currentCartProduct = cartProductList.get(i);
			int productQuantityInCart = currentCartProduct.getQuantity();

			Product product = currentCartProduct.getProduct();
			double productPrice = product.getPrice();

			cartTotal += productPrice * productQuantityInCart;

		}

		// Add the shopping cart total to the model
		model.addAttribute("cartTotal", cartTotal);
		model.addAttribute("memberId", loggedInMemberId);

		return "cart";
	}

	@PostMapping("/cart/add/{productId}")
	public String addToCart(@PathVariable("productId") int productId, @RequestParam("quantity") int quantity,
			Principal principal) {

		// Get currently logged in user
		MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInMemberId = loggedInMember.getMember().getId();

		// Check in the cartProductRepo if product was previously added by user.
		// *Hint: we will need to write a new method in the CartProductRepository
		CartProduct cartProduct = cartProductRepo.findByMemberIdAndProductId(loggedInMemberId, productId);

		// if the product was previously added, then we get the quantity that was
		// previously added and increase that
		// Save the CartProduct object back to the repository
		if (cartProduct != null) {
			int currentQuantity = cartProduct.getQuantity();
			cartProduct.setQuantity(quantity + currentQuantity);
			cartProductRepo.save(cartProduct);
		} else {

			// if the product was NOT previously added,
			// then prepare the product and member objects
			Product product = productRepo.getById(productId);
			Member member = memberRepo.getById(loggedInMemberId);

			// Create a new CartProduct object
			CartProduct newCartProduct = new CartProduct();

			// Set the product and member as well as the new quantity in the new CartProduct
			// object
			newCartProduct.setProduct(product);
			newCartProduct.setMember(member);
			newCartProduct.setQuantity(quantity);

			// Save the new CartProduct object to the repository
			cartProductRepo.save(newCartProduct);

		}

		return "redirect:/cart";
	}

	@PostMapping("/cart/update/{id}")
	public String updateCart(@PathVariable("id") int cartProductId, @RequestParam("qty") int qty) {

// Get cartProduct object by cartProduct's id
		CartProduct cartProduct = cartProductRepo.getById(cartProductId);

		// Set the quantity of the carProduct object retrieved
		cartProduct.setQuantity(qty);

		// Save the cartProduct back to the cartProductRepo
		cartProductRepo.save(cartProduct);

		return "redirect:/cart";
	}

	@GetMapping("/cart/remove/{id}")
	public String removeFromCart(@PathVariable("id") int cartProductId) {

		// Remove product from cartProductRepo
		cartProductRepo.deleteById(cartProductId);

		return "redirect:/cart";
	}

	@PostMapping("/cart/process_order")
	  public String processOrder(Model model, @RequestParam("cartTotal") double cartTotal,
	    @RequestParam("memberId") int memberId, @RequestParam("orderId") String orderId,
	    @RequestParam("transactionId") String transactionId) {
	       // Retrieve cart products purchased  
	    List<CartProduct> cartProductList = cartProductRepo.findByMemberId(memberId);
	    
	       // Get member object  
	    Member member = memberRepo.getById(memberId);
	       // Loop to iterate through all cart products
	    for (int i = 0; i < cartProductList.size(); i++) {
	      // Retrieve details about current cart product  
	      CartProduct currentCartProduct = cartProductList.get(i);
	      Product productToUpdate = currentCartProduct.getProduct();
	      int quantityOfProductPurchased = currentCartProduct.getQuantity();
	      int productToUpdateId = productToUpdate.getId();
	      
	      System.out.println("Product: " + productToUpdate.getDescription());
	      
	      // Update product table
	      Product inventoryProduct = productRepo.getById(productToUpdateId);
	      int currentInventoryQuantity = inventoryProduct.getQuantity();
	      inventoryProduct.setQuantity(currentInventoryQuantity - quantityOfProductPurchased);
	      productRepo.save(inventoryProduct);
	      
	      // Add product to order table  
	      OrderProduct orderProduct = new OrderProduct();
	      orderProduct.setOrderId(orderId);
	      orderProduct.setTransactionId(transactionId);
	      orderProduct.setProduct(productToUpdate);
	      orderProduct.setMember(member);
	      orderProduct.setQuantity(quantityOfProductPurchased);
	      orderRepo.save(orderProduct);
	      
	      // clear cart products belonging to member    
	      cartProductRepo.deleteById(currentCartProduct.getId());
	    }
	    // Pass info to view to display success page
	    model.addAttribute("cartTotal", cartTotal);
	    model.addAttribute("cartProductList", cartProductList);
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
	    List<OrderProduct> orderList = orderRepo.findByMemberId(loggedInMemberId);

	    // Calculate total price for each seller
	    Map<String, Double> totalPriceBySeller = new HashMap<>();
	    double overallTotalPrice = 0.0;

	    for (OrderProduct orderProduct : orderList) {
	        String sellerName = orderProduct.getProduct().getSeller();
	        double productPrice = orderProduct.getProduct().getPrice();
	        double totalPrice = totalPriceBySeller.getOrDefault(sellerName, 0.0);
	        totalPrice += productPrice * orderProduct.getQuantity();
	        totalPriceBySeller.put(sellerName, totalPrice);
	        overallTotalPrice += productPrice * orderProduct.getQuantity();
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
        List<OrderProduct> orderList = orderRepo.findByMemberId(loggedInMemberId);
        
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
