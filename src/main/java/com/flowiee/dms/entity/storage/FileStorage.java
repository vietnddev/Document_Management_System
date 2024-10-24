package com.flowiee.dms.entity.storage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.base.BaseEntity;
import com.flowiee.dms.entity.system.Account;
import com.flowiee.dms.model.MODULE;
import com.flowiee.dms.utils.CommonUtils;
import javax.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Builder
@Entity
@Table(name = "file_storage")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileStorage extends BaseEntity implements Serializable {
	@Column(name = "customize_name", length = 200)
    String customizeName;

    @Column(name = "saved_name", nullable = false, length = 200)
    String storageName;

    @Column(name = "original_name", nullable = false, length = 200)
    String originalName;

    @Column(name = "note", length = 500)
    String note;

    @Column(name = "extension", length = 10)
    String extension;

    @Column(name = "content_type", length = 100)
    String contentType;

    @Column(name = "file_size")
    long fileSize;

    @Column(name = "content")
    byte[] content;

    @Column(name = "directory_path", length = 500)
    String directoryPath;

    @Column(name = "sort")
    Integer sort;

    @Column(name = "status", nullable = false)
    boolean status;

    @Column(name = "module", nullable = false, length = 20)
    String module;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upload_by", nullable = false)
    Account account;

    @Column(name = "is_active", nullable = false)
    boolean isActive;

    @Transient
    String src;

    @JsonIgnore
    @Transient
    MultipartFile fileAttach;

    @JsonIgnore
    @OneToMany(mappedBy = "fileStorage", fetch = FetchType.LAZY)
    List<DocHistory> listDocHistory;

    public FileStorage(MultipartFile file, MODULE pModule) {
        this.module = pModule.name();
        this.extension = CommonUtils.getFileExtension(file.getOriginalFilename());
        this.originalName = file.getOriginalFilename();
        this.storageName = CommonUtils.generateUniqueString() + "." + this.extension;
        this.fileSize = file.getSize();
        this.contentType = file.getContentType();
        this.directoryPath = CommonUtils.getPathDirectory(pModule.name()).substring(CommonUtils.getPathDirectory(pModule.name()).indexOf("uploads"));
        this.account = CommonUtils.getUserPrincipal().toAccountEntity();
        this.isActive = false;
        this.fileAttach = file;
    }

	@Override
	public String toString() {
		return "FileStorage [id=" + super.id + ", customizeName=" + customizeName + ", storageName=" + storageName +
                ", originalName=" + originalName + ", status=" + status + ", module=" + module + ", isActive=" + isActive + "]";
	}        
}