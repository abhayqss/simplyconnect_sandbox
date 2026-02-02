ALTER TABLE [dbo].[OrganizationAddress] ADD [longitude] [decimal](10,4) NULL
GO

ALTER TABLE [dbo].[OrganizationAddress] ADD [latitude] [decimal](10,4) NULL
GO

ALTER TABLE [dbo].[OrganizationAddress] ADD [locationUpToDate] [bit] NULL DEFAULT 0;
GO

CREATE FUNCTION [dbo].[GreatCircleDistanceAngleGrad] (
  @userLongitudeDegree  DECIMAL(22,12),
  @userLatitudeDegree  DECIMAL(22,12),
  @pointLongitudeDegree  DECIMAL(22,12),
  @pointLatitudeDegree  DECIMAL(22,12)
)
  RETURNS DECIMAL(22,12)
  WITH SCHEMABINDING -- deterministic function
AS
  BEGIN

    DECLARE @toRad DECIMAL(22,12), @toDeg DECIMAL(22,12), @earthRadMiles DECIMAL(22,12);
    SET @toRad = PI() / 180.0;
    SET @earthRadMiles = 3963;
    SET @toDeg = 180.0 / PI();

    if (@pointLongitudeDegree is null or @pointLatitudeDegree is null)
      return 2 * @earthRadMiles;

    DECLARE  @userLongitudeRad  DECIMAL(22,12);
    DECLARE  @userLatitudeRad  DECIMAL(22,12);
    DECLARE  @pointLongitudeRad  DECIMAL(22,12);
    DECLARE  @pointLatitudeRad  DECIMAL(22,12);


    SET  @userLongitudeRad = @userLongitudeDegree * @toRad;
    SET  @userLatitudeRad = @userLatitudeDegree * @toRad;
    SET  @pointLongitudeRad = @pointLongitudeDegree * @toRad;
    SET  @pointLatitudeRad = @pointLatitudeDegree * @toRad;

    DECLARE @ResultRad DECIMAL(22,12);
    DECLARE @diffLongitudeRad DECIMAL(22,12);

    set @diffLongitudeRad = ABS(@userLongitudeRad - @pointLongitudeRad);
    SET @ResultRad = @earthRadMiles * ACOS(sin(@userLatitudeRad) * sin(@pointLatitudeRad) + cos(@userLatitudeRad) * cos(@pointLatitudeRad) * cos(@diffLongitudeRad));
    RETURN @ResultRad;
  END;
GO