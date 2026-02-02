SET XACT_ABORT ON
GO

/****** Object:  Table [dbo].[Communication]    Script Date: 05/04/2015 17:03:08 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Communication]') AND type in (N'U'))
DROP TABLE [dbo].[Communication]
GO

/****** Object:  Table [dbo].[FuneralHome]    Script Date: 05/04/2015 17:12:06 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[FuneralHome]') AND type in (N'U'))
DROP TABLE [dbo].[FuneralHome]
GO

/****** Object:  Table [dbo].[MedicationTreatmentSetup]    Script Date: 05/04/2015 17:13:53 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[MedicationTreatmentSetup]') AND type in (N'U'))
DROP TABLE [dbo].[MedicationTreatmentSetup]
GO

/****** Object:  Table [dbo].[MedIncident]    Script Date: 05/04/2015 17:14:26 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[MedIncident]') AND type in (N'U'))
DROP TABLE [dbo].[MedIncident]
GO

/****** Object:  Table [dbo].[MedProviderScheduleLog]    Script Date: 05/04/2015 17:14:59 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[MedProviderScheduleLog]') AND type in (N'U'))
DROP TABLE [dbo].[MedProviderScheduleLog]
GO

/****** Object:  Table [dbo].[MedProviderSchedule]    Script Date: 05/04/2015 17:54:51 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[MedProviderSchedule]') AND type in (N'U'))
DROP TABLE [dbo].[MedProviderSchedule]
GO

/****** Object:  Table [dbo].[MedScheduleCode]    Script Date: 05/04/2015 17:55:13 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[MedScheduleCode]') AND type in (N'U'))
DROP TABLE [dbo].[MedScheduleCode]
GO

/****** Object:  Table [dbo].[MedTimeCode]    Script Date: 05/04/2015 17:55:41 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[MedTimeCode]') AND type in (N'U'))
DROP TABLE [dbo].[MedTimeCode]
GO

/****** Object:  Table [dbo].[OccupancyGoal]    Script Date: 05/04/2015 17:58:37 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[OccupancyGoal]') AND type in (N'U'))
DROP TABLE [dbo].[OccupancyGoal]
GO

/****** Object:  Table [dbo].[Prospect]    Script Date: 05/04/2015 17:59:03 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Prospect]') AND type in (N'U'))
DROP TABLE [dbo].[Prospect]
GO

/****** Object:  Table [dbo].[ResIncident]    Script Date: 05/04/2015 18:01:03 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[ResIncident]') AND type in (N'U'))
DROP TABLE [dbo].[ResIncident]
GO

/****** Object:  Table [dbo].[ResLeaveOfAbsence]    Script Date: 05/04/2015 18:01:31 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[ResLeaveOfAbsence]') AND type in (N'U'))
DROP TABLE [dbo].[ResLeaveOfAbsence]
GO

/****** Object:  Table [dbo].[ResMedDup]    Script Date: 05/04/2015 18:02:03 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[ResMedDup]') AND type in (N'U'))
DROP TABLE [dbo].[ResMedDup]
GO

/****** Object:  Table [dbo].[ResMedProvider]    Script Date: 05/04/2015 18:02:27 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[ResMedProvider]') AND type in (N'U'))
DROP TABLE [dbo].[ResMedProvider]
GO

/****** Object:  Table [dbo].[ResidentUnitHistory]    Script Date: 05/04/2015 18:03:50 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[ResidentUnitHistory]') AND type in (N'U'))
DROP TABLE [dbo].[ResidentUnitHistory]
GO

/****** Object:  Table [dbo].[UnitHistory]    Script Date: 05/04/2015 18:04:24 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[UnitHistory]') AND type in (N'U'))
DROP TABLE [dbo].[UnitHistory]
GO

/****** Object:  Table [dbo].[UnitTypeRateHistory]    Script Date: 05/04/2015 18:04:50 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[UnitTypeRateHistory]') AND type in (N'U'))
DROP TABLE [dbo].[UnitTypeRateHistory]
GO

/****** Object:  Table [dbo].[CommunicationType]    Script Date: 05/04/2015 18:46:44 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[CommunicationType]') AND type in (N'U'))
DROP TABLE [dbo].[CommunicationType]
GO

/****** Object:  Table [dbo].[Inquiry]    Script Date: 05/04/2015 18:47:08 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Inquiry]') AND type in (N'U'))
DROP TABLE [dbo].[Inquiry]
GO

/****** Object:  Table [dbo].[LoaReason]    Script Date: 05/04/2015 18:47:23 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[LoaReason]') AND type in (N'U'))
DROP TABLE [dbo].[LoaReason]
GO

/****** Object:  Table [dbo].[MedProvider]    Script Date: 05/04/2015 18:49:10 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[MedProvider]') AND type in (N'U'))
DROP TABLE [dbo].[MedProvider]
GO

/****** Object:  Table [dbo].[ProfessionalContact]    Script Date: 05/04/2015 18:49:26 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[ProfessionalContact]') AND type in (N'U'))
DROP TABLE [dbo].[ProfessionalContact]
GO

/****** Object:  Table [dbo].[Unit]    Script Date: 05/04/2015 18:49:56 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Unit]') AND type in (N'U'))
DROP TABLE [dbo].[Unit]
GO

/****** Object:  Table [dbo].[UnitStation]    Script Date: 05/04/2015 18:50:14 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[UnitStation]') AND type in (N'U'))
DROP TABLE [dbo].[UnitStation]
GO

/****** Object:  Table [dbo].[UnitType]    Script Date: 05/04/2015 18:50:34 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[UnitType]') AND type in (N'U'))
DROP TABLE [dbo].[UnitType]
GO