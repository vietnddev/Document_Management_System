package com.flowiee.dms.model.role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActionModel {
    private String actionKey;
    private String actionLabel;
    private String moduleKey;

    public ActionModel() {}
    
    public ActionModel(String actionKey, String actionLabel, String moduleKey) {
    	this.actionKey = actionKey;
    	this.actionLabel = actionLabel;
    	this.moduleKey = moduleKey;
    }
}