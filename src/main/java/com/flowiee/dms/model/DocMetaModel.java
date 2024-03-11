package com.flowiee.dms.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DocMetaModel {
	private Integer fieldId;
	private String fieldName;
    private Integer dataId;
    private String dataValue;
    private String fieldType;
    private Boolean fieldRequired;
	private Integer docId;

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