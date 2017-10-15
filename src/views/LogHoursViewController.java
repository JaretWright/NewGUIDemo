package views;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import model.Person;

/**
 * FXML Controller class
 *
 * @author JWright
 */
public class LogHoursViewController implements Initializable {

    private Person person;
  
    @FXML private Label personIdLabel;
    @FXML private Label firstNameLabel;
    @FXML private Label lastNameLabel;
    @FXML private DatePicker datePicker;
    @FXML private Spinner hoursSpinner;
    
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
            
    
    
    public void preloadData(Person personSelected)
    {
        person = personSelected;
        personIdLabel.setText("Volunteer ID: " + person.getPersonID());
        firstNameLabel.setText("First name: " + person.getFirstName());
        lastNameLabel.setText("Last name: "+ person.getLastName());
        datePicker.setValue(LocalDate.now());
    }
    
    
    /**
     * If the save button is pushed, the hours should be logged in the database
     * @param event 
     */
    public void saveButtonPushed(ActionEvent event) throws SQLException, IOException
    {
        person.logHours( datePicker.getValue(), (Integer) hoursSpinner.getValue());
        cancelButtonPushed(event);
    }
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configure the hoursSpinner object to only accept values 0-24, with a default value of 8
        SpinnerValueFactory<Integer> hoursFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,24,8);
        hoursSpinner.setValueFactory(hoursFactory);
        
    }    
    
}
