CREATE DATABASE 2rh;
USE 2rh;

SET GLOBAL event_scheduler = ON;


DELIMITER ||


CREATE TABLE User (
  id         INT UNSIGNED              AUTO_INCREMENT, -- [0, 42 9496 7295]
  username   VARCHAR(20)      NOT NULL,
  email      VARCHAR(30)      NOT NULL,
  token      CHAR(64)         NOT NULL,
  salt       CHAR(16)         NOT NULL,
  hash       CHAR(128)        NOT NULL,
  name       VARCHAR(50)      NOT NULL,
  majorCode  TINYINT UNSIGNED NOT NULL, -- [0, 255]
  genderCode CHAR(1)          NOT NULL,
  avatarUrl  VARCHAR(255)     NOT NULL DEFAULT '/static/avatar/default.jpg',
  bio        VARCHAR(255),
  PRIMARY KEY (id),
  UNIQUE KEY (username),
  UNIQUE KEY (email),
  UNIQUE KEY (token)
);


CREATE TABLE TempUser (
  username    VARCHAR(20) NOT NULL,
  email       VARCHAR(30) NOT NULL,
  salt        CHAR(16)    NOT NULL,
  hash        CHAR(128)   NOT NULL,
  regCred     CHAR(64)    NOT NULL, -- hash, 64 hex
  createdTime TIMESTAMP   NOT NULL,
  PRIMARY KEY (username),
  UNIQUE KEY (email)
);


CREATE TABLE Location (
  id       INT UNSIGNED AUTO_INCREMENT,
  name     VARCHAR(30)       NOT NULL,
  imageUrl VARCHAR(255)      NOT NULL,
  coords   POLYGON           NOT NULL,
  port     SMALLINT UNSIGNED NOT NULL,
  visitors BIGINT UNSIGNED   NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY (name)
);


CREATE TABLE HobbyTag (
  id    INT UNSIGNED AUTO_INCREMENT,
  title VARCHAR(30) NOT NULL,
  PRIMARY KEY (id)
);


CREATE TABLE UserHobby (
  userId     INT UNSIGNED NOT NULL,
  hobbyTagId INT UNSIGNED NOT NULL,
  FOREIGN KEY (userid) REFERENCES User (id)
    ON DELETE CASCADE,
  FOREIGN KEY (hobbyTagId) REFERENCES HobbyTag (id)
    ON DELETE CASCADE,
  PRIMARY KEY (userId, hobbyTagId)
);


CREATE TABLE Appear (
  userId    INT UNSIGNED NOT NULL,
  locId     INT UNSIGNED NOT NULL,
  coord     POINT        NOT NULL,
  timestamp TIMESTAMP    NOT NULL,
  FOREIGN KEY (userId) REFERENCES User (id)
    ON DELETE CASCADE,
  FOREIGN KEY (locId) REFERENCES Location (id)
    ON DELETE CASCADE,
  PRIMARY KEY (userId, timestamp)
);


CREATE TABLE Follow (
  fromId INT UNSIGNED NOT NULL,
  toId   INT UNSIGNED NOT NULL,
  FOREIGN KEY (fromId) REFERENCES User (id)
    ON DELETE CASCADE,
  FOREIGN KEY (toId) REFERENCES User (id)
    ON DELETE CASCADE,
  PRIMARY KEY (fromId, toId)
);


CREATE TABLE Rank (
  forId        INT UNSIGNED NOT NULL,
  recId        INT UNSIGNED NOT NULL,
  profileScore DOUBLE       NOT NULL,
  wtfScore     DOUBLE       NOT NULL,
  FOREIGN KEY (forId) REFERENCES User (id)
    ON DELETE CASCADE,
  FOREIGN KEY (recId) REFERENCES User (id)
    ON DELETE CASCADE,
  PRIMARY KEY (forId, recId)
);


CREATE TABLE Message (
  fromId    INT UNSIGNED NOT NULL,
  toId      INT UNSIGNED NOT NULL,
  timestamp TIMESTAMP    NOT NULL,
  content   VARCHAR(255) NOT NULL,
  FOREIGN KEY (fromId) REFERENCES User (id)
    ON DELETE CASCADE,
  FOREIGN KEY (toId) REFERENCES User (id)
    ON DELETE CASCADE,
  PRIMARY KEY (fromId, toId, timestamp)
);


CREATE VIEW TakenRegistrationView AS
  SELECT
    username,
    email
  FROM User
  UNION
  SELECT
    username,
    email
  FROM TempUser;


/*******************************************************************************
 * TRIGGERS
 ******************************************************************************/


/*******************************************************************************
 * PROCEDURES
 ******************************************************************************/


CREATE PROCEDURE AddLocation(p_name VARCHAR(30), p_coords POLYGON)
  BEGIN
    SET @centerCoords = ST_CENTROID(p_coords);
    SET @newUrl = CONCAT(
        'https://maps.googleapis.com/maps/api/staticmap?size=640x640&scale=2&format=jpg&maptype=satellite&center=',
        X(@centerCoords), ',', Y(@centerCoords),
        '&zoom=', 17);
    INSERT INTO Location (name, imageUrl, coords, port, visitors)
    VALUES (p_name, @newUrl, p_coords, 0, 0);
  END;


CREATE PROCEDURE AddAppearance(p_userId INT UNSIGNED, p_coord POINT)
  BEGIN
    SET @withinLocId = (SELECT id
                        FROM Location
                        WHERE Within(p_coord, coords) = 1);

    UPDATE Location
    SET visitors = visitors + 1
    WHERE id = 1;

    IF @withinLocId IS NOT NULL
    THEN
      INSERT INTO Appear (userId, locId, coord)
      VALUES (p_userId, @withinLocId, p_coord);

      UPDATE Location
      SET visitors = visitors + 1
      WHERE id = @withinLocId;
    END IF;

    SELECT @withinLocId AS locId;
  END;


CREATE PROCEDURE GetAllLocInfo()
  BEGIN
    SELECT
      id,
      name,
      x(centroid(coords)) AS lat,
      y(centroid(coords)) AS lng,
      visitors,
      port
    FROM Location;
  END;


CREATE PROCEDURE GetLoc(p_id INT UNSIGNED)
  BEGIN
    SELECT
      id,
      name,
      imageUrl,
      port
    FROM Location
    WHERE id = p_id;
  END;


CREATE PROCEDURE GetHotLocs(p_limit TINYINT UNSIGNED)
  BEGIN
    SELECT
      id,
      name,
      imageUrl,
      port
    FROM Location
    ORDER BY visitors DESC
    LIMIT p_limit;
  END;


CREATE PROCEDURE AddUserHobby(p_userId INT UNSIGNED, p_tagId INT UNSIGNED)
  BEGIN
    INSERT INTO UserHobby (userId, hobbyTagId) VALUES (p_userId, p_tagId);
  END;


CREATE PROCEDURE GetUserIds()
  BEGIN
    SELECT id
    FROM User;
  END;


CREATE PROCEDURE GetHobbyTagIdsOfUser(p_userId INT UNSIGNED)
  BEGIN
    SELECT hobbyTagId
    FROM UserHobby
    WHERE userId = p_userId;
  END;


CREATE PROCEDURE SetRankProfileScore(p_forId INT UNSIGNED, p_recId INT UNSIGNED, p_score DOUBLE)
  BEGIN
    INSERT INTO Rank (forId, recId, profileScore, wtfScore) VALUES (p_forId, p_recId, p_score, 0.0)
    ON DUPLICATE KEY UPDATE profileScore = p_score;
  END;


CREATE PROCEDURE SetRankWtfScore(p_forId INT UNSIGNED, p_recId INT UNSIGNED, p_score DOUBLE)
  BEGIN
    INSERT INTO Rank (forId, recId, profileScore, wtfScore) VALUES (p_forId, p_recId, 0.0, p_score)
    ON DUPLICATE KEY UPDATE wtfScore = p_score;
  END;


CREATE PROCEDURE AddTempUser(
  p_cred     CHAR(64),
  p_email    VARCHAR(30),
  p_salt     CHAR(16),
  p_hash     CHAR(128),
  p_username VARCHAR(20)
)
  BEGIN
    INSERT INTO TempUser (regCred,
                          username,
                          email,
                          salt,
                          hash)
    VALUES (p_cred,
            p_username,
            p_email,
            p_salt,
            p_hash);
  END;


CREATE PROCEDURE LiftTempUser(
  p_email     VARCHAR(30),
  p_salt      CHAR(16),
  p_hash      CHAR(128),
  p_token     CHAR(64),
  p_username  VARCHAR(20),
  p_name      VARCHAR(50),
  p_major     TINYINT UNSIGNED,
  p_gender    CHAR(1),
  p_bio       VARCHAR(255),
  p_avatarUrl VARCHAR(255)
)
  BEGIN
    INSERT INTO User (
      username,
      email,
      token,
      salt,
      hash,
      name,
      majorCode,
      genderCode,
      avatarUrl,
      bio)
    VALUES
      (p_username,
       p_email,
       p_token,
       p_salt,
       p_hash,
       p_name,
       p_major,
       p_gender,
       p_avatarUrl,
       p_bio);
    DELETE FROM TempUser
    WHERE username = p_username;
  END;


CREATE PROCEDURE GetTempUser(p_cred CHAR(64))
  BEGIN
    SELECT *
    FROM TempUser
    WHERE regCred = p_cred;
  END;


CREATE PROCEDURE CanRegisterWith(p_username VARCHAR(20), p_email VARCHAR(30))
  BEGIN
    SELECT EXISTS(SELECT
                    username,
                    email
                  FROM TakenRegistrationView
                  WHERE username = p_username OR email = p_email)
      AS e;
  END;


CREATE PROCEDURE GetUserWithToken(p_token CHAR(64))
  BEGIN
    SELECT *
    FROM User
    WHERE token = p_token;
  END;


CREATE PROCEDURE GetUser(p_username VARCHAR(20))
  BEGIN
    SELECT *
    FROM User
    WHERE username = p_username;
  END;


CREATE PROCEDURE GetUserCount()
  BEGIN
    SELECT COUNT(*)
    FROM User;
  END;


CREATE PROCEDURE GetFollow()
  BEGIN
    SELECT *
    FROM Follow;
  END;


CREATE PROCEDURE GetUserWithId(p_uid INT UNSIGNED)
  BEGIN
    SELECT *
    FROM User
    WHERE id = p_uid;
  END;


CREATE PROCEDURE UpdateUserWithId(
  p_id        INT UNSIGNED,
  p_major     TINYINT UNSIGNED,
  p_gender    CHAR(1),
  p_avatarUrl VARCHAR(255),
  p_bio       VARCHAR(255)
)
  BEGIN
    UPDATE User
    SET majorCode = p_major,
      genderCode  = p_gender,
      avatarUrl   = p_avatarUrl,
      bio         = p_bio
    WHERE id = p_id;
  END;


CREATE PROCEDURE ReplaceProfileWithUsername(
  p_username   VARCHAR(20),
  p_name       VARCHAR(50),
  p_majorCode  TINYINT UNSIGNED,
  p_genderCode CHAR(1),
  p_avatarUrl  VARCHAR(255),
  p_bio        VARCHAR(255)
)
  BEGIN
    UPDATE User
    SET name     = p_name,
      majorCode  = p_majorCode,
      genderCode = p_genderCode,
      avatarUrl  = p_avatarUrl,
      bio        = p_bio
    WHERE username = p_username;
  END;


CREATE PROCEDURE RemoveHobbiesWithUsername(p_username VARCHAR(20))
  BEGIN
    DELETE FROM UserHobby
    WHERE userId = (SELECT id
                    FROM User
                    WHERE username = p_username);
  END;


CREATE PROCEDURE GetUserRecs(
  p_userId INT UNSIGNED,
  p_limit  TINYINT UNSIGNED
)
  BEGIN
    SELECT
      User.id,
      User.username,
      User.name,
      User.genderCode,
      User.majorCode,
      Rank.profileScore
    FROM Rank, User
    WHERE Rank.forId = p_userId AND User.id = Rank.recId
    ORDER BY (0.5 * Rank.profileScore + 0.5 * Rank.wtfScore) DESC
    LIMIT p_limit;
  END;


CREATE PROCEDURE GetUserFollows(p_userId INT UNSIGNED)
  BEGIN
    SELECT toId
    FROM Follow
    WHERE fromId = p_userId;
  END;


CREATE PROCEDURE GetUserFollowsNames(p_userId INT UNSIGNED)
  BEGIN
    SELECT
      username,
      name
    FROM Follow, User
    WHERE fromId = p_userId AND toId = User.id;
  END;


CREATE PROCEDURE AddFollow(p_fromId INT UNSIGNED, p_toId INT UNSIGNED)
  BEGIN
    INSERT IGNORE INTO Follow VALUES (p_fromId, p_toId);
  END;


CREATE PROCEDURE RmFollow(p_fromId INT UNSIGNED, p_toId INT UNSIGNED)
  BEGIN
    DELETE FROM Follow
    WHERE fromId = p_fromId AND toId = p_toId;
  END;


CREATE PROCEDURE NextUserId()
  BEGIN
    SET @foo = (SELECT AUTO_INCREMENT
                FROM INFORMATION_SCHEMA.TABLES
                WHERE TABLE_NAME = 'User'
                LIMIT 1);
    SELECT @foo AS nextId;
  END;


/*******************************************************************************
 * EVENTS
 ******************************************************************************/


CREATE EVENT purge_tempuser
  ON SCHEDULE EVERY 30 MINUTE DO
  DELETE FROM TempUser
  WHERE unix_timestamp(now()) - unix_timestamp(createdTime) > 1800;


||

DELIMITER ;

SOURCE locations.sql;
SOURCE hobby-tags.sql;
SOURCE users.sql