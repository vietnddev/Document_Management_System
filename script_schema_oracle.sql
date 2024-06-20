BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE DOCUMENT_TREE_VIEW';
    EXCEPTION
       WHEN OTHERS THEN
          IF SQLCODE != -942 THEN
             RAISE;
    END IF;
END;

BEGIN
    EXECUTE IMMEDIATE 'DROP VIEW DOCUMENT_TREE_VIEW';
    EXCEPTION
       WHEN OTHERS THEN
          IF SQLCODE != -942 THEN
             RAISE;
    END IF;
END;

CREATE OR REPLACE VIEW DOCUMENT_TREE_VIEW AS
WITH DocumentHierarchy(ID, NAME, AS_NAME, PARENT_ID, IS_FOLDER, Path, HierarchyLevel) AS (
	SELECT ID, NAME, AS_NAME, PARENT_ID, IS_FOLDER, CAST(NAME AS VARCHAR2(4000)) AS Path, 0 AS HierarchyLevel  
	FROM DOCUMENT  
	WHERE PARENT_ID = 0 AND IS_FOLDER = 'Y'  
	UNION ALL  
	SELECT d.ID, d.NAME, d.AS_NAME, d.PARENT_ID, d.IS_FOLDER, dh.Path || '/' || d.NAME || '' || ' ', dh.HierarchyLevel + 1  
	FROM DOCUMENT d  
	INNER JOIN DocumentHierarchy dh ON d.PARENT_ID = dh.ID  
	WHERE d.IS_FOLDER = 'Y'
), 
RecursiveHierarchy AS (  
	SELECT ID, NAME, AS_NAME, PARENT_ID, IS_FOLDER, HierarchyLevel, Path,  
	ROW_NUMBER() OVER (PARTITION BY SUBSTR(Path, 1, INSTR(Path, '/', -1) - 1) ORDER BY Path) AS RowNumm  
	FROM DocumentHierarchy
),
SubFolderList AS (  
 	SELECT dh.PARENT_ID AS Parent_ID,  
	LISTAGG(dh.ID, '|') WITHIN GROUP (ORDER BY dh.ID) AS SubFoldersId  
	FROM  
	DocumentHierarchy dh  
	GROUP BY dh.PARENT_ID  
)
SELECT
    rh.ID AS IDTemp,
    rh.NAME AS NameTemp,
    rh.AS_NAME AS AsNameTemp,
    rh.PARENT_ID AS ParentIdTemp,
    CASE WHEN EXISTS (SELECT 1 FROM DOCUMENT sub WHERE sub.PARENT_ID = rh.ID AND sub.IS_FOLDER = 'Y') THEN 'Y' ELSE 'N' END AS Has_SubFolders,
    sf.SubFoldersId,
    rh.HierarchyLevel,
    rh.RowNumm,
    RTRIM(rh.Path) as PATH,
    d.*
FROM RecursiveHierarchy rh
LEFT JOIN SubFolderList sf ON rh.ID = sf.Parent_ID
INNER JOIN DOCUMENT d ON d.ID = rh.ID;