package com.flowiee.dms.controller.storage;

import com.flowiee.dms.base.BaseController;
import com.flowiee.dms.entity.category.Category;
import com.flowiee.dms.entity.storage.DocField;
import com.flowiee.dms.entity.storage.Document;
import com.flowiee.dms.exception.AppException;
import com.flowiee.dms.exception.ForbiddenException;
import com.flowiee.dms.exception.NotFoundException;
import com.flowiee.dms.model.DocMetaModel;
import com.flowiee.dms.model.dto.DocumentDTO;
import com.flowiee.dms.model.dto.FileDTO;
import com.flowiee.dms.service.category.CategoryService;
import com.flowiee.dms.service.storage.*;
import com.flowiee.dms.utils.*;
import net.logstash.logback.encoder.org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/stg")
public class DocumentControllerView extends BaseController {
    private final DocumentInfoService documentInfoService;
    private final DocFieldService     docFieldService;
    private final FileStorageService fileStorageService;
    private final DocShareService docShareService;
    private final CategoryService categoryService;
    private final DocMetadataService docMetadataService;

    @Autowired
    public DocumentControllerView(DocumentInfoService documentInfoService, DocFieldService docFieldService, FileStorageService fileStorageService,
                                  DocShareService docShareService, CategoryService categoryService, DocMetadataService docMetadataService) {
        this.documentInfoService = documentInfoService;
        this.docFieldService = docFieldService;
        this.fileStorageService = fileStorageService;
        this.docShareService = docShareService;
        this.categoryService = categoryService;
        this.docMetadataService = docMetadataService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("@vldModuleStorage.dashboard(true)")
    public ModelAndView showDashboardOfSTG() {
        ModelAndView modelAndView = new ModelAndView(PagesUtils.STG_DASHBOARD);
        //Loại tài liệu
        List<Category> listLoaiTaiLieu = categoryService.findSubCategory(AppConstants.CATEGORY.DOCUMENT_TYPE.getName(), null, null, -1, -1).getContent();
        List<String> listTenOfDocType = new ArrayList<>();
        List<Integer> listSoLuongOfDocType = new ArrayList<>();
        for (Category docType : listLoaiTaiLieu) {
            listTenOfDocType.add(docType.getName());
            listSoLuongOfDocType.add(docType.getListDocument() != null ? docType.getListDocument().size() : 0);
        }
        modelAndView.addObject("reportOfDocType_listTen", listTenOfDocType);
        modelAndView.addObject("reportOfDocType_listSoLuong", listSoLuongOfDocType);
        return baseView(modelAndView);
    }

    @GetMapping("/doc")
    @PreAuthorize("@vldModuleStorage.readDoc(true)")
    public ModelAndView viewRootDocuments() {
        ModelAndView modelAndView = new ModelAndView(PagesUtils.STG_DOCUMENT);
        modelAndView.addObject("parentId", 0);
        modelAndView.addObject("folderTree", documentInfoService.findFolderByParentId(0));
        return baseView(modelAndView);
    }

    @GetMapping("/doc/{aliasPath}")
    @PreAuthorize("@vldModuleStorage.readDoc(true)")
    public ModelAndView viewSubDocuments(@PathVariable("aliasPath") String aliasPath) {
        String aliasName = aliasPath.substring(0, aliasPath.lastIndexOf("-"));
        int documentId = Integer.parseInt(aliasPath.substring(aliasPath.lastIndexOf("-") + 1));
        Optional<Document> documentOptional = documentInfoService.findById(documentId);
        if (documentOptional.isEmpty() || !(aliasName + "-" + documentId).equals(documentOptional.get().getAsName() + "-" + documentOptional.get().getId())) {
            throw new NotFoundException("Document not found!");
        }
        if (!docShareService.isShared(documentId)) {
            throw new ForbiddenException(MessageUtils.ERROR_FORBIDDEN);
        }
        Document document = documentOptional.get();
        try {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("docBreadcrumb", documentInfoService.findHierarchyOfDocument(document.getId(), document.getParentId()));
            modelAndView.addObject("folderTree", documentInfoService.findFolderByParentId(0));
            modelAndView.addObject("documentParentName", document.getName());
            if (document.getIsFolder().equals("Y")) {
                modelAndView.setViewName(PagesUtils.STG_DOCUMENT);
                modelAndView.addObject("parentId", document.getId());
            }
            if (document.getIsFolder().equals("N")) {
                DocumentDTO docDTO = DocumentDTO.fromDocument(document);
                docDTO.setFile(FileDTO.fromFileStorage(fileStorageService.findFileIsActiveOfDocument(document.getId())));
                modelAndView.setViewName(PagesUtils.STG_DOCUMENT_DETAIL);
                modelAndView.addObject("docDetail", docDTO);
                modelAndView.addObject("docMeta", docMetadataService.findMetadata(document.getId()));
                modelAndView.addObject("documentId", document.getId());
                //modelAndView.addObject("listFileOfDocument", fileStorageService.getFileOfDocument(documentId));
            }
            return baseView(modelAndView);
        } catch (RuntimeException ex) {
            throw new AppException(String.format(MessageUtils.SEARCH_ERROR_OCCURRED, "subDocs/ docDetail"), ex);
        }
    }

    @GetMapping("/doc/doc-type/{id}")
    @PreAuthorize("@vldModuleStorage.updateDoc(true)")
    public ModelAndView viewDocTypeDetail(@PathVariable("id") Integer docTypeId) {
        Optional<Category> docType = categoryService.findById(docTypeId);
        if (docType.isEmpty()) {
            throw new NotFoundException("Document type not found!");
        }
        ModelAndView modelAndView = new ModelAndView(PagesUtils.STG_DOCTYPE_DETAIL);
        modelAndView.addObject("docTypeId", docTypeId);
        modelAndView.addObject("docFields", docFieldService.findByDocTypeId(docTypeId));
        return baseView(modelAndView);
    }

    @PostMapping("/doc/change-file/{id}")
    @PreAuthorize("@vldModuleStorage.updateDoc(true)")
    public ModelAndView changeFile(@RequestParam("file") MultipartFile file,
                                   @PathVariable("id") Integer documentId,
                                   HttpServletRequest request) throws IOException {
        if (documentId <= 0 || documentInfoService.findById(documentId).isEmpty()) {
            throw new NotFoundException("Document not found!");
        }
        fileStorageService.changFileOfDocument(file, documentId);
        return new ModelAndView("redirect:" + request.getHeader("referer"));
    }

    @PostMapping("/document/update/{id}")
    @PreAuthorize("@vldModuleStorage.updateDoc(true)")
    public ModelAndView update(@ModelAttribute("document") Document document, @PathVariable("id") Integer documentId, HttpServletRequest request) {
        if (document == null || documentId <= 0 || documentInfoService.findById(documentId).isEmpty()) {
            throw new NotFoundException("Document not found!");
        }
        documentInfoService.update(document, documentId);
        return new ModelAndView("redirect:" + request.getHeader("referer"));
    }

    @GetMapping("/doc/update-metadata/{id}")
    @PreAuthorize("@vldModuleStorage.updateDoc(true)")
    public ModelAndView updateMetadata(HttpServletRequest request,
                                       @PathVariable("id") Integer documentId,
                                       @RequestParam(value = "fieldId", required = false) Integer[] fieldIds,
                                       @RequestParam(value = "dataId", required = false) Integer[] dataIds,
                                       @RequestParam(value = "dataValue", required = false) String[] dataValues) {
        if (documentId <= 0 || documentInfoService.findById(documentId).isEmpty()) {
            throw new NotFoundException("Document not found!");
        }
        if (ObjectUtils.isNotEmpty(fieldIds) && ObjectUtils.isNotEmpty(dataIds) && ObjectUtils.isNotEmpty(dataValues)) {
            List<DocMetaModel> metaDTOs = new ArrayList<>();
            for (int i = 0; i <fieldIds.length; i++) {
                int fieldId = fieldIds[i];
                int dataId = dataIds[i];
                String dataValue = dataValues[i];
                metaDTOs.add(new DocMetaModel(fieldId, null, dataId, dataValue, null, null, documentId));
            }
            docMetadataService.updateMetadata(metaDTOs, documentId);
        }
        return new ModelAndView("redirect:" + request.getHeader("referer"));
    }

    @PostMapping("/document/delete/{id}")
    @PreAuthorize("@vldModuleStorage.deleteDoc(true)")
    public ModelAndView deleteDocument(@PathVariable("id") Integer documentId, HttpServletRequest request) {
        if (documentId <= 0 || documentInfoService.findById(documentId).isEmpty()) {
            throw new NotFoundException("Document not found!");
        }
        documentInfoService.delete(documentId);
        return new ModelAndView("redirect:" + request.getHeader("referer"));
    }

    @PostMapping("/document/move/{id}")
    @PreAuthorize("@vldModuleStorage.moveDoc(true)")
    public ModelAndView moveDocument(@PathVariable("id") Integer documentId, HttpServletRequest request) {
        if (documentId <= 0 || documentInfoService.findById(documentId).isEmpty()) {
            throw new NotFoundException("Document not found!");
        }
        return new ModelAndView("redirect:" + request.getHeader("referer"));
    }

    @PostMapping("/document/share/{id}")
    @PreAuthorize("@vldModuleStorage.shareDoc(true)")
    public ModelAndView share(@PathVariable("id") Integer documentId, HttpServletRequest request) {
        if (documentId <= 0 || documentInfoService.findById(documentId).isEmpty()) {
            throw new NotFoundException("Document not found!");
        }
        return new ModelAndView("redirect:" + request.getHeader("referer"));
    }

    @PostMapping("/doc/doc-field/create")
    @PreAuthorize("@vldModuleStorage.updateDoc(true)")
    public ModelAndView createDocField(DocField docField, HttpServletRequest request) {
        docField.setRequired(docField.getRequired() != null ? docField.getRequired() : false);
        docField.setStatus(false);
        docFieldService.save(docField);
        return new ModelAndView("redirect:" + request.getHeader("referer"));
    }

    @PostMapping(value = "/doc/doc-field/update/{id}", params = "update")
    @PreAuthorize("@vldModuleStorage.updateDoc(true)")
    public ModelAndView updateDocField(HttpServletRequest request, @ModelAttribute("docField") DocField docField, @PathVariable("id") Integer docFieldId) {
        if (docFieldId <= 0 || docFieldService.findById(docFieldId).isEmpty()) {
            throw new NotFoundException("DocField not found!");
        }
        docField.setRequired(docField.getRequired() != null ? docField.getRequired() : false);
        docFieldService.update(docField, docFieldId);
        return new ModelAndView("redirect:" + request.getHeader("referer"));
    }
}