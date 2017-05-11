<template>
  <div>
    <md-layout md-align="center">
      <md-card md-with-hover id="login-card">
        <md-card-header>
          <div class="md-title">Two Red Hearts @ Illinois</div>
          <div class="md-subhead">Join now to find your love on campus!</div>
          <div class="md-subhead">
            Sign up with your illinois.edu email to verify that you are part of our campus community
          </div>

        </md-card-header>
        <md-card-content>

          <form @submit.prevent novalidate>
            <md-input-container>
              <label>Username</label>
              <md-input v-model="username" maxlength="20"></md-input>
            </md-input-container>

            <md-input-container>
              <label>Password</label>
              <md-input type="password" v-model="password"  maxlength="255"></md-input>
            </md-input-container>
          </form>
        </md-card-content>

        <md-card-actions>
          <md-button class="md-raised" @click.native="signup">sign up</md-button>
          <md-button class="md-raised md-primary" @click.native="login">login</md-button>
        </md-card-actions>
      </md-card>

    </md-layout>

    <md-dialog-alert
      :md-content="alert.content"
      md-ok-text="OK"
      ref="alerterr">
    </md-dialog-alert>
  </div>
</template>

<script>
  export default {
    name: 'signin',
    data() {
      return {
        username: '',
        password: '',
        alert: {
          content: 'Error'
        }
      };
    },
    methods: {
      signup() {
        this.$router.push('/signup');
      },
      login() {
        this.$http.post(`/api/users/${this.username}`, {
          username: this.username,
          password: this.password
        }).then((res) => {
          const resObj = JSON.parse(res.bodyText);
          this.$store.commit('loadApiKey', resObj.token);
          this.$http.get(`/api/users/${this.username}`).then((res) => {
            this.$store.commit('loadUser', JSON.parse(res.bodyText));
            this.$router.push('/home');
          }, (err) => {
            this.alert.content = `Error: ${err.bodyText}`;
            this.openDialog('alerterr');
          });
        }, (err) => {
          this.alert.content = `Error: ${err.bodyText}`;
          this.openDialog('alerterr');
        });
      },
      openDialog(ref) {
        this.$refs[ref].open();
      },
      closeDialog(ref) {
        this.$refs[ref].close();
      }
    },
    beforeCreate () {
      if (this.$store.getters.apikey) {
        this.$router.push({name:'home'});
      }
    }
  };

</script>

<style scoped>
  #login-card {
    margin-top: 100px;
    margin-bottom: 100px;
    max-width: 500px;
    width: 100%;
    cursor: default
  }
</style>
