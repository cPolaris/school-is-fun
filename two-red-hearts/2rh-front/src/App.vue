<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>
  export default {
    name: 'app'
  };
</script>

<style>
  html, body {
    margin: 0;
  }

  body {
    background: url("/static/index-image.jpg") no-repeat center;
    background-size: cover;
  }
</style>
