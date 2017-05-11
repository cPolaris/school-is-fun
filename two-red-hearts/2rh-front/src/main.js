// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue';
import VueMaterial from 'vue-material';
import VueResource from 'vue-resource';
import App from './App.vue';
import router from './router';
import store from './store';

Vue.use(VueMaterial);
Vue.use(VueResource);

// watch requests and set authorization header for all same-origin API requests
Vue.http.interceptors.push(function(request, next) {
  // request:
  // body, headers, method, params, url
  const storedKey = this.$store.getters.apikey;

  if (request.url.indexOf('/api/') === 0 && storedKey) {
    request.headers.set('Golden-Ticket', storedKey);
  }

  console.log(`  ----------begin http request-----------
  using key ${storedKey}
  ${request.method} ${request.url}
  ${JSON.stringify(request.body)}
  ---------------------------------------`);
  next();
});

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  template: '<App/>',
  components: {App},
});
