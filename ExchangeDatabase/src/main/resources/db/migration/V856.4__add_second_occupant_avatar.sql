if col_length('Avatar', 'second_occupant_id') is not null
    begin
        alter table Avatar
            drop constraint FK_Avatar_SecondOccupant_enc_id;
        alter table Avatar
            drop column second_occupant_id;
    end
go
alter table Avatar
    add second_occupant_id bigint
go

alter table Avatar
    add constraint FK_Avatar_SecondOccupant_enc_id foreign key (second_occupant_id) references SecondOccupant_enc (id)
go
