<template>
  <div>
    <md-layout md-align="center">
      <md-card id="result-card">
        <md-card-header>
          <md-card-header-text>
            <div class="md-title">Recommendations</div>
            <div class="md-subhead">Based on your profile and the users you like, here's a list of more users you might be interested in. The list will change as you and other users modify your profile and like-relationships. This list is not refreshed in real-time, though.</div>
          </md-card-header-text>
        </md-card-header>

        <md-card-content>
          <md-table id="result-table">
            <md-table-header>
              <md-table-row>
                <md-table-head>Name</md-table-head>
                <md-table-head>Gender</md-table-head>
                <md-table-head>Major</md-table-head>
                <md-table-head>Similarity Score</md-table-head>
                <md-table-head></md-table-head>
              </md-table-row>
            </md-table-header>

            <md-table-body>
              <md-table-row v-for="row in result" :key="row.id">
                <md-table-cell>{{ row.name }}</md-table-cell>
                <md-table-cell>{{ row.gender }}</md-table-cell>
                <md-table-cell>{{ row.major }}</md-table-cell>
                <md-table-cell>{{ row.profileScore }}</md-table-cell>
                <md-table-cell><router-link :to="{name:'profile', params: { username: row.username }}">see profile</router-link></md-table-cell>
              </md-table-row>
            </md-table-body>
          </md-table>
        </md-card-content>
      </md-card>

    </md-layout>
  </div>
</template>


<script>
  export default {
    name: 'search',
    data() {
      return {
        name: '',
        genderCode: '',
        majorCode: '',
        tagboxes: Array(17).fill(false),
        result: []
      };
    },
    methods: {},
    beforeCreate () {
      if (!this.$store.getters.apikey) {
        this.$router.replace('/');
      }
    },
    mounted () {
      const forId = this.$store.getters.userProfile.id;

      this.$http.get(`/api/users/rec/${forId}`).then((res) => {
        const resObj = JSON.parse(res.bodyText);
        console.log(resObj);
        for (let row of resObj) {
          this.result.push(row);
        }
      }, (err) => {
        alert(err.bodyText);
      });
    }
  };
</script>


<style scoped>
  #result-card {
    max-width: 800px;
    cursor: default;
    margin-top: 20px;
    margin-bottom: 50px;
  }

  #result-table {
    padding: 5px;
  }
</style>
