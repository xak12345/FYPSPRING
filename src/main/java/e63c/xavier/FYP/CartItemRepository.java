/**
 * 
 * I declare that this code was written by me, xavier. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Xavier
 * Student ID: 21020683
 * Class: E63C
 * Date created: 2023-Feb-09 9:51:19 am 
 * 
 */

package e63c.xavier.FYP;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
	
	public List<CartItem>findByMemberId(int id);

	public CartItem findByMemberIdAndItemId(int memberId, int itemId);

}
