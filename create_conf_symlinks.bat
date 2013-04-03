:: Search Application
::
:: author     Tharanga Kothalawala <tharanga.kothalawala@gmail.com>

echo ## executing mklink

cd config
del /f configuration.xml
mklink /H configuration.xml ..\src\database\configuration.xml

cd databases
:: Communigram DB entity_config
del /f diecast_entity_config.xml
mklink /H diecast_entity_config.xml ..\..\src\database\plugins\diecast_entity_config.xml

:: Diecast DB entity_config
del /f CGDB_entity_config.xml
mklink /H CGDB_entity_config.xml ..\..\src\database\plugins\CGDB_entity_config.xml


PAUSE