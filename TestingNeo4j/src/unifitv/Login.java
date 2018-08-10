package unifitv;


import java.awt.EventQueue;
import java.awt.Image;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.ImageIcon;
import javax.swing.JFrame;



import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPasswordField;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;




import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.awt.Color;
import java.awt.Font;

public class Login {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login window = new Login();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	Connection coneConnection=null;
	private JTextField textName;
	private JPasswordField passwordField;
	private JButton btnSignUp;
	public Login() {
		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 450);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Login id");
		lblNewLabel.setFont(new Font("Book Antiqua", Font.BOLD, 14));
		lblNewLabel.setForeground(Color.BLUE);
		lblNewLabel.setBounds(93, 125, 60, 23);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Password");
		lblNewLabel_1.setFont(new Font("Book Antiqua", Font.BOLD, 14));
		lblNewLabel_1.setForeground(Color.BLUE);
		lblNewLabel_1.setBounds(93, 159, 73, 14);
		frame.getContentPane().add(lblNewLabel_1);
		
		textName = new JTextField();
		textName.setBounds(204, 125, 201, 20);
		frame.getContentPane().add(textName);
		textName.setColumns(10);
		
		JButton btnlogin = new JButton("Login");
		btnlogin.setFont(new Font("Book Antiqua", Font.BOLD, 14));
		btnlogin.setForeground(Color.BLUE);
		btnlogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					GraphDatabaseFactory graphDbFactory = new GraphDatabaseFactory();
					File storeFile = new File("F:/MMU_FCI_RECOMMENDER_SYSTEM/DataBase/demo");
				      GraphDatabaseService graphDb = graphDbFactory.newEmbeddedDatabase(storeFile);
				      Result er = graphDb.execute("MATCH(n:Subscriber) WHERE n.BILLING_ACCOUNT_NUMBER=" + "'"+textName.getText()+"'" +"  RETURN n.ACCOUNT");
					int count=0;
					if(er.hasNext()){
						count++;
					}else{
						count=0;
					}
					if(count>0){
						JOptionPane.showMessageDialog(null,"Login successful");
						
						frame.dispose();
						ShowData showData1=new ShowData(Integer.parseInt(textName.getText()), graphDb);
						showData1.setVisible(true);
					
					}else{
						JOptionPane.showMessageDialog(null,"User name and password not correct");
					}
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null,ex);
				}
			}
		});
		btnlogin.setBounds(219, 217, 137, 23);
		frame.getContentPane().add(btnlogin);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(204, 156, 201, 20);
		frame.getContentPane().add(passwordField);
		
		btnSignUp = new JButton("Sign UP");
		btnSignUp.setFont(new Font("Book Antiqua", Font.BOLD, 14));
		btnSignUp.setForeground(Color.BLUE);
		btnSignUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				
			}
		});
		btnSignUp.setBounds(219, 251, 137, 22);
		frame.getContentPane().add(btnSignUp);
		
		JLabel lblNewLabel_2 = new JLabel("");
		lblNewLabel_2.setBounds(10, 11, 73, 61);
		Image img=new ImageIcon(this.getClass().getResource("/tm.png")).getImage();
		Image newimg=img.getScaledInstance(lblNewLabel_2.getWidth(),lblNewLabel_2.getHeight(), Image.SCALE_SMOOTH);
		lblNewLabel_2.setIcon(new ImageIcon(newimg));
		
		frame.getContentPane().add(lblNewLabel_2);
	}
}
