@echo off
rem USAGE
rem Execute "generate-all.bat X:\path\to\specification\api\swagger.yaml" in cmd
rem The path parameter is optional and defaults to "swagger.yaml" located in the working directory

rem swagger codegen cli v2.3.1
call generate-html.bat %1
call generate-spring-boot.bat %1