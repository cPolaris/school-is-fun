<template>
  <div>
    <md-layout md-align="center" row="true" md-gutter id="center-layout">

      <md-layout md-align="center">

        <md-card class="button-card">

          <md-card-header>
            <div class="md-title">Search</div>
          </md-card-header>

          <md-card-content>
            <router-link to="/search" class="card-click">
              <md-icon class="md-size-4x">search</md-icon>
            </router-link>
          </md-card-content>

        </md-card>

      </md-layout>

      <md-layout md-align="center">
        <md-card class="button-card">

          <md-card-header>
            <div class="md-title">Recommend</div>
          </md-card-header>

          <md-card-content>
            <router-link to="/rec" class="card-click">
              <md-icon class="md-size-4x">lightbulb_outline</md-icon>
            </router-link>
          </md-card-content>
        </md-card>
      </md-layout>

      <md-layout md-align="center">
        <md-card class="button-card">

          <md-card-header>
            <div class="md-title">Place</div>
          </md-card-header>

          <md-card-content>
            <router-link to="/place" class="card-click">
              <md-icon class="md-size-4x">near_me</md-icon>
            </router-link>
          </md-card-content>
        </md-card>
      </md-layout>

      <md-layout md-align="center">
        <md-card class="button-card">

          <md-card-header>
            <div class="md-title">Profile</div>
          </md-card-header>

          <md-card-content>
            <router-link :to="{name:'profile', params: { username: currUsername }}" class="card-click">
              <md-icon class="md-size-4x">account_box</md-icon>
            </router-link>
          </md-card-content>
        </md-card>
      </md-layout>
    </md-layout>

    <md-layout md-align="center">
      <md-table-card id="follow-card">

        <md-toolbar>
          <h1 class="md-title">Your Likes</h1>
        </md-toolbar>

        <md-table id="follow-table">
          <md-table-body>
            <md-table-row v-for="row in likes" :key="row.name">
              <router-link :to="{name:'profile', params: { username: row.username }}">
                <md-table-cell>{{ row.name }}</md-table-cell>
              </router-link>
            </md-table-row>
          </md-table-body>
        </md-table>
      </md-table-card>
    </md-layout>
  </div>
</template>


<script>
  export default {
    name: 'userhome',
    data() {
      return {
        likes: []
      }
    },
    computed: {
      currUsername () {
        return this.$store.getters.userProfile.username;
      }
    },
    methods: {},
    beforeCreate () {
      if (!this.$store.getters.apikey) {
        this.$router.replace('/');
      }
    },
    mounted () {
      const storedLikes = this.$store.getters.likes;
      if (storedLikes.length > 0) {
        for (let row of storedLikes) {
          this.likes.push(row);
        }

        console.log(`reusing ${storedLikes.length} likes record`);
      } else {
        const forId = this.$store.getters.userProfile.id;

        this.$http.get(`/api/users/follow/${forId}`).then(res => {
          const resObj = JSON.parse(res.bodyText);
          for (let row of resObj) {
            this.likes.push(row);
          }

          this.$store.commit('loadLikes', this.likes);

        }, err => {
          alert(err.bodyText);
        });
      }
    }
  };
</script>


<style scoped>
  #center-layout {
    padding-top: 150px;
    max-width: 800px;
    margin: auto;
  }

  .button-card {
    width: 200px;
    height: 200px;
  }

  a.card-click {
    height: 100%;
    width: 100%;
    display: block;
    text-align: center;
  }

  #follow-card {
    min-width: 400px;
    max-width: 800px;
    cursor: default;
    margin-top: 20px;
    margin-bottom: 50px;
  }

  #follow-table {
    padding: 5px;
  }

  .router-link-active {
    display: block;
    width: 100%;
    height: 100%;
  }

  .md-table-cell {
    width: 100%;
    display: block;
  }
</style>
