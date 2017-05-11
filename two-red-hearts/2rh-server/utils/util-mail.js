const mustache = require('mustache');
const fs = require('fs');
const spawn = require('child_process').spawn;

const config = require('../config');

// mail -a "From: no-reply@example.com" -a "Content-type: text/html" -s "Subject Line" receiver@foo.bar < content.html
function sendRegister(emailAddress, link) {
  const emailTemplate = fs.readFileSync(config.registerMailTemplatePath, 'utf8');
  const emailRendered = mustache.render(emailTemplate, {link});
  const mail = spawn('mail', [
    '-a', 'From: no-reply@2rh.date',
    '-a', 'Content-type: text/html',
    '-s', '2rh@illinois Registration',
    emailAddress]);

  mail.stdin.end(emailRendered);
  mail.stdin.on('finish', () => {
    console.warn('mail: write finished');
  });

  mail.stdout.on('data', data => {
    console.log(`mail: ${data}`);
  });

  mail.stderr.on('data', data => {
    console.log(`mail: ${data}`);
  });

  mail.on('close', code => {
    if (code === 0) {
      console.log(`sendRegister succeeded: ${code}`);
    } else {
      console.error(`sendRegister FAILED: ${code}`);
    }
  });
}

function sendAdmin(str) {
  const mail = spawn('mail', [
    '-a', 'From: no-reply@2rh.date',
    '-a', 'Content-type: text/plain',
    '-s', '2RH FATAL ERROR MESSAGE',
    config.ADMIN_EMAIL]);

  mail.stdin.end(str);

  mail.stdin.on('finish', () => {
    console.warn('mail: write finished');
  });

  mail.stdout.on('data', data => {
    console.log(`mail: ${data}`);
  });

  mail.stderr.on('data', data => {
    console.log(`mail: ${data}`);
  });

  mail.on('close', code => {
    if (code === 0) {
      console.log(`sendAdmin success: ${code}`);
    } else {
      console.error(`sendAdmin fail: ${code}`);
    }
  });
}

module.exports = { sendRegister, sendAdmin };
