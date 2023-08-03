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
public class ProductController {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@GetMapping("/products")
	public String viewProducts(Model model) {
		List<Product> listProducts = productRepository.findAll();
		model.addAttribute("listProducts", listProducts);
		return "products";

	}

	@GetMapping("/products/add")
	public String addProduct(Model model) {
		model.addAttribute("product", new Product());
		List<Category> catList = categoryRepository.findAll();
		model.addAttribute("catList", catList);
		return "add_product";
	}

	@GetMapping("/products/{id}")
	public String viewSingleProduct(@PathVariable("id") Integer id, Model model) {
		Product product = productRepository.getById(id);
		model.addAttribute("product", product);
		return "view_single_products";
	}

	@PostMapping("/products/save")
	public String saveProduct(@Valid Product product, BindingResult bindingResult,
			@RequestParam("productImage") MultipartFile imgFile, Model model) {

		if (bindingResult.hasErrors()) {
			System.out.println(bindingResult.getFieldError());
			List<Category> catList = categoryRepository.findAll();
			model.addAttribute("catList", catList);
			return "add_product";
		}

		String imageName = imgFile.getOriginalFilename();

		// Set the image name in product object
		product.setImgName(imageName);

		// Save the product obj to the db
		Product savedProduct = productRepository.save(product);

		try {
			// Preparing the directory path
			String uploadDir = "uploads/products/" + savedProduct.getId();
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

		return "redirect:/products";
	}

	@GetMapping("/products/edit/{id}")
	public String editProduct(@PathVariable("id") Integer id, Model model) {
		Product product = productRepository.getById(id);
		model.addAttribute("product", product);
		List<Category> catList = categoryRepository.findAll();
		model.addAttribute("catList", catList);
		return "edit_products";

	}

	@PostMapping("/products/edit/{id}")
	public String saveUpdatedProduct(Product product, @RequestParam("productImage") MultipartFile imgFile) {
		String imageName = imgFile.getOriginalFilename();
		product.setImgName(imageName);
		Product savedProduct = productRepository.save(product);
		try {
			String uploadDir = "uploads/products/" + savedProduct.getId();
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
		return "redirect:/products";
	}

	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable("id") Integer id) {
		productRepository.deleteById(id);
		return "redirect:/products";
	}

	@GetMapping("/phones")
	public String getPhone(Model model) {
		Category cat = categoryRepository.findById(1).get();
		Set<Product> listOfProducts = cat.getProducts();
		model.addAttribute("listOfProducts", listOfProducts);
		return "phones";
	}

	@GetMapping("/earbuds") public String getEarbud(Model model) { 
	//find the product list which has category id of earbud
	  Category cat = categoryRepository.findById(2).get(); Set<Product> listOfProducts =
	  cat.getProducts();
	  model.addAttribute("listOfProducts", listOfProducts);
	  return "earbuds"; }

	@GetMapping("/laptops")
	public String getLaptop(Model model) {
		Category cat = categoryRepository.findById(3).get();
		Set<Product> listOfProducts = cat.getProducts();
		model.addAttribute("listOfProducts", listOfProducts);
		return "laptops";
	}

	@GetMapping("/mouses")
	public String getMouse(Model model) {
		Category cat = categoryRepository.findById(4).get();
		Set<Product> listOfProducts = cat.getProducts();
		model.addAttribute("listOfProducts", listOfProducts);
		return "mouses";
	}
	
	@GetMapping("/generatead")
    public String generateAd() {
        return "generatead";
    }

//	@GetMapping(path = "/products/category/{id}")
//	public String getProductsbyCategory(@PathVariable Integer id, Model model) {
//
//		List<Product> listProducts = productRepository.findByCategory_Id(id);
//
//		model.addAttribute("listProducts", listProducts);
//		return "products";
//	}

}
