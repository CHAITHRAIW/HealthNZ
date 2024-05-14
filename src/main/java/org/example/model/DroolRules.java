package org.example.model;

import javax.persistence.*;

@Entity
@Table(name = "DroolRules", schema = "healthnzrules_schema")
public class DroolRules {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ruleId;
    private int fileId;

    public int getRuleId() {
        return ruleId;
    }

    public void setRuleId(int ruleId) {
        this.ruleId = ruleId;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleContent() {
        return ruleContent;
    }

    public void setRuleContent(String ruleContent) {
        this.ruleContent = ruleContent;
    }

    private String ruleName;
    private String ruleContent;
}