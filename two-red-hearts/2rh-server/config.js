const fs = require('fs');
const constants = require('./config/constants');
const serverConfig = JSON.parse(fs.readFileSync('config/server-config.json', 'utf8'));

serverConfig.constants = constants;
module.exports = serverConfig;
