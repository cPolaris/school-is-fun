const illinoisEmailPattern = new RegExp(/^[a-z0-9]{1,20}@illinois\.edu$/);
const alphaNumericPattern = new RegExp(/^[a-zA-Z0-9_]+$/);
const emailPattern = new RegExp(/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/);
const genderPattern = new RegExp(/^[omf]$/);
const hexStrPattern = new RegExp(/^[0-9a-z]+$/);
const avatarUrlPattern = new RegExp(/\/static\/avatar\/[a-zA-Z0-9]+\.jpg/);

module.exports = {
  username(str) {
    return typeof(str) === 'string'
      && str.length < 20
      && alphaNumericPattern.test(str);
  },
  password(str) {
    return typeof(str) === 'string'
      && str.length < 255;
  },
  email(str) {
    return typeof(str) === 'string'
      && str.length < 30
      && emailPattern.test(str);
  },
  name(str) {
    return typeof(str) === 'string'
      && str.length < 50;
  },
  bio(str) {
    return typeof(str) === 'string'
      && str.length < 255;
  },
  majorCode(num) {
    return typeof(num) === 'number'
      && num >= 0 && num <= 255;
  },
  genderCode(str) {
    return typeof(str) === 'string'
      && genderPattern.test(str);
  },
  token(str) {
    return typeof(str) === 'string'
      && str.length === 64
      && hexStrPattern.test(str);
  },
  regCred(str) {
    return this.token(str);
  },
  latitude(val) {
    return typeof(val) === 'number'
      && val >= -90.0 && val <= 90.0;
  },
  longitude(val) {
    return typeof(val) === 'number'
      && val >= -180.0 && val <= 180.0;
  },
  id(num) {
    return typeof(num) === 'number'
      && Number.isInteger(num)
      && num >= 1 && num <= 4294967295;
  },
  avatarUrl(url) {
    return typeof(url) === 'string'
      && avatarUrlPattern.test(url);
  },
  noDupNumArray(arr) {
    return Array.isArray(arr)
      && arr.length === new Set(arr).size;
  }
};
