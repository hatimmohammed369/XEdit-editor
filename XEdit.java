/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xedit;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 *
 * @author Kaiser
 */
public class XEdit extends Application {
    private final BorderPane rootPane = new BorderPane();
    private final MenuBar menuBar = new MenuBar();
    private final XEditFile file = new XEditFile();
    private final TextArea textArea = new TextArea();
    private int lastNavedLine = linesCount();
    private boolean firstClick = false;
    
    @Override
    public void start(Stage stage) {
        stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, (windowEvent) -> {
            windowEvent.consume();
            quit(stage);
        });
        
        textArea.setOnKeyTyped((event) -> {
            file.setSaved(false);
            stage.setTitle(getFileName()+" (not saved) - XEdit");
        });
        
        //--------------------------------------------------------------------------------
        //////////////////////////////////////////////////////////////////////////////////
        Menu fileMenu = new Menu("_File");
        fileMenu.setMnemonicParsing(true);
        
        MenuItem newSelection = new MenuItem("_New");
        newSelection.setMnemonicParsing(true);
        newSelection.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
        newSelection.setOnAction((event) -> newFile(stage));
        fileMenu.getItems().add(newSelection);
        
        MenuItem openSelection = new MenuItem("_Open");
        openSelection.setMnemonicParsing(true);
        openSelection.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
        openSelection.setOnAction((event) -> openFile(stage));
        fileMenu.getItems().add(openSelection);
        
        MenuItem saveSelection = new MenuItem("_Save");
        saveSelection.setMnemonicParsing(true);
        saveSelection.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        saveSelection.setOnAction((event) -> saveFile(stage));
        fileMenu.getItems().add(saveSelection);
        
        MenuItem saveAsSelection = new MenuItem("Save _As");
        saveAsSelection.setMnemonicParsing(true);
        saveAsSelection.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+S"));
        saveAsSelection.setOnAction((event) -> saveFileAs(stage));
        fileMenu.getItems().add(saveAsSelection);
        
        fileMenu.getItems().add(new SeparatorMenuItem());
        
        MenuItem quitSelection = new MenuItem("_Quit");
        quitSelection.setMnemonicParsing(true);
        quitSelection.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));
        quitSelection.setOnAction((event) -> quit(stage));
        fileMenu.getItems().add(quitSelection);
        
        menuBar.getMenus().add(fileMenu);
        //////////////////////////////////////////////////////////////////////////////////
        
        //////////////////////////////////////////////////////////////////////////////////
        Menu editMenu = new Menu("_Edit");
        editMenu.setMnemonicParsing(true);
        
        MenuItem cutSelection = new MenuItem("_Cut");
        cutSelection.setMnemonicParsing(true);
        cutSelection.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));
        cutSelection.setOnAction((event) -> textArea.cut());
        editMenu.getItems().add(cutSelection);
        
        MenuItem copySelection = new MenuItem("C_opy");
        copySelection.setMnemonicParsing(true);
        copySelection.setAccelerator(KeyCombination.keyCombination("Ctrl+C"));
        copySelection.setOnAction((event) -> textArea.copy());
        editMenu.getItems().add(copySelection);
        
        MenuItem pasteSelection = new MenuItem("_Paste");
        pasteSelection.setMnemonicParsing(true);
        pasteSelection.setAccelerator(KeyCombination.keyCombination("Ctrl+V"));
        pasteSelection.setOnAction((event) -> textArea.paste());
        editMenu.getItems().add(pasteSelection);
        
        MenuItem goToSelection = new MenuItem("_Go To Line");
        goToSelection.setMnemonicParsing(true);
        goToSelection.setAccelerator(KeyCombination.keyCombination("Ctrl+G"));
        goToSelection.setOnAction((event) -> {
            Stage goToStage = new Stage();
            
            goToStage.setTitle("Go To Line");
            goToStage.initOwner(stage);
            goToStage.initModality(Modality.WINDOW_MODAL);
            goToStage.initStyle(StageStyle.UTILITY);
            
            Pane goToStageRootPane = new Pane();
            
            Label msg = new Label("Go to line:");
            msg.setFont(Font.font(13));
            
            TextField lineNumberField = new TextField(String.valueOf(lastNavedLine));
            lineNumberField.setFont(Font.font(13));
            lineNumberField.setPrefSize(200, 15);
            
            goToStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, (windowEvent) -> {
                windowEvent.consume();
                goToStage.close();
                final int lineNumber = Integer.parseInt(lineNumberField.getText());
                if(lineNumber == 0 || lineNumber > linesCount())
                    lastNavedLine = linesCount();
            });
            
            goToStage.addEventHandler(KeyEvent.KEY_PRESSED, (keyEvent) -> {
                if(keyEvent.getCode() != KeyCode.ESCAPE)
                    return;
                keyEvent.consume();
                goToStage.close();
                final int lineNumber = Integer.parseInt(lineNumberField.getText());
                if(lineNumber == 0 || lineNumber > linesCount())
                    lastNavedLine = linesCount();
            });
            
            Button go = new Button("Go To");
            go.setFont(Font.font(13));
            go.setPrefSize(70, 15);
            go.setOnAction((actionEvent) -> {
                final int lineNumber = Integer.parseInt(lineNumberField.getText());
                final int linesCount = linesCount();
                final String text = text();
                lastNavedLine = lineNumber;
                if(lineNumber == 0 || lineNumber > linesCount){
                    Stage errorStage = new Stage();
                    errorStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, (windowEvent) -> {
                        windowEvent.consume();
                        errorStage.close();
                    });
            
                    errorStage.addEventHandler(KeyEvent.KEY_PRESSED, (keyEvent) -> {
                        if(keyEvent.getCode() != KeyCode.ESCAPE)
                            return;
                        keyEvent.consume();
                        errorStage.close();
                    });
            
                    errorStage.setTitle("Go To Line");
                    errorStage.initOwner(goToStage);
                    errorStage.initModality(Modality.WINDOW_MODAL);
                    errorStage.initStyle(StageStyle.UTILITY);
                    
                    BorderPane errorStageRootPane = new BorderPane();
                    Label errorMsg = new Label("The line number entered is beyond the total number of lines");
                    errorMsg.setFont(Font.font(15));
                    errorStageRootPane.setCenter(errorMsg);
                    
                    errorStage.setScene(new Scene(errorStageRootPane, 430, 40));
                    errorStage.setTitle("Error!");
                    errorStage.show();
                    errorStage.centerOnScreen();
                }else{
                    if(lineNumber == 1){
                        actionEvent.consume();
                        goToStage.close();
                        textArea.selectPositionCaret(0);
                        textArea.deselect();
                        return;
                    }
                    ArrayList<Integer> indices = new ArrayList<>();
                    for(int i = 0;i < text.length();i++)
                        if(text.charAt(i) == '\n')
                            indices.add(i);
                    actionEvent.consume();
                    goToStage.close();
                    textArea.selectPositionCaret(indices.get(lineNumber-2)+1);
                    textArea.deselect();
                }
            });
            
            Button cancel = new Button("Cancel");
            cancel.setFont(Font.font(13));
            cancel.setPrefSize(70, 15);
            cancel.setOnAction((actionEvent) -> {
                actionEvent.consume();
                goToStage.close();
            });
            
            goToStageRootPane.getChildren().addAll(msg, lineNumberField, go, cancel);
            msg.relocate(5,3);
            lineNumberField.relocate(5, 22.5);
            go.relocate(20,55);
            cancel.relocate(110,55);
            
            goToStage.setScene(new Scene(goToStageRootPane, 230, 100));
            goToStage.setTitle("Go To Line");
            goToStage.show();
        });
        
        editMenu.getItems().add(goToSelection);
        
        menuBar.getMenus().add(editMenu);
        //////////////////////////////////////////////////////////////////////////////////
        
        rootPane.setTop(menuBar);
        rootPane.setCenter(textArea);
        //--------------------------------------------------------------------------------
        
        stage.setScene(new Scene(rootPane));
        stage.setTitle("Untitled - XEdit");
        stage.show();
    }
    
    private void quit(Stage owner){
        if(file.isSaved() || (!file.isOpened() && text().equals("")))
            System.exit(0);
        Stage exitRequestStage = new Stage();
        exitRequestStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, (windowEvent) -> {
            windowEvent.consume();
            exitRequestStage.close();
        });
            
        exitRequestStage.addEventHandler(KeyEvent.KEY_PRESSED, (keyEvent) -> {
            if(keyEvent.getCode() != KeyCode.ESCAPE)
                return;
            keyEvent.consume();
            exitRequestStage.close();
        });
        exitRequestStage.initOwner(owner);
        exitRequestStage.initModality(Modality.WINDOW_MODAL);
        exitRequestStage.initStyle(StageStyle.UTILITY);
        exitRequestStage.setTitle("XEdit");
                
        Pane exitRequestStageRootPane = new Pane();
        exitRequestStageRootPane.setBackground( new Background( new BackgroundFill(Color.WHITE, null, null) ) );
                
        Label msg = new Label("Do you want to save "+getFileName()+"?");
        msg.setFont(Font.font(15));
        msg.setTextFill(Color.BLUE);
                
        Button yes = new Button("Yes");
        yes.setFont(Font.font(15));
        yes.setOnAction((Event) -> {
            Event.consume();
            exitRequestStage.close();
            saveFile(owner);
            owner.close();
            System.exit(0);
        });
        yes.setPrefSize(70, 30);
                
        Button no = new Button("No");
        no.setFont(Font.font(15));
        no.setOnAction((Event) -> {
            Event.consume();
            exitRequestStage.close();
            owner.close();
            System.exit(0);
        });
        no.setPrefSize(70, 30);
        
        Button cancel = new Button("Cancel");
        cancel.setFont(Font.font(15));
        cancel.setOnAction((Event) -> {
            Event.consume();
            exitRequestStage.close();
        });
        cancel.setPrefSize(70, 30);
                
        Rectangle rect = new Rectangle(300, 50);
        rect.setFill(Color.rgb(220, 220, 220));
                
        exitRequestStageRootPane.getChildren().addAll(msg, rect, yes, no, cancel);
        msg.relocate(10, 10);
        rect.relocate(0, 50);
        yes.relocate(10, 55);
        no.relocate(100, 55);
        cancel.relocate(190, 55);
        
        exitRequestStage.setScene(new Scene(exitRequestStageRootPane, 300, 100));
        exitRequestStage.show();
    }
    
    private void saveRequest(Stage owner){
        if(file.isSaved())
            return;
        Stage exitRequestStage = new Stage();
        exitRequestStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, (windowEvent) -> {
            windowEvent.consume();
            exitRequestStage.close();
        });
            
        exitRequestStage.addEventHandler(KeyEvent.KEY_PRESSED, (keyEvent) -> {
            if(keyEvent.getCode() != KeyCode.ESCAPE)
                return;
            keyEvent.consume();
            exitRequestStage.close();
        });
        exitRequestStage.setTitle("XEdit");
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
        yes.setOnAction((Event) -> {
            Event.consume();
            exitRequestStage.close();
            saveFile(owner);
            initNewUntitledFile(owner);
        });
        yes.setPrefSize(70, 30);
                
        Button no = new Button("No");
        no.setFont(Font.font(15));
        no.setOnAction((Event) -> {
            Event.consume();
            exitRequestStage.close();
            initNewUntitledFile(owner);
        });
        no.setPrefSize(70, 30);
        
        Button cancel = new Button("Cancel");
        cancel.setFont(Font.font(15));
        cancel.setOnAction((Event) -> {
            Event.consume();
            exitRequestStage.close();
        });
        cancel.setPrefSize(70, 30);
                
        Rectangle rect = new Rectangle(300, 50);
        rect.setFill(Color.rgb(220, 220, 220));
                
        exitRequestStageRootPane.getChildren().addAll(msg, rect, yes, no, cancel);
        msg.relocate(10, 10);
        rect.relocate(0, 50);
        yes.relocate(10, 55);
        no.relocate(100, 55);
        cancel.relocate(190, 55);
        
        exitRequestStage.setScene(new Scene(exitRequestStageRootPane, 300, 100));
        exitRequestStage.showAndWait();
    }
    
    private void newFile(Stage owner){
        if((!file.isSaved() && file.isOpened()) || !text().equals(""))
            saveRequest(owner);
        initNewUntitledFile(owner);
    }
    
    private void openFile(Stage owner){
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open file");
        chooser.getExtensionFilters().add(new ExtensionFilter("Text files", "*.txt"));
        chooser.getExtensionFilters().add(new ExtensionFilter("All files", "*.*"));
        chooser.setSelectedExtensionFilter(chooser.getExtensionFilters().get(0));
        File resultantFile = chooser.showOpenDialog(owner);
        if(resultantFile == null)
            return;
        initNewFile(resultantFile, true, owner);
    }
    
    private void saveFile(Stage owner){
        if(file.isOpened()){
            if(!file.isSaved()){
                updateFile(owner);
                return;
            }
        }else{
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save file");
            chooser.getExtensionFilters().add(new ExtensionFilter("All files", "*.*"));
            chooser.setSelectedExtensionFilter(chooser.getExtensionFilters().get(0));
            File resultantFile = chooser.showSaveDialog(owner);
            if(resultantFile == null)
                return;
            try {
                resultantFile.createNewFile();
            } catch (IOException ex) {
                return;
            }
            initNewFile(resultantFile, false, owner);
        }
    }
    
    private void saveFileAs(Stage owner){
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save file");
        chooser.getExtensionFilters().add(new ExtensionFilter("All files", "*.*"));
        chooser.setSelectedExtensionFilter(chooser.getExtensionFilters().get(0));
        File resultantFile = chooser.showSaveDialog(owner);
        if(resultantFile == null)
            return;
        try {
            resultantFile.createNewFile();
        } catch (IOException ex) {
            return;
        }
        initNewFile(resultantFile, true, owner);
    }
    
    private String text(){
        return textArea.getText();
    }
    
    private String getFileName(){
        return file.getFile() == null ? "Untitled" : file.getFile().getName();
    }
    
    private void initNewUntitledFile(Stage owner){
        file.init(null, false);
        textArea.setText("");
        owner.setTitle("Untitled - XEdit");
    }
    
    private void initNewFile(File newFile, boolean saved, Stage owner){
        file.init(newFile, saved);
        updateFile(owner);
        updateTextArea();
        owner.setTitle(getFileName()+" - XEdit");
    }
    
    private void updateFile(Stage owner){
        if(file.isSaved() || !file.isOpened())
            return;
        writeFile( file.getFile(), text() );
        file.setSaved(true);
        owner.setTitle(getFileName()+" - XEdit");
    }
    
    private void updateTextArea(){
        if(!file.isSaved() || !file.isOpened())
            return;
        textArea.setText(readFile(file.getFile()));
    }
    
    private void writeFile(File file, String text){
        try(FileWriter writer = new FileWriter( file )){
            writer.write(text);
            writer.flush();
        }catch(Exception e){
            return;
        }
    }
    
    private String readFile(File file){
        String text = "";
        try(FileReader reader = new FileReader( file )){
            int c;
            while((c = reader.read()) != -1)
                text += (char)c;
        }catch(Exception e){
            return "";
        }
        return text;
    }
    
    private int linesCount(){
        int count = 0;
        for(char c:text().toCharArray())
            if(c == '\n')
                count++;
        return count+1;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
