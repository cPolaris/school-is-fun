const WebSocket = require('ws');
const http = require('http');
// const express = require('express');

// const app = express();
// app.use(express.static('public'));

const wss = new WebSocket.Server({
  port: 2000,
  clientTracking: true
});

let clientCount = 0;

// wss.broadcast = (data) => {
//   wss.clients.forEach((client) => {
//     if (client.readyState === WebSocket.OPEN) {
//       client.send(data);
//     }
//   });
// };

wss.on('connection', function (ws) {
  console.log('connect');

  ws.on('message', (msg) => {
    console.log(msg);
  });

  ws.on('close', function () {
    console.log('closing');
  });
});

// app.listen(2000);