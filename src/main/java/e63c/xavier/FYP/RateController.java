/**
 * 
 * I declare that this code was written by me, 21022015. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: zhen hong
 * Student ID: 21022015
 * Class: ABC1
 * Date created: 2023-May-31 12:16:50 pm 
 * 
 */

package e63c.xavier.FYP;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 21022015
 *
 */
@Controller
public class RateController {
	@Autowired
	private RateRepository rateRepository;
	@Autowired
	private ItemRepository itemRepository;
	@Autowired
	private MemberRepository memberRepository;

	@GetMapping("/rate/create/{itemId}/{itemQuantity}")
	public String showRatingForm(@PathVariable("itemId") int itemId,
			@PathVariable("itemQuantity") int itemQuantity, Model model, Principal principal) {

		// Get the currently logged-in user's memberId
		MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int memberId = loggedInMember.getMember().getId();

		// Get the item from the database using the itemId
		Item item = itemRepository.findById(itemId).orElse(null);
		if (item == null) {
			// Handle error here, item not found for the given itemId
			return "redirect:/orderhistory"; // Or a custom error page
		}

		// Pass the memberId, item, and itemQuantity to the view
		model.addAttribute("memberId", memberId); // Pass memberId to the template
		model.addAttribute("item", item); // Pass item to the template
		model.addAttribute("itemQuantity", itemQuantity);
		model.addAttribute("rating",new Rate()); // For the form submission

		return "rate_form";
	}

	@PostMapping("/rate/create")
	public String createRating(@ModelAttribute("rating") Rate rating, @RequestParam("itemId") Integer itemId,
			@RequestParam("memberId") Integer memberId, @RequestParam("itemImage") MultipartFile imgFile, Model model) {
		// Check if itemId or memberId is null, and handle the error if needed
		if (itemId == null || memberId == null) {
			// Handle error here, itemId or memberId is not provided in the form
			return "redirect:/orderhistory"; // Or a custom error page
		}

		// Retrieve the item and Member entities from the database using itemId
		// and memberId
		Item item = itemRepository.findById(itemId).orElse(null);
		Member member = memberRepository.findById(memberId).orElse(null);

		if (item == null || member == null) {
			// Handle error here, item or Member not found for the given IDs
			return "redirect:/orderhistory"; // Or a custom error page
		}

		// Set the item and Member entities in the Rate object
		rating.setitem(item);
		rating.setMember(member);
		
		String imageName = imgFile.getOriginalFilename();
		rating.setImgName(imageName);
		Rate savedRating = rateRepository.save(rating);
		try {
			String uploadDir = "uploads/rate/" + savedRating.getId();
			Path uploadPath = Paths.get(uploadDir);
			System.out.println("Directory path: " + uploadPath);

			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			Path fileToCreatePath = uploadPath.resolve(imageName);
			System.out.println("File path: " + fileToCreatePath);

			Files.copy(imgFile.getInputStream(), fileToCreatePath, StandardCopyOption.REPLACE_EXISTING);

		} catch (IOException io) {
			io.printStackTrace();
		}
		// Save the rating to the database using the rateRepository
		rateRepository.save(rating);

		// Redirect back to the orderhistory page
		return "redirect:/purchase_history";
	}
	@GetMapping("/rate/view/{itemId}")
	public String viewitemRatings(@PathVariable("itemId") int itemId, Model model) {
	    // Get the item from the database using the itemId
	    Item item = itemRepository.findById(itemId).orElse(null);
	    if (item == null) {
	        // Handle error here, item not found for the given itemId
	        return "redirect:/purchasehistory"; // Or a custom error page
	    }

	    // Get the ratings for the given itemId
	    List<Rate> ratings = rateRepository.findByitemId(itemId);

	    // Pass the item and its ratings to the view
	    model.addAttribute("item", item);
	    model.addAttribute("ratings", ratings);

	    return "item_ratings";
	}


}
