package chapter6.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import chapter6.beans.User;
import junit.framework.TestCase;

public class UserServiceTest extends TestCase {
	private static final String DRIVER = "com.mysql.jdbc.Driver";
	private static final String URL = "jdbc:mysql://localhost/test";
	private static final String USER = "root";
	private static final String PASSWORD = "root";

	private File file;
	private int usersTestDataSize;

	@Test
	public void testUserSelect() throws Exception {
		//Userのリスト
		List<User> selectUsersList = new ArrayList<>();
		//インスタンス生成
		UserService userService = new UserService();
		//テストデータの文だけfor文を回して、ユーザを取得する
		for (int i = 0; i < this.usersTestDataSize; i++) {
			selectUsersList.add(userService.select(i + 1));
		}
		//件数
		assertEquals(3, selectUsersList.size());
		User user001 = selectUsersList.get(0);
		assertEquals("id=1", "id=" + user001.getId());
		assertEquals("account=test001", "account=" + user001.getAccount());
		assertEquals("name=テスト001", "name=" + user001.getName());
		assertEquals("email=test001@com", "email=" + user001.getEmail());
		assertEquals("password=hoge001", "password=" + user001.getPassword());
		assertEquals("description=テスト001", "description=" +
				user001.getDescription());

		User user002 = selectUsersList.get(1);
		assertEquals("id=2", "id=" + user002.getId());
		assertEquals("account=test002", "account=" + user002.getAccount());
		assertEquals("name=テスト002", "name=" + user002.getName());
		assertEquals("email=test002@com", "email=" + user002.getEmail());
		assertEquals("password=hoge002", "password=" + user002.getPassword());
		assertEquals("description=テスト002", "description=" +
				user002.getDescription());
		User user003 = selectUsersList.get(2);
		assertEquals("id=3", "id=" + user003.getId());
		assertEquals("account=test003", "account=" + user003.getAccount());
		assertEquals("name=テスト003", "name=" + user003.getName());
		assertEquals("email=test003@com", "email=" + user003.getEmail());
		assertEquals("password=hoge003", "password=" + user003.getPassword());
		assertEquals("description=テスト003", "description=" +
				user003.getDescription());
	}

}
