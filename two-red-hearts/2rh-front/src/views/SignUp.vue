<template>
  <div>
    <md-layout md-align="center">
      <md-card md-with-hover id="additional-card">
        <md-card-header>
          <div class="md-title">Thank you for signing up!</div>
          <div class="md-subhead">
            Your email will be used to verify your identity. You will receive a registration link valid for 30 minutes. We will not use your email address for any other purposes.
          </div>
        </md-card-header>
        <md-card-content>
          <form @submit.stop.prevent>

            <md-input-container>
              <label>Email</label>
              <md-input v-model="email" type="email" maxlength="30"></md-input>
            </md-input-container>

            <md-input-container>
              <label>Username</label>
              <md-input v-model="username" maxlength="20"></md-input>
            </md-input-container>

            <md-input-container>
              <label>Password</label>
              <md-input v-model="password" type="password" maxlength="255"></md-input>
            </md-input-container>
          </form>

          <md-card-actions>
            <md-button class="md-raised md-primary" @click.native="signup">Sign Up</md-button>
          </md-card-actions>
        </md-card-content>
      </md-card>
    </md-layout>

    <md-dialog-alert
      md-ok-text="OK"
      :md-content="alerttext"
      ref="alerterr">
    </md-dialog-alert>

    <md-dialog-alert
      md-ok-text="OK"
      :md-content="alerttext"
      @close="onRegSucClose"
      ref="alertsuc">
    </md-dialog-alert>
  </div>
</template>

<script>
  export default {
    name: 'signup',
    data() {
      return {
        alerttext: 'Error',
        email: '',
        username: '',
        password: ''
      };
    },
    methods: {
      signup() {
        this.$http.post('/api/usersreg', {
          username: this.username,
          email: this.email,
          password: this.password
        }).then((res) => {
          this.alerttext = `Done! Please check your email. The registration link is valid for 30 minutes`;
          this.openDialog('alertsuc');
        }, (err) => {
          this.alerttext = `Error: ${err.bodyText}`;
          this.openDialog('alerterr');
        });
      },
      openDialog(ref) {
        this.$refs[ref].open();
      },
      closeDialog(ref) {
        this.$refs[ref].close();
      },
      onRegSucClose() {
        this.$router.push('/');
      }
    }
  };
</script>

<style>
  #additional-card {
    margin-top: 100px;
    margin-bottom: 100px;
    max-width: 650px;
    width: 100%;
    cursor: default
  }
</style>
