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
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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
import javafx.stage.WindowEvent;

/**
 *
 * @author Acer
 */
public class XEdit extends Application {
    Stage stage = new Stage();
    TabPane tabs = new TabPane();
    BorderPane root = new BorderPane();
    HashMap<Tab, XEditFile> files = new HashMap<>();
    MenuBar menuBar = new MenuBar();
    
    @Override
    public void start(Stage primaryStage) {
        //Setting up the menu bar
        Menu fileMenu = new Menu("_File");
        fileMenu.setMnemonicParsing(true);
        
        MenuItem newSelection = new MenuItem("New...");
        newSelection.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
        newSelection.setOnAction((event) -> newFile());
        
        MenuItem openSelection = new MenuItem("Open");
        openSelection.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
        openSelection.setOnAction((event) -> openFile());
        
        MenuItem saveAsSelection = new MenuItem("Save As");
        saveAsSelection.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+S"));
        saveAsSelection.setOnAction((event) -> saveFileAs());
        
        MenuItem saveSelection = new MenuItem("Save");
        saveSelection.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        saveSelection.setOnAction((event) -> saveFile());
        
        MenuItem exitSelection = new MenuItem("Exit");
        exitSelection.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));
        exitSelection.setOnAction((event) -> exit());
        
        fileMenu.getItems().addAll(newSelection, openSelection, saveAsSelection, saveSelection, new SeparatorMenuItem(),
                exitSelection);
        
        menuBar.getMenus().addAll(fileMenu);
        root.setTop(menuBar);
        //--------------------------------------------------------------------------------
        
        root.setCenter(tabs);
        
        stage.setScene(new Scene(root, 600, 600));
        stage.show();
        stage.setOnCloseRequest((event) -> {
            exit();
        });
    }
    
    Tab getSelectedTab(){
        return tabs.getSelectionModel().getSelectedItem();
    }
    
    XEditFile getCurrentXEditFile(){
        return files.get(getSelectedTab());
    }
    
    File getCurrentFile(){
        return getCurrentXEditFile().getFile();
    }
    
    TextArea getCurrentTextArea(){
        return ((TextArea) getSelectedTab().getContent());
    }
    
    String getCurrentText(){
        return getCurrentTextArea().getText();
    }
    
    void updateCurrentFile(){
        if(getCurrentFile() == null)
            return;
        String text = getCurrentText();
        try(FileWriter writer = new FileWriter( getCurrentFile() )){
            writer.write(text);
        } catch (IOException ex) {
            Logger.getLogger(XEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void updateCurrentTextArea(){
        if(getCurrentFile() == null)
            return;
        String data = "";
        try(FileReader reader = new FileReader( getCurrentFile() )){
            int c;
            while((c = reader.read()) != -1)
                data += (char)c;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(XEdit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
        getCurrentTextArea().setText(data);
    }
    
    void newFile(){
        Tab tab = new Tab("Untitled");
        XEditFile file = new XEditFile(null, false, false);
        tab.setOnCloseRequest((event) -> {
            tabs.getTabs().remove(tab);
            files.remove(tab);
        });
        
        TextArea content = new TextArea();
        content.setOnKeyTyped((event) -> {
            tab.setText(getCurrentFile() == null ? "Untitled (not saved)" : 
                    getCurrentFile().toPath().toString()+" (not saved)");
            getCurrentXEditFile().setSaved(false);
        });
        tab.setContent(content);
        
        tabs.getTabs().add(tab);
        files.put(tab, file);
    }
    
    void openFile(){
        FileChooser chooser = new FileChooser();
        File resultantFile = chooser.showOpenDialog(stage);
        if(resultantFile == null || !resultantFile.canRead())
            return;
        String data = "";
        try(FileReader reader = new FileReader(resultantFile)){
            int c;
            while((c = reader.read()) != -1)
                data += (char)c;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(XEdit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
        newFile();
        getSelectedTab().setContent((Node) new TextArea(data));
        getSelectedTab().setText(resultantFile.toPath().toString());
        getSelectedTab().getContent().setOnKeyTyped((event) -> {
            getSelectedTab().setText(resultantFile.toPath().toString()+" (not saved)");
            getCurrentXEditFile().setSaved(false);
        });
        getCurrentXEditFile().setFile(resultantFile);
        getCurrentXEditFile().setSaved(true);
        getCurrentXEditFile().setOpened(true);
    }
    
    void saveFileAs(){
        if(tabs.getTabs().isEmpty())
            return;
        FileChooser chooser = new FileChooser();
        File resultantFile = chooser.showSaveDialog(stage);
        if(resultantFile == null)
            return;
        try {
            resultantFile.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(XEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
        getCurrentXEditFile().setFile(resultantFile);
        try(FileWriter writer = new FileWriter( resultantFile )){
            writer.write(getCurrentText());
        } catch (IOException ex) {
            Logger.getLogger(XEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
        getSelectedTab().setText(resultantFile.toPath().toString());
        getCurrentXEditFile().setSaved(true);
        getCurrentXEditFile().setOpened(true);
    }
    
    void saveFile(){
        if(tabs.getTabs().isEmpty())
            return;
        if(getCurrentFile() == null){
            FileChooser chooser = new FileChooser();
            File resultantFile = chooser.showSaveDialog(stage);
            if(resultantFile == null)
                return;
            try {
                resultantFile.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(XEdit.class.getName()).log(Level.SEVERE, null, ex);
            }
            getCurrentXEditFile().setFile(resultantFile);
            try(FileWriter writer = new FileWriter( resultantFile )){
                writer.write(getCurrentText());
            } catch (IOException ex) {
                Logger.getLogger(XEdit.class.getName()).log(Level.SEVERE, null, ex);
            }
            getSelectedTab().setText(resultantFile.toPath().toString());
            getCurrentXEditFile().setSaved(true);
            getCurrentXEditFile().setOpened(true);
        }else{
            if(!getCurrentXEditFile().isSaved() && getCurrentXEditFile().isOpened()){
                getCurrentXEditFile().setSaved(true);
                getSelectedTab().setText(getCurrentFile().toPath().toString());
                updateCurrentFile();
            }
        }
    }
    
    void exit(){
        if(files.isEmpty() || tabs.getTabs().isEmpty()){
            System.exit(0);
        }else{
            if(getCurrentFile() == null){
                Stage exitRequestStage = new Stage();
                exitRequestStage.initModality(Modality.APPLICATION_MODAL);
                exitRequestStage.initStyle(StageStyle.UTILITY);
                
                Pane exitRequestStageRootPane = new Pane();
                exitRequestStageRootPane.setBackground( new Background( new BackgroundFill(Color.WHITE, null, null) ) );
                
                Label msg = new Label("Do you want to save untitled?");
                msg.setFont(Font.font(15));
                msg.setTextFill(Color.BLUE);
                
                Button yes = new Button("Yes");
                yes.setFont(Font.font(15));
                yes.setOnAction((Event) -> {
                    saveFile();
                    System.exit(0);
                });
                yes.setPrefSize(70, 30);
                
                Button no = new Button("No");
                no.setFont(Font.font(15));
                no.setOnAction((Event) -> System.exit(0));
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
                yes.relocate(10, 55);
                no.relocate(100, 55);
                cancel.relocate(200, 55);
                
                exitRequestStage.setScene(new Scene(exitRequestStageRootPane, 300, 100));
                exitRequestStage.show();
            }else{
                if(getCurrentXEditFile().isSaved()){
                    System.exit(0);
                }else{
                    Stage exitRequestStage = new Stage();
                    exitRequestStage.initModality(Modality.APPLICATION_MODAL);
                    exitRequestStage.initStyle(StageStyle.UTILITY);
                
                    Pane exitRequestStageRootPane = new Pane();
                    exitRequestStageRootPane.setBackground( new Background( new BackgroundFill(Color.WHITE, null, null) ) );
                
                    Label msg = new Label("Do you want to save untitled?");
                    msg.setFont(Font.font(15));
                    msg.setTextFill(Color.BLUE);
                
                    Button yes = new Button("Yes");
                    yes.setFont(Font.font(15));
                    yes.setOnAction((Event) -> {
                        saveFile();
                        System.exit(0);
                    });
                    yes.setPrefSize(70, 30);
                    
                    Button no = new Button("No");
                    no.setFont(Font.font(15));
                    no.setOnAction((Event) -> System.exit(0));
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
                    yes.relocate(10, 55);
                    no.relocate(100, 55);
                    cancel.relocate(200, 55);
                
                    exitRequestStage.setScene(new Scene(exitRequestStageRootPane, 300, 100));
                    exitRequestStage.show();
                }
            }
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
