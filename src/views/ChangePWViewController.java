package views;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import model.Person;

/**
 * FXML Controller class
 *
 * @author JWright
 */
public class ChangePWViewController implements Initializable {

    private Person person;
    
    @FXML private PasswordField pwField1;
    @FXML private PasswordField pwField2;
    @FXML private Label errMsgLabel;
     
    public void cancelButtonPushed(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("AllVolunteerView.fxml"));
        Parent parent = loader.load();
        
        Scene tableViewScene = new Scene(parent);
          
        //This line gets the Stage information
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(tableViewScene);
        window.show();
    }
        

    /**
     * This method will update the users password and return to the edit user view
     * @param event 
     */
    public void changePWButtonPushed(ActionEvent event) throws IOException, NoSuchAlgorithmException, SQLException
    {
        //check if password meets the requirements of Minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character
        if (!(pwField1.getText().matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$")))
            errMsgLabel.setText(String.format("Password must have a minimum of 8 characters, at least one uppercase letter, %none lowercase letter, one number and one special character"));
            
            
        //check that the passwords match
        else if (!(pwField1.getText().equals(pwField2.getText())))
        {
            errMsgLabel.setText("The passwords do not match.");
        }
        else    
        {
            try
            {
                person.changePassword(pwField1.getText());
                changeToPersonView(event);
            }
            catch (Exception e)
            {
                errMsgLabel.setText(e.getMessage());
            }
                    
            
        }
    }
    
    public void changeToPersonView(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("PersonView.fxml"));
        Parent parent = loader.load();
        
        Scene tableViewScene = new Scene(parent);
        
        //access the controller and call a method
        PersonViewController controller = loader.getController();
        
        controller.preloadData(person);
        
        //This line gets the Stage information
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(tableViewScene);
        window.show();
    }
    
    public void loadPerson(Person personSelected)
    {
        person = personSelected;
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        errMsgLabel.setText("");
    }    
    
}
