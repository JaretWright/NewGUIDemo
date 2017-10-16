
package views;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.SHAExample;

/**
 * FXML Controller class
 *
 * @author JWright
 */
public class LoginViewController implements Initializable {
    @FXML private TextField userNameTextField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorMsg;
    
    public void submitButtonPushed(ActionEvent event) throws SQLException
    {
        //query the database with the volunteer #, retrieve the
        //encrypted password and the salt
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String dbPassword = null;
        String dbSalt = null;
        
        String userName = "student";
        String password = "student";

        try{
            // 1. connect to the DB
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbcDemo", userName, password);

           //2.  create a string that holds query with ? for user inputs
            String sql = "SELECT pw, salt FROM people WHERE personID = 19";
            
            //3. prepare the query
            statement = conn.prepareStatement(sql);

            
            //4.  bind values to the parameters
           // statement.setInt(1, Integer.parseInt(userNameTextField.getText()));

            
            // 5. execute the query
            resultSet = statement.executeQuery();
            
            System.out.printf("result set: %s%n", resultSet);
            while (resultSet.next())
            {
                dbPassword = resultSet.getString(1);
                dbSalt = resultSet.getString(2);
            }
            
            System.out.printf("The DB password is: %s%n", dbPassword);
            
            
            //create encrypted password
            byte[] salt = dbSalt.getBytes();
            String userPw = SHAExample.get_SHA_512_SecurePassword(this.passwordField.getText(), salt);
            
            System.out.printf("The US password is: %s%n", userPw);
            
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (resultSet != null)
                resultSet.close();
            if (statement != null)
                statement.close();
            if (conn != null)
                conn.close();
        }
        
        
        
        //convert the password provided into an encrypted password
        //check to see if they are the same, if yes, go to the all volunteers scene
        //if not, display a message to the screen
    }
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
