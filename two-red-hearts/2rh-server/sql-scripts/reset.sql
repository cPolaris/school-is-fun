/*
  Rest without having to drop the database
 */

DELETE FROM User;
DELETE FROM Location;
DELETE FROM TempUser;
DELETE FROM HobbyTag;

ALTER TABLE User AUTO_INCREMENT = 1;
ALTER TABLE Location AUTO_INCREMENT = 1;
ALTER TABLE HobbyTag AUTO_INCREMENT = 1;