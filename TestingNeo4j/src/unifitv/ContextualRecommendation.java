package unifitv;

import java.io.BufferedReader;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class ContextualRecommendation {

	public int userID;
	public String city;
	public String gender;
	public int age;
	public String userName = "";
	public KmeansForTM kmeansForTM;
	public Map<Double, Double> ratingMap;
	public UserList userListobJ;
	public List<Integer> userList;
	public static Map<Integer, Integer> numberofchanellwatch = null;
	public static Map<Integer, Double> averageUserRating = null;
	public Map<Integer, List<String>> listofchannelwatchbyUser;
	public Map<Integer, Integer> similaritybetweenusr;
	public Map<Integer, Integer> KmostSimilarUser;
	Map<String, Double> prefOfUser = new HashMap<String, Double>();
	/*GraphDatabaseFactory graphDbFactory = new GraphDatabaseFactory();
	File storeFile = new File("F:/MMU_FCI_RECOMMENDER_SYSTEM/DataBase/demo");*/
	GraphDatabaseService graphDb;

	public ContextualRecommendation(int usrIDtemp,GraphDatabaseService graphDb) throws Exception {
		this.userID = usrIDtemp;
		// System.out.println(userID);
		// dbConnection = new DBConnection();
		this.graphDb=graphDb;
		userList = new ArrayList<Integer>();
		ratingMap = new HashMap<Double, Double>();
		KmostSimilarUser = new HashMap<Integer, Integer>();

		numberofchanellwatch = new HashMap<Integer, Integer>();
		averageUserRating = new HashMap<Integer, Double>();
		listofchannelwatchbyUser = new HashMap<Integer, List<String>>();
		similaritybetweenusr = new HashMap<Integer, Integer>();

		/*
		 * kmeansForTM = new KmeansForTM(); ratingMap =
		 * kmeansForTM.getRatingMap();
		 */
		// System.out.println(ratingMap.toString());

		generateUserList();

		generateRatingMap();
		String query2 = "MATCH (s:Subscriber)RETURN s.BILLING_ACCOUNT_NUMBER, s.LOGIN_ID, s.ACCOUNT, s.CITY,s.AGE,s.GENDER";
		// System.out.println(query);
		Result er = graphDb.execute(query2);
		while (er.hasNext()) {
			Map<String, Object> row = er.next();
			if (row.get("s.CITY") != null) {
				city = row.get("s.CITY").toString();
			} else {
				city = "PUCHONG";
			}
			if (row.get("s.GENDER") != null) {
				gender = row.get("s.GENDER").toString();
			} else {
				gender = "M";
			}
			if (row.get("s.AGE") != null) {
				age = Integer.parseInt(row.get("s.AGE").toString());
			} else {
				age = 44;
			}

		}

		// System.out.println(city+" "+gender+" "+age);
		calculateuserStatistics();
		if (KmostSimilarUser.size() > 0) {
			CalculateUserpreferences();
		} else {
			JOptionPane.showMessageDialog(null,
					"You donot have any similar user");
		}
	}

	public void generateRatingMap() {
		// TODO Auto-generated method stub
		List<Double> chnlUsge;
		double m1, m2, m3, m4, m5, a, b, c, d, e;
		double m[] = new double[5];
		int n = 0, i;
		double sum1, sum2, sum3, sum4, sum5;
		BufferedReader br = null;
		ratingMap=new HashMap<Double, Double>();
		 chnlUsge=new ArrayList<Double>();
		 try{
			 String s2 = "MATCH a=(s:Subscriber)-[u:USE]->(cu:Channel_Usage)<-[IS]-(c:Channel) RETURN SUM(cu.HOURS)*60  AS Usage_Minutes,c.CONTENTCODE,s.BILLING_ACCOUNT_NUMBER ORDER BY Usage_Minutes";
				Result er = graphDb.execute(s2);
				while (er.hasNext()) {
					Map<String, Object> row = er.next();
					for (String key : er.columns()) {
						// System.out.printf( "%s ",row.get( key ) );
						if (key.equalsIgnoreCase("Usage_Minutes")) {
							double tempo=Math.round(Double
									.parseDouble(row.get(key).toString()) * 100.0) / 100.0;
							chnlUsge.add(tempo);
						}
					}
					// System.out.println();
				}
		 }catch(Exception ex){
			 JOptionPane.showMessageDialog(null,ex);
		 }
			//System.out.println(chnlUsge.toString());
			
			//System.out.println();
			// TODO Auto-generated method stub
			
			
			 boolean flag;
			 a=chnlUsge.get(0);
			 b=chnlUsge.get(2);
			 c=chnlUsge.get(5);
			 d=chnlUsge.get(7);
			 e=chnlUsge.get(10);
			 m[0]=a;
			 m[1]=b;
			 m[2]=c;
			 m[3]=d;
			 m[4]=e;
			 List<Double>cluster1=new ArrayList<Double>();
			 List<Double>cluster2=new ArrayList<Double>();
			 List<Double>cluster3=new ArrayList<Double>();
			 List<Double>cluster4=new ArrayList<Double>();
			 List<Double>cluster5=new ArrayList<Double>();
			 do{
				 sum1=0;
				 sum2=0;
				 sum3=0;
				 sum4=0;
				 sum5=0;
				 cluster1=new ArrayList<Double>();
				 cluster2=new ArrayList<Double>();
				 cluster3=new ArrayList<Double>();
				 cluster4=new ArrayList<Double>();
				 cluster5=new ArrayList<Double>();
				// n++;
				int j=0;
				int tempJ=0;
				 for(i=0;i<chnlUsge.size();i++){
					 double diff=1000000000;
					 double temp=0.0;
					 for(j=0;j<5;j++){
						 temp=Math.abs(chnlUsge.get(i)-m[j]);
						 if(temp<diff){
							 diff=temp;
							 tempJ=j;
						 }
					}
					 if(tempJ==0){
						 cluster1.add(chnlUsge.get(i));
					 }else if(tempJ==1){
						 cluster2.add(chnlUsge.get(i));
					 }else if(tempJ==2){
						 cluster3.add(chnlUsge.get(i));
					 }else if(tempJ==3){
						 cluster4.add(chnlUsge.get(i));
					 }else {
						 cluster5.add(chnlUsge.get(i));
					 }
				 }
				 for(i=0;i<cluster1.size();i++){
					 sum1=sum1+cluster1.get(i);
				 }
				 for(i=0;i<cluster2.size();i++){
					 sum2=sum2+cluster2.get(i);
				 }
				 for(i=0;i<cluster3.size();i++){
					 sum3=sum3+cluster3.get(i);
				 }
				 for(i=0;i<cluster4.size();i++){
					 sum4=sum4+cluster4.get(i);
				 }
				 for(i=0;i<cluster5.size();i++){
					 sum5=sum5+cluster5.get(i);
				 }
				 a=m[0];
				 b=m[1];
				 c=m[2];
				 d=m[3];
				 e=m[4];
				 m[0] = (sum1 / cluster1.size());
				 m[1] = (sum2 / cluster2.size());
				 m[2] = (sum3 / cluster3.size());
				 m[3] = (sum4 / cluster4.size());
				 m[4] = (sum5 / cluster5.size());
				 n++;
				 
				 if(n>10 ||(m[0]==a && m[1]==b&& m[2]==c && m[3]==d && m[4]==e)){
					/*System.out.println(Math.round(m[0] * 100.0) / 100.0+" " +Math.round(a * 100.0) / 100.0);
					System.out.println(Math.round(m[1] * 100.0) / 100.0+" " +Math.round(b * 100.0) / 100.0);
					System.out.println(Math.round(m[2] * 100.0) / 100.0+" " +Math.round(c * 100.0) / 100.0);
					System.out.println(Math.round(m[3] * 100.0) / 100.0+" " +Math.round(d * 100.0) / 100.0);
					System.out.println(Math.round(m[4] * 100.0) / 100.0+" " +Math.round(e * 100.0) / 100.0);*/
					 flag=false; 
				 }else{
					 flag=true;
				 }
			 }while(flag);
			 int cn=0;
			 //System.out.println("Cluster 1"+ " " +n);
			 for(i=0;i<cluster1.size();i++){
				// System.out.print(cluster1.get(i)+" ");
				 cn++;
				 ratingMap.put(cluster1.get(i), 0.2);
			 }
			// System.out.println(c+" ");
			 cn=0;
			//// System.out.println();
			// System.out.println("Cluster 2");
			 for(i=0;i<cluster2.size();i++){
				// System.out.print(cluster2.get(i)+" ");
				 cn++;
				 ratingMap.put(cluster2.get(i), 0.4);
			 }
			// System.out.println(c+"");
			 cn=0;
			 //System.out.println();
			// System.out.println("Cluster 3");
			 for(i=0;i<cluster3.size();i++){
				// System.out.print(cluster3.get(i)+" ");
				 cn++;
				 ratingMap.put(cluster3.get(i), 0.6);
			 }
			// System.out.println(c+"");
			 cn=0;
			 //System.out.println();
			// System.out.println("Cluster 4");
			 for(i=0;i<cluster4.size();i++){
				 //System.out.print(cluster4.get(i)+" ");
				 cn++;
				 ratingMap.put(cluster4.get(i), 0.8);
			 }
			 //System.out.println(c+"");
			 cn=0;
			// System.out.println();
			// System.out.println("Cluster 5");
			 for(i=0;i<cluster5.size();i++){
				// System.out.print(cluster5.get(i)+" ");
				 cn++;
				 ratingMap.put(cluster5.get(i), 1.0);
			 }
			// System.out.println(c+"");
			 cn=0;
			// System.out.println();
			 Arrays.sort(m);
			 for(i=0;i<m.length;i++){
				 //System.out.println(Math.round(m[i] * 100.0) / 100.0);
			 }
	}

	private void generateUserList() {
		// TODO Auto-generated method stub
		userList = new ArrayList<Integer>();
		String query = "MATCH (s:Subscriber) RETURN s.BILLING_ACCOUNT_NUMBER";

		Result er = graphDb.execute(query);
		while (er.hasNext()) {
			Map<String, Object> row = er.next();
			for (String key : er.columns()) {

				userList.add(Integer.parseInt(row.get(key).toString()));
			}

		}
	}

	public String getGender() {
		return gender;
	}

	public String getName() {
		return userName;
	}

	public String getCity() {
		return city;
	}

	public int getAge() {
		return age;
	}

	private void CalculateUserpreferences() throws Exception {
		// TODO Auto-generated method stub
		List<String> notwatched = new ArrayList<String>();
		notwatched = getchannelsnotWatchbyuser(userID);
		Set<String> hs = new HashSet<>();
		hs.addAll(notwatched);
		notwatched.clear();
		notwatched.addAll(hs);
		for (int i = 0; i < notwatched.size(); i++) {
			double prefs = 0.0;
			double sumofsim = 0.0;
			for (Map.Entry<Integer, Integer> entry : KmostSimilarUser
					.entrySet()) {
				int simiuser = entry.getKey();
				List<String> chnllist2 = listofchannelwatchbyUser.get(simiuser);
				// System.out.println("Sim: " + simiuser + " "
				// + chnllist2.toString());
				if (chnllist2.contains(notwatched.get(i))) {
					String chnl = notwatched.get(i);

					String query = "MATCH a=(s:Subscriber)-[u:USE]->(cu:Channel_Usage)<-[IS]-(c:Channel) "
							+ "WHERE s.BILLING_ACCOUNT_NUMBER="
							+ "'"
							+ simiuser
							+ "'"
							+ " and "
							+ "c.CONTENTCODE="
							+ "'"
							+ notwatched.get(i)
							+ "'"
							+ " RETURN SUM(cu.HOURS)*60  AS Usage_Minutes";
					Result er5 = graphDb.execute(query);

					if (er5.hasNext()) {
						Map<String, Object> row = er5.next();
						if (Double.isNaN(Double.parseDouble(row.get(
								"Usage_Minutes").toString()))) {
							continue;
						} else {
							double tempresult = Math.round(Double
									.parseDouble(row.get("Usage_Minutes")
											.toString()) * 100.0) / 100.0;
							;

							tempresult = ratingMap.get(tempresult);
							prefs = prefs
									+ ((tempresult - averageUserRating
											.get(simiuser)) * KmostSimilarUser
											.get(simiuser));
							sumofsim = sumofsim
									+ KmostSimilarUser.get(simiuser);
						}
					}
				}
			}
			prefs = prefs / sumofsim;
			prefs = prefs + averageUserRating.get(userID);

			if (prefs > 5.0) {
				prefOfUser.put(notwatched.get(i),
						Math.round(5.0 * 100.0) / 100.0);
			} else {
				prefOfUser.put(notwatched.get(i),
						Math.round(prefs * 100.0) / 100.0);
			}

		}
		prefOfUser = sortByValue1(prefOfUser);
		//printTOP5iten(prefOfUser);
	}

	public Map<String, Double> getPrefofUser() {
		return prefOfUser;
	}

	private void printTOP5iten(Map<String, Double> prefOfUser2) {
		// TODO Auto-generated method stub
		int count = 0;

		for (Map.Entry<String, Double> en : prefOfUser2.entrySet()) {
			if (count > 5) {
				break;
			}

			System.out.println("Top item: " + en.getKey() + " "
					+ "preference value:" + Math.round(en.getValue() * 100.0)
					/ 100.0);

			count++;

		}
	}

	private static Map<String, Double> sortByValue1(
			Map<String, Double> prefOfUser) {
		// TODO Auto-generated method stub
		List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(
				prefOfUser.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1,
					Map.Entry<String, Double> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		for (Map.Entry<String, Double> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		;
		return sortedMap;
	}

	private List<String> getchannelsnotWatchbyuser(int userID) {
		// TODO Auto-generated method stub
		List<String> alreadyWatched = listofchannelwatchbyUser.get(userID);
		// System.out.println("New "+alreadyWatched.size());
		List<String> possibleMoviesTowatch = new ArrayList<String>();
		for (Map.Entry<Integer, Integer> entry : KmostSimilarUser.entrySet()) {
			List<String> watchBysimilarUSer = listofchannelwatchbyUser
					.get(entry.getKey());
			// System.out.println(watchBysimilarUSer.size());
			possibleMoviesTowatch.addAll(watchBysimilarUSer);
			// System.out.println(possibleMoviesTowatch.size());
		}
		// System.out.println("New "+possibleMoviesTowatch.size());
		possibleMoviesTowatch.removeAll(alreadyWatched);

		if (possibleMoviesTowatch.size() > 200) {
			return possibleMoviesTowatch.subList(0, 199);
		} else
			return possibleMoviesTowatch;
	}

	private void calculateuserStatistics() throws Exception {
		// TODO Auto-generated method stub
		int similarityVal = 0;
		for (int i = 0; i < userList.size(); i++) {
			String id = userList.get(i).toString();
			String s4 = "MATCH a=(s:Subscriber)-[u:USE]->(cu:Channel_Usage)<-[IS]-(c:Channel) WHERE s.BILLING_ACCOUNT_NUMBER="
					+ "'"
					+ id
					+ "'"
					+ "RETURN SUM(cu.HOURS)*60  AS Usage_Minutes,c.CONTENTCODE,"
					+ "s.CITY,s.AGE,s.GENDER,s.BILLING_ACCOUNT_NUMBER ORDER BY Usage_Minutes";
			Result er1 = graphDb.execute(s4);
			int count = 0;
			double pref = 0.0;
			List<String> ls = new ArrayList<String>();
			while (er1.hasNext()) {
				Map<String, Object> row = er1.next();
				similarityVal = 0;
				/*
				 * System.out.println(userList.get(i) + " " +
				 * resultSet.getDouble("chnlusage"));
				 */
				// pref = pref + resultSet.getDouble("rating");)
				double temp = Math.round(Double.parseDouble(row.get(
						"Usage_Minutes").toString()) * 100.0) / 100.0;
				pref = pref + ratingMap.get(temp);
				// pref = pref + resultSet.getDouble("rating");
				ls.add(row.get("c.CONTENTCODE").toString());
				count++;
				/*
				 * System.out.println("UserID: " + userList.get(i) + " City: " +
				 * resultSet.getString("CITY") + " Gender: " +
				 * resultSet.getString("GENDER") + " Age: " +
				 * resultSet.getInt("AGE"));
				 */
				if (row.get("s.CITY") != null
						&& row.get("s.CITY").toString().equalsIgnoreCase(city)
						&& userID != userList.get(i)) {
					similarityVal++;
				}
				if (row.get("s.GENDER") != null
						&& row.get("s.GENDER").toString()
								.equalsIgnoreCase(gender)
						&& userID != userList.get(i)) {
					similarityVal++;
				}
				if (row.get("s.AGE") != null
						&& Integer.parseInt(row.get("s.AGE").toString()) == age
						&& userID != userList.get(i)) {
					similarityVal++;
				}

			}
			similaritybetweenusr.put(userList.get(i), similarityVal);
			// System.out.println("similarity: "+userList.get(i) + " " +
			// similarityVal);
			// System.out.println("USER: "+userList.get(i)+" "+pref+" count : "+count);
			listofchannelwatchbyUser.put(userList.get(i), ls);
			/*
			 * System.out.println("USER id: " + userList.get(i) + " " +
			 * ls.toString());
			 */
			numberofchanellwatch.put(userList.get(i), count);
			/* System.out.println("USER: " + userList.get(i) + " " + count); */
			pref = pref / count;
			averageUserRating.put(userList.get(i),
					Math.round(pref * 100.0) / 100.0);

		}
		int count = 0;
		for (Map.Entry<Integer, Integer> entry : similaritybetweenusr
				.entrySet()) {
			if (entry.getValue() == 1) {
				KmostSimilarUser.put(entry.getKey(), entry.getValue());
				count++;
			}
			if (count == 10) {
				break;
			}
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {

			/*ContextualRecommendation contextualRecommendation = new ContextualRecommendation(
					1000896702);*/
			// 1000896702 1000000000
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
