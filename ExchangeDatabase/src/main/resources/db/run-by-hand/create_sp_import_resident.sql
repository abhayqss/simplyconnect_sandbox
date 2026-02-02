IF (OBJECT_ID('[dbo].[import_resident]') IS NOT NULL)
  DROP PROCEDURE [dbo].[import_resident];
GO

/*
### USAGE EXAMPLE ###

OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;
GO

DECLARE @DatabaseId BIGINT = 3;
DECLARE @MGenderId BIGINT, @FGenderId BIGINT, @UNGenderId BIGINT, @MaritalStatusId BIGINT;

SELECT TOP (1) @MGenderId = id
FROM ConcreteCcdCode
WHERE value_set = '2.16.840.1.113883.1.11.1' AND code = 'M';
SELECT TOP (1) @FGenderId = id
FROM ConcreteCcdCode
WHERE value_set = '2.16.840.1.113883.1.11.1' AND code = 'F';
SELECT TOP (1) @UNGenderId = id
FROM ConcreteCcdCode
WHERE value_set = '2.16.840.1.113883.1.11.1' AND code = 'UN';
SELECT TOP (1) @MaritalStatusId = id
FROM ConcreteCcdCode
WHERE value_set = '2.16.840.1.113883.1.11.12212' AND code = 'S';

EXEC [dbo].[import_resident] @DatabaseId, 'Futurama', 'FUTURAMA_OID', 'Bender', 'Bending', 'Rodríguez', '04/09/1996', '123654931', @MGenderId, @MaritalStatusId, 'Planet Express Office', NULL , 'Tijuana', 'TX', '12354', 'Mexico', NULL, NULL, NULL;
GO

CLOSE SYMMETRIC KEY SymmetricKey1;
GO
*/

CREATE PROCEDURE [dbo].[import_resident]
    @DatabaseId       BIGINT,
    @OrganizationName VARCHAR(MAX), -- (unique across database)
    @OrganizationOid  VARCHAR(MAX), -- (unique across database)
    @FirstName        VARCHAR(MAX),
    @LastName         VARCHAR(MAX),
    @MiddleName       VARCHAR(MAX), -- (first name + last name + middle name should be unique across organization)
    @BirthDateStr     VARCHAR(MAX), -- U.S. format ('mm/dd/yyyy' or 'mm-dd-yyyy')
    @Ssn              VARCHAR(MAX), -- '###-##-####' or '#########' (unique across organization)
    @GenderId         BIGINT,
    @MaritalStatusId  BIGINT,
    @AddressStreet    VARCHAR(MAX),
    @AddressStreet2   VARCHAR(MAX), -- street 2, if specified, is concatenated with street 1
    @AddressCity      VARCHAR(MAX),
    @AddressState     VARCHAR(MAX), -- state (e.g. 'MN')
    @AddressZipCode   VARCHAR(MAX), -- postal code, '#####-####' or '#####'
    @AddressCountry   VARCHAR(MAX) = 'USA',
    @Phone            VARCHAR(MAX), -- home phone
    @Email            VARCHAR(MAX),
    @Npi              VARCHAR(MAX),  -- organization National Provider Identifier (NPI), '##########' (unique across application)
    @UnitNumber       VARCHAR(12)
AS
  BEGIN
    -- Validation
    IF @DatabaseId IS NULL
      RAISERROR ('The value for @DatabaseId should not be NULL', 15, 1);
    IF @OrganizationName IS NULL
      RAISERROR ('The value for @OrganizationName should not be NULL', 15, 1);
    IF @OrganizationOid IS NULL
      RAISERROR ('The value for @OrganizationOid should not be NULL', 15, 1);
    IF @Ssn IS NULL
      RAISERROR ('The value for @Ssn should not be NULL', 15, 1);
    IF @FirstName IS NULL
      RAISERROR ('The value for @FirstName should not be NULL', 15, 1);
    IF @LastName IS NULL
      RAISERROR ('The value for @LastName should not be NULL', 15, 1);

    IF (LEN(@OrganizationOid) > 50)
      RAISERROR ('Invalid organization OID. @OrganizationOid length should be less than 51 character.', 15, 1);
    IF (LEN(@FirstName) < 2)
      RAISERROR ('@FirstName is too short (%s).', 15, 1, @FirstName);
    IF (LEN(@LastName) < 2)
      RAISERROR ('@LastName is too short (%s).', 15, 1, @LastName);
    IF (@AddressStreet IS NOT NULL AND LEN(@AddressStreet) > 255)
      RAISERROR ('Invalid street. @AddressStreet length should be less than 256 characters.', 15, 1);
    IF (@AddressStreet IS NOT NULL AND @AddressStreet2 IS NOT NULL AND LEN(@AddressStreet + ' ' + @AddressStreet2) > 255)
      RAISERROR ('Invalid street + street2. Concatenated @AddressStreet + @AddressStreet2 length should be less than 256 characters.', 15, 1);
    IF (@AddressCity IS NOT NULL AND LEN(@AddressCity) > 30)
      RAISERROR ('Invalid city. @AddressCity length should be less than 31 character.', 15, 1);
    IF (@AddressState IS NOT NULL AND LEN(@AddressState) <> 2)
      RAISERROR ('Invalid state. @AddressState length should be equal to 2 characters.', 15, 1);
    IF (@AddressZipCode IS NOT NULL AND LEN(@AddressZipCode) <> 5 AND LEN(@AddressZipCode) <> 10)
      RAISERROR ('Invalid US postal code. Valid @AddressZipCode length should be 5 or 10 characters.', 15, 1);
    IF (@Email IS NOT NULL AND (LEN(@Email) < 3 OR LEN(@Email) > 150))
      RAISERROR ('Invalid email. @Email length should be between 3 and 150 characters.', 15, 1);
    IF (@Phone IS NOT NULL AND (LEN(@Phone) < 5 OR LEN(@Phone) > 150))
      RAISERROR ('Invalid phone. @Phone length should be between 5 and 150 characters.', 15, 1);
    IF (@Phone IS NOT NULL AND (LEN(@Phone) < 5 OR LEN(@Phone) > 150))
      RAISERROR ('Invalid phone. @Phone length should be between 5 and 150 characters.', 15, 1);
    IF (@Npi IS NOT NULL)
      BEGIN
        -- remove non-numeric characters from NPI
        SET @Npi = [dbo].[normalize_phone](@Npi);
        IF (LEN(@Npi) <> 10)
          RAISERROR ('Invalid Organization NPI. The @Npi string should be all numeric and be 10 characters in length.', 15, 1);
      END;

    DECLARE @LegacyId BIGINT, @LegacyTable VARCHAR(MAX), @Birthday DATE, @DateFormat INT;
    DECLARE @AssigningAuthorityUniversal VARCHAR(MAX), @AssigningAuthorityNamespace VARCHAR(MAX);
    SET @LegacyTable = 'CCN_IMPORT_Resident';
    SET @AssigningAuthorityUniversal = '2.16.840.1.113883.3.6492';
    SET @AssigningAuthorityNamespace = 'EXCHANGE';

    -- SSN may contain hyphens, spaces, or other delimiters ==>
    -- remove non-numeric characters from SSN
    SET @Ssn = [dbo].[normalize_phone](@Ssn);
    IF (LEN(@Ssn) <> 9)
      BEGIN
        RAISERROR ('Invalid SSN (%s). The length of @Ssn should be equal to 9.', 15, 1, @Ssn);
        RETURN;
      END;

    -- concatenate street addresses
    IF (@AddressStreet2 IS NOT NULL)
      BEGIN
        SET @AddressStreet = @AddressStreet + ' ' + @AddressStreet2;
      END;

    SET @Birthday = NULL;
    IF (@BirthDateStr IS NOT NULL)
      BEGIN
        IF (@BirthDateStr LIKE '[0-1]_-__-____' OR @BirthDateStr LIKE '_-__-____' OR @BirthDateStr LIKE '[0-1]_-_-____' OR @BirthDateStr LIKE '_-_-____')
          SET @DateFormat = 110;
        ELSE IF (@BirthDateStr LIKE '[0-1]_/__/____' OR @BirthDateStr LIKE '_/__/____' OR @BirthDateStr LIKE '[0-1]_/_/____' OR @BirthDateStr LIKE '_/_/____')
          SET @DateFormat = 101;
        ELSE
          BEGIN
            RAISERROR ('Unsupported @BirthDateStr date format (%s).', 15, 1, @BirthDateStr);
            RETURN;
          END;

        SET @Birthday = CONVERT(DATE, @BirthDateStr, @DateFormat);
      END;

    -- Check that this procedure won't create a duplicated resident
    IF 0 != (SELECT count([resident].[id])
             FROM [dbo].[resident]
               INNER JOIN [dbo].[name] ON [resident].[person_id] = [name].[person_id]
             WHERE
               [name].[family] = @LastName AND
               [name].[given] = @FirstName AND
               (@MiddleName IS NULL OR [name].[middle] IS NULL OR [name].[middle] = @MiddleName) AND
               [resident].[database_id] = @DatabaseId)
      BEGIN
        RAISERROR ('There is a resident with specified name (%s %s).', 10, 1, @LastName, @FirstName);
        RETURN;
      END;
    IF 0 != (SELECT count([resident].[id])
             FROM [dbo].[resident]
             WHERE
               [resident].[ssn] = @Ssn AND
               [resident].[database_id] = @DatabaseId)
      BEGIN
        RAISERROR ('There is a resident with specified SSN (%s).', 10, 1, @Ssn);
        RETURN;
      END;

    -- Check that this procedure won't create a duplicated organization (community)
    DECLARE @OrganizationId BIGINT, @OrganizationIdTest BIGINT, @OrganizationIdTest2 BIGINT, @OrganizationIdTest3 BIGINT, @OrganizationNpi VARCHAR(25), @XmlOrg XML;
    SET @OrganizationId = (SELECT TOP (1) o.[id]
                           FROM [dbo].[Organization] o
                           WHERE o.[name] = @OrganizationName AND o.[oid] = @OrganizationOid AND o.[database_id] = @DatabaseId);
    SET @OrganizationIdTest = (SELECT TOP (1) o.[id]
                               FROM [dbo].[Organization] o
                               WHERE o.[name] = @OrganizationName AND o.[database_id] = @DatabaseId);
    SET @OrganizationIdTest2 = (SELECT TOP (1) o.[id]
                                FROM [dbo].[Organization] o
                                WHERE o.[oid] = @OrganizationOid AND o.[database_id] = @DatabaseId);
    IF (@Npi IS NOT NULL)
      BEGIN
        SET @OrganizationIdTest3 = (SELECT TOP (1) o.[id]
                                    FROM [dbo].[Organization] o
                                    WHERE o.[provider_npi] = @Npi);
        IF (@OrganizationIdTest3 IS NOT NULL AND (@OrganizationId IS NULL OR @OrganizationId <> @OrganizationIdTest3))
          BEGIN
            SET @XmlOrg = (SELECT
                             o.[id]           AS [community_ID],
                             o.[name]         AS [community_name],
                             o.[oid]          AS [community_OID],
                             o.[database_id]  AS [organization_ID],
                             sd.[name]        AS [organization_name],
                             o.[provider_npi] AS [organization_NPI]
                           FROM [dbo].[Organization] o
                             INNER JOIN [dbo].[SourceDatabase] sd ON sd.[id] = o.[database_id]
                           WHERE o.[id] = @OrganizationIdTest3
                           FOR XML AUTO);
            PRINT CONVERT(VARCHAR(MAX), @XmlOrg);
            RAISERROR ('There is an existing community with specified NPI and different OID / name. NPI should be unique.', 10, 1);
            RETURN;
          END;
        -- update community if NPI was not specified previously
        IF (@OrganizationIdTest3 IS NULL AND @OrganizationId IS NOT NULL)
          BEGIN
            SET @OrganizationNpi = (SELECT TOP (1) o.[provider_npi]
                                    FROM [dbo].[Organization] o
                                    WHERE o.[id] = @OrganizationId);
            IF (@OrganizationNpi IS NULL)
              BEGIN
                UPDATE [dbo].[Organization]
                SET [provider_npi] = @Npi
                WHERE [id] = @OrganizationId;
                PRINT 'Existing community updated: NPI added.';
              END;
          END;
      END;
    IF (@OrganizationId IS NULL AND @OrganizationIdTest IS NOT NULL)
      BEGIN
        SET @XmlOrg = (SELECT
                         o.[id]          AS [community_ID],
                         o.[name]        AS [community_name],
                         o.[oid]         AS [community_OID],
                         o.[database_id] AS [organization_ID]
                       FROM [dbo].[Organization] o
                       WHERE o.[id] = @OrganizationIdTest
                       FOR XML AUTO);
        PRINT CONVERT(VARCHAR(MAX), @XmlOrg);
        RAISERROR ('There is an existing community with specified name and different OID. One organization can''t have multiple OIDs.', 10, 1);
        RETURN;
      END;
    IF (@OrganizationId IS NULL AND @OrganizationIdTest2 IS NOT NULL)
      BEGIN
        SET @XmlOrg = (SELECT
                         o.[id]          AS [community_ID],
                         o.[name]        AS [community_name],
                         o.[oid]         AS [community_OID],
                         o.[database_id] AS [organization_ID]
                       FROM [dbo].[Organization] o
                       WHERE o.[id] = @OrganizationIdTest2
                       FOR XML AUTO);
        PRINT CONVERT(VARCHAR(MAX), @XmlOrg);
        RAISERROR ('There is an existing community with specified OID and different name. OID should be unique.', 10, 1);
        RETURN;
      END;

    DECLARE @OutputTable TABLE([id] BIGINT NOT NULL);
    BEGIN TRANSACTION;

    -- create community if not exists
    IF (@OrganizationId IS NULL)
      BEGIN
        SET @LegacyId = (SELECT COALESCE(MAX(o.[id]), 0) + 1
                         FROM [dbo].[Organization] o);

        INSERT INTO [dbo].[Organization] (
          [legacy_id]
          , [legacy_table]
          , [name]
          , [oid]
          , [testing_training]
          , [inactive]
          , [module_hie]
          , [last_modified]
          , [provider_npi]
          , [database_id])
        OUTPUT Inserted.ID INTO @OutputTable([id])
        VALUES
          (
            CONCAT('CCN_', CAST(@LegacyId AS VARCHAR(MAX))),
            'Company',
            @OrganizationName,
            @OrganizationOid,
            0,
            0,
            1,
            GETDATE(),
            @Npi,
            @DatabaseId
          );

        SELECT TOP (1) @OrganizationId = [id]
        FROM @OutputTable;
        DELETE FROM @OutputTable;
      END;

    -- create resident

    SET @LegacyId = (SELECT COALESCE(MAX(p.[id]), 0) + 1
                     FROM [dbo].[Person] p);

    INSERT INTO [dbo].[Person]
    ([legacy_id]
      , [type_code_id]
      , [legacy_table]
      , [database_id])
    OUTPUT Inserted.ID INTO @OutputTable([id])
    VALUES (
      CONCAT('CCN_', CAST(@LegacyId AS VARCHAR(MAX)))
      , NULL
      , @LegacyTable
      , @DatabaseId);

    DECLARE @PersonId BIGINT;
    SELECT TOP (1) @PersonId = [id]
    FROM @OutputTable;
    DELETE FROM @OutputTable;

    PRINT @PersonId;

    INSERT INTO [dbo].[name] (
      [family]
      , [family_normalized]
      , [family_qualifier]
      , [given]
      , [given_normalized]
      , [given_qualifier]
      , [middle]
      , [middle_normalized]
      , [middle_qualifier]
      , [use_code]
      , [prefix]
      , [prefix_qualifier]
      , [suffix]
      , [suffix_qualifier]
      , [person_id]
      , [legacy_table]
      , [legacy_id]
      , [database_id])
    VALUES (
      @LastName
      , lower(@LastName)
      , NULL
      , @FirstName
      , lower(@FirstName)
      , NULL
      , @MiddleName
      , lower(@MiddleName)
      , NULL
      , 'L'
      , NULL
      , NULL
      , NULL
      , NULL
      , @PersonId
      , @LegacyTable
      , CONCAT('CCN_', CAST(@LegacyId AS VARCHAR(MAX)))
      , @DatabaseId
    );

    INSERT INTO [dbo].[PersonAddress] (
      [city]
      , [country]
      , [use_code]
      , [postal_code]
      , [state]
      , [street_address]
      , [person_id]
      , [database_id]
      , [legacy_table]
      , [legacy_id])
    VALUES (
      @AddressCity
      , @AddressCountry
      , 'HP'
      , @AddressZipCode
      , @AddressState
      , @AddressStreet
      , @PersonId
      , @DatabaseId
      , @LegacyTable
      , CONCAT('CCN_', CAST(@LegacyId AS VARCHAR(MAX)))
    );

    IF (@Phone IS NOT NULL)
      BEGIN
        SET @LegacyId = (SELECT COALESCE(MAX(pt.[id]), 0) + 1
                         FROM [dbo].[PersonTelecom] pt);

        INSERT INTO [dbo].[PersonTelecom] (
          [sync_qualifier]
          , [use_code]
          , [value]
          , [person_id]
          , [database_id]
          , [legacy_table]
          , [legacy_id])
        VALUES (
          2
          , 'HP'
          , @Phone
          , @PersonId
          , @DatabaseId
          , @LegacyTable
          , CONCAT('CCN_', CAST(@LegacyId AS VARCHAR(MAX)))
        );
      END;

    IF (@Email IS NOT NULL)
      BEGIN
        SET @LegacyId = (SELECT COALESCE(MAX(pt.[id]), 0) + 1
                         FROM [dbo].[PersonTelecom] pt);

        INSERT INTO [dbo].[PersonTelecom] (
          [sync_qualifier]
          , [use_code]
          , [value]
          , [person_id]
          , [database_id]
          , [legacy_table]
          , [legacy_id])
        VALUES (
          0
          , 'EMAIL'
          , @Email
          , @PersonId
          , @DatabaseId
          , @LegacyTable
          , CONCAT('CCN_', CAST(@LegacyId AS VARCHAR(MAX)))
        );
      END;

    SET @LegacyId = (SELECT COALESCE(MAX(r.[id]), 0) + 1
                     FROM [dbo].[resident] r);

    INSERT INTO [dbo].[resident] (
      [first_name]
      , [last_name]
      , [middle_name]
      , [ssn]
      , [ssn_last_four_digits]
      , [birth_date]
      , [gender_id]
      , [marital_status_id]
      , [admit_date]
      , [unit_number]
      , [prev_addr_street]
      , [prev_addr_city]
      , [prev_addr_state]
      , [prev_addr_zip]
      , [person_id]
      , [legacy_table]
      , [legacy_id]
      , [facility_id]
      , [provider_organization_id]
      , [database_id])
    VALUES (
      @FirstName
      , @LastName
      , @MiddleName
      , @Ssn
      , right(@Ssn, 4)
      , @Birthday
      , @GenderId
      , @MaritalStatusId
      , GETDATE()
      , @UnitNumber
      , @AddressStreet
      , @AddressCity
      , @AddressState
      , @AddressZipCode
      , @PersonId
      , @LegacyTable
      , CONCAT('CCN_', CAST(@LegacyId AS VARCHAR(MAX)))
      , @OrganizationId
      , @OrganizationId
      , @DatabaseId
    );

    DECLARE @ResidentId BIGINT;
    SELECT @ResidentId = IDENT_CURRENT('resident_enc');

    INSERT INTO [dbo].[MPI] (
      [registry_patient_id]
      , [merged]
      , [surviving_patient_id]
      , [deleted]
      , [patient_id]
      , [resident_id]
      , [assigning_authority_universal_type]
      , [assigning_authority_universal]
      , [assigning_authority_namespace]
      , [assigning_authority])
    VALUES (
      NEWID(),
      'N'
      , NULL
      , 'N'
      , @ResidentId
      , @ResidentId
      , 'ISO'
      , @AssigningAuthorityUniversal
      , @AssigningAuthorityNamespace
      , CONCAT(@AssigningAuthorityNamespace, '&', @AssigningAuthorityUniversal, '&ISO')
    );

    COMMIT TRANSACTION;
  END;
GO
