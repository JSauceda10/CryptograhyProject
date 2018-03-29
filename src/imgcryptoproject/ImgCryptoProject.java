/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imgcryptoproject;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.security.Provider;
import javax.crypto.SecretKey;
/**
 *
 * @author Family
 */
public class ImgCryptoProject extends Application {
    BufferedImage bufferedImage,newSecureImg;
    Cipher cipher;
    MessageDigest md;
    SecretKeySpec secretKey;
    
    byte[] encryptedImage;
    @Override
    public void start(Stage primaryStage){
        byte[] imgData = null;
        
        
        Button btn = new Button("Choose an image to encrypt");
        Button btn2 = new Button("Encrypt");
        Button btn3 = new Button("Decrypt");
        
        Label nameofImgToBeEncrypted = new Label("Name of image to be encrypted");
        Label password = new Label("Password");
        Label password1 = new Label("Password for decryptor");
        
        TextField encryptPasswordField = new TextField();
        TextField decryptPasswordField = new TextField();
        TextField nOfImgToEncrField = new TextField();
        
        
        ImageView myImageView = new ImageView();
        ImageView myImageView2 = new ImageView();
        myImageView.setFitHeight(700);
        myImageView.setFitWidth(700);
        myImageView2.setFitHeight(200);
        myImageView2.setFitWidth(200);
        
        
        
        btn.setOnAction(new EventHandler<ActionEvent>() {
            
            
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Upload image to handle");
                fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.jpg")
                    //new FileChooser.ExtensionFilter("Image Files", "*.png")
                );
                File selectedFile = fileChooser.showOpenDialog(primaryStage);                       
                bufferedImage = null;
                
                try {
                    bufferedImage = ImageIO.read(selectedFile);
                    
                } catch (IOException ex) {
                    Logger.getLogger(ImgCryptoProject.class.getName()).log(Level.SEVERE, null, ex);
                }
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                try {   

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "jpg", baos);
                    baos.flush();
                    byte[] imageInByte = baos.toByteArray();
                    baos.close();
                } catch(IOException e){
                    System.out.println(e.getMessage());
                }		
               	
                myImageView.setImage(image);
                
                
            }
        });
        btn2.setOnAction(new EventHandler<ActionEvent>() {
            
            
            @Override
            public void handle(ActionEvent event) {
                
                
                
                byte[] key = encryptPasswordField.getText().getBytes();
                
                try {
                    
                    cipher = Cipher.getInstance("AES");
                    
                    md = MessageDigest.getInstance("SHA-256");
                    md.update(key);
                    key = Arrays.copyOfRange(md.digest(),0,16);
                    secretKey = new SecretKeySpec(key, "AES");
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//                    DataBufferByte db = (DataBufferByte)bufferedImage.getRaster().getDataBuffer();
//                    byte[] encryptedImage = db.getData();
                    int rows = bufferedImage.getWidth();
                    int cols = bufferedImage.getHeight();
                    byte[] ColorBytes = new byte[rows*cols];
                    
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            ColorBytes[i * cols + j] = (byte) bufferedImage.getRGB(i, j);
                            
                        }
                        
                    }
                    
                    encryptedImage = cipher.doFinal(ColorBytes);
                    //CANNOT USE IOUtilities. ARRAY CREATED IS NOT LARGE ENOUGH.
                    //encryptedImage = cipher.doFinal(IOUtilities.getImageRawBytes("jungle.jpg"));
                    System.out.println("Encrypted image length: ");
                    System.out.println(encryptedImage.length);
                    
                    int x = 0;
                    int y=0,z=0;
                    byte[][] encryptedImgByteArray = new byte[rows][cols];
                    
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            encryptedImgByteArray[i][j] = encryptedImage[x];
                            x++;  
                        }
                        
                    }

                    
                    
                    
                    newSecureImg = new BufferedImage(rows, cols, BufferedImage.TYPE_INT_RGB);
                    
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            
                            newSecureImg.setRGB(i, j, encryptedImgByteArray[i][j]);
                            
                        }
                        
                    }
                    
                    myImageView.setImage(SwingFXUtils.toFXImage(newSecureImg, null));
                    ImageIoFX.writeImage(newSecureImg, "jpg", nOfImgToEncrField.getText() + ".jpg");
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(ImgCryptoProject.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchPaddingException ex) {
                    Logger.getLogger(ImgCryptoProject.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalBlockSizeException ex) {
                    Logger.getLogger(ImgCryptoProject.class.getName()).log(Level.SEVERE, null, ex);
                } catch (BadPaddingException ex) {
                    Logger.getLogger(ImgCryptoProject.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidKeyException ex) {
                    Logger.getLogger(ImgCryptoProject.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(ImgCryptoProject.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
                
            }
        });
        btn3.setOnAction(new EventHandler<ActionEvent>() {
            
            
            @Override
            public void handle(ActionEvent event) {
                
                //Start Decrypting with newImg
                try {
                    byte[] keyBytes = decryptPasswordField.getText().getBytes();
                    
                    Cipher cipherNy = Cipher.getInstance("AES");
                    String algorithm = "AES"; 
                    md = MessageDigest.getInstance("SHA-256");
                    md.update(keyBytes);
                    keyBytes = Arrays.copyOfRange(md.digest(),0,16);
                    System.out.println(keyBytes.length);
                    SecretKeySpec key  = new SecretKeySpec(keyBytes, algorithm);

                    //Set up Enc or Dec Operations
                    cipherNy.init(Cipher.DECRYPT_MODE, key); 
                    
//                    ByteArrayOutputStream out = new ByteArrayOutputStream();
//                    ImageIO.write(newSecureImg, "jpg", out);
//                    STILL NOT ENOUGH
//                    System.out.println("NEW LENGTH:");
//                    System.out.println(out.toByteArray().length);
                    
                    byte[] decryptedImage = cipherNy.doFinal(encryptedImage);
                    System.out.println("Decrypted image length: ");
                    System.out.println(decryptedImage.length);
                    int rows = bufferedImage.getWidth();
                    int cols = bufferedImage.getHeight();
                    int x = 0;
                    byte[][] decryptedImgByteArray = new byte[rows][cols];
                    
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
//                            System.out.println(x);
                            decryptedImgByteArray[i][j] = decryptedImage[x];
                            x++;  
                        }
                        
                    }
                    
                    
                    BufferedImage newRevealedImg = new BufferedImage(rows, cols, BufferedImage.TYPE_INT_RGB);
                    
//                    byte[][] r = new byte[rows][cols];
//                    byte[][] g = new byte[rows][cols];
//                    byte[][] b = new byte[rows][cols];
                    int[] rgbColor = new int[rows*cols];
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
//                            r[i][j] = (byte) (((decryptedImage[i*cols+j] & 0xff) >> 16) & 0x000000FF);
//                            g[i][j] = (byte) (((decryptedImage[i*cols+j] & 0xff) >> 8) & 0x000000FF);
//                            b[i][j] = (byte) (((decryptedImage[i*cols+j] & 0xff) >> 0) & 0x000000FF);
                            rgbColor[i*cols+j] = new Color((((decryptedImage[i*cols+j] & 0xFF) /*>> 16*/) & 0xFF),
                                                           (((decryptedImage[i*cols+j] & 0xFF) /*>> 8*/) & 0xFF),
                                                           (((decryptedImage[i*cols+j] & 0xFF) /*>> 0*/) & 0xFF)).getRGB();
                        }
                        
                    }
                    
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            newRevealedImg.setRGB(i, j, rgbColor[i*cols+j]);
                            
                        }
                        
                    }
                    //newRevealedImg = ImageIoFX.setColorByteImageArray2DToBufferedImage(r, g, b);
                    myImageView.setImage(SwingFXUtils.toFXImage(newRevealedImg, null));
                    ImageIoFX.writeImage(newRevealedImg, "jpg", nOfImgToEncrField.getText() + ".jpg");
                } catch (InvalidKeyException ex) {
                    Logger.getLogger(ImgCryptoProject.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalBlockSizeException ex) {
                    Logger.getLogger(ImgCryptoProject.class.getName()).log(Level.SEVERE, null, ex);
                } catch (BadPaddingException ex) {
                    Logger.getLogger(ImgCryptoProject.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(ImgCryptoProject.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchPaddingException ex) {
                    Logger.getLogger(ImgCryptoProject.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
            }
        });
        
        
        
        GridPane gp = new GridPane();
        ScrollPane root = new ScrollPane(gp);
        
        gp.add(nameofImgToBeEncrypted, 0, 0);
        gp.add(nOfImgToEncrField, 0, 1);
        gp.add(myImageView, 0, 2);
        gp.add(password, 0, 3);
        gp.add(encryptPasswordField, 0, 4);
        gp.add(password1, 0, 5);
        gp.add(decryptPasswordField, 0, 6);
        
        gp.add(btn, 0, 7);
        gp.add(btn2, 0, 8);
        gp.add(btn3, 0, 9);
        
        
        
        Scene scene = new Scene(root, 700, 850);
        
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
