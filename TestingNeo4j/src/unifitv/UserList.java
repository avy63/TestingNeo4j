package unifitv;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class UserList {
	public List<Integer> userList;
	GraphDatabaseFactory graphDbFactory = new GraphDatabaseFactory();
	File storeFile = new File("F:/MMU_FCI_RECOMMENDER_SYSTEM/DataBase/demo");
	GraphDatabaseService graphDb = graphDbFactory
			.newEmbeddedDatabase(storeFile);
	
	public UserList() throws Exception{
		userList=new ArrayList<Integer>();
		String query = "MATCH (s:Subscriber) RETURN s.BILLING_ACCOUNT_NUMBER";

		Result er = graphDb.execute(query);
		while (er.hasNext()) {
			Map<String, Object> row = er.next();
			for (String key : er.columns()) {

				userList.add(Integer.parseInt(row.get(key).toString()));
			}

		}
		System.out.println("User List size" + userList.size());
		System.out.println("User List " + userList.toString());
		 graphDb.shutdown();
	}
	public List<Integer> getUserList(){
		return userList;
	}
	public static void main(String[] args) throws Exception {
		UserList list=new UserList();
	}
}
