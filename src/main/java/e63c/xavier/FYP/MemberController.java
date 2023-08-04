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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityManager;
import javax.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MemberController {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private OrderItemRepository orderRepo;
    

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
    public String processSignupForm(@ModelAttribute("member") @Valid Member member, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
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
        memberRepository.save(member);

        // Redirect to a success page or login page
        return "redirect:/signupsuccess";
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
