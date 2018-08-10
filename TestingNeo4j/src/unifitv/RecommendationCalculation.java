package unifitv;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
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

public class RecommendationCalculation {

	/*
	 * public static Connection coneConnection = null; public static
	 * DBConnection dbConnection;
	 */
	// for classification
	public List<Double> chnlUsge;
	public double m1, m2, m3, m4, m5, a, b, c, d, e;
	public double m[] = new double[5];
	public int n = 0, i;
	public double sum1, sum2, sum3, sum4, sum5;
	public Map<Double, Double> ratingMap;

	// genre list222222222222
	public Map<Integer, Double> chiense = new HashMap<Integer, Double>();
	public Map<Integer, Double> ge = new HashMap<Integer, Double>();
	public Map<Integer, Double> indian = new HashMap<Integer, Double>();
	public Map<Integer, Double> kids = new HashMap<Integer, Double>();
	public Map<Integer, Double> lifestyle = new HashMap<Integer, Double>();
	public Map<Integer, Double> ng = new HashMap<Integer, Double>();
	public Map<Integer, Double> news = new HashMap<Integer, Double>();
	public Map<Integer, Double> sports = new HashMap<Integer, Double>();
	public List<Integer> userList;
	public static Map<Integer, Integer> numberofchanellwatch = null;
	public static Map<Integer, Double> averageUserRating = null;
	public Map<Integer, List<String>> listofchannelwatchbyUser;
	public Map<Integer, Double> similaritybetUser;
	public Map<Integer, Double> kMostSimialrUser;
	Map<String, Double> prefOfUser = new HashMap<String, Double>();
	private int userID = 0;
	public DecimalFormat df1 = new DecimalFormat("#.##");
	/*GraphDatabaseFactory graphDbFactory = new GraphDatabaseFactory();
	File storeFile = new File("F:/MMU_FCI_RECOMMENDER_SYSTEM/DataBase/demo");*/
	GraphDatabaseService graphDb;

	public RecommendationCalculation(int userID,GraphDatabaseService graphDb) throws Exception {
		this.userID = userID;
		// dbConnection = new DBConnection();
		this.graphDb=graphDb;
		ratingMap = new HashMap<Double, Double>();
		chnlUsge = new ArrayList<Double>();
		numberofchanellwatch = new HashMap<Integer, Integer>();
		averageUserRating = new HashMap<Integer, Double>();
		similaritybetUser = new HashMap<Integer, Double>();
		userList = new ArrayList<Integer>();
		kMostSimialrUser = new HashMap<Integer, Double>();
		listofchannelwatchbyUser = new HashMap<Integer, List<String>>();
		try {

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
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex);
		}
		// System.out.println(chnlUsge.toString());
		boolean flag;
		a = chnlUsge.get(0);
		b = chnlUsge.get(2);
		c = chnlUsge.get(5);
		d = chnlUsge.get(7);
		e = chnlUsge.get(10);
		m[0] = a;
		m[1] = b;
		m[2] = c;
		m[3] = d;
		m[4] = e;
		List<Double> cluster1 = new ArrayList<Double>();
		List<Double> cluster2 = new ArrayList<Double>();
		List<Double> cluster3 = new ArrayList<Double>();
		List<Double> cluster4 = new ArrayList<Double>();
		List<Double> cluster5 = new ArrayList<Double>();
		do {
			sum1 = 0;
			sum2 = 0;
			sum3 = 0;
			sum4 = 0;
			sum5 = 0;
			cluster1 = new ArrayList<Double>();
			cluster2 = new ArrayList<Double>();
			cluster3 = new ArrayList<Double>();
			cluster4 = new ArrayList<Double>();
			cluster5 = new ArrayList<Double>();
			// n++;
			int j = 0;
			int tempJ = 0;
			for (i = 0; i < chnlUsge.size(); i++) {
				double diff = 1000000000;
				double temp = 0.0;
				for (j = 0; j < 5; j++) {
					temp = Math.abs(chnlUsge.get(i) - m[j]);
					if (temp < diff) {
						diff = temp;
						tempJ = j;
					}
				}
				if (tempJ == 0) {
					cluster1.add(chnlUsge.get(i));
				} else if (tempJ == 1) {
					cluster2.add(chnlUsge.get(i));
				} else if (tempJ == 2) {
					cluster3.add(chnlUsge.get(i));
				} else if (tempJ == 3) {
					cluster4.add(chnlUsge.get(i));
				} else {
					cluster5.add(chnlUsge.get(i));
				}
			}
			for (i = 0; i < cluster1.size(); i++) {
				sum1 = sum1 + cluster1.get(i);
			}
			for (i = 0; i < cluster2.size(); i++) {
				sum2 = sum2 + cluster2.get(i);
			}
			for (i = 0; i < cluster3.size(); i++) {
				sum3 = sum3 + cluster3.get(i);
			}
			for (i = 0; i < cluster4.size(); i++) {
				sum4 = sum4 + cluster4.get(i);
			}
			for (i = 0; i < cluster5.size(); i++) {
				sum5 = sum5 + cluster5.get(i);
			}
			a = m[0];
			b = m[1];
			c = m[2];
			d = m[3];
			e = m[4];
			m[0] = (sum1 / cluster1.size());
			m[1] = (sum2 / cluster2.size());
			m[2] = (sum3 / cluster3.size());
			m[3] = (sum4 / cluster4.size());
			m[4] = (sum5 / cluster5.size());
			n++;

			if (n > 10
					|| (m[0] == a && m[1] == b && m[2] == c && m[3] == d && m[4] == e)) {
				/*
				 * System.out.println(Math.round(m[0] * 100.0) / 100.0+" "
				 * +Math.round(a * 100.0) / 100.0);
				 * System.out.println(Math.round(m[1] * 100.0) / 100.0+" "
				 * +Math.round(b * 100.0) / 100.0);
				 * System.out.println(Math.round(m[2] * 100.0) / 100.0+" "
				 * +Math.round(c * 100.0) / 100.0);
				 * System.out.println(Math.round(m[3] * 100.0) / 100.0+" "
				 * +Math.round(d * 100.0) / 100.0);
				 * System.out.println(Math.round(m[4] * 100.0) / 100.0+" "
				 * +Math.round(e * 100.0) / 100.0);
				 */
				flag = false;
			} else {
				flag = true;
			}
		} while (flag);
		// System.out.println("Cluster 1"+ " " +n);
		for (i = 0; i < cluster1.size(); i++) {
			// System.out.print(cluster1.get(i)+" ");
			ratingMap.put(Math.round(cluster1.get(i) * 100.0) / 100.0, 0.2);
		}
		// System.out.println();
		// System.out.println("Cluster 2");
		for (i = 0; i < cluster2.size(); i++) {
			// System.out.print(cluster2.get(i)+" ");
			ratingMap.put(Math.round(cluster2.get(i) * 100.0) / 100.0, 0.4);
		}
		// System.out.println();
		// System.out.println("Cluster 3");
		for (i = 0; i < cluster3.size(); i++) {
			// System.out.print(cluster3.get(i)+" ");
			ratingMap.put(Math.round(cluster3.get(i) * 100.0) / 100.0, 0.6);
		}
		// System.out.println();
		// System.out.println("Cluster 4");
		for (i = 0; i < cluster4.size(); i++) {
			// System.out.print(cluster4.get(i)+" ");
			ratingMap.put(Math.round(cluster4.get(i) * 100.0) / 100.0, 0.8);
		}
		// System.out.println();
		// System.out.println("Cluster 5");
		for (i = 0; i < cluster5.size(); i++) {
			// System.out.print(cluster5.get(i)+" ");
			ratingMap.put(Math.round(cluster5.get(i) * 100.0) / 100.0, 1.0);
		}
		// System.out.println();
		Arrays.sort(m);
		for (i = 0; i < m.length; i++) {
			// System.out.println(Math.round(m[i] * 100.0) / 100.0);
		}
		 //printMap(ratingMap);
		getUserList();
		calculateNumberofchannelwatchbyuser();
		calculateUserPreferences();
		calculatesimilarityBetweenUser();
	}

	private void calculatesimilarityBetweenUser() {
		double sim = 0.0;
		for (int i = 0; i < userList.size(); i++) {
			if (userID == userList.get(i)) {
				continue;
			}
			sim = Math.pow(
					(chiense.get(userID) - chiense.get(userList.get(i))), 2)
					+ Math.pow((ng.get(userID) - ng.get(userList.get(i))), 2)
					+ Math.pow(
							(indian.get(userID) - indian.get(userList.get(i))),
							2)
					+ Math.pow((kids.get(userID) - kids.get(userList.get(i))),
							2)
					+ Math.pow((lifestyle.get(userID) - lifestyle.get(userList
							.get(i))), 2)
					+ Math.pow(
							(sports.get(userID) - sports.get(userList.get(i))),
							2)
					+ Math.pow((news.get(userID) - news.get(userList.get(i))),
							2)
					+ Math.pow((ge.get(userID) - ge.get(userList.get(i))), 2);
			sim = Math.pow(sim, 0.5);
			sim = Math.round(sim * 100.0) / 100.0;
			similaritybetUser.put(userList.get(i), sim);
			// System.out.println("User : " + userList.get(i) + " " + sim);
		}
		similaritybetUser = findKmostSimilarUser();
		int count = 0;
		for (Map.Entry<Integer, Double> entry : similaritybetUser.entrySet()) {
			if (count == 10) {
				break;
			}
			kMostSimialrUser.put(entry.getKey(), entry.getValue());
			count++;
		}
		//printKmostsimilaruser(kMostSimialrUser);
		try {
			predictUserRating();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Map<Integer, Double> getSimilaruser() {
		return kMostSimialrUser;
	}

	private void predictUserRating() throws Exception {
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
			for (Map.Entry<Integer, Double> entry : kMostSimialrUser.entrySet()) {
				int simiuser = entry.getKey();
				List<String> chnllist2 = listofchannelwatchbyUser.get(simiuser);
				// System.out.println("Sim: "+simiuser+" "+chnllist2.toString());
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
							double tempresult = Math.round(Double.parseDouble(row.get("Usage_Minutes").toString()) * 100.0) / 100.0;;
							
							tempresult = ratingMap.get(tempresult);
						/*	System.out.println("Rating Map: "+tempresult+" "+averageUserRating
									.get(simiuser));*/
							prefs = prefs
									+ ((tempresult - averageUserRating
											.get(simiuser)) * kMostSimialrUser
											.get(simiuser));
							
							sumofsim = sumofsim
									+ kMostSimialrUser.get(simiuser);
						}
					}
				}
			}
			prefs = prefs / sumofsim;
			prefs = prefs + averageUserRating.get(userID);

			if (prefs > 1.0) {
				prefOfUser.put(notwatched.get(i),
						Math.round(1.0 * 100.0) / 100.0);
				//System.out.println("notwatched.get(i): "+notwatched.get(i)+" prefs: "+Math.round(prefs * 100.0) / 100.0);
			} else {
				prefOfUser.put(notwatched.get(i),
						Math.round(prefs * 100.0) / 100.0);
				//System.out.println("notwatched.get(i): "+notwatched.get(i)+" prefs: "+Math.round(prefs * 100.0) / 100.0);
			}

		}
		prefOfUser = sortByValue1(prefOfUser);
	//	printTOP5iten(prefOfUser);

	}

	public Map<String, Double> getPrefofUser() {
		return prefOfUser;
	}

	private void printTOP5iten(Map<String, Double> prefOfUser2) {
		// TODO Auto-generated method stub
		int count = 0;

		for (Map.Entry<String, Double> en : prefOfUser2.entrySet()) {
			
			  if (count > 5) { break; }
			 
			  System.out.println("Top item: " + en.getKey() + " " +
			  "preference value:" + Math.round(en.getValue() * 100.0) / 100.0);
			 
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
		List<String> possibleMoviesTowatch = new ArrayList<String>();
		for (Map.Entry<Integer, Double> entry : kMostSimialrUser.entrySet()) {
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

	private void printKmostsimilaruser(Map<Integer, Double> kMostSimialrUser2) {
		// TODO Auto-generated method stub
		// System.out.println("Kmost Similarity value ");
		for (Map.Entry<Integer, Double> map : kMostSimialrUser2.entrySet()) {
			
			  System.out.println("User id: " + map.getKey() + " simival: " +
			  map.getValue());
			 
		}
	}

	private Map<Integer, Double> findKmostSimilarUser() {
		// TODO Auto-generated method stub
		List<Map.Entry<Integer, Double>> list = new LinkedList<Map.Entry<Integer, Double>>(
				similaritybetUser.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
			public int compare(Map.Entry<Integer, Double> o1,
					Map.Entry<Integer, Double> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});
		Map<Integer, Double> sortedMap = new LinkedHashMap<Integer, Double>();
		for (Map.Entry<Integer, Double> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;

	}

	private void calculateUserPreferences() {
		// TODO Auto-generated method stub
		for (int i = 0; i < userList.size(); i++) {
			chiense.put(userList.get(i), 0.0);
			ge.put(userList.get(i), 0.0);
			sports.put(userList.get(i), 0.0);
			indian.put(userList.get(i), 0.0);
			ng.put(userList.get(i), 0.0);
			lifestyle.put(userList.get(i), 0.0);
			kids.put(userList.get(i), 0.0);
			news.put(userList.get(i), 0.0);

		}
		try {
			String query = "MATCH a=(s:Subscriber)-[u:USE]->(cu:Channel_Usage)<-[IS]-(c:Channel) RETURN SUM(cu.HOURS)*60  AS Usage_Minutes,c.CONTENTCODE,s.BILLING_ACCOUNT_NUMBER,c.CHANNEL_TYPE,c.CHANNEL_GENRE ORDER BY Usage_Minutes";
			Result er2 = graphDb.execute(query);

			while (er2.hasNext()) {
				Map<String, Object> row = er2.next();
				
				int userID = Integer.parseInt(row.get(
						"s.BILLING_ACCOUNT_NUMBER").toString());
				String chnlGenres="";
				// int chnlid = resultSet.getInt("chnlID");
				if(row.get("c.CHANNEL_GENRE")!=null){
				 chnlGenres = row.get("c.CHANNEL_GENRE").toString();
				 }else{
					 chnlGenres="NG";
				 }
				double usage = Math.round(Double.parseDouble(row.get("Usage_Minutes")
						.toString()) * 100.0) / 100.0;
				if (chnlGenres.contains("Chinese")) {
					double sum = chiense.get(userID);
					sum = sum + ratingMap.get(usage);
					chiense.put(userID, sum);
				} else if (chnlGenres.contains("NG")) {
					double sum = ng.get(userID);
					sum = sum + ratingMap.get(usage);
					ng.put(userID, sum);
				} else if (chnlGenres.contains("News")) {
					double sum = news.get(userID);
					sum = sum + ratingMap.get(usage);
					news.put(userID, sum);
				} else if (chnlGenres.contains("Lifestyle")) {
					double sum = lifestyle.get(userID);
					sum = sum + ratingMap.get(usage);
					lifestyle.put(userID, sum);
				} else if (chnlGenres.contains("Indian")) {
					double sum = indian.get(userID);
					sum = sum + ratingMap.get(usage);
					indian.put(userID, sum);
				} else if (chnlGenres.contains("GE")) {
					double sum = ge.get(userID);
					sum = sum + ratingMap.get(usage);
					ge.put(userID, sum);
				} else if (chnlGenres.contains("Sports")) {
					double sum = sports.get(userID);
					sum = sum + ratingMap.get(usage);
					sports.put(userID, sum);
				} else if (chnlGenres.contains("Kids")) {
					double sum = kids.get(userID);
					sum = sum + ratingMap.get(usage);
					kids.put(userID, sum);
				}
			}
			for (int i = 0; i < userList.size(); i++) {
				double val = 0.0;
				val = chiense.get(userList.get(i))
						/ numberofchanellwatch.get(userList.get(i));
				chiense.put(userList.get(i), val);
				// System.out.println("user id:"+userList.get(i)+" Chinese  "+val);
				val = ng.get(userList.get(i))
						/ numberofchanellwatch.get(userList.get(i));
				ng.put(userList.get(i), val);
				// System.out.println("user id:"+userList.get(i)+" ng  "+val);
				val = sports.get(userList.get(i))
						/ numberofchanellwatch.get(userList.get(i));
				sports.put(userList.get(i), val);
				// System.out.println("user id:"+userList.get(i)+" Sports  "+val);
				val = news.get(userList.get(i))
						/ numberofchanellwatch.get(userList.get(i));
				news.put(userList.get(i), val);
				// System.out.println("user id:" + userList.get(i) + " news  " +
				// val);
				val = lifestyle.get(userList.get(i))
						/ numberofchanellwatch.get(userList.get(i));
				lifestyle.put(userList.get(i), val);
				// System.out.println("user id:"+userList.get(i)+" lifestyle  "+val);
				val = indian.get(userList.get(i))
						/ numberofchanellwatch.get(userList.get(i));
				indian.put(userList.get(i), val);
				// System.out.println("user id:"+userList.get(i)+" indian  "+val);
				val = kids.get(userList.get(i))
						/ numberofchanellwatch.get(userList.get(i));
				kids.put(userList.get(i), val);
				// System.out.println("user id:"+userList.get(i)+" kids  "+val);
				val = ge.get(userList.get(i))
						/ numberofchanellwatch.get(userList.get(i));
				ge.put(userList.get(i), val);
				// System.out.println("user id:"+userList.get(i)+" ge  "+val);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void printontologyval() {
		 System.out.println(" User ID "+" "+"Chinese , Sports , News , Lifstyle , Indian , Kids , Ge , Other");
		for (int i = 0; i < userList.size(); i++) {
			double a = chiense.get(userList.get(i));
			double b = sports.get(userList.get(i));
			double c = news.get(userList.get(i));
			double d = lifestyle.get(userList.get(i));
			double e = indian.get(userList.get(i));
			double f = kids.get(userList.get(i));
			double g = ge.get(userList.get(i));
			double h = ng.get(userList.get(i));
			
			  System.out.println(userList.get(i)+" "+df1.format(a)+" , "+df1.format
			  (b)+" , " + df1.format(c)+" , "+df1.format(d)+" , "+df1.format(e)+" , " +
			 df1.format(f)+" , "+df1.format(g)+" , "+df1.format(h));
			 
		}
	}

	public double getChinese() {
		return chiense.get(userID);
	}

	public double getindian() {
		return indian.get(userID);
	}

	public double getlifestyle() {
		return lifestyle.get(userID);
	}

	public double getSports() {
		return sports.get(userID);
	}

	public double getNews() {
		return news.get(userID);
	}

	public double getkids() {
		return kids.get(userID);
	}

	public double getge() {
		return ge.get(userID);
	}

	public double getother() {
		return ng.get(userID);
	}

	private void getUserList() throws Exception {
		// TODO Auto-generated method stub
		String query = "MATCH (s:Subscriber) RETURN s.BILLING_ACCOUNT_NUMBER";

		Result er = graphDb.execute(query);
		while (er.hasNext()) {
			Map<String, Object> row = er.next();
			for (String key : er.columns()) {

				userList.add(Integer.parseInt(row.get(key).toString()));
			}

		}
		// System.out.println("User List size" + userList.size());

	}

	private void calculateNumberofchannelwatchbyuser() throws Exception {
		// TODO Auto-generated method stub
		for (int i = 0; i < userList.size(); i++) {
			String id = userList.get(i).toString();
			String s4 = "MATCH a=(s:Subscriber)-[u:USE]->(cu:Channel_Usage)<-[IS]-(c:Channel) WHERE s.BILLING_ACCOUNT_NUMBER="
					+ "'"
					+ id
					+ "'"
					+ "RETURN SUM(cu.HOURS)*60  AS Usage_Minutes,c.CONTENTCODE,s.BILLING_ACCOUNT_NUMBER ORDER BY Usage_Minutes";
			Result er1 = graphDb.execute(s4);

			int count = 0;
			double pref = 0.0;
			List<String> ls = new ArrayList<String>();
			while (er1.hasNext()) {
				Map<String, Object> row = er1.next();
				double temp =Math.round(Double.parseDouble(row.get("Usage_Minutes").toString()) * 100.0) / 100.0;
				pref = pref + ratingMap.get(temp);
				// pref = pref + resultSet.getDouble("rating");
				ls.add(row.get("c.CONTENTCODE").toString());
				count++;
			}
			/*System.out.println("USER: " + userList.get(i) + " " + pref
					+ " count : " + count);*/
			listofchannelwatchbyUser.put(userList.get(i), ls);
		/*	System.out.println("USER id: " + userList.get(i) + " "
					+ ls.toString());*/
			numberofchanellwatch.put(userList.get(i), count);
			/*System.out.println("Number of movies watch by USER: "
					+ userList.get(i) + " " + count);*/
			pref = pref / count;
			averageUserRating.put(userList.get(i),
					Math.round(pref * 100.0) / 100.0);

		}
		//printMapDouble(averageUserRating);
		// CalculatePredictedUSerrating();
	}

	private void printMapDouble(Map<Integer, Double> similaritybetweenusr2) {
		// TODO Auto-generated method stub
		for (Map.Entry<Integer, Double> entry : similaritybetweenusr2
				.entrySet()) {
			
			  System.out.println("Key : " + entry.getKey() + " Value : " +
			  entry.getValue());
			 
		}

	}

	private void printMap(Map<Double, Double> ratingMap2) {
		// TODO Auto-generated method stub
		for (Map.Entry entry : ratingMap2.entrySet()) {
			 System.out.println(entry.getKey() + ", " + entry.getValue()); 
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			
			/*RecommendationCalculation a = new RecommendationCalculation(
					1002882833);*/
			// 1002882833
			// 1000896702
			//1030383259
			//a.printontologyval();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
