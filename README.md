# fyp-chessengine

Setup Instructions:
1. Project requires JDK version 16.0.2
2. Install xampp or similar software supporting MySQL Servers
3. Run MySQL Server
4. Run .sql script found in src/Engine/EngineDB.sql
5. Call "new UI()" from main for UI
or
6. Call new "Engine().trainEngine(int seconds)" to train engine
7. Engine training automated via batch script found under out/artifacts/fyp_chessengine_jar
8. Executable jar under the same directory is configured to run the UI

Note: Trained engine data not currently provided, can be trained by calling Engine.trainEngine(int secondsToTrain)
