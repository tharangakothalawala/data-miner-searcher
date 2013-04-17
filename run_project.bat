:: Product	: Search Application
:: Purpose	: To execute the program though command line.
:: author	: Tharanga Kothalawala <tharanga.kothalawala@gmail.com>

::set locationtosearchapp = C:\Users\Tharanga\Documents\NetBeansProjects\ProjectAttempt_1\dist

echo ## running java -jar ProjectAttempt_1.jar in %cd%

::cd %locationtosearchapp%
echo.
java -jar ./dist/ProjectAttempt_1.jar

PAUSE