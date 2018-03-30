/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xedit;

import java.io.File;
import java.util.Objects;

/**
 *
 * @author Kaiser
 */
public class XEditFile {
    private File wrappedFile;
    private boolean saved;
    
    public XEditFile(File file, boolean saved){
        wrappedFile = file;
        this.saved = saved;
    }
    
    public XEditFile(){
        this(null, false);
    }
    
    @Override
    public boolean equals(Object obj){
        if(obj instanceof XEditFile){
            XEditFile file = (XEditFile) obj;
            return wrappedFile.equals(file.wrappedFile) && saved == file.saved;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.wrappedFile);
        hash = 47 * hash + (this.saved ? 1 : 0);
        return hash;
    }
    
    public void init(File newFile, boolean saved){
        wrappedFile = newFile;
        this.saved = saved;
    }
    
    public File getFile(){
        return wrappedFile;
    }
    
    public boolean isSaved(){
        return saved;
    }
    
    public void setSaved(boolean value){
        saved = value;
    }
    
    public boolean isOpened(){
        return wrappedFile != null;
    }
}
