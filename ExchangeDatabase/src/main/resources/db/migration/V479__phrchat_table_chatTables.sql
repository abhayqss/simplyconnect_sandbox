-- phr chat company table  
CREATE TABLE [dbo].[companies] 
  ( 
     [id]              [BIGINT] IDENTITY(1, 1) NOT NULL, 
     [notifycompanyid] [INT] NOT NULL, 
     [name]            [VARCHAR](255) NULL, 
     [namespace]       [VARCHAR](45) NOT NULL, 
     [password]        [VARCHAR](255) NOT NULL, 
     [enabled]         [TINYINT] NOT NULL, 
     [createdat]       [DATETIME2](7) NOT NULL, 
     [updatedat]       [DATETIME2](7) NULL, 
     CONSTRAINT [PK__companie__3213E83F28F9F3DA] PRIMARY KEY CLUSTERED ( [id] 
     ASC )WITH (pad_index = OFF, statistics_norecompute = OFF, ignore_dup_key = 
     OFF, allow_row_locks = on, allow_page_locks = on) ON [PRIMARY] 
  ) 
ON [PRIMARY] 

ALTER TABLE [dbo].[companies] 
  ADD CONSTRAINT [DF__companies__name__46D346CA] DEFAULT (NULL) FOR [name] 

ALTER TABLE [dbo].[companies] 
  ADD CONSTRAINT [DF__companies__enabl__47C76B03] DEFAULT ('1') FOR [enabled] 

ALTER TABLE [dbo].[companies] 
  ADD CONSTRAINT [DF__companies__creat__48BB8F3C] DEFAULT (Getdate()) FOR 
  [createdAt] 

ALTER TABLE [dbo].[companies] 
  ADD CONSTRAINT [DF__companies__updat__49AFB375] DEFAULT (NULL) FOR [updatedAt] 

/****** Object:  Table [dbo].[timezones]    Script Date: 15-02-2019 15:50:51 ******/ 
/*  ALTER User Mobile table */ 
ALTER TABLE [dbo].[usermobile] 
  WITH CHECK ADD CONSTRAINT [FK_UserMobile_SourceDatabase] FOREIGN KEY( 
  [database_id]) REFERENCES [dbo].[sourcedatabase] ([id]) 

CREATE TABLE [dbo].[timezones] 
  ( 
     [id]           [BIGINT] IDENTITY(1, 1) NOT NULL, 
     [utc_offset]   [VARCHAR](9) NOT NULL, 
     [name]         [VARCHAR](255) NOT NULL, 
     [abbreviation] [VARCHAR](5) NOT NULL, 
     [createdat]    [DATETIME2](7) NOT NULL, 
     [updatedat]    [DATETIME2](7) NULL, 
     CONSTRAINT [PK__timezone__3213E83F5F3478FF] PRIMARY KEY CLUSTERED ( [id] 
     ASC )WITH (pad_index = OFF, statistics_norecompute = OFF, ignore_dup_key = 
     OFF, allow_row_locks = on, allow_page_locks = on) ON [PRIMARY] 
  ) 
ON [PRIMARY] 

ALTER TABLE [dbo].[timezones] 
  ADD CONSTRAINT [DF__timezones__creat__627B613F] DEFAULT (Getdate()) FOR 
  [createdAt] 

ALTER TABLE [dbo].[timezones] 
  ADD CONSTRAINT [DF__timezones__updat__636F8578] DEFAULT (NULL) FOR [updatedAt] 

-- phr chat Handset table  
CREATE TABLE [dbo].[handsets] 
  ( 
     [id]          [BIGINT] IDENTITY(1, 1) NOT NULL, 
     [uuid]        [VARCHAR](38) NOT NULL, 
     [pn_token]    [VARCHAR](200) NULL, 
     [type]        [VARCHAR](10) NOT NULL, 
     [company_id]  [BIGINT] NOT NULL, 
     [createdat]   [DATETIME2](7) NOT NULL, 
     [updatedat]   [DATETIME2](7) NULL, 
     [device_name] [VARCHAR](100) NULL, 
     PRIMARY KEY CLUSTERED ( [id] ASC )WITH (pad_index = OFF, 
     statistics_norecompute = OFF, ignore_dup_key = OFF, allow_row_locks = on, 
     allow_page_locks = on) ON [PRIMARY], 
     UNIQUE NONCLUSTERED ( [uuid] ASC )WITH (pad_index = OFF, 
     statistics_norecompute = OFF, ignore_dup_key = OFF, allow_row_locks = on, 
     allow_page_locks = on) ON [PRIMARY] 
  ) 
ON [PRIMARY] 

ALTER TABLE [dbo].[handsets] 
  ADD DEFAULT (NULL) FOR [pn_token] 

ALTER TABLE [dbo].[handsets] 
  ADD DEFAULT (Getdate()) FOR [createdAt] 

ALTER TABLE [dbo].[handsets] 
  ADD DEFAULT (NULL) FOR [updatedAt] 

ALTER TABLE [dbo].[handsets] 
  ADD DEFAULT (NULL) FOR [device_name] 

ALTER TABLE [dbo].[handsets] 
  WITH CHECK ADD CONSTRAINT [FK__handsets__compan__6CF8EFB2] FOREIGN KEY( 
  [company_id]) REFERENCES [dbo].[companies] ([id]) 

ALTER TABLE [dbo].[handsets] 
  CHECK CONSTRAINT [FK__handsets__compan__6CF8EFB2] 

ALTER TABLE [dbo].[handsets] 
  WITH CHECK ADD CHECK (([type]='ios' OR [type]='android')) 

/****** Object:  Table [dbo].[users]    Script Date: 15-02-2019 15:51:51 ******/ 
CREATE TABLE [dbo].[users] 
  ( 
     [id]              [BIGINT] IDENTITY(1, 1) NOT NULL, 
     [notifyuserid]    [INT] NOT NULL, 
     [name]            [VARCHAR](255) NOT NULL, 
     [logged]          [TINYINT] NOT NULL, 
     [role]            [VARCHAR](10) NOT NULL, 
     [current_handset] [VARCHAR](38) NULL, 
     [company_id]      [BIGINT] NOT NULL, 
     [timezone_id]     [BIGINT] NOT NULL, 
     [createdat]       [DATETIME2](7) NULL, 
     [updatedat]       [DATETIME2](7) NULL, 
     CONSTRAINT [PK__users__3213E83F627BF905] PRIMARY KEY CLUSTERED ( [id] ASC ) 
     WITH (pad_index = OFF, statistics_norecompute = OFF, ignore_dup_key = OFF, 
     allow_row_locks = on, allow_page_locks = on) ON [PRIMARY] 
  ) 
ON [PRIMARY] 

ALTER TABLE [dbo].[users] 
  ADD CONSTRAINT [DF__users__logged__664BF223] DEFAULT ('0') FOR [logged] 

ALTER TABLE [dbo].[users] 
  ADD CONSTRAINT [DF__users__role__68343A95] DEFAULT ('text') FOR [role] 

ALTER TABLE [dbo].[users] 
  ADD CONSTRAINT [DF__users__current_h__69285ECE] DEFAULT (NULL) FOR 
  [current_handset] 

ALTER TABLE [dbo].[users] 
  ADD CONSTRAINT [DF__users__createdAt__6A1C8307] DEFAULT (Getdate()) FOR 
  [createdAt] 

ALTER TABLE [dbo].[users] 
  ADD CONSTRAINT [DF__users__updatedAt__6B10A740] DEFAULT (NULL) FOR [updatedAt] 

ALTER TABLE [dbo].[users] 
  WITH CHECK ADD CONSTRAINT [FK__users__company_i__70C98096] FOREIGN KEY( 
  [company_id]) REFERENCES [dbo].[companies] ([id]) 

ALTER TABLE [dbo].[users] 
  CHECK CONSTRAINT [FK__users__company_i__70C98096] 

ALTER TABLE [dbo].[users] 
  WITH CHECK ADD CONSTRAINT [FK__users__company_i__7C3B3342] FOREIGN KEY( 
  [company_id]) REFERENCES [dbo].[companies] ([id]) ON UPDATE CASCADE ON DELETE 
  CASCADE 

ALTER TABLE [dbo].[users] 
  CHECK CONSTRAINT [FK__users__company_i__7C3B3342] 

ALTER TABLE [dbo].[users] 
  WITH CHECK ADD CONSTRAINT [FK__users__timezone___6FD55C5D] FOREIGN KEY( 
  [timezone_id]) REFERENCES [dbo].[timezones] ([id]) 

ALTER TABLE [dbo].[users] 
  CHECK CONSTRAINT [FK__users__timezone___6FD55C5D] 

ALTER TABLE [dbo].[users] 
  WITH CHECK ADD CONSTRAINT [FK__users__timezone___7B470F09] FOREIGN KEY( 
  [timezone_id]) REFERENCES [dbo].[timezones] ([id]) 

ALTER TABLE [dbo].[users] 
  CHECK CONSTRAINT [FK__users__timezone___7B470F09] 

ALTER TABLE [dbo].[users] 
  WITH CHECK ADD CONSTRAINT [FK_users_handsets] FOREIGN KEY([current_handset]) 
  REFERENCES [dbo].[handsets] ([uuid]) 

ALTER TABLE [dbo].[users] 
  CHECK CONSTRAINT [FK_users_handsets] 

ALTER TABLE [dbo].[users] 
  WITH CHECK ADD CONSTRAINT [CK__users__role__6740165C] CHECK (([role]='admin' 
  OR [role]='user')) 

ALTER TABLE [dbo].[users] 
  CHECK CONSTRAINT [CK__users__role__6740165C] 

/****** Object:  Table [dbo].[session_histories]    Script Date: 15-02-2019 15:44:46 ******/ 
CREATE TABLE [dbo].[session_histories] 
  ( 
     [id]         [BIGINT] IDENTITY(1, 1) NOT NULL, 
     [ip]         [VARCHAR](45) NULL, 
     [intime]     [DATETIME2](7) NOT NULL, 
     [outtime]    [DATETIME2](7) NULL, 
     [user_id]    [BIGINT] NOT NULL, 
     [company_id] [BIGINT] NOT NULL, 
     [handset_id] [BIGINT] NOT NULL, 
     CONSTRAINT [PK__session___3213E83FB1DEA7F7] PRIMARY KEY CLUSTERED ( [id] 
     ASC )WITH (pad_index = OFF, statistics_norecompute = OFF, ignore_dup_key = 
     OFF, allow_row_locks = on, allow_page_locks = on) ON [PRIMARY] 
  ) 
ON [PRIMARY] 

ALTER TABLE [dbo].[session_histories] 
  ADD CONSTRAINT [DF__session_hist__ip__4C8C2020] DEFAULT (NULL) FOR [ip] 

ALTER TABLE [dbo].[session_histories] 
  ADD CONSTRAINT [DF__session_h__inTim__4D804459] DEFAULT (Getdate()) FOR 
  [inTime] 

ALTER TABLE [dbo].[session_histories] 
  ADD CONSTRAINT [DF__session_h__outTi__4E746892] DEFAULT (NULL) FOR [outTime] 

ALTER TABLE [dbo].[session_histories] 
  WITH CHECK ADD CONSTRAINT [FK__session_h__compa__73A5ED41] FOREIGN KEY( 
  [company_id]) REFERENCES [dbo].[companies] ([id]) 

ALTER TABLE [dbo].[session_histories] 
  CHECK CONSTRAINT [FK__session_h__compa__73A5ED41] 

ALTER TABLE [dbo].[session_histories] 
  WITH CHECK ADD CONSTRAINT [FK__session_h__hands__72B1C908] FOREIGN KEY( 
  [handset_id]) REFERENCES [dbo].[handsets] ([id]) ON UPDATE CASCADE ON DELETE 
  CASCADE 

ALTER TABLE [dbo].[session_histories] 
  CHECK CONSTRAINT [FK__session_h__hands__72B1C908] 

ALTER TABLE [dbo].[session_histories] 
  WITH CHECK ADD CONSTRAINT [FK__session_h__user___71BDA4CF] FOREIGN KEY( 
  [user_id]) REFERENCES [dbo].[users] ([id]) ON UPDATE CASCADE ON DELETE CASCADE 

ALTER TABLE [dbo].[session_histories] 
  CHECK CONSTRAINT [FK__session_h__user___71BDA4CF] 

/****** Object:  Table [dbo].[tenant_masters]    Script Date: 15-02-2019 15:46:53 ******/ 
CREATE TABLE [dbo].[tenant_masters] 
  ( 
     [id]        [INT] IDENTITY(1, 1) NOT NULL, 
     [username]  [VARCHAR](255) NOT NULL, 
     [password]  [VARCHAR](255) NOT NULL, 
     [createdat] [DATETIME2](7) NOT NULL, 
     [updatedat] [DATETIME2](7) NOT NULL, 
     CONSTRAINT [PK__tenant_m__3213E83FDE82ABDE] PRIMARY KEY CLUSTERED ( [id] 
     ASC )WITH (pad_index = OFF, statistics_norecompute = OFF, ignore_dup_key = 
     OFF, allow_row_locks = on, allow_page_locks = on) ON [PRIMARY], 
     CONSTRAINT [UQ__tenant_m__F3DBC5728D9B14E6] UNIQUE NONCLUSTERED ( 
     [username] ASC )WITH (pad_index = OFF, statistics_norecompute = OFF, 
     ignore_dup_key = OFF, allow_row_locks = on, allow_page_locks = on) ON 
     [PRIMARY] 
  ) 
ON [PRIMARY] 

/****** Object:  Table [dbo].[threads]    Script Date: 15-02-2019 15:49:41 ******/ 
CREATE TABLE [dbo].[threads] 
  ( 
     [id]          [BIGINT] IDENTITY(1, 1) NOT NULL, 
     [name]        [VARCHAR](45) NULL, 
     [description] [VARCHAR](255) NULL, 
     [quantity]    [INT] NOT NULL, 
     [createdat]   [DATETIME] NOT NULL, 
     [updatedat]   [DATETIME] NULL, 
     CONSTRAINT [PK_threads] PRIMARY KEY CLUSTERED ( [id] ASC )WITH (pad_index = 
     OFF, statistics_norecompute = OFF, ignore_dup_key = OFF, allow_row_locks = 
     on, allow_page_locks = on) ON [PRIMARY] 
  ) 
ON [PRIMARY] 

ALTER TABLE [dbo].[threads] 
  ADD DEFAULT (NULL) FOR [name] 

ALTER TABLE [dbo].[threads] 
  ADD DEFAULT (NULL) FOR [description] 

ALTER TABLE [dbo].[threads] 
  ADD DEFAULT (Getdate()) FOR [createdAt] 

ALTER TABLE [dbo].[threads] 
  ADD DEFAULT (NULL) FOR [updatedAt] 

/****** Object:  Table [dbo].[thread_messages]    Script Date: 15-02-2019 15:47:51 ******/ 
CREATE TABLE [dbo].[thread_messages] 
  ( 
     [id]          [BIGINT] IDENTITY(1, 1) NOT NULL, 
     [sender]      [BIGINT] NOT NULL, 
     [receiver]    [BIGINT] NOT NULL, 
     [type]        [VARCHAR](10) NOT NULL, 
     [text]        [VARCHAR](255) NOT NULL, 
     [createdat]   [DATETIME2](7) NOT NULL, 
     [notifiedat]  [DATETIME2](7) NULL, 
     [deliveredat] [DATETIME2](7) NULL, 
     CONSTRAINT [PK__thread_m__3213E83F765DBFC3] PRIMARY KEY CLUSTERED ( [id] 
     ASC )WITH (pad_index = OFF, statistics_norecompute = OFF, ignore_dup_key = 
     OFF, allow_row_locks = on, allow_page_locks = on) ON [PRIMARY] 
  ) 
ON [PRIMARY] 

ALTER TABLE [dbo].[thread_messages] 
  ADD CONSTRAINT [DF__thread_mes__type__5ADA3F77] DEFAULT ('text') FOR [type] 

ALTER TABLE [dbo].[thread_messages] 
  ADD CONSTRAINT [DF__thread_me__creat__5BCE63B0] DEFAULT (Getdate()) FOR 
  [createdAt] 

ALTER TABLE [dbo].[thread_messages] 
  ADD CONSTRAINT [DF__thread_me__notif__5CC287E9] DEFAULT (NULL) FOR 
  [notifiedAt] 

ALTER TABLE [dbo].[thread_messages] 
  ADD CONSTRAINT [DF__thread_me__deliv__5DB6AC22] DEFAULT (NULL) FOR 
  [deliveredAt] 

ALTER TABLE [dbo].[thread_messages] 
  WITH CHECK ADD FOREIGN KEY([receiver]) REFERENCES [dbo].[threads] ([id]) 

ALTER TABLE [dbo].[thread_messages] 
  WITH CHECK ADD FOREIGN KEY([sender]) REFERENCES [dbo].[users] ([id]) 

ALTER TABLE [dbo].[thread_messages] 
  WITH CHECK ADD CONSTRAINT [CK__thread_mes__type__59E61B3E] CHECK (([type]= 
  'stream' OR [type]='video' OR [type]='audio' OR [type]='image' OR [type]= 
  'text')) 

ALTER TABLE [dbo].[thread_messages] 
  CHECK CONSTRAINT [CK__thread_mes__type__59E61B3E] 

/****** Object:  Table [dbo].[thread_participants]    Script Date: 15-02-2019 15:48:58 ******/ 
CREATE TABLE [dbo].[thread_participants] 
  ( 
     [id]        [BIGINT] IDENTITY(1, 1) NOT NULL, 
     [thread_id] [BIGINT] NOT NULL, 
     [user_id]   [BIGINT] NOT NULL, 
     CONSTRAINT [PK_thread_participants] PRIMARY KEY CLUSTERED ( [id] ASC )WITH 
     (pad_index = OFF, statistics_norecompute = OFF, ignore_dup_key = OFF, 
     allow_row_locks = on, allow_page_locks = on) ON [PRIMARY] 
  ) 
ON [PRIMARY] 

ALTER TABLE [dbo].[thread_participants] 
  WITH CHECK ADD FOREIGN KEY([thread_id]) REFERENCES [dbo].[threads] ([id]) 

ALTER TABLE [dbo].[thread_participants] 
  WITH CHECK ADD FOREIGN KEY([user_id]) REFERENCES [dbo].[users] ([id]) 

/* Insert data into timezone */ 
INSERT INTO [dbo].[timezones] 
            ([utc_offset], 
             [name], 
             [abbreviation], 
             [createdat], 
             [updatedat]) 
VALUES      ('+10:30', 
             'Australian Central Daylight Savings Time', 
             'ACDT', 
             Getdate(), 
             Getdate()) 

/* DB script for user and companies table insert data for chat */ 
INSERT INTO [dbo].[companies] 
SELECT [sd].[id], 
       [ss].[login_company_id], 
       Lower(Replace(Replace([sd].[alternative_id], ' ', ''), '_', '')) 
       [alternative_id], 
       '$2a$14$3DTAybV7O9u.iB7S6B3AquwTCu4BVfdx7pJAPA4VIjyqrcLYUbtG.', 
       '1', 
       Getdate(), 
       Getdate() 
FROM   [dbo].[sourcedatabase] sd 
       INNER JOIN [dbo].[systemsetup] ss 
               ON [sd].[id] = [ss].[database_id] 
WHERE  alternative_id IS NOT NULL; 

INSERT INTO [dbo].[users] 
SELECT [id], 
       [first_name], 
       0, 
       'user', 
       NULL, 
       (SELECT TOP 1 [id] 
        FROM   [dbo].[companies] 
        WHERE  [notifycompanyid] = [database_id]) AS [company_id], 
       1, 
       Getdate(), 
       Getdate() 
FROM   [dbo].[usermobile] 
WHERE  [first_name] IS NOT NULL 
       AND [database_id] IS NOT NULL 
       AND [id] NOT IN (SELECT [notifyuserid] 
                        FROM   [dbo].[users]); 