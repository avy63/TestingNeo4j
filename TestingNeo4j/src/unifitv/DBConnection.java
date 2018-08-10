package unifitv;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.sql.DataSource;
import javax.swing.JOptionPane;

public class DBConnection {

	Connection connection=null;
	public Connection sqliteConncetion(){
		try{
			Class.forName("org.sqlite.JDBC");
			connection=DriverManager.getConnection("jdbc:sqlite:F:\\OCJP\\HyppTV_UI\\HyppTVdatabase.sqlite");
			//JOptionPane.showMessageDialog(null,"Connection successfull");
			System.out.println("Connection successfull");
			/*System.out.println(connection.getCatalog());
			System.out.println(connection.toString());*/
			
			return connection;
			
			
		}catch(Exception e){
			//JOptionPane.showMessageDialog(null, e);
			System.out.println("error: "+e);
			return null;
		}
	}
}
