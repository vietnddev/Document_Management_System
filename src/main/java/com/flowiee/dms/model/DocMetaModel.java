package com.flowiee.dms.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocMetaModel {
	Integer fieldId;
	String fieldName;
    Integer dataId;
    String dataValue;
    String fieldType;
    Boolean fieldRequired;
	Integer docId;

	public DocMetaModel(Integer fieldId, String fieldName, Integer dataId, String dataValue, String fieldType, Boolean fieldRequired, Integer docId) {
		this.fieldId = fieldId;
		this.fieldName = fieldName;
		this.dataId = dataId;
		this.dataValue = dataValue;
		this.fieldType = fieldType;
		this.fieldRequired = fieldRequired;
		this.docId = docId;
	}

	@Override
	public String toString() {
		return "DocMetaDTO [docDataId=" + dataId + ", docDataValue=" + dataValue + ", docFieldName="
				+ fieldName + ", docFieldTypeInput=" + fieldType + ", docFieldRequired=" + fieldRequired + "]";
	}
}