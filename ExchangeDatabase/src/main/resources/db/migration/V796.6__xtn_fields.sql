IF COL_LENGTH('XTN_PhoneNumber', 'telecommunication_use_code_id') IS NOT NULL
    BEGIN
        alter table XTN_PhoneNumber
            drop constraint FK_XTN_PhoneNumber_ID_CodedValuesForHL7Tables_telecommunication_use_code_id;
        alter table XTN_PhoneNumber
            drop column telecommunication_use_code_id;
    END
GO

alter table XTN_PhoneNumber
    add telecommunication_use_code_id bigint,
        constraint FK_XTN_PhoneNumber_ID_CodedValuesForHL7Tables_telecommunication_use_code_id FOREIGN KEY (telecommunication_use_code_id)
            references ID_CodedValuesForHL7Tables (id)
GO

IF COL_LENGTH('XTN_PhoneNumber', 'telecommunication_equipment_type_id') IS NOT NULL
    BEGIN
        alter table XTN_PhoneNumber
            drop constraint FK_XTN_PhoneNumber_ID_CodedValuesForHL7Tables_telecommunication_equipment_type_id;
        alter table XTN_PhoneNumber
            drop column telecommunication_equipment_type_id;
    END
GO

alter table XTN_PhoneNumber
    add telecommunication_equipment_type_id bigint,
        constraint FK_XTN_PhoneNumber_ID_CodedValuesForHL7Tables_telecommunication_equipment_type_id FOREIGN KEY (telecommunication_equipment_type_id)
            references ID_CodedValuesForHL7Tables (id)
GO

if not exists(select 1
              from HL7CodeTable
              where table_number = '0201')
    begin
        exec addHL7Code 'ASN', 'Answering Service Number', '0201', 'HL7';
        exec addHL7Code 'BPN', 'Beeper Number', '0201', 'HL7';
        exec addHL7Code 'EMR', 'Emergency Number', '0201', 'HL7';
        exec addHL7Code 'NET', 'Network (email) Address', '0201', 'HL7';
        exec addHL7Code 'ORN', 'Other Residence Number', '0201', 'HL7';
        exec addHL7Code 'PRN', 'Primary Residence Number', '0201', 'HL7';
        exec addHL7Code 'VHN', 'Vacation Home Number', '0201', 'HL7';
        exec addHL7Code 'WPN', 'Work Number', '0201', 'HL7';
    end

if not exists(select 1
              from HL7CodeTable
              where table_number = '0202')
    begin
        exec addHL7Code 'BP', 'Beeper', '0202', 'HL7';
        exec addHL7Code 'CP', 'Cellular Phone', '0202', 'HL7';
        exec addHL7Code 'FX', 'Fax', '0202', 'HL7';
        exec addHL7Code 'Internet', 'Internet Address: Use Only If Telecommunication Use Code Is NET', '0202', 'HL7';
        exec addHL7Code 'MD', 'Modem', '0202', 'HL7';
        exec addHL7Code 'PH', 'Telephone', '0202', 'HL7';
        exec addHL7Code 'TDD', 'Telecommunications Device for the Deaf', '0202', 'HL7';
        exec addHL7Code 'TTY', 'Teletypewriter', '0202', 'HL7';
        exec addHL7Code 'X.400', 'X.400 email address: Use Only If Telecommunication Use Code Is NET', '0202', 'HL7';
    end
