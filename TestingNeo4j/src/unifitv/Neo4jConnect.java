package unifitv;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class Neo4jConnect {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GraphDatabaseFactory graphDbFactory = new GraphDatabaseFactory();
		File storeFile = new File("F:/MMU_FCI_RECOMMENDER_SYSTEM/DataBase/demo");
		GraphDatabaseService graphDb = graphDbFactory
				.newEmbeddedDatabase(storeFile);
		String p1 = "1000896702";
		String s = "MATCH(n:Subscriber) WHERE n.BILLING_ACCOUNT_NUMBER="
				+ "'1000896702'" + "  RETURN n.ACCOUNT";
		String s1 = "MATCH(n:Subscriber) WHERE n.BILLING_ACCOUNT_NUMBER=" + "'"
				+ p1 + "'" + "  RETURN n.ACCOUNT";

		String s2 = "MATCH a=(s:Subscriber)-[u:USE]->(cu:Channel_Usage)<-[IS]-(c:Channel) RETURN SUM(cu.HOURS)*60  AS Usage_Minutes,c.CONTENTCODE,s.BILLING_ACCOUNT_NUMBER ORDER BY Usage_Minutes";
		String s3 = "MATCH (s:Subscriber) RETURN s.BILLING_ACCOUNT_NUMBER";
		Result er = graphDb.execute(s3);
		List<Double> chnlUsge = new ArrayList<Double>();
		List<Integer> userList = new ArrayList<Integer>();
		Map<Integer, Integer> numberofchanellwatch = new HashMap<Integer, Integer>();
		Map<Integer, Double> averageUserRating = new HashMap<Integer, Double>();
		Map<Integer, List<String>> listofchannelwatchbyUser;
		listofchannelwatchbyUser = new HashMap<Integer, List<String>>();
		while (er.hasNext()) {
			Map<String, Object> row = er.next();
			for (String key : er.columns()) {
				userList.add(Integer.parseInt(row.get(key).toString()));
				/*
				 * if(key.equalsIgnoreCase("Usage_Minutes")){
				 * chnlUsge.add(Double.parseDouble(row.get(key).toString())); }
				 */
			}
			// System.out.println();
		}
		for (int i = 0; i < userList.size(); i++) {
			String id = userList.get(i).toString();
			String s4 = "MATCH a=(s:Subscriber)-[u:USE]->(cu:Channel_Usage)<-[IS]-(c:Channel) WHERE s.BILLING_ACCOUNT_NUMBER="
					+ "'"+id+"'"
					+ "RETURN SUM(cu.HOURS)*60  AS Usage_Minutes,c.CONTENTCODE,s.BILLING_ACCOUNT_NUMBER ORDER BY Usage_Minutes";
			Result er1 = graphDb.execute(s4);

			int count = 0;
			double pref = 0.0;
			List<String> ls = new ArrayList<String>();
			while (er1.hasNext()) {
				Map<String, Object> row = er1.next();
				double temp = Double.parseDouble(row.get("Usage_Minutes")
						.toString());

				pref = pref + temp;
				// pref = pref + resultSet.getDouble("rating");
				ls.add(row.get("c.CONTENTCODE").toString());
				count++;
			}
			System.out.println("USER: " + userList.get(i) + " " + pref
					+ " count : " + count);
			listofchannelwatchbyUser.put(userList.get(i), ls);
			System.out.println("USER id: " + userList.get(i) + " "
					+ ls.toString());
			numberofchanellwatch.put(userList.get(i), count);
			System.out.println("Number of movies watch by USER: "
					+ userList.get(i) + " " + count);
			pref = pref / count;
			averageUserRating.put(userList.get(i),
					Math.round(pref * 100.0) / 100.0);

		}

		// System.out.println(er.resultAsString());
		// System.out.println(er.columns());

	}
}
