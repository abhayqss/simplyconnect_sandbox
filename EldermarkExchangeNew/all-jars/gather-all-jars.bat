copy ..\eldermark-exchange-event-xml-api\target\eldermark-*.jar .
copy ..\eldermark-exchange-external-api\target\eldermark-*.jar .
copy ..\eldermark-exchange-mobile-api\target\eldermark-*.jar .
copy ..\eldermark-exchange-openxds-api\target\eldermark-*.jar .
copy ..\eldermark-exchange-web-portal\target\eldermark-*.jar .

tar.exe -a -cf all.zip eldermark-*.jar

del eldermark-*.jar
