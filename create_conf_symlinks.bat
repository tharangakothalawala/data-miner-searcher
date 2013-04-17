:: Product	: Search Application
:: Purpose	: To create hard links outside the project repository (outside the ./src/ directory)
::			to load configuration details using external XML files.
:: author	: Tharanga Kothalawala <tharanga.kothalawala@gmail.com>

echo ## executing mklink in %cd%

cd config
del /f configuration.xml
mklink /H configuration.xml ..\src\database\configuration.xml

cd databases
:: Communigram DB entity_config
del /f diecast_entity_config.xml
mklink /H diecast_entity_config.xml ..\..\src\database\databases\diecast_entity_config.xml

:: Diecast DB entity_config
del /f CGDB_entity_config.xml
mklink /H CGDB_entity_config.xml ..\..\src\database\databases\CGDB_entity_config.xml


PAUSE