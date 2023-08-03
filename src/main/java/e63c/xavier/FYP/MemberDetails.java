/**
 * 
 * I declare that this code was written by me, xavier. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Xavier
 * Student ID: 21020683
 * Class: E63C
 * Date created: 2023-Jan-18 12:29:41 am 
 * 
 */

package e63c.xavier.FYP;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class MemberDetails implements UserDetails {
	
	private Member member;
	
	public MemberDetails(Member member) {
		this.member = member;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities(){
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(member.getRole());
		return Arrays.asList(authority);
	}
	
	@Override
	public String getPassword() {
		return member.getPassword();
	}
	
	@Override
	public String getUsername() {
		return member.getUsername();
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

}
