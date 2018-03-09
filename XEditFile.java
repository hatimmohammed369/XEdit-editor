/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xedit;

import java.io.File;

/**
 *
 * @author Acer
 */
public class XEditFile {
    private File wrappedFile;
    private boolean isSaved, isOpened;
    
    public XEditFile(File file, boolean isSaved, boolean isOpened){
        wrappedFile = file;
        this.isSaved = isSaved;
        this.isOpened = isOpened;
    }
    
    public XEditFile(File file){
        this(file, true, true);
    }
    
    @Override
    public boolean equals(Object obj){
        XEditFile file = (XEditFile) obj;
        return wrappedFile.equals(file.wrappedFile);
    }
    
    public File getFile(){
        return wrappedFile;
    }
    
    public void setFile(File newFile){
        wrappedFile = newFile;
    }
    
    public boolean isSaved(){
        return isSaved;
    }
    
    public void setSaved(boolean newValue){
        isSaved = newValue;
    }
    
    public boolean isOpened(){
        return isOpened;
    }
    
    public void setOpened(boolean newValue){
        isOpened = newValue;
    }
}
