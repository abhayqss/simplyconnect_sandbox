CREATE TABLE [dbo].[VitalSignReferenceInformation](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[vital_sign_type_code] [varchar](50) NULL,
	[reference_info] [varchar](max) NULL,
 CONSTRAINT [PK_VitalSignReferenceInformation] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO


INSERT INTO [dbo].[VitalSignReferenceInformation]
           ([vital_sign_type_code]
           ,[reference_info])
     VALUES
           ('9279-1',
				'<p>Respiration Rate</p>'+
				'<p>Here will be some information concerning Respiration Rate</p>'),
			('8867-4',
				'<p>Heart Beat</p>'+
				'<p>Here will be some information concerning Heart Beat</p>'),
			('2710-2',
				'<p>Oxygen Saturation</p>'+
				'<p>Here will be some information concerning Oxygen Saturation</p>'),
			('8480-6',
				'<p>Intravascular Systolic</p>'+
				'<p>Here will be some information concerning Intravascular Systolic</p>'),
			('8462-4',
				'<p>Intravascular Diastolic</p>'+
				'<p>Here will be some information concerning Intravascular Diastolic</p>'),
			('8310-5',
				'<p>Body Temperature</p>'+
				'<p>Here will be some information concerning Body Temperature</p>'),
			('8302-2',
				'<p>Body Height</p>'+
				'<p>Here will be some information concerning Body Height</p>'),
			('8306-3',
				'<p>Body Height (Lying)</p>'+
				'<p>Here will be some information concerning Body Height (Lying)</p>'),
			('8287-5',
				'<p>Circumfrence Occipital-Frontal (Tape Measure)</p>'+
				'<p>Here will be some information concerning Circumfrence Occipital-Frontal (Tape Measure)</p>'),
			('3141-9',
				'<p>Body Weight</p>'+
				'<p>Here will be some information concerning Body Weight</p>')

GO



