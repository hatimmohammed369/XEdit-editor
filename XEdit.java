/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xedit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Kaiser
 */
public class XEdit extends Application {
    BorderPane rootPane = new BorderPane();
    MenuBar menusBar = new MenuBar();
    final XEditFile file = new XEditFile(null, false, false);
    TextArea textArea = new TextArea();
    
    @Override
    public void start(Stage stage) {
        
        stage.setOnCloseRequest((event) -> {
            exitRequest(stage);
        });
        textArea.setOnKeyTyped((event) -> {
            file.setSaved(false);
            stage.setTitle(getFileName()+" (not saved) - XEdit");
        });
        //--------------------------------------------------------------------------------
        Menu fileMenu = new Menu("_File");
        fileMenu.setMnemonicParsing(true);
        
        MenuItem newSelection = new MenuItem("_New...");
        newSelection.setMnemonicParsing(true);
        newSelection.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
        newSelection.setOnAction((event) -> newFile(stage));
        
        MenuItem openSelection = new MenuItem("_Open");
        openSelection.setMnemonicParsing(true);
        openSelection.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
        openSelection.setOnAction((event) -> openFile(stage));
        
        MenuItem saveSelection = new MenuItem("_Save");
        saveSelection.setMnemonicParsing(true);
        saveSelection.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        saveSelection.setOnAction((event) -> saveFile(stage));
        
        MenuItem saveAsSelection = new MenuItem("Save _As");
        saveAsSelection.setMnemonicParsing(true);
        saveAsSelection.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+S"));
        saveAsSelection.setOnAction((event) -> saveFileAs(stage));
        
        MenuItem exitSelection = new MenuItem("_Exit");
        exitSelection.setMnemonicParsing(true);
        exitSelection.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));
        exitSelection.setOnAction((event) -> exit(stage));
        
        fileMenu.getItems().addAll(newSelection, openSelection, saveSelection, saveAsSelection, new SeparatorMenuItem(),
                exitSelection);
        
        menusBar.getMenus().addAll(fileMenu);
        
        rootPane.setTop(menusBar);
        //--------------------------------------------------------------------------------
        
        rootPane.setCenter(textArea);
        
        stage.setScene( new Scene(rootPane, 600, 600) );
        stage.setTitle("Untitled - XEdit");
        stage.show();
    }
    
    void openFile(Stage owner){
        FileChooser chooser = new FileChooser();
        File resultantFile = chooser.showOpenDialog(owner);
        if(resultantFile == null)
            return;
        file.init( new XEditFile(resultantFile, true, true) );
        updateTextArea();
        owner.setTitle(getFileName()+" - XEdit");
    }
    
    void newFile(Stage owner){
        if((file.getFile() != null && !file.isSaved()) || file == new XEditFile(null, false, false))
            saveRequest(owner);
        Stage exitRequestStage = new Stage();
        exitRequestStage.initModality(Modality.APPLICATION_MODAL);
        exitRequestStage.initStyle(StageStyle.UTILITY);
                
        Pane exitRequestStageRootPane = new Pane();
        exitRequestStageRootPane.setBackground( new Background( new BackgroundFill(Color.WHITE, null, null) ) );
                
        Label msg = new Label("Do you want to new window for the new file?");
        msg.setFont(Font.font(15));
        msg.setTextFill(Color.BLUE);
                
        Button yes = new Button("Yes");
        yes.setFont(Font.font(15));
        yes.setOnAction((Event) -> {
            new XEdit().start(new Stage());
            exitRequestStage.close();
            return;
        });
        yes.setPrefSize(100, 30);
                
        Button no = new Button("No");
        no.setFont(Font.font(15));
        no.setOnAction((Event) -> {
            file.init( new XEditFile(null, false, false) );
            textArea.setText("");
            owner.setTitle("Untitled - XEdit");
            exitRequestStage.close();
            return;
        });
        no.setPrefSize(100, 30);
                
        Rectangle rect = new Rectangle(330, 50);
        rect.setFill(Color.rgb(220, 220, 220));
                
        exitRequestStageRootPane.getChildren().addAll(msg, rect, yes, no);
        msg.relocate(10, 10);
        rect.relocate(0, 50);
        yes.relocate(30, 55);
        no.relocate(160, 55);
                
        exitRequestStage.setScene(new Scene(exitRequestStageRootPane, 330, 100));
        exitRequestStage.show();
    }
    
    void saveFile(Stage owner){
        if(file.isSaved())
            return;
        if(file.getFile() != null){
            updateFile();
            return;
        }
        FileChooser chooser = new FileChooser();
        File resultantFile = chooser.showSaveDialog(owner);
        if(resultantFile == null)
            return;
        try {
            resultantFile.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(XEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
        file.init( new XEditFile(resultantFile, true, true) );
        updateFile();
        owner.setTitle(getFileName()+" - XEdit");
    }
    
    void saveFileAs(Stage owner){
        FileChooser chooser = new FileChooser();
        File resultantFile = chooser.showSaveDialog(owner);
        if(resultantFile == null)
            return;
        try {
            resultantFile.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(XEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
        file.init( new XEditFile(resultantFile, true, true) );
        updateFile();
        owner.setTitle(getFileName()+" - XEdit");
    }
    
    void exit(Stage owner){
        if(file.isSaved())
            System.exit(0);
        exitRequest(owner);
    }
    
    void saveRequest(Stage owner) {
        if(file.isSaved())
            return;
        Stage exitRequestStage = new Stage();
        exitRequestStage.initOwner(owner);
        exitRequestStage.initModality(Modality.WINDOW_MODAL);
        exitRequestStage.initStyle(StageStyle.UTILITY);
                
        Pane exitRequestStageRootPane = new Pane();
        exitRequestStageRootPane.setBackground( new Background( new BackgroundFill(Color.WHITE, null, null) ) );
                
        Label msg = new Label("Do you want to save "+getFileName()+"?");
        msg.setFont(Font.font(15));
        msg.setTextFill(Color.BLUE);
                
        Button yes = new Button("Yes");
        yes.setFont(Font.font(15));
        yes.setOnAction((Event) -> saveFile(owner));
        yes.setPrefSize(70, 30);
                
        Button no = new Button("No");
        no.setFont(Font.font(15));
        no.setOnAction((Event) -> {
            exitRequestStage.close();
            owner.setTitle(getFileName()+" - XEdit");
            textArea.setText("");
            file.init( new XEditFile(null, false, false) );
        });
        no.setPrefSize(70, 30);
        
        Button cancel = new Button("Cancel");
        cancel.setFont(Font.font(15));
        cancel.setOnAction((Event) -> exitRequestStage.close());
        cancel.setPrefSize(70, 30);
                
        Rectangle rect = new Rectangle(300, 50);
        rect.setFill(Color.rgb(220, 220, 220));
                
        exitRequestStageRootPane.getChildren().addAll(msg, rect, yes, no, cancel);
        msg.relocate(10, 10);
        rect.relocate(0, 50);
        yes.relocate(20, 55);
        no.relocate(110, 55);
        cancel.relocate(200, 55);
        
        exitRequestStage.setScene(new Scene(exitRequestStageRootPane, 300, 100));
        exitRequestStage.show();
    }
    
    void exitRequest(Stage owner) {
        if(file.isSaved()){
            System.exit(0);
            return;
        }
        Stage exitRequestStage = new Stage(StageStyle.UTILITY);
        Pane exitRequestStageRootPane = new Pane();
        exitRequestStageRootPane.setBackground( new Background( new BackgroundFill(Color.WHITE, null, null) ) );
                
        Label msg = new Label("Do you want to save "+getFileName()+"?");
        msg.setFont(Font.font(15));
        msg.setTextFill(Color.BLUE);
                
        Button yes = new Button("Yes");
        yes.setFont(Font.font(15));
        yes.setOnAction((Event) -> saveFile(owner));
        yes.setPrefSize(70, 30);
                
        Button no = new Button("No");
        no.setFont(Font.font(15));
        no.setOnAction((Event) -> {
            exitRequestStage.close();
            owner.close();
            System.exit(0);
        });
        no.setPrefSize(70, 30);
        
        Button cancel = new Button("Cancel");
        cancel.setFont(Font.font(15));
        cancel.setOnAction((Event) -> exitRequestStage.close());
        cancel.setPrefSize(70, 30);
                
        Rectangle rect = new Rectangle(300, 50);
        rect.setFill(Color.rgb(220, 220, 220));
                
        exitRequestStageRootPane.getChildren().addAll(msg, rect, yes, no, cancel);
        msg.relocate(10, 10);
        rect.relocate(0, 50);
        yes.relocate(20, 55);
        no.relocate(110, 55);
        cancel.relocate(200, 55);
        
        
        exitRequestStage.initOwner(owner);
        exitRequestStage.initModality(Modality.WINDOW_MODAL);
        exitRequestStage.setScene(new Scene(exitRequestStageRootPane, 300, 100));
        exitRequestStage.show();
    }
    
    void updateFile(){
        if(!file.isOpened() || file.isSaved() || file.getFile() == null)
            return;
        String data = textArea.getText();
        try(FileWriter writer = new FileWriter( file.getFile() )){
            writer.write(data);
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(XEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
        file.setSaved(true);
    }
    
    void updateTextArea(){
        if(!file.isOpened() || file.getFile() == null)
            return;
        String data = "";
        try(FileReader reader = new FileReader( file.getFile() )){
            int c;
            while((c = reader.read()) != -1)
                data += (char)c;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(XEdit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
        textArea.setText(data);
    }
    
    String getFileName(){
        return file.getFile() == null ? "Untitled" : file.getFile().getName();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
