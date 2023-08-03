/**
 * 
 * I declare that this code was written by me, xavier. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Xavier
 * Student ID: 21020683
 * Class: E63C
 * Date created: 2023-Jan-18 12:02:19 am 
 * 
 */

package e63c.xavier.FYP;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Integer> {

	public Member findByUsername(String username);
	
	public boolean existsByUsername(String username);
	
	public Member findByEmail(String email); 
	
	public Member findByResetPasswordToken(String token);
	

}
