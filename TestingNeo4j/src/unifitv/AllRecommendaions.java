package unifitv;

import java.io.File;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class AllRecommendaions {

	public static GraphDatabaseFactory graphDbFactory = new GraphDatabaseFactory();
	public static File storeFile = new File("F:/MMU_FCI_RECOMMENDER_SYSTEM/DataBase/demo");
	static GraphDatabaseService graphDb = graphDbFactory
			.newEmbeddedDatabase(storeFile);
	int userID;
	public AllRecommendaions(int userID){
		this.userID=userID;
		try {
			ContextualRecommendation contextualRecommendation=new ContextualRecommendation(userID,graphDb);
			Map<String, Double> contextRecommendationlist=contextualRecommendation.getPrefofUser();
			RecommendationCalculation recommendationCalculation=new RecommendationCalculation(userID,graphDb);
			Map<String, Double> recommendlist=recommendationCalculation.getPrefofUser();
			
			int count = 0;
			System.out.println("Top-5 recommended items:");
			for (Map.Entry<String, Double> en : recommendlist.entrySet()) {
				
				  if (count > 5) { break; }
				 
				  System.out.println("Top item: " + en.getKey() + " " +
				  "preference value:" + Math.round(en.getValue() * 100.0) / 100.0);
				 
				count++;
			}
			count=0;
			System.out.println("Top-5 Contextual recommended items:");
			for (Map.Entry<String, Double> en : contextRecommendationlist.entrySet()) {
				
				  if (count > 5) { break; }
				 
				  System.out.println("Top item: " + en.getKey() + " " +
				  "preference value:" + Math.round(en.getValue() * 100.0) / 100.0);
				 
				count++;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
