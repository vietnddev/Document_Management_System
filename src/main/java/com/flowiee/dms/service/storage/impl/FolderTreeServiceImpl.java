package com.flowiee.dms.service.storage.impl;

import com.flowiee.dms.entity.storage.view.DocumentTreeView;
import com.flowiee.dms.model.dto.DocumentDTO;
import com.flowiee.dms.repository.storage.DocumentRepository;
import com.flowiee.dms.service.BaseService;
import com.flowiee.dms.service.storage.FolderTreeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FolderTreeServiceImpl extends BaseService implements FolderTreeService {
    DocumentRepository documentRepository;

    /**
     * HierarchyLevel: Thư mục ở cấp thứ mấy
     * RowNumm: Thư mục số mấy của cấp HierarchyLevel
     * */
    @Override
    public List<DocumentDTO> getDocumentWithTreeForm(Integer parentId, boolean isOnlyFolder) {
        logger.info("Generate folder tree");
        List<DocumentDTO> folderTree = new ArrayList<>();

        List<DocumentTreeView> documentTreeViews = documentRepository.findGeneralFolderTree(null, parentId, isOnlyFolder ? "Y" : null);

        for (DocumentTreeView docTreeView : documentTreeViews) {
            folderTree.add(DocumentTreeView.toDocDTO(docTreeView));
        }

        for (int i = 0; i < folderTree.size(); i++) {
            if (folderTree.get(i).getHasSubFolder().equals("Y")) {
                List<Integer> subFolderIds = new ArrayList<>();
                if (documentTreeViews.get(i).getSubFoldersId() != null) {
                    for (String subId : documentTreeViews.get(i).getSubFoldersId().split("\\|")) {
                        subFolderIds.add(Integer.parseInt(subId));
                    }
                }
                folderTree.get(i).setSubFolders(this.getSubFoldersFromListByIds(folderTree, subFolderIds));
            }
        }

        return folderTree;
    }

    @Override
    public DocumentDTO findByDocId(int documentId) {
        List<DocumentTreeView> documentTreeViews = documentRepository.findGeneralFolderTree(documentId, null, null);
        if (ObjectUtils.isEmpty(documentTreeViews)) {
            return null;
        }
        DocumentDTO documentDTO = DocumentTreeView.toDocDTO(documentTreeViews.get(0));
        if (documentDTO.getHasSubFolder().equals("Y")) {
            List<Integer> subFolderIds = new ArrayList<>();
            if (documentTreeViews.get(0).getSubFoldersId() != null) {
                for (String subId : documentTreeViews.get(0).getSubFoldersId().split("\\|")) {
                    subFolderIds.add(Integer.parseInt(subId));
                }
            }
            documentDTO.setSubFolders(this.getSubFoldersFromListByIds(List.of(documentDTO), subFolderIds));
        }
        return documentDTO;
    }

    private List<DocumentDTO> getSubFoldersFromListByIds(List<DocumentDTO> lsFolders, List<Integer> subFolderId) {
        List<DocumentDTO> listSubFolders = new ArrayList<>();
        for (DocumentDTO dto : lsFolders) {
            if (listSubFolders.size() == subFolderId.size()) {
                break;
            }
            if (subFolderId.contains(dto.getId())) {
                listSubFolders.add(dto);
            }
        }
        return listSubFolders;
    }
}
