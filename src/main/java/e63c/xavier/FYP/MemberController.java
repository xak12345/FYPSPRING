/**
 * 
 * I declare that this code was written by me, xavier. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Xavier
 * Student ID: 21020683
 * Class: E63C
 * Date created: 2023-Jan-18 12:01:40 am 
 * 
 */

package e63c.xavier.FYP;

import org.hibernate.Session;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityManager;
import javax.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class MemberController {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private OrderItemRepository orderRepo;

	@Autowired
	private JavaMailSender javaMailSender;

	@GetMapping("/members")
	public String viewMember(Model model) {
		Session session = entityManager.unwrap(Session.class);
		org.hibernate.Filter filter = session.enableFilter("deletedItemFilter");
		filter.setParameter("isDeleted", false);
		List<Member> listMembers = memberRepository.findAll();
		session.disableFilter("deletedItemFilter");

		// Calculate and add the purchase history total price for each member
		for (Member member : listMembers) {
			double totalPrice = calculatePurchaseHistoryTotalPrice(member.getId());
			member.setTotalPrice(totalPrice);
		}

		// Add the totalPriceBySeller attribute to the model
		List<OrderItem> orderList = orderRepo.findAll();
		Map<String, Double> totalPriceBySeller = new HashMap<>();
		for (OrderItem orderItem : orderList) {
			String seller = orderItem.getItem().getSeller();
			double price = orderItem.getItem().getPrice();
			totalPriceBySeller.put(seller, totalPriceBySeller.getOrDefault(seller, 0.0) + price);
		}
		model.addAttribute("totalPriceBySeller", totalPriceBySeller);

		model.addAttribute("listMembers", listMembers);
		return "view_members";
	}

	// Helper method to calculate the purchase history total price for a member
	private double calculatePurchaseHistoryTotalPrice(int memberId) {
		List<OrderItem> orderList = orderRepo.findByMemberId(memberId);
		double totalPrice = 0.0;
		for (OrderItem orderItem : orderList) {
			totalPrice += orderItem.getItem().getPrice();
		}
		return totalPrice;
	}

	// add
	@GetMapping("/members/add")
	public String addMember(Model model) {
		model.addAttribute("member", new Member());
		return "add_member";
	}

	@PostMapping("/members/save")
	public String saveMember(Member member, RedirectAttributes redirectAttribute) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(member.getPassword());
		member.setPassword(encodedPassword);
		memberRepository.save(member);
		redirectAttribute.addFlashAttribute("success", "Member registered");
		return "redirect:/members";
	}

	@GetMapping("/signup")
	public String showSignupForm(Model model) {
		model.addAttribute("member", new Member());
		return "signup";
	}

	@PostMapping("/signup")
	public String processSignupForm(@ModelAttribute("member") @Valid Member member, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			// Handle validation errors
			return "signup";
		}

		// Check if the username already exists
		if (memberRepository.existsByUsername(member.getUsername())) {
			// Handle the case when the username is already taken
			redirectAttributes.addAttribute("error", "username_taken");
			return "redirect:/failure";
		}

		// Save the member to the database
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(member.getPassword());
		member.setPassword(encodedPassword);
		member.setVerificationCode(UUID.randomUUID().toString()); // Generate a random verification code
		member.setEnabled(false); // Set user as not enabled until they verify their email
		memberRepository.save(member);

		// Code to send a confirmation email to the new user
		String userEmail = member.getEmail();
		String subject = "Welcome to XK Electronics! Please confirm your email address";
		String verificationLink = "http://localhost:8080/verify?code=" + member.getVerificationCode();
		String loginUrl = "http://localhost:8080/login";
		String body = "Dear " + member.getName()
				+ ",\n\nWelcome to XK Electronics! To complete your registration, please click on the following link to verify your email address:\n\n"
				+ verificationLink
				+ "\n\nTo login, please visit: " + loginUrl
				+ "\n\nIf you have any questions or need assistance, please don't hesitate to reach out to our support team.\n\nHappy shopping!\nXK Electronics Team";
				
		sendEmail(userEmail, subject, body);

		// Add a success message to display on the Thymeleaf template
		redirectAttributes.addAttribute("successMessage",
				"You have successfully signed up! A confirmation email has been sent to your email address.");

		// Redirect to a success page or login page
		return "redirect:/signupsuccess";
	}

	private void sendEmail(String to, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(body);
		javaMailSender.send(message);

	}

	@GetMapping("/verify")
	public String verifyEmail(@RequestParam("code") String verificationCode, RedirectAttributes redirectAttributes) {
		Member member = memberRepository.findByVerificationCode(verificationCode);

		if (member == null) {
			// Handle the case when the verification code is invalid or not found
			redirectAttributes.addAttribute("error", "verification_failed");
			return "failure";
		}

		// Mark the user as enabled (verified) and save to the database
		member.setEnabled(true);
		memberRepository.save(member);

		// Add a success message to display on the Thymeleaf template
		redirectAttributes.addAttribute("successMessage", "Email verification successful! You can now login.");

		return "redirect:/"; // Redirect to the home page (localhost)
	}

	@GetMapping("/failure")
	public String showFailurePage() {
		return "failure";
	}

	@GetMapping("/signupsuccess")
	public String showSignupSuccessPage() {
		return "signupsuccess";
	}

	// edit
	@GetMapping("/members/edit/{id}")
	public String editMember(@PathVariable("id") Integer id, Model model) {
		Member member = memberRepository.getById(id);
		model.addAttribute("member", member);
		return "edit_member";
	}

	@PostMapping("/members/edit/{id}")
	public String saveUpdatedMember(@PathVariable("id") Integer id, Member member) {
		memberRepository.save(member);
		return "redirect:/members";
	}

	// delete
	@GetMapping("/members/delete/{id}")
	public String deleteMember(@PathVariable("id") Integer id) {
		Member member = memberRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + id));

		member.setDeleted(true);
		memberRepository.save(member);
		return "redirect:/members";
	}
	

@GetMapping("/profile")
 public String viewProfile(Model model, @AuthenticationPrincipal MemberDetails principal) {
     model.addAttribute("principal", principal);
     return "profile";
 }

//    @GetMapping("/members/undelete/{id}")
//    public String undeleteMember(@PathVariable("id") Integer id) {
//        Member member = memberRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + id));
//
//        member.setDeleted(false);
//        memberRepository.save(member);
//        return "redirect:/members";
//    }

}
