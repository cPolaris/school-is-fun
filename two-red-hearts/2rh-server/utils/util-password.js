/*
 https://ciphertrick.com/2016/01/18/salt-hash-passwords-using-nodejs-crypto/
 */
const crypto = require('crypto');

function randStr(length) {
  return crypto.randomBytes(Math.ceil(length / 2)).toString('hex').slice(0, length);
}

function hmacsha512(password, salt) {
  return crypto.createHmac('sha512', salt).update(password);
}

function sha256(str) {
  return crypto.createHash('sha256').update(str);
}

const pwutil = {
  generate(password) {
    const randSalt = randStr(16);  // 16 hex, that is 8 bytes
    const hashData = hmacsha512(password, randSalt);
    return {
      hash: hashData.digest('hex'),
      salt: randSalt
    }
  },
  validate(password, salt, pwhash) {
    const hashData = hmacsha512(password, salt);
    return hashData.digest('hex') === pwhash;
  },
  genToken () {
    return crypto.randomBytes(32).toString('hex');  // 64 hex
  },
  sha256
};

module.exports = pwutil;
