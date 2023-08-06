//21041531 - Badi's Code & function

package e63c.xavier.FYP;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

//Import statements

@Controller
public class ItemController {

	// Autowired repositories
	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	// Mapping for the banS page
	@GetMapping("/banS")
	public String banPage() {
		return "banS"; // Return the name of the view for the "banS" page
	}

	// Mapping for the unban page
	@GetMapping("/unban")
	public String unbanPage() {
		return "unban"; // Return the name of the view for the "unban" page
	}

	// Mapping for the hasBanned page
	@GetMapping("/hasBanned")
	public String hasBannedPage() {
		return "hasBanned"; // Return the name of the view for the "hasBanned" page
	}

	// Mapping for the isOnSale page
	@GetMapping("/items/isOnSale/{id}")
	public String isOnSalePage(@PathVariable("id") Integer id, Model model) {
		Item item = itemRepository.getById(id);
		model.addAttribute("item", item);
		return "isOnSale"; // Return the name of the view for the "isOnSale" page
	}

	// Mapping for the OnSale page
	@GetMapping("/OnSale")
	public String onSalePage() {
		return "OnSale"; // Return the name of the view for the "onSale" page
	}

	// Mapping for the view_items page
	@GetMapping("/items")
	public String viewItems(Model model, Authentication authentication) {
		List<Item> listItems = itemRepository.findAll();
		List<Item> filteredItems;
		if (authentication != null && authentication.getAuthorities().stream()
				.anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) {
			filteredItems = listItems;
		} else {
			filteredItems = listItems.stream().filter(item -> !item.isBanned()).collect(Collectors.toList());
		}

		model.addAttribute("listItems", filteredItems);
		return "view_items"; // Return the name of the view for the "view_items" page
	}

	// Mapping for the add_item page
	@GetMapping("/items/add")
	public String addItem(Model model) {
		model.addAttribute("item", new Item()); // Add a new item object to the model with attribute name "item"

		List<Category> catList = categoryRepository.findAll();
		model.addAttribute("catList", catList); // Get all categories and add them to the model with attribute name
												// "catList"

		return "add_item"; // Return the name of the view for the "add_item" page
	}

	// Mapping for saving a new item
	@PostMapping("/items/save")
	public String saveItem(@Valid Item item, BindingResult bindingResult,
			@RequestParam("itemImage") MultipartFile imgFile, Model model) {
		if (bindingResult.hasErrors()) {
			System.out.println(bindingResult.getFieldError());
			List<Category> catList = categoryRepository.findAll();
			model.addAttribute("catList", catList); // Handle validation errors and add categories to the model
			return "add_item"; // Return the name of the view for the "add_item" page
		}

		String imageName = imgFile.getOriginalFilename();
		item.setImgName(imageName); // Set the image name in the item object

		Item savedItem = itemRepository.save(item); // Save the item object to the database

		try {
			// Preparing the directory path for file upload
			String uploadDir = "uploads/items/" + savedItem.getId();
			Path uploadPath = Paths.get(uploadDir);
			System.out.println("Directory path: " + uploadPath);

			// Checking if the upload path exists, if not it will be created.
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			// Prepare path for file
			Path fileToCreatePath = uploadPath.resolve(imageName);
			System.out.println("File path: " + fileToCreatePath);

			// Copy file to the upload location
			Files.copy(imgFile.getInputStream(), fileToCreatePath, StandardCopyOption.REPLACE_EXISTING);

		} catch (IOException io) {
			io.printStackTrace();
		}

		return "redirect:/items"; // Redirect to the view_items page
	}

	// Mapping for editing an item
	@GetMapping("/items/edit/{id}")
	public String editItem(@PathVariable("id") Integer id, Model model) {
		Item item = itemRepository.getById(id); // Get the item by id from the itemRepository
		model.addAttribute("item", item); // Add the item to the model with attribute name "item"

		List<Category> catList = categoryRepository.findAll();
		model.addAttribute("catList", catList); // Get all categories and add them to the model with attribute name
												// "catList"

		return "edit_item"; // Return the name of the view for the "edit_item" page
	}

	// Mapping for saving the updated item
	@PostMapping("/items/edit/{id}")
	public String saveUpdatedItem(@PathVariable("id") Integer id, Item item,
			@RequestParam("itemImage") MultipartFile imgFile) {
		if (!imgFile.isEmpty()) {
			String imageName = imgFile.getOriginalFilename();
			item.setImgName(imageName); // Set the image name in the item object

			Item savedItem = itemRepository.save(item); // Save the item object to the database

			try {
				// Preparing the directory path for file upload
				String uploadDir = "uploads/items/" + savedItem.getId();
				Path uploadPath = Paths.get(uploadDir);
				System.out.println("Directory path: " + uploadPath);

				// Checking if the upload path exists, if not it will be created.
				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}

				// Prepare path for file
				Path fileToCreatePath = uploadPath.resolve(imageName);
				System.out.println("File path: " + fileToCreatePath);

				// Copy file to the upload location
				Files.copy(imgFile.getInputStream(), fileToCreatePath, StandardCopyOption.REPLACE_EXISTING);

			} catch (IOException io) {
				io.printStackTrace();
			}
		} else {
			// No edit to image, save the item object as passed.
			System.out.println("Image name from item object: " + item.getImgName());
			itemRepository.save(item);
		}

		return "redirect:/items"; // Redirect to the view_items page
	}

	// Mapping for deleting an item
	@GetMapping("/items/delete/{id}")
	public String deleteItem(@PathVariable("id") Integer id) {
		itemRepository.deleteById(id); // Delete the item by id from the itemRepository
		return "redirect:/items"; // Redirect to the view_items page
	}

	// Mapping for banning an item
	@GetMapping("/items/ban/{id}")
	public String banItem(@PathVariable("id") Integer id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getAuthorities().stream()
				.anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) {
			Optional<Item> optionalItem = itemRepository.findById(id);
			if (optionalItem.isPresent()) {
				Item item = optionalItem.get();
				item.setBanned(true); // Set the item as banned
				itemRepository.save(item);
			}
			return "redirect:/hasBanned"; // Redirect to the hasBanned page
		} else {
			return "banS"; // Redirect to the banS page if the user is not an admin
		}
	}

	// Mapping for unbanning an item
	@GetMapping("/items/unban/{id}")
	public String unbanItem(@PathVariable("id") Integer id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getAuthorities().stream()
				.anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) {
			Optional<Item> optionalItem = itemRepository.findById(id);
			if (optionalItem.isPresent()) {
				Item item = optionalItem.get();
				item.setBanned(false); // Set the item as unbanned
				itemRepository.save(item);
			}
			return "redirect:/unban"; // Redirect to the unban page
		} else {
			return "banS"; // Redirect to the banS page if the user is not an admin
		}
	}

	// Mapping for setting an item on sale
	@GetMapping("/items/onSale/{id}")
	public String setItemOnSale(@PathVariable("id") Integer id, Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		List<Item> listItems = itemRepository.findAll();
		model.addAttribute("listItems", listItems);

		if (authentication != null && authentication.getAuthorities().stream()
				.anyMatch(role -> role.getAuthority().equals("ROLE_SELLER"))) {
			Optional<Item> optionalItem = itemRepository.findById(id);
			if (optionalItem.isPresent()) {
				Item item = optionalItem.get();
				item.setOnSale(true); // Set the item as on sale

				/*
				 * // Reduce the price by 15% double originalPrice = item.getPrice(); double
				 * discount = originalPrice * 0.15; double discountedPrice = originalPrice -
				 * discount; discountedPrice = Math.round(discountedPrice * 100.0) / 100.0; //
				 * Round to 2 decimal places item.setPrice(discountedPrice);
				 */

				itemRepository.save(item);
			}
			return "redirect:/isOnSale"; // Redirect to the isOnSale page
		} else {
			return "403"; // Redirect to an access denied page if the user is not a seller
		}
	}

	// Mapping for setting an item off sale
	@GetMapping("/items/offSale/{id}")
	public String setItemOffSale(@PathVariable("id") Integer id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getAuthorities().stream()
				.anyMatch(role -> role.getAuthority().equals("ROLE_SELLER"))) {
			Optional<Item> optionalItem = itemRepository.findById(id);
			if (optionalItem.isPresent()) {
				Item item = optionalItem.get();
				if (item.isOnSale()) {
					item.setOnSale(false); // Set the item as off sale

					// Reset the item price to its original price (assuming there's a field to store
					// the original price)
					double originalPrice = item.isOriginalPrice();
					item.setPrice(originalPrice);

					itemRepository.save(item);
				}
			}
			return "redirect:/items"; // Redirect to the items page
		} else {
			return "redirect:/403"; // Redirect to an access denied page if the user is not a seller
		}
	}

	// Mapping for viewing a single item
	@GetMapping("/items/{id}")
	public String viewSingleItem(@PathVariable("id") Integer id, Model model, Authentication authentication) {
		Item item = itemRepository.getById(id);

		if (item.isBanned() && !isAdmin(authentication)) {
			return "banS"; // Redirect to the banS page if the item is banned and the user is not an admin
		}

		model.addAttribute("item", item);

		if (item.isOnSale()) {
			return "onSale"; // Redirect to onSale.html if the item is on sale
		} else {
			return "view_single_item"; // Redirect to view_single_item.html if the item is not on sale
		}
	}

	// Helper method to check if the user is an admin
	private boolean isAdmin(Authentication authentication) {
		return authentication != null
				&& authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));
	}
	
	@GetMapping("/generatead")
    public String generateAd() {
        return "generatead";
    }
}
