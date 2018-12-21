/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicplayer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javazoom.jl.player.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javazoom.jl.decoder.JavaLayerException;

/**
 *
 * @author Zachery Soles
 */
public class MusicPlayer {
  long pauseLocation, totalSongLength;
  String fileLocation, path;
  FileInputStream fis;
  Player player;
  private final UI frame = new UI();
  JFileChooser choice = getFileChoice();
  
  //Adds functionality to open files
  private static JFileChooser getFileChoice() {
    JFileChooser choice = new JFileChooser();
    //Specify where chooser should open up
    File workingDirectory = new File(System.getProperty("user.dir"));
    choice.setCurrentDirectory(workingDirectory);
    //Only show files that are .txt
    choice.addChoosableFileFilter(new FileNameExtensionFilter(".mp3 files", "mp3"));
    //Do not accept all files
    choice.setAcceptAllFileFilterUsed(false);
    return choice;
  }
  //Makes all of the buttons and text fields uneditable
  public final void start(){
    frame.getSongTextField().setEnabled(false);
    frame.getPlayButton().setEnabled(false);
    frame.getPauseButton().setEnabled(false);
    frame.getStopButton().setEnabled(false);
  }
  //Plays the selected mp3 file
  public void play(String path){
    try{
      //Finds the path of the file, and finds the total length
      fis = new FileInputStream(path);
      player = new Player(fis);
      totalSongLength = fis.available();
      fileLocation = path+"";
    //Displays the exceptions
    }catch(FileNotFoundException | JavaLayerException ex){
      System.out.println(ex);
    }catch (IOException ex) {
      Logger.getLogger(MusicPlayer.class.getName()).log(Level.SEVERE, null, ex);
    }
    //plays the file and catches the exception, if there is one
    new Thread(){
      @Override
      public void run(){
        try{
            player.play();

        }catch (JavaLayerException ex) {
          System.out.println(ex);
        }
      }
    }.start();
  }
  //Pauses the song, and finds the location of the pause in the song
  public void pause(){
    if(player != null){
      try {
        pauseLocation = fis.available();
        player.close();
      }catch (IOException ex) {
        System.out.println(ex);
      }
    }
  }
  //Resumes the song, by starting the song at the paused time
  public void resume(){
    try{
      fis = new FileInputStream(fileLocation);
      player = new Player(fis);
      fis.skip(totalSongLength - pauseLocation);
    }catch(IOException | JavaLayerException ex){
      System.out.println(ex);
    }
    new Thread(){
      @Override
      public void run(){
        try{
            player.play();

        }catch (JavaLayerException ex) {
          System.out.println(ex);
        }
      }
    }.start();
  }
  //Stops the song and sets everything back to regular when you opened the application
  public void stop(){
    if(player != null){
      player.close();
      totalSongLength = 0;
      pauseLocation = 0;
      frame.getPlayButton().setEnabled(false);
      frame.getSongTextField().setText("Untitled");
    }
  }
  //Checks to see if the file is in the subdirectory
  public static boolean inSubDir(File f) {
    return f.getPath().contains("subdir");
  }
  //Contructor
  public MusicPlayer(){
    start();
    //Action for the open button
    frame.getOpenButton().addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e){
        //JFile choice
        int status = choice.showOpenDialog(frame);
        if(status != JFileChooser.APPROVE_OPTION)
        {
          return;
        }
        File f = choice.getSelectedFile();
        Path pPath = f.toPath();
        path = pPath.toString();
        String fileName;
        try {
          //Content has not been edited
          String content = new String(Files.readAllBytes(pPath));
        }
        catch (IOException ex) {
          Logger.getLogger(MusicPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Checks to see if file is in a subdirectory
        if(inSubDir(f))
          fileName = "subdir/" + f.getName();
        else
          fileName = f.getName();
        //Opens the file
        frame.getSongTextField().setText(fileName);
        frame.getPlayButton().setEnabled(true);
      }
    });
    //Action for the play button
    frame.getPlayButton().addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e){
        //Checks to see if you are starting a new song or are resuming
        if(player != null){
          resume();
        }
        else
          play(path);
        //Sets the pause and stop buttons to enabled
        frame.getPauseButton().setEnabled(true);
        frame.getStopButton().setEnabled(true);
      }
    });
    //Action for the pause button
    frame.getPauseButton().addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e){
        pause();
      }  
    });
    //Action for the stop button
    frame.getStopButton().addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e){
        stop();
        start();
      }
    }); 
  }
  //Main Method
  public static void main(String[] args) {
    MusicPlayer app = new MusicPlayer();
    app.frame.setVisible(true);
  }
}