package com.five.springboot.bean;

public class NewsModule {

    private String moduleId;
    private String moduleName;
    private String updateTime;

    public NewsModule(String moduleId, String moduleName, String updateTime) {
        this.moduleId = moduleId;
        this.moduleName = moduleName;
        this.updateTime = updateTime;
    }

    public NewsModule(String moduleName) {
        this.moduleId = null;
        this.moduleName = moduleName;
        this.updateTime = null;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
