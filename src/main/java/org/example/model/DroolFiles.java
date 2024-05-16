package org.example.model;

import javax.persistence.*;

@Entity
@Table(name = "DroolFiles")
public class DroolFiles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int fileId;

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private String fileName;

}