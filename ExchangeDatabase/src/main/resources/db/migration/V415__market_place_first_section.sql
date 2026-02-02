ALTER TABLE [dbo].[InNetworkInsurance] ADD [is_first_group] [bit] NULL DEFAULT 0;
GO

INSERT INTO [dbo].[InNetworkInsurance] ([display_name], [code], [is_popular], [is_first_group])
VALUES ('I''ll choose my insurance later', 'CHOOSE_LATER', 0, 1);

UPDATE [dbo].[InNetworkInsurance]
set [display_name] = 'I''m paying for myself',
  [is_first_group] = 1,
  [is_popular] = 0
where [code] = 'CASH_OR_SELF_PAYMENT';