# 2RedHearts-Front

CS 411 at UIUC

## Build Setup

``` bash
# install dependencies
npm install

# serve with hot reload at localhost:8080
npm run dev

# build for production with minification
npm run build

# build for production and view the bundle analyzer report
npm run build --report

# run unit tests
npm run unit

# run e2e tests
npm run e2e

# run all tests
npm test
```

## Development

In [build/dev-server.js](build/dev-server.js) you can set up to proxy API traffic to the development server. Currently all `/api` endpoints are proxied to `localhost:3000`. 

## Deployment

You can specify a relative path in the file build/deploy.js by changing the value of the variable `deployDirectoryRel`. You must have already set up a git repository in the directory by making an initial commit and set up the upstream.

Then you can just:

``` bash
npm run deploy
```

to build static files and then push latest changes to remote in one command. Look into build/deploy.js for more details.

For detailed explanation on how things work, checkout the [guide](http://vuejs-templates.github.io/webpack/) and [docs for vue-loader](http://vuejs.github.io/vue-loader).
