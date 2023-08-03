/**
 * 
 * I declare that this code was written by me, xavier. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Xavier
 * Student ID: 21020683
 * Class: E63C
 * Date created: 2023-May-03 1:26:03 am 
 * 
 */

package e63c.xavier.FYP;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class LoginController {
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("/banned")
	public String banned() {
		return "banned";

	}

}
