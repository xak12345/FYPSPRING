/**
 * 
 * I declare that this code was written by me, kangz. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: zhen hong
 * Student ID: 21022015
 * Class: ABC1
 * Date created: 2022-Dec-03 12:45:05 am 
 * 
 */

package e63c.xavier.FYP;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author kangz
 *
 */
public interface RateRepository extends JpaRepository<Rate, Integer> {

	/**
	 * @param itemId
	 * @return
	 */
	List<Rate> findByitemId(int itemId);


}
