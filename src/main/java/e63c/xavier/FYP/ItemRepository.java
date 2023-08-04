//21041531 - Badi's Code & function

package e63c.xavier.FYP;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Integer> {

	public List<Item> findByCategory_Id(int id);

	public List<Item> findByCategory_Id(String keyword);

	public List<Item> findByNameContainingIgnoreCase(String keyword);

}
