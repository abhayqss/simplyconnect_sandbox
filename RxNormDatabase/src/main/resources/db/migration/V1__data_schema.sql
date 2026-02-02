SET XACT_ABORT ON;

CREATE TABLE [dbo].[RXNSAT]
(
   RXCUI            varchar(8),
   LUI              varchar(8),
   SUI              varchar(8),
   RXAUI            varchar(8),
   STYPE            varchar (50),
   CODE             varchar (50),
   ATUI             varchar (11),
   SATUI            varchar (50),
   ATN              varchar (1000) NOT NULL,
   SAB              varchar (20) NOT NULL,
   ATV              varchar (4000),
   SUPPRESS         varchar (1),
   CVF              varchar (50)
);