/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import model.Person;

/**
 * FXML Controller class
 *
 * @author JWright
 */
public class PersonViewController implements Initializable {

    //hold the Person object that is displayed in the view
    private Person person;
    private File imageFile;
    private boolean imageChanged=false;
    
    //FXML elements that interact with the Person class
    @FXML private TextField firstNameTextField;
    @FXML private TextField lastNameTextField;
    @FXML private TextField userNameTextField;
    @FXML private DatePicker datePicker;
    @FXML private ImageView image;
    @FXML private Label errMsg;
    
    
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
     * This method will allow a user to change their password
     */
    public void changePWButtonPushed(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("ChangePWView.fxml"));
        Parent parent = loader.load();

        Scene tableViewScene = new Scene(parent);

        //access the controller and call a method
        ChangePWViewController controller = loader.getController();
        controller.loadPerson(person);

        //This line gets the Stage information
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

        window.setScene(tableViewScene);
        window.show();
    }
    
    
    /**
     * This method will launch a FileChooser and load the image if accessible
     */
    public void chooseImageButtonPushed(ActionEvent event)
    {
        //get the stage to open a new window
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");
        
        //filter for only .jpg and .png files
        FileChooser.ExtensionFilter jpgFilter 
                = new FileChooser.ExtensionFilter("Image File (*.jpg)", "*.jpg");
        FileChooser.ExtensionFilter pngFilter 
                = new FileChooser.ExtensionFilter("Image File (*.png)", "*.png");
        
        fileChooser.getExtensionFilters().addAll(jpgFilter, pngFilter);
        
        
        //Set to the user's picture directory or C drive if not available
        String userDirectoryString = System.getProperty("user.home")+"\\Pictures";
        File userDirectory = new File(userDirectoryString);
        
        //if you cannnot navigate to the pictures directory, go to the user home
        if (!userDirectory.canRead())
            userDirectory = new File(System.getProperty("user.home"));
        
        fileChooser.setInitialDirectory(userDirectory);
        
        //open the file dialog window
        imageFile = fileChooser.showOpenDialog(stage);
        
        //ensure the user selected a file
        if (imageFile.isFile())
        {
            try
            {
                BufferedImage bufferedImage = ImageIO.read(imageFile);
                Image img = SwingFXUtils.toFXImage(bufferedImage,null);
                image.setImage(img);
            }
            catch (IOException e)
            {
                System.err.println(e.getMessage());
            }
            imageChanged = true;
        }
    }
 
    
    /**
     * This method will load a Person object and set all of the fields
     * to reflect the Person
     */
    public void preloadData(Person personSelected) throws IOException
    {
        person = personSelected;
        firstNameTextField.setText(person.getFirstName());
        lastNameTextField.setText(person.getLastName());
        userNameTextField.setText(person.getUserName());
        datePicker.setValue(person.getBirthdate());    
        BufferedImage bufferedImage = ImageIO.read(person.getImageFile());
        Image img = SwingFXUtils.toFXImage(bufferedImage, null);
        image.setImage(img);
    }
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        errMsg.setText("");
        
        try
        {
            imageFile = new File("./src/images/defaultPerson.png");
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            Image img = SwingFXUtils.toFXImage(bufferedImage, null);
            image.setImage(img);
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
        }
    }    
    
    
    /**
     * This method will create a Person object and store it in the database
     */
    public void saveButtonPushed(ActionEvent event) throws SQLException, IOException
    {
        try{
            //check if it is a new Person with a custom image
            if (person == null && imageChanged)
            {
                person = new Person(firstNameTextField.getText(),
                                    lastNameTextField.getText(),
                                    userNameTextField.getText(),
                                    datePicker.getValue(),
                                    imageFile
                                    );
                person.insertIntoDB();
            }
            else if (person == null)  //a new Person with the default image
            {
                person = new Person(firstNameTextField.getText(),
                                    lastNameTextField.getText(),
                                    userNameTextField.getText(),
                                    datePicker.getValue());
                person.insertIntoDB();
            }
            else    //editting an existing user
            {
                person.setFirstName(firstNameTextField.getText());
                person.setLastName(lastNameTextField.getText());
                person.setUserName(userNameTextField.getText());
                person.setBirthdate(datePicker.getValue());
                person.updateDB();
            }
            if (imageChanged)
            {
                person.setImageFile(imageFile);
                person.copyImageFile();
                person.updateDB();
            }
                
            
        }catch (IllegalArgumentException e)
        {
            System.err.println(e.getMessage());
        }
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("AllVolunteerView.fxml"));
        Parent tableViewParent = loader.load();
        
        Scene tableViewScene = new Scene(tableViewParent);
          
        //This line gets the Stage information
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(tableViewScene);
        window.show();
    }
    
}
