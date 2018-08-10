import java.io.File;






import java.util.Iterator;
import java.util.Map;

import org.neo4j.cypher.internal.ExecutionEngine;
import org.neo4j.cypher.internal.compiler.v2_3.commands.Query;
import org.neo4j.cypher.internal.frontend.v2_3.parser.CypherParser;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
public class JavaNeo4jCQLRetrivalTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GraphDatabaseFactory graphDbFactory = new GraphDatabaseFactory();
		File storeFile = new File("F:/MMU_FCI_RECOMMENDER_SYSTEM/DataBase/demo");
	      GraphDatabaseService graphDb = graphDbFactory.newEmbeddedDatabase(storeFile);
	 String p1="1000896702";
	 String s="MATCH(n:Subscriber) WHERE n.BILLING_ACCOUNT_NUMBER=" + "'1000896702'" +"  RETURN n.ACCOUNT";
     String s1="MATCH(n:Subscriber) WHERE n.BILLING_ACCOUNT_NUMBER=" + "'"+p1+"'" +"  RETURN n.ACCOUNT"; 
     String query = "MATCH a=(s:Subscriber)-[u:USE]->(cu:Channel_Usage)<-[IS]-(c:Channel) RETURN SUM(cu.HOURS)*60 "
     		+ " AS Usage_Minutes,c.CONTENTCODE,s.BILLING_ACCOUNT_NUMBER,"
     		+ "c.CHANNEL_TYPE,c.CHANNEL_GENRE,s.AGE,s.GENDER,"
     		+ "c.CHANNEL_TYPE,c.CHANNEL_GENRE ORDER BY Usage_Minutes";   
     String query2="MATCH (s:Subscriber)RETURN s.BILLING_ACCOUNT_NUMBER, s.LOGIN_ID, s.ACCOUNT, s.CITY,s.AGE,s.GENDER";
     Result er = graphDb.execute(query2);
	     
	    /*s*/
	      while ( er.hasNext() )
	      {
	          Map<String, Object> row = er.next();
	          int userID=0;
	          String chnlGenres="";
	          double usage=0.0;
	          System.out.printf( "%s ",row.get( "s.CITY" ) );
	          System.out.printf( "%s ",row.get( "s.AGE" ) );
	          System.out.printf( "%s ",row.get( "s.GENDER" ) );
	          /*for ( String key : er.columns() )
	          {
	              System.out.printf( "%s ",row.get( key ) );
	        	  if(row.get("s.BILLING_ACCOUNT_NUMBER")!=null){
	               userID = Integer.parseInt(row.get(
							"s.BILLING_ACCOUNT_NUMBER").toString());}
					// int chnlid = resultSet.getInt("chnlID");
	        	  else if(row.get("c.CHANNEL_GENRE")!=null){
					 chnlGenres = row.get("c.CHANNEL_GENRE").toString();}
	        	  else if(row.get("c.CHANNEL_GENRE")!=null){
					 usage = Double.parseDouble(row.get("Usage_Minutes")
							.toString());
	        	  }*/
	          System.out.println();
	          }
	          
				//System.out.println("User id: "+userID+" chnlGenres: "+chnlGenres+" usage: "+usage);
	      }
	     
	     // System.out.println(er.resultAsString());
	      //System.out.println(er.columns());
	      
	}


