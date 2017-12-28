db.getCollection("log").ensureIndex({"robot.ticks":1});
db.getCollection("log").ensureIndex({"robot_id":1});
db.getCollection("log").ensureIndex({"timestamp":1});
db.getCollection("log").ensureIndex({"timestamp_nano":1});
