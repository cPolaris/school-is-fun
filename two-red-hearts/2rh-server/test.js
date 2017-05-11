const mysql = require('mysql');
const config = require('./config');
const db = mysql.createConnection(config.db);

db.connect((err) => {
  if (err) {
    console.error('error connecting database: ' + err.stack);
    throw err;
  } else {
    console.log('database connected');
  }
});

const sName = '';
const sMajorCode = '102';
const sGenderCode = 'f';

const searchResult = [];

// stream all user rows
db.query('SELECT * FROM User').on('error', err => {
  throw err;
}).on('fields', fields => {
  // the field packets for the rows to follow
}).on('result', row => {
  console.log(row);
}).on('end', () => {
  console.log(searchResult);
});

db.end();

// const express = require('express');
// const morgan = require('morgan');
// const multer = require('multer');
//
// const app = express();
//
// const avatarStorage = multer.diskStorage({
//   destination: function (req, file, cb) {
//     cb(null, 'public/static/avatar')
//   },
//   filename: function (req, file, cb) {
//     console.log(file);
//     cb(null, file.fieldname + '-' + Date.now() + '.jpg')
//   }
// });
//
// const avatarUploadMiddleware = multer({
//   storage: avatarStorage,
//   fileFilter(req, file, cb) {
//     cb(null, file.mimetype === 'image/jpeg');
//   },
//   limits: {
//     fileSize: 512 * 1024
//   }
// }).single('avatar');
//
// app.use(morgan('dev'));
//
// app.post('/upload', avatarUploadMiddleware, (req, res) => {
//   console.log(req.file);
//   res.send('thanks!');
// });
//
// app.use('*', (err, req, res, next) => {
//   res.send('INTERNAL SERVER ERROR');
// });
//
// app.use(express.static('testing'));
//
// app.listen(4321);


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