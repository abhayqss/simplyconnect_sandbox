

CREATE TABLE Avatar (id BigInt IDENTITY(1,1) NOT NULL PRIMARY KEY, resident_id BigInt NULL  CONSTRAINT FK_Resident_ResidentAvatar_id FOREIGN KEY (resident_id) REFERENCES [dbo].[resident_enc] ([id]), 
employee_id BigInt NULL  CONSTRAINT FK_Employee_ResidentAvatar_id FOREIGN KEY (employee_id) REFERENCES [dbo].[employee_enc] ([id]) ,
avatar_name VARCHAR(100) ) 