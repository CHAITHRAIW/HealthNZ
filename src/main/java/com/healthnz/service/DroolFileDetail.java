package com.healthnz.service;

import java.io.File;

public class DroolFileDetail {
    private File droolFile;
    private String xmlFileName;

    public DroolFileDetail(File droolFile, String xmlFileName) {
        this.droolFile = droolFile;
        this.xmlFileName = xmlFileName;
    }

    public File getDroolFile() {
        return droolFile;
    }

    public String getXmlFileName() {
        return xmlFileName;
    }
}
