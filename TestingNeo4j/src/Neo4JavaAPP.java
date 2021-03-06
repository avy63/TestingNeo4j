import java.io.File;

import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class Neo4JavaAPP {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
		File storeFile = new File("F:/MMU_FCI_RECOMMENDER_SYSTEM/DataBase/test");
	
		GraphDatabaseService db = dbFactory.newEmbeddedDatabase(storeFile);
		try (Transaction tx = db.beginTx()) {
			// Perform DB operations	
			Node javaNode = db.createNode(Tutorials.JAVA);
			javaNode.setProperty("TutorialID", "JAVA001");
			javaNode.setProperty("Title", "Learn Java");
			javaNode.setProperty("NoOfChapters", "25");
			javaNode.setProperty("Status", "Completed");				
			
			Node scalaNode = db.createNode(Tutorials.SCALA);
			scalaNode.setProperty("TutorialID", "SCALA001");
			scalaNode.setProperty("Title", "Learn Scala");
			scalaNode.setProperty("NoOfChapters", "20");
			scalaNode.setProperty("Status", "Completed");
			
			Relationship relationship = javaNode.createRelationshipTo
			(scalaNode,TutorialRelationships.JVM_LANGIAGES);
			relationship.setProperty("Id","1234");
			relationship.setProperty("OOPS","YES");
			relationship.setProperty("FP","YES");
			
			tx.success();
			
		}
		  System.out.println("Done successfully");
	}

}
