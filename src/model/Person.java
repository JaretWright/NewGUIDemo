
package model;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;

/**
 *
 * @author JWright
 */
public class Person {
    //instance variables to describe a person
    private int personID;
    private String firstName, lastName, pwSalt;
    private LocalDate birthdate;
    private File imageFile;

    /**
     * Creating a new person object with
     * @param firstName String
     * @param lastName String
     * @param birthdate LocalDate
     * @param imageFile File
     */
    public Person(String firstName, String lastName, LocalDate birthdate, File imageFile) throws IOException, SQLException {
        setFirstName(firstName);
        setLastName(lastName);
        setBirthdate(birthdate);
        setImageFile(imageFile);
        copyImageFile();
    }

    
    /**
     * Default constructor, assumes the default image
     */
    public Person(String firstName, String lastName, LocalDate birthdate) throws IOException, SQLException {
        setFirstName(firstName);
        setLastName(lastName);
        setBirthdate(birthdate);
        setImageFile(new File("./src/images/defaultPerson.png"));
    }

    
    /**
     * Used to create an instance of Person where the personID is known
     */
    public Person(int personID, String firstName, String lastName, LocalDate birthdate) {
        setPersonID(personID);
        setFirstName(firstName);
        setLastName(lastName);
        setBirthdate(birthdate);
    }

    
    /**
     * Constructor
     * @param personID
     * @param firstName
     * @param lastName
     * @param birthdate
     * @param imageFile
     * @throws IOException
     * @throws SQLException 
     */
    public Person(int personID, String firstName, String lastName, LocalDate birthdate, File imageFile) throws IOException, SQLException {
        this(firstName, lastName, birthdate, imageFile);
        this.personID = personID;
    }
    
    private byte[] getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        pwSalt = salt.toString();
        return salt;
    }
    
    private String get_SHA_512_SecurePassword(String passwordToHash) throws NoSuchAlgorithmException
    {
        byte[] salt = this.getSalt();
        
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return generatedPassword;
    }
    
    /**
     * This method will update a users password on the database
     * @param newPassword 
     */
    public void changePassword(String newPassword) throws NoSuchAlgorithmException, SQLException
    {
        Connection conn =null;
        PreparedStatement  preparedStatement = null;
        ResultSet resultSet = null;

        String userName = "student";
        String password = "student";

        newPassword = get_SHA_512_SecurePassword(newPassword);
        
        try{
            // 1. connect to the DB
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbcDemo", userName, password);
            
            //2.  create a string that holds query with ? for user inputs
            String sql = "UPDATE people SET pw = ?, salt = ? WHERE personID = ?";
                    
            //3. prepare the query
            preparedStatement = conn.prepareStatement(sql);
            
            //4.  Convert the birthday (LocalDate) to Date object
            Date bd = Date.valueOf(birthdate);
            
            //5.  bind values to the parameters
            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, pwSalt);
            preparedStatement.setInt(3, personID);
            
            preparedStatement.executeUpdate();
            preparedStatement.close();
            
        } 
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
        finally
        {
            if (resultSet != null)
                resultSet.close();
            if (preparedStatement != null)
                preparedStatement.close();
            if (conn != null)
                conn.close();
        }
    }
    
    
     public void copyImageFile() throws IOException, SQLException
    {
        //create a new Path to copy the image into a local directory
        Path sourcePath = imageFile.toPath();
        
        String uniqueFileName = getUniqueFileName(imageFile.getName());
        
        Path targetPath = Paths.get("./src/images/"+uniqueFileName);
 
        //Copy the file to the new directory
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        //update the imageFile to point to the new file
        imageFile = new File(targetPath.toString());
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

         
    public String getFirstName() {
        return firstName;
    }
    

    public File getImageFile() {
        return imageFile;
    }
    

    public String getLastName() {
        return lastName;
    }

    
    public int getPersonID() {
        return personID;
    }
    
       
    private String getUniqueFileName(String oldFileName)
    {
        String newName;
        
        
        //create a random number generator
        SecureRandom rng = new SecureRandom();
        
        //keep looping until it is a unique name
        do
        {
            newName = ""; //reset the newName
            
            //generate 32 random characters
            for (int count = 1; count <= 32; count++)
            { 
                int nextChar;

                do
                {
                    nextChar = rng.nextInt(123);
                }
                while(!validCharacterValue(nextChar));

                newName = String.format("%s%c",newName,nextChar);
            }
            newName += oldFileName;
            
        } while (!uniqueFileInDirectory(newName));
        
        return newName;
    }
    
    /**
     * This method will update the hours worked database with the date and hours
     * worked
     */
    public void logHours(LocalDate dateWorked, int hoursWorked) throws SQLException
    {
        Connection conn =null;
        PreparedStatement  preparedStatement = null;
        ResultSet resultSet = null;

        String userName = "student";
        String password = "student";

        try{
            // 1. connect to the DB
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbcDemo", userName, password);
            
            //2.  create a string that holds query with ? for user inputs
            String sql = "INSERT INTO hoursWorked (personID, dayWorked, hoursWorked) VALUES (?,?,?)";
            
            //3. prepare the query
            preparedStatement = conn.prepareStatement(sql);
            
            //4.  Convert the birthday (LocalDate) to Date object
            Date dw = Date.valueOf(dateWorked);
            
            //5.  bind values to the parameters
            preparedStatement.setInt(1, personID);
            preparedStatement.setDate(2, dw);
            preparedStatement.setInt(3, hoursWorked);
            
            preparedStatement.executeUpdate();
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (resultSet != null)
                resultSet.close();
            if (preparedStatement != null)
                preparedStatement.close();
            if (conn != null)
                conn.close();
        }
    }
    
    /**
     * This method will store the new Person object in the database
     */
    public void insertIntoDB() throws SQLException
    {
        Connection conn =null;
        PreparedStatement  preparedStatement = null;
        ResultSet resultSet = null;

        String userName = "student";
        String password = "student";

        try{
            // 1. connect to the DB
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbcDemo", userName, password);
            
            //2.  create a string that holds query with ? for user inputs
            String sql = "INSERT INTO people (firstName, lastName, birthday, photo) VALUES (?,?,?,?)";
            
            //3. prepare the query
            preparedStatement = conn.prepareStatement(sql);
            
            //4.  Convert the birthday (LocalDate) to Date object
            Date bd = Date.valueOf(birthdate);
            
            //5.  bind values to the parameters
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setDate(3, bd);
            preparedStatement.setString(4, imageFile.getName());
            
            preparedStatement.executeUpdate();
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (resultSet != null)
                resultSet.close();
            if (preparedStatement != null)
                preparedStatement.close();
            if (conn != null)
                conn.close();
        }
    }
    
    
    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }
    
    
    public void setFirstName(String firstName) { 
            this.firstName = firstName;
    }
    
    
    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }
    
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

   
    public void setPersonID(int personID) {
        this.personID = personID;
    }
     
    
    private boolean uniqueFileInDirectory(String fileName)
    {
        File directory = new File("./src/images");
        File[] dir_contents = directory.listFiles();
       
        for (File file : dir_contents)
        {
            if (file.getName().equals(fileName))
                return false;
        }
        return true;
    }
    
    
    private boolean validCharacterValue(int asciiValue)
    {
        //0-9 = 48 to 57
        if (asciiValue >= 48 && asciiValue <= 57)
            return true;

        //A-Z = 65 to 90        
        if (asciiValue >= 65 && asciiValue <= 90 )
            return true;
        
        //a-z = 97 to 122
        if (asciiValue >= 97 && asciiValue <= 122)
            return true;
        else
            return false;
    }
    
    
    /**
     * This method will update the existing Person object in the database
     */
    public void updateDB() throws SQLException
    {
        Connection conn =null;
        PreparedStatement  preparedStatement = null;
        ResultSet resultSet = null;

        String userName = "student";
        String password = "student";

        try{
            // 1. connect to the DB
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbcDemo", userName, password);
            
            //2.  create a string that holds query with ? for user inputs
            String sql = "UPDATE people SET firstName = ?, lastName = ?, birthday = ?, photo  = ? WHERE personID = ?";
                    
            //3. prepare the query
            preparedStatement = conn.prepareStatement(sql);
            
            //4.  Convert the birthday (LocalDate) to Date object
            Date bd = Date.valueOf(birthdate);
            
            //5.  bind values to the parameters
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setDate(3, bd);
            preparedStatement.setString(4, imageFile.getName());
            preparedStatement.setInt(5, personID);
            
            preparedStatement.executeUpdate();
            preparedStatement.close();
            
        } 
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
        finally
        {
            if (resultSet != null)
                resultSet.close();
            if (preparedStatement != null)
                preparedStatement.close();
            if (conn != null)
                conn.close();
        }
    }
        
}
    
