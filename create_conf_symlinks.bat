:: Product	: Search Application
:: Purpose	: To create hard links outside the project GIT repository (outside the ./src/ directory)
::			to load configuration details using external XML files.
:: Author	: Tharanga Kothalawala <tharanga.kothalawala@gmail.com>

echo ## executing mklink in %cd%

cd config
del /f configuration.xml
mklink /H configuration.xml ..\src\database\configuration.xml

cd databases
:: Project Test DB entity_config
del /f fproject_test_entity_config.xml
mklink /H fproject_test_entity_config.xml ..\..\src\database\databases\fproject_test_entity_config.xml

:: Communigram DB entity_config
del /f CGDB_entity_config.xml
mklink /H CGDB_entity_config.xml ..\..\src\database\databases\CGDB_entity_config.xml


PAUSE