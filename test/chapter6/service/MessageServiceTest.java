package chapter6.service;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import chapter6.beans.Message;
import chapter6.beans.UserMessage;
import chapter6.utils.DBUtil;

// 1. テストクラス名は任意のものに変更してください。
// 2. L.23~86は雛形として使用してください。
// 3．L.44のファイル名は各自作成したファイル名に書き換えてください。
public class MessageServiceTest {

	private File file;
	private int testDataSize;

	@Before
	public void setUp() throws Exception {

		IDatabaseConnection connection = null;
		try {
			Connection conn = DBUtil.getConnection();
			connection = new DatabaseConnection(conn);

			//(2)現状のバックアップを取得
			QueryDataSet partialDataSet = new QueryDataSet(connection);
			partialDataSet.addTable("users");
			partialDataSet.addTable("messages");

			file = File.createTempFile("temp", ".xml");
			FlatXmlDataSet.write(partialDataSet,
					new FileOutputStream(file));

			//(3)テストデータを投入する
			IDataSet dataSetBranch = new FlatXmlDataSet(new File("message_data_init"));
			DatabaseOperation.CLEAN_INSERT.execute(connection, dataSetBranch);

			// テスト結果として期待されるべきデータを表すインスタンスを取得
			IDataSet expectedDataSet = new FlatXmlDataSet(new File("messages_data_init.xml"));

			ITable expectedTable = expectedDataSet.getTable("users");
			this.testDataSize = expectedTable.getRowCount();

			DBUtil.commit(conn);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
			}
		}
	}

	@After
	public void tearDown() throws Exception {
		IDatabaseConnection connection = null;
		try {
			Connection conn = DBUtil.getConnection();
			connection = new DatabaseConnection(conn);

			IDataSet dataSet = new FlatXmlDataSet(file);
			DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);

			DBUtil.commit(conn);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			//一時ファイルの削除
			if (file != null) {
				file.delete();
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
			}

		}
	}

	// 以下に課題のテストメソッドを作成

	@Test
	public void testInsertMessage() throws Exception {

		List<Message> insertMessagesList = new ArrayList<>();

		//テストのインスタンスを生成
		Message message001 = new Message();
		message001.setText("テストデータ004");
		message001.setUserId(1);
		insertMessagesList.add(message001);

		Message message002 = new Message();
		message002.setText("テストデータ005");
		message002.setUserId(2);
		insertMessagesList.add(message002);
		Message message003 = new Message();

		message003.setText("テストデータ006");
		message003.setUserId(3);
		insertMessagesList.add(message003);
		MessageService messageService = new MessageService();

		for (int i = 0; i < insertMessagesList.size(); i++) {
			messageService.insert(insertMessagesList.get(i));
		}

		//データ
		IDatabaseConnection connection = null;

		try {
			Connection conn = DBUtil.getConnection();
			connection = new DatabaseConnection(conn);

			//メソッド実行した実際のテーブル
			IDataSet databaseDataSet = connection.createDataSet();
			ITable actualTable = databaseDataSet.getTable("messages");

			// テスト結果として期待されるべきテーブルデータを表すITableインスタンス
			IDataSet expectedDataSet = new FlatXmlDataSet(new File("messages_data_insert.xml"));
			ITable expectedTable = expectedDataSet.getTable("messages");
			//期待されるITableと実際のITableの比較
			//id、created_date、updated_dateを除いたデータを確認
			Assertion.assertEqualsIgnoreCols(actualTable, expectedTable,
					new String[] { "id", "created_date", "updated_date" });

		} finally {
			if (connection != null)
				connection.close();
		}
	}

	@Test
	public void testSelectMessage() throws Exception
	{
		//参照メソッドの実行
		MessageService messageService = new MessageService();
		List<UserMessage> resultList = new ArrayList<>();
	}

}