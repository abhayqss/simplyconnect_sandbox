IF COL_LENGTH('LabResearchOrder', 'document_id') IS NOT NULL
  BEGIN
    alter table LabResearchOrder
      drop constraint FK_LabResearchOrder_LabResearchOrder_document_id;
    alter table LabResearchOrder
      drop column document_id;
  END
GO
