const config = require('./config');
const mysql = require('mysql');
const WebSocket = require('ws');

const db = mysql.createConnection(config.db);

db.connect((err) => {
  if (err) {
    console.error('error connecting database: ' + err.stack);
    throw err;
  } else {
    console.log('database connected');
  }
});

const servers = [];

db.query('SELECT * FROM Location').on('error', err => {
  throw err;
}).on('fields', fields => {
  // the field packets for the rows to follow
}).on('result', row => {
  // Pausing the connnection is useful if your processing involves I/O
  // db.pause();
  handleLoc(row);
  // db.resume();
}).on('end', () => {

});

function handleLoc(loc) {
  const wss = new WebSocket.Server({
    port: loc.port,
    clientTracking: true
  });

  wss.broadcast = (data) => {
    wss.clients.forEach((client) => {
      if (client.readyState === WebSocket.OPEN) {
        client.send(data);
      }
    });
  };

  wss.on('connection', ws => {
    ws.on('message', msg => {
      wss.broadcast(msg);
    });
  });

  servers.push({port: loc.port, server: wss});

  console.warn(`ws server up at port ${loc.port} for ${loc.name}`);

}


process.on('SIGINT', () => {
  console.log('shutting down...');
  db.end((err) => {
    if (err) {
      console.error('Error ending database connection');
      throw err;
    }
    console.log('database disconnected');
  });

  servers.forEach((wss) => {
    wss.server.close(() => {
      console.log(`server shutdown at port ${wss.port}`);
    });
  });
});
