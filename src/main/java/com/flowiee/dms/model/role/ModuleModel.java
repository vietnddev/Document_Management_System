package com.flowiee.dms.model.role;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModuleModel {
    String moduleKey;
    String moduleLabel;
}