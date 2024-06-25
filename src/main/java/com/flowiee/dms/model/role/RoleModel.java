package com.flowiee.dms.model.role;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleModel {
    Integer accountId;
    Integer groupId;
    ModuleModel module;
    ActionModel action;
    Boolean isAuthor;
}