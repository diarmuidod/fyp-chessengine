echo Starting training process...
start java -jar fyp-chessengine.jar

:LOOP
echo.

TIMEOUT /T 900 /NOBREAK
	echo.
	start java -Xms8G -Xmx28G -jar fyp-chessengine.jar
	echo.

GOTO LOOP