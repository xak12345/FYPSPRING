/**
 * 
 * I declare that this code was written by me, xavier. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Xavier
 * Student ID: 21020683
 * Class: E63C
 * Date created: 2023-Jan-18 12:30:24 am 
 * 
 */

package e63c.xavier.FYP;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class MemberDetailsService implements UserDetailsService{
	
	@Autowired
	private MemberRepository memberRepository;
	
	@Override
	public MemberDetails loadUserByUsername(String username) throws UsernameNotFoundException{
		Member member = memberRepository.findByUsername(username);
		
		if(member == null) {
			throw new UsernameNotFoundException("Could not find user");
			
		}
		
		return new MemberDetails(member);
		
	}
	
	public void updateResetPasswordToken(String token, String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email);
        if (member != null) {
        	member.setResetPasswordToken(token);
        	memberRepository.save(member);
        } else {
            throw new UsernameNotFoundException("Could not find any member with the email " + email);
        }
    }
     
    public Member getByResetPasswordToken(String token) {
        return memberRepository.findByResetPasswordToken(token);
    }
     
    public void updatePassword(Member member, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        member.setPassword(encodedPassword);
         
        member.setResetPasswordToken(null);
        memberRepository.save(member);
    }

}

