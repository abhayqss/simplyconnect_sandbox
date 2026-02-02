UPDATE [dbo].[MPI] SET [dbo].[MPI].assigning_authority_universal = '2.16.840.1.113883.3.6492',
  [dbo].[MPI].assigning_authority_namespace = 'EXCHANGE',
  [dbo].[MPI].assigning_authority = 'EXCHANGE&2.16.840.1.113883.3.6492&ISO'
WHERE [dbo].[MPI].assigning_authority = 'home.community.id.namespace&home.community.id&ISO';
GO

