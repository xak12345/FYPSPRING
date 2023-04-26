/**
 * 
 * I declare that this code was written by me, xavier. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Xavier
 * Student ID: 21020683
 * Class: E63C
 * Date created: 2023-Apr-26 1:54:34 pm 
 * 
 */

package e63c.xavier.FYP;

import static org.junit.Assert.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.After;
import org.junit.Before;
//import org.junit.Test;

/**
 * @author xavie
 *
 */
public class HelloWorldTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {

		// fail("Not yet implemented");
		System.out.println("This is the testcase for HelloWorld");
		String str1 = "This is the testcase for HelloWorld";
		String str2 = "This is the testcase for HelloWorld";
		assertEquals(str1, str2);

	}

}
