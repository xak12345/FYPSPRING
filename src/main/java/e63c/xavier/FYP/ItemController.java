/**
 * 
 * I declare that this code was written by me, xavier. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Xavier
 * Student ID: 21020683
 * Class: E63C
 * Date created: 2022-Nov-17 3:04:53 PM 
 * 
 */

package e63c.xavier.FYP;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ItemController {

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@GetMapping("/items")
	public String viewItems(Model model) {
		List<Item> listItems = itemRepository.findAll();
		model.addAttribute("listItems", listItems);
		return "items";

	}

	@GetMapping("/items/add")
	public String addItem(Model model) {
		model.addAttribute("item", new Item());
		List<Category> catList = categoryRepository.findAll();
		model.addAttribute("catList", catList);
		return "add_item";
	}

	@GetMapping("/items/{id}")
	public String viewSingleItem(@PathVariable("id") Integer id, Model model) {
		Item item = itemRepository.getById(id);
		model.addAttribute("item", item);
		return "view_single_items";
	}

	@PostMapping("/items/save")
	public String saveItem(@Valid Item item, BindingResult bindingResult,
			@RequestParam("itemImage") MultipartFile imgFile, Model model) {

		if (bindingResult.hasErrors()) {
			System.out.println(bindingResult.getFieldError());
			List<Category> catList = categoryRepository.findAll();
			model.addAttribute("catList", catList);
			return "add_item";
		}

		String imageName = imgFile.getOriginalFilename();

		// Set the image name in item object
		item.setImgName(imageName);

		// Save the item obj to the db
		Item savedItem = itemRepository.save(item);

		try {
			// Preparing the directory path
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

		return "redirect:/items";
	}

	@GetMapping("/items/edit/{id}")
	public String editItem(@PathVariable("id") Integer id, Model model) {
		Item item = itemRepository.getById(id);
		model.addAttribute("item", item);
		List<Category> catList = categoryRepository.findAll();
		model.addAttribute("catList", catList);
		return "edit_items";

	}

	@PostMapping("/items/edit/{id}")
	public String saveUpdatedItem(Item item, @RequestParam("itemImage") MultipartFile imgFile) {
		String imageName = imgFile.getOriginalFilename();
		item.setImgName(imageName);
		Item savedItem = itemRepository.save(item);
		try {
			String uploadDir = "uploads/items/" + savedItem.getId();
			Path uploadPath = Paths.get(uploadDir);
			System.out.println("Directory path:" + uploadPath);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			Path fileToCreatePath = uploadPath.resolve(imageName);
			System.out.println("File Path: " + fileToCreatePath);
			Files.copy(imgFile.getInputStream(), fileToCreatePath, StandardCopyOption.REPLACE_EXISTING);

		} catch (IOException io) {
			io.printStackTrace();
		}
		return "redirect:/items";
	}

	@GetMapping("/items/delete/{id}")
	public String deleteItem(@PathVariable("id") Integer id) {
		itemRepository.deleteById(id);
		return "redirect:/items";
	}

	@GetMapping("/phones")
	public String getPhone(Model model) {
		Category cat = categoryRepository.findById(1).get();
		Set<Item> listOfItems = cat.getItems();
		model.addAttribute("listOfItems", listOfItems);
		return "phones";
	}

	@GetMapping("/earbuds") public String getEarbud(Model model) { 
	//find the item list which has category id of earbud
	  Category cat = categoryRepository.findById(2).get(); Set<Item> listOfItems =
	  cat.getItems();
	  model.addAttribute("listOfItems", listOfItems);
	  return "earbuds"; }

	@GetMapping("/laptops")
	public String getLaptop(Model model) {
		Category cat = categoryRepository.findById(3).get();
		Set<Item> listOfItems = cat.getItems();
		model.addAttribute("listOfItems", listOfItems);
		return "laptops";
	}

	@GetMapping("/mouses")
	public String getMouse(Model model) {
		Category cat = categoryRepository.findById(4).get();
		Set<Item> listOfItems = cat.getItems();
		model.addAttribute("listOfItems", listOfItems);
		return "mouses";
	}
	
	@GetMapping("/generatead")
    public String generateAd() {
        return "generatead";
    }

//	@GetMapping(path = "/items/category/{id}")
//	public String getItemsbyCategory(@PathVariable Integer id, Model model) {
//
//		List<Item> listItems = itemRepository.findByCategory_Id(id);
//
//		model.addAttribute("listItems", listItems);
//		return "items";
//	}

}
