import Vue from 'vue';
import Vuex from 'vuex';
import majors from './majors';

const hobbyCount = 17;
const genderNames = {
  'f': 'female',
  'm': 'male',
  '9': 'other'
};

Vue.use(Vuex);

const state = {
  apikey: null,
  user: {
    id: '',
    username: '',
    name: '',
    gender: '',
    genderCode: '',
    major: '',
    bio: '',
    hobbyTags: new Array(hobbyCount).fill(false),
    avatarUrl: ''
  },
  likes: [],  // contains objects like { name:'foo', username: 'bar }
  locs: [],   // { id name lat lng visitors port }
  defaultLocation: {
    center: {lat: 40.108351, lng: -88.227186},
    zoom: 14
  }
};

export default new Vuex.Store({
  state,
  getters: {
    apikey: state => {
      return state.apikey;
    },

    userProfile: state => {
      return state.user;
    },

    likes: state => {
      return state.likes;
    },

    locs: state => {
      return state.locs;
    },

    majorCodeFromName: state => majorName => {
      return Object.keys(majors).find(mcode => majors[mcode] === majorName);
    },

    genderNameFromCode: state => genderCode => {
      return genderNames[genderCode];
    },

    currLocation: state => {
      return state.currLoc;
    }
  },
  mutations: {
    loadUser (state, newProfile) {
      state.user.id = newProfile.id;
      state.user.username = newProfile.username;
      state.user.name = newProfile.name;
      state.user.gender = newProfile.gender;
      state.user.genderCode = newProfile.genderCode;
      state.user.major = newProfile.major;
      state.user.bio = newProfile.bio;
      state.user.hobbyTags = newProfile.hobbyTags;
      state.user.avatarUrl = newProfile.avatarUrl;

      console.warn(`COMMIT userProfile: ${JSON.stringify(state.user)}`);
    },
    loadApiKey (state, key) {
      state.apikey = key;

      console.warn(`COMMIT apiKey: ${state.apikey}`);
    },
    loadLikes (state, likesArr) {
      state.likes.length = 0;

      for (let row of likesArr) {
        state.likes.push(row);
      }

      console.warn(`COMMIT likes: ${state.likes}`);
    },
    appendLikes (state, entry) {
      state.likes.push(entry);
      console.warn(`COMMIT likes: ${state.likes}`);
    },
    rmLikes (state, rmUsername) {
      const likesArr = state.likes;

      for (let i = 0; i < likesArr.length; i++) {
        if (likesArr[i].username === rmUsername) {
          state.likes.splice(i, 1);
          break;
        }
      }
      console.warn(`COMMIT likes: ${state.likes}`);
    },
    loadLocs (state, locsArr) {
      state.locs.length = 0;

      for (let row of locsArr) {
        state.locs.push(row);
      }

      console.warn(`COMMIT locs: ${state.locs}`);
    },
  }
});
