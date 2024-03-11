package com.flowiee.dms.model.role;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Role {
    @Data
    public static class Action {
        private boolean isChecked;
        private String keyAction;
        private String valueAction;
        
        public Action(String key, String value) {
        	this.keyAction = key;
        	this.valueAction = value;
        }
    }

    private Map<String, String> module;
    private List<Action> action;
}