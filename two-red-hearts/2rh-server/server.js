const express = require('express');             // server
const morgan = require('morgan');               // request logging
const cookieParser = require('cookie-parser');  // http parsing
const bodyParser = require('body-parser');      // http parsing
const multer = require('multer');               // handle form upload
const spawn = require('child_process').spawn;   // run other programs

const mysql = require('mysql');
const fs = require('fs');
const pwutil = require('./utils/util-password');
const schemaValidate = require('./utils/util-validation');
const mailutil = require('./utils/util-mail');

const config = require('./config');             // configuration file

/*******************************************************************************
 * SET UP
 *******************************************************************************/

// set up database
const db = mysql.createConnection(config.db);

db.connect((err) => {
  if (err) {
    console.error('error connecting database: ' + err.stack);
    throw err;
  } else {
    console.log('database connected');
  }
});

/*******************************************************************************
 * FILE UPLOAD SERVICE
 *******************************************************************************/

// set up file upload with multer
const avatarStorage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, 'public/static/avatar')
  },
  filename: function (req, file, cb) {
    const randPrefix = pwutil.sha256(file.originalname).digest().toString('hex').slice(0, 20);
    cb(null, `${randPrefix}${Date.now()}.jpg`)
  }
});

const avatarUploadMiddleware = multer({
  storage: avatarStorage,
  fileFilter(req, file, cb) {
    cb(null, file.mimetype === 'image/jpeg');
  },
  limits: {
    fileSize: config.maxAvatarSize
  }
}).single('avatar');

const uploadApp = express();
uploadApp.disable('x-powered-by');
uploadApp.disable('ETag');

uploadApp.all('*', (req, res, next) => {
  const token = req.get('Golden-Ticket');

  if (token && schemaValidate.token(token)) {
    db.query('CALL GetUserWithToken(?)', [token], (err, rslt, fids) => {
      const user = rslt[0][0];

      if (user && user.token === token) {
        req.userProfile = user;
        next();
      } else {
        console.warn(`rejected ip: ${req.ip}`);
        res.status(403).end('unauthorized');
      }
    });
  } else {
    console.warn(`rejected ip: ${req.ip}`);
    res.status(403).end('unauthorized');
  }
});

uploadApp.post('/', avatarUploadMiddleware, (req, res, next) => {
  if (req.file) {

  } else {
    res.status(400).send('schema');
  }
});

/*******************************************************************************
 * API - NO AUTH
 *******************************************************************************/

const app = express();
app.disable('x-powered-by');
app.disable('ETag');
app.use(cookieParser());
app.use(bodyParser.json({
  limit: config.maxHttpBodySize
}));

app.post('/ping', (req, res) => {
  res.json(req.body);
});

// validate registration
app.post('/usersregv', (req, res, next) => {

  const credential = req.body.cred;
  const name = req.body.name;
  const majorCode = req.body.majorCode;
  const genderCode = req.body.genderCode;
  const hobbyIds = req.body.hobbyIds;
  const bio = req.body.bio;
  const avatarUrl = config.DEFAULT_AVATAR_URL;  // @TODO handle avatar upload

  if (schemaValidate.regCred(credential)
    && schemaValidate.name(name)
    && schemaValidate.majorCode(majorCode)
    && schemaValidate.genderCode(genderCode)
    && schemaValidate.noDupNumArray(hobbyIds)
    && schemaValidate.bio(bio)
    && schemaValidate.avatarUrl(avatarUrl)
  ) {
    db.query('CALL GetTempUser(?)', [credential], (err, rslt, fids) => {
      if (err) next(err);
      const tempProfile = rslt[0][0];

      if (tempProfile) {
        db.query('CALL NextUserId()', (err2, rslt2, fids2) => {
          if (err2) next(err2);
          const newUserId = rslt2[0][0].nextId;
          const newToken = pwutil.genToken();

          db.query('CALL LiftTempUser(?,?,?,?,?,?,?,?,?,?)',
            [tempProfile.email,       //p_email
              tempProfile.salt,       //p_salt
              tempProfile.hash,       //p_hash
              newToken,               //p_token
              tempProfile.username,   //p_username
              name,                   //p_name
              majorCode,              //p_major
              genderCode,             //p_gender
              bio,                    //p_bio
              avatarUrl],             //p_avatarUrl
            (err3, rslt2, fids2) => {
              if (err3) next(err3);
              res.json({
                newToken,
                username: tempProfile.username
              });
            });

          let tagsToFill = hobbyIds.length;

          if (tagsToFill === 0) {
            spawnToCf();
          }

          hobbyIds.forEach((hobbyTagId) => {
            db.query('CALL AddUserHobby(?,?)', [newUserId, hobbyTagId], (err4, rslt4, fids4) => {
              if (err4) {
                console.error(`AddUserHobby Error: ${err4}`);
              }

              tagsToFill--;
              if (tagsToFill === 0) {  // spawn child to do content_filter
                spawnToCf();
              }
            });
          });
        });
      } else {
        res.status(400).send('invalid');
      }
    });
  } else {
    res.status(400).send('schema');
  }
});

// Register a user
app.post('/usersreg', (req, res, next) => {

  const username = req.body.username;
  const password = req.body.password;
  const email = req.body.email;

  if (!(schemaValidate.email(email)  // first check schema
    && schemaValidate.username(username)
    && schemaValidate.password(password))) {

    res.status(400).send('schema');

  } else {
    // then check duplication
    db.query('CALL CanRegisterWith(?,?)', [username, email], (err, rslt, fids) => {
      if (err) next(err);

      if (rslt[0][0].e) {
        res.status(400).send('duplicate');
      } else {
        // create new record
        const newCred = pwutil.sha256(email).digest('hex');
        const hashData = pwutil.generate(password);

        db.query('CALL AddTempUser(?,?,?,?,?)',
          [newCred, email, hashData.salt, hashData.hash, username],
          (err2, rslt2, fids2) => {
            if (err2) next(err2);

            if (config.emailEnabled) {
              mailutil.sendRegister(email, config.REGISTER_URL_BASE + newCred);
            }

            res.json({msg: config.OK});
            console.warn('new TempUser');
          });
      }
    });
  }
});

// login
app.post('/users/:username', (req, res, next) => {
  const username = req.params.username;
  const password = req.body.password;

  if (!(schemaValidate.username(username)
    && schemaValidate.password(password))) {
    res.status(400).send('schema');
  } else {
    db.query('CALL GetUser(?)', [username], (err, rslt, fids) => {
      if (err) next(err);

      const userProfile = rslt[0][0];

      if (userProfile) {
        if (pwutil.validate(password, userProfile.salt, userProfile.hash)) {
          res.json({
            token: userProfile.token
          });
        } else {
          res.status(400).send('credential');
        }
      } else {
        res.status(400).send('not found');
      }
    });
  }
});

/*******************************************************************************
 * API - AUTH REQUIRED
 *******************************************************************************/

app.all('*', (req, res, next) => {
  const token = req.get('Golden-Ticket');

  if (token && schemaValidate.token(token)) {
    db.query('CALL GetUserWithToken(?)', [token], (err, rslt, fids) => {
      const user = rslt[0][0];

      if (user && user.token === token) {
        req.userProfile = user;
        next();
      } else {
        console.warn(`rejected ip: ${req.ip}`);
        res.status(403).end('unauthorized');
      }
    });
  } else {
    console.warn(`rejected ip: ${req.ip}`);
    res.status(403).end('unauthorized');
  }
});

// get user recommendations
app.get('/users/rec/:forId', (req, res, next) => {
  const forId = parseInt(req.params.forId);

  if (!(schemaValidate.id(forId))) {
    res.status(400).send('schema');
  } else {
    db.query('CALL GetUserRecs(?,?)', [forId, 20], (err, rslt, fids) => {
      if (err) next(err);
      const result = [];
      for (let row of rslt[0]) {
        result.push({
          id: row.id,
          username: row.username,
          name: row.name,
          gender: config.constants.genders[row.genderCode],
          major: config.constants.majors[row.majorCode],
          profileScore: row.profileScore
        });
      }
      res.json(result);
    });
  }
});

// get user follows
app.get('/users/follow/:fromId', (req, res, next) => {
  const fromId = parseInt(req.params.fromId);

  if (!schemaValidate.id(fromId)) {
    res.status(400).send('schema');
  } else {
    db.query('CALL GetUserFollowsNames(?)', [fromId], (err, rslt, fids) => {
      if (err) next(err);
      const result = [];
      for (let row of rslt[0]) {
        result.push({name: row.name, username: row.username});
      }
      res.json(result);
    });
  }
});

// get user profile by id
app.get('/users/id/:userId', (req, res, next) => {
  const userId = req.params.userId;

  if (!schemaValidate.id(userId)) {
    res.status(400).send('schema');
  } else {
    db.query('CALL GetUserWithId(?)', [userId], (err, rslt, fids) => {
      if (err) next(err);
      const userProfile = rslt[0][0];

      if (userProfile) {
        db.query('CALL GetHobbyTagIdsOfUser(?)', [userProfile.id], (err2, rslt2, fids2) => {
          if (err2) next(err2);

          const hobbyTags = new Array(17).fill(false);

          for (let row of rslt2[0]) {
            hobbyTags[row.hobbyTagId - 1] = true;
          }

          res.json({
            id: userProfile.id,
            username: userProfile.username,
            name: userProfile.name,
            gender: config.constants.genders[userProfile.genderCode],
            major: config.constants.majors[userProfile.majorCode],
            bio: userProfile.bio,
            hobbyTags,
            avatarUrl: userProfile.avatarUrl
          });
        });
      } else {
        res.status(400).send('not found');
      }
    });
  }
});

// get user profile by username
app.get('/users/:username', (req, res, next) => {
  const username = req.params.username;

  if (!schemaValidate.username(username)) {
    res.status(400).send('schema');
  } else {
    db.query('CALL GetUser(?)', [username], (err, rslt, fids) => {
      if (err) next(err);
      const userProfile = rslt[0][0];

      if (userProfile) {
        db.query('CALL GetHobbyTagIdsOfUser(?)', [userProfile.id], (err2, rslt2, fids2) => {
          if (err2) next(err2);

          const hobbyTags = new Array(17).fill(false);

          for (let row of rslt2[0]) {
            hobbyTags[row.hobbyTagId - 1] = true;
          }

          res.json({
            id: userProfile.id,
            username: userProfile.username,
            name: userProfile.name,
            gender: config.constants.genders[userProfile.genderCode],
            major: config.constants.majors[userProfile.majorCode],
            bio: userProfile.bio,
            hobbyTags,
            avatarUrl: userProfile.avatarUrl
          });
        });
      } else {
        res.status(400).send('not found');
      }
    });
  }
});

// follow user
app.post('/usersfollow', (req, res, next) => {
  const fromId = req.body.fromId;
  const toId = req.body.toId;

  if (!(schemaValidate.id(fromId) && schemaValidate.id(toId))) {
    res.status(400).send('schema');
  } else {
    db.query('CALL AddFollow(?,?)', [fromId, toId], (err, rslt, fids) => {
      if (err) next(err);

      res.json({msg: config.OK});
    });
  }
});

// unfollow user
app.delete('/usersfollow/:fromId-:toId', (req, res, next) => {
  const fromId = parseInt(req.params.fromId);
  const toId = parseInt(req.params.toId);

  if (!(schemaValidate.id(fromId) && schemaValidate.id(toId))) {
    res.status(400).send('schema');
  } else {
    db.query('CALL RmFollow(?,?)', [fromId, toId], (err, rslt, fids) => {
      if (err) next(err);

      res.json({msg: config.OK});
    });
  }
});

// do search
app.post('/usersearch', (req, res, next) => {
  const sName = req.body.name;
  const sGenderCode = req.body.genderCode;
  const sMajorCode = req.body.majorCode;
  const sTraits = req.body.hobbyTags;

  const searchTagArr = new Array(17).fill(0);
  let matchTraits = false;
  for (let i = 0; i < 17; i++) {
    if (sTraits[i]) {
      searchTagArr[i] = 1;
      matchTraits = true;
    }
  }

  // only perform match if we have valid input
  const matchName = schemaValidate.name(sName);
  const matchGender = schemaValidate.genderCode(sGenderCode);
  const matchMajor = schemaValidate.majorCode(sMajorCode);

  const searchResult = [];

  // some flags to cope with async database query
  // Because NodeJS is single-threaded, these flags do not suffer from
  // race conditions! Yeah!!!!!!!
  let busyCount = 0;
  let streaming = true;

  // stream all user rows
  db.query('SELECT * FROM User').on('error', err => {
    next(err);
  }).on('fields', fields => {
    // pass
  }).on('result', row => {
    let acceptRow = false;

    if (matchTraits) {
      busyCount++;

      db.query('CALL GetHobbyTagIdsOfUser(?)', [row.id], (err2, rslt2, fids2) => {

        if (err2) {
          next(err2);
        }

        doFurtherMatch();

        if (acceptRow) {
          const targetTagArr = new Array(17).fill(0);

          for (let rw of rslt2[0]) {
            targetTagArr[rw.hobbyTagId - 1] = 1;
          }

          row.similarity = cosDist(searchTagArr, targetTagArr);
          searchResult.push(filterRowFileds(row));
        }

        busyCount--;

        if (!streaming && busyCount === 0) {
          res.json(searchResult);
          // console.log(`return from inside ${searchResult.length}`);
        }
      });
    } else {
      doFurtherMatch();

      if (acceptRow) {
        searchResult.push(filterRowFileds(row));
      }
    }

    function doFurtherMatch() {
      // accept if distance smaller than half original length
      if (matchName) {
        const origLen = sName.length;
        const distance = levDist(sName, row.name);
        acceptRow = distance < origLen / 2;

        if (acceptRow) {
          row.levDist = distance;
        }
      }

      if (matchGender) {
        //noinspection EqualityComparisonWithCoercionJS
        acceptRow = row.genderCode == sGenderCode;
      }

      if (matchMajor) {
        //noinspection EqualityComparisonWithCoercionJS
        acceptRow = row.majorCode == sMajorCode;
      }
    }

    function filterRowFileds(rawRow) {
      const filtered = {};
      filtered.name = rawRow.name;
      filtered.username = rawRow.username;
      filtered.gender = config.constants.genders[rawRow.genderCode];
      filtered.major = config.constants.majors[parseInt(rawRow.majorCode)];
      filtered.similarity = rawRow.similarity;
      filtered.levDist = rawRow.levDist;
      return filtered;
    }

  }).on('end', () => {
    streaming = false;
    if (busyCount === 0) {
      res.json(searchResult);
      // console.log(`return from outside ${searchResult.length}`);
    }
  });
});

// post new apperance
app.post('/locs', (req, res, next) => {
  const xCoord = req.body.x;
  const yCoord = req.body.y;

  if (schemaValidate.latitude(xCoord) && schemaValidate.longitude(yCoord)) {
    db.query('CALL AddAppearance(?, PointFromText(?))',
      [req.userProfile.id, `POINT(${xCoord} ${yCoord})`],
      (err, rslt, fids) => {
        if (err) next(err);

        const locId = rslt[0][0].locId;

        if (locId) {
          res.json({locId});
        } else {
          res.json({locId: 0});
        }
      });
  } else {
    res.status(400).send('schema');
  }
});

// get info about all locations
app.get('/locs', (req, res, next) => {
  db.query('CALL GetAllLocInfo()', (err, rslt, fids) => {
    if (err) next(err);
    const result = rslt[0];
    res.json(result);
  });
});

// get top locations
app.get('/hotlocs', (req, res, next) => {
  db.query('CALL GetHotLocs(?)', [5], (err, rslt, fids) => {
    if (err) next(err);
    res.json(rslt[0]);
  });
});

/*******************************************************************************
 * API - AUTH - SAME PERSON REQUIRED
 *******************************************************************************/

app.all('/users/:username', (req, res, next) => {
  if (!schemaValidate.username(req.params.username)) {
    res.status(400).send('schema');
  } else {
    if (req.userProfile.username !== req.params.username) {
      console.warn(`rejected ip: ${req.ip}`);
      res.status(403).end('unauthorized');
    } else {
      next();
    }
  }
});

// replace some profile entries
app.put('/users/:username', (req, res, next) => {
  const username = req.params.username;
  const userId = req.body.id;
  const name = req.body.name;
  const genderCode = req.body.genderCode;
  const majorCode = req.body.majorCode;
  const bio = req.body.bio;
  const avatarUrl = config.DEFAULT_AVATAR_URL;  // @TODO handle avatar upload
  // const avatarUrl = req.body.avatarUrl;

  const hobbyIds = req.body.hobbyIds;

  if (!(schemaValidate.id(userId)
    && schemaValidate.username(username)
    && schemaValidate.name(name)
    && schemaValidate.genderCode(genderCode)
    && schemaValidate.majorCode(majorCode)
    && schemaValidate.bio(bio)
    && schemaValidate.noDupNumArray(hobbyIds))) {
    res.status(400).send('schema');
  } else {
    db.query('CALL ReplaceProfileWithUsername(?,?,?,?,?,?)',
      [username, name, majorCode, genderCode, avatarUrl, bio], (err, rslt, fids) => {
        if (err) next(err);

        db.query('CALL RemoveHobbiesWithUsername(?)', [username], (err2, rslt2, fids2) => {
          if (err2) next(err2);

          hobbyIds.forEach((hobbyTagId) => {
            db.query('CALL AddUserHobby(?,?)', [userId, hobbyTagId], (err3, rslt3, fids3) => {
              if (err3) {
                console.error(`AddUserHobby Error: ${err3}`);
              }
            });
          });

          res.json(config.OK);
        });
      });
  }
});

// unregister user
app.delete('/users/:username', (req, res) => {
  res.status(400).send('unsupported operation  :)');
});

/*******************************************************************************
 * API 404
 *******************************************************************************/

app.all('*', (req, res) => {
  res.status(400).send('undefined endpoint');
});

/*******************************************************************************
 * START SERVER
 *******************************************************************************/

// set up server
const baseapp = express();
baseapp.disable('x-powered-by');
baseapp.use(morgan('dev'));

baseapp.use('/api', app);
baseapp.use('/upload', uploadApp);
baseapp.use(express.static('public'));

baseapp.all('*', (req, res) => {
  res.status(404).end('Quit poking around, dude.');
});

// Catch thrown errors
baseapp.use(function (err, req, res, next) {
  console.error(err.stack);

  if (config.emailEnabled) {
    mailutil.sendAdmin(err);
  }

  res.status(500).json({error: 'internal'});
});

const server = baseapp.listen(config.serverPort, () => {
  console.log(`listening on port ${config.serverPort}`);
});

process.on('SIGINT', () => {
  if (config.emailEnabled) {
    // mailutil.sendAdmin('WARNING: SIGINT RECEIVED');
  }

  console.log('shutting down...');
  db.end((err) => {
    if (err) {
      console.error('Error ending database connection');
      throw err;
    }
    console.log('database disconnected');
    console.log('waiting for remote connections to terminate');
    server.close((err) => {
      if (err) {
        console.error('Error closing server');
        throw err;
      }
      console.log('Server close complete. お疲れ様でした!');
    });
  });
});


/**
 * Spawn a child to do content_filter
 * Typically called when a new user registers
 */
function spawnToCf() {
  const cfChild = spawn('python3', ['-W', 'ignore', config.cfPath]);
  cfChild.stdout.on('data', data => {
    console.log(`cf on register: ${data}`);
  });

  cfChild.stderr.on('data', data => {
    console.error(`cf on register: ${data}`);
  });

  cfChild.on('close', code => {
    console.warn(`content_filter on register exited with ${code}`);
  });
}

/**
 * Compute diff of two arrays of integers. n^2 is good enough
 * for our use case.
 * @param oldList
 * @param newList
 * @return {{delList: Array, addList: Array}} lists of integers
 * that should be deleted or added in order to transform oldList to newList
 */
function arrayDiff(oldList, newList) {
  const delList = oldList.filter((val) => {
    return !newList.includes(val)
  });
  const addList = newList.filter((val) => {
    return !oldList.includes(val)
  });
  return {delList, addList};
}


/**
 * The Levenshtein distance for comparing string distance,
 * coutesy of http://stackoverflow.com/questions/11919065
 * @param s
 * @param t
 * @return {*} edit distance. Smaller is closer.
 */
function levDist(s, t) {
  const d = []; //2d matrix

  // Step 1
  const n = s.length;
  const m = t.length;

  if (n === 0) return m;
  if (m === 0) return n;

  //Create an array of arrays in javascript (a descending loop is quicker)
  for (let i = n; i >= 0; i--) d[i] = [];

  // Step 2
  for (let i = n; i >= 0; i--) d[i][0] = i;
  for (let j = m; j >= 0; j--) d[0][j] = j;

  // Step 3
  for (let i = 1; i <= n; i++) {
    let s_i = s.charAt(i - 1);

    // Step 4
    for (let j = 1; j <= m; j++) {

      //Check the jagged ld total so far
      if (i === j && d[i][j] > 4) return n;

      const t_j = t.charAt(j - 1);
      const cost = (s_i === t_j) ? 0 : 1; // Step 5

      //Calculate the minimum
      let mi = d[i - 1][j] + 1;
      const b = d[i][j - 1] + 1;
      const c = d[i - 1][j - 1] + cost;

      if (b < mi) mi = b;
      if (c < mi) mi = c;

      d[i][j] = mi; // Step 6

      //Damerau transposition
      if (i > 1 && j > 1 && s_i === t.charAt(j - 2) && s.charAt(i - 2) === t_j) {
        d[i][j] = Math.min(d[i][j], d[i - 2][j - 2] + cost);
      }
    }
  }

  // Step 7
  return d[n][m];
}


/**
 * Plain and strightforward cosine distance.
 * Courtesy of http://stackoverflow.com/questions/520241
 * @param vectorA
 * @param vectorB
 * @return {number}
 */
function cosDist(vectorA, vectorB) {
  let dotProduct = 0.0;
  let normA = 0.0;
  let normB = 0.0;
  for (let i = 0; i < vectorA.length; i++) {
    dotProduct += vectorA[i] * vectorB[i];
    normA += Math.pow(vectorA[i], 2);
    normB += Math.pow(vectorB[i], 2);
  }
  let result = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
  if (isNaN(result)) result = 0.0;
  return result;
}
