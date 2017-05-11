import Vue from 'vue';
import Router from 'vue-router';

import Place from 'views/Place';
import Search from 'views/Search';
import UserHome from 'views/UserHome';
import UserProfile from 'views/UserProfile';
import SignIn from 'views/SignIn';
import SignUp from 'views/SignUp';
import RegValidate from 'views/RegValidate';
import Rec from 'views/Rec';

Vue.use(Router);

export default new Router({
  routes: [
    {
      path: '/',
      component: SignIn,
    },
    {
      path: '/signup',
      component: SignUp
    },
    {
      path: '/place',
      component: Place
    },
    {
      path: '/home',
      name: 'home',
      component: UserHome
    },
    {
      path: '/search',
      component: Search
    },
    {
      path: '/rec',
      component: Rec
    },
    {
      path: '/profile/:username',
      name: 'profile',
      component: UserProfile
    },
    {
      path: '/usersregv/:cred',
      component: RegValidate
    },
    {
      path: '*',
      redirect: '/'
    }
  ]
  // mode: 'history' // back to this bitch later
});
