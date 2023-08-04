/**
 * 
 * I declare that this code was written by me, xavier. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Xavier
 * Student ID: 21020683
 * Class: E63C
 * Date created: 2023-May-03 1:29:48 am 
 * 
 */

package e63c.xavier.FYP;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MainController {
	@Autowired
	private ItemRepository itemRepository;

	@GetMapping("/")
	public String home(Model model) {
		List<Item> listItems = itemRepository.findAll();
		model.addAttribute("listItems", listItems);
		return "index";
	}

	@GetMapping("/aboutus")
	public String aboutUs() {
		return "aboutus";
	}

	@GetMapping("/contactus")
	public String ourContact() {
		return "contactus";
	}

	@GetMapping("/403")
	public String error403() {
		return "403";
	}

	@GetMapping("/success2")
	public String success2() {
		return "success2";
	}

}
