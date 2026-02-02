@echo off
rem USAGE
rem Execute "generate-html.bat X:\path\to\specification\api\swagger.yaml" in cmd
rem The path parameter is optional and defaults to "swagger.yaml" located in the working directory

set path_to_swagger="./swagger.yaml"

if [%1] == [] goto generate
set path_to_swagger=%1
shift

:generate
java -jar swagger-codegen-cli.jar generate -i %path_to_swagger% -o "out_html" -l html