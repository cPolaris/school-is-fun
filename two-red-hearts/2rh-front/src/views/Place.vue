<template>
  <div>
    <div id="google-map"></div>
    <md-layout md-align="center">
      <md-card id="place-card">
        <md-progress md-indeterminate v-if="working"></md-progress>
        <md-card-header>
          <md-card-header-text>
            <div class="md-title"><span>Locations</span>
              <md-button @click.native="startTracking" id="appear-btn" class="md-raised md-warn"
                         :disabled="disableStart">start
              </md-button>
            </div>
            <div class="md-subhead">
              We can stream position of users at the following locations! Hit start button to locate yourself and join other people around you!
            </div>
          </md-card-header-text>
        </md-card-header>

        <md-card-content>
          <md-table id="result-table">
            <md-table-header>
              <md-table-row>
                <md-table-head>Name</md-table-head>
                <md-table-head>Visitors</md-table-head>
                <md-table-head></md-table-head>
              </md-table-row>
            </md-table-header>

            <md-table-body>
              <md-table-row v-for="row in locs" :key="row.id">
                <md-table-cell>{{ row.name }}</md-table-cell>
                <md-table-cell>{{ row.visitors }}</md-table-cell>
              </md-table-row>
            </md-table-body>
          </md-table>
        </md-card-content>
      </md-card>
    </md-layout>

    <md-dialog-alert
      md-ok-text="OK"
      :md-content="alerttext"
      ref="alerterr">
    </md-dialog-alert>
  </div>
</template>
<script>
  const geoOptions = {
    enableHighAccuracy: false
  };

  export default {
    name: 'place',
    data() {
      return {
        currLoc: {
          lat: 0,
          lng: 0,
          name: ''
        },
        currUserId: '',
        currName: '',
        alerttext: 'Error',
        markers: [],
        mapInstance: null,
        heatMapLayer: null,
        ws: null,
        working: false,
        disableStart: false
      };
    },
    computed: {
      locs() {
        return this.$store.getters.locs;
      }
    },
    methods: {
      broadcastPosition() {
        navigator.geolocation.getCurrentPosition(pos => {
          const payload = {
            userId: this.currUserId,
            name: this.currName,
            lat: pos.coords.latitude,
            lng: pos.coords.longitude
          };

          console.log(payload);
          this.ws.send(JSON.stringify(payload));
        }, err => {
          this.alerttext = `Error: ${err}`;
          this.openDialog('alerterr');

          this.working = false;
          this.disableStart = false;
        }, geoOptions);
      },
      startTracking() {
        this.currUserId = this.$store.getters.userProfile.id;
        this.currName = this.$store.getters.userProfile.name;

        this.disableStart = true;
        this.working = true;

        navigator.geolocation.getCurrentPosition(pos => {
          this.currLoc.lat = pos.coords.latitude;
          this.currLoc.lng = pos.coords.longitude;

          this.$http.post('/api/locs', {
            x: this.currLoc.lat,
            y: this.currLoc.lng
          }).then(res => {
            // 0 = global, 1 = alma mater
            let locId = JSON.parse(res.bodyText).locId;
            let wsport = 0;
            let easterEgg = false;

            // @EasterEgg
            if (locId === 1) {
              this.alerttext = `Congrats! You've found our Easter Egg!\nHail to the Orange.\nHail to the Blue.\nHail Alma Mater,\nEver so true.\nWe love no other,\nSo let our motto be\nVictory, Illinois, Varsity.`;
              this.openDialog('alerterr');

              this.currLoc.name = 'Alma Mater';
              easterEgg = true;
              this.mapInstance.setZoom(19);
            } else if (locId === 0) {
              this.mapInstance.setZoom(15);
              locId = 1;
            } else {
              this.mapInstance.setZoom(18);
            }

            for (let locInfo of this.locs) {
              if (locInfo.id === locId) {

                wsport = locInfo.port;

                if (easterEgg) {
                  this.currLoc.lat = locInfo.lat;
                  this.currLoc.lng = locInfo.lng;
                } else {
                  this.currLoc.name = locInfo.name;
                }

                if (locId !== 1) {
                  // replace with predefined center
                  this.currLoc.lat = locInfo.lat;
                  this.currLoc.lng = locInfo.lng;
                }

                const currHost = window.location.hostname;
                this.ws = new WebSocket(`wss://${currHost}/ws/${wsport}`);
                // this.ws = new WebSocket(`ws://${currHost}:${wsport}`);
                break;
              }
            }

            this.ws.onmessage = (event) => {
              const markerInfo = JSON.parse(event.data);
              const userId = markerInfo.userId;
              let updated = false;

              for (let marker of this.markers) {
                if (marker.userId === userId) {
                  marker.setPosition({lat: markerInfo.lat, lng: markerInfo.lng});
                  updated = true;
                }
              }

              if (!updated) {
                const newMarker = new google.maps.Marker({
                  position: {lat: markerInfo.lat, lng: markerInfo.lng},
                  map: this.mapInstance,
                  label: markerInfo.name
                });

                newMarker.userId = userId;
                this.markers.push(newMarker);
              }
            };

            this.ws.onopen = (evt) => {
              const payload = {
                userId: this.currUserId,
                name: this.currName,
                lat: pos.coords.latitude,
                lng: pos.coords.longitude
              };

              console.log(payload);
              this.broadcastPosition();
              setInterval(this.broadcastPosition, 5500);
            }

            this.mapInstance.setCenter({lat: this.currLoc.lat, lng: this.currLoc.lng});
            this.toggleHeatMapLayer();

          }, err => {
            this.alerttext = `Error: ${err.bodyText}`;
            this.openDialog('alerterr');
          });
        }, err => {
          this.alerttext = `Error: ${err}`;
          this.openDialog('alerterr');

          this.working = false;
          this.disableStart = false;
        }, geoOptions);

      },
      toggleHeatMapLayer() {
        this.heatMapLayer.setMap(this.heatMapLayer.getMap() ? null : this.mapInstance);
      },
      openDialog(ref) {
        this.$refs[ref].open();
      },
      closeDialog(ref) {
        this.$refs[ref].close();
      }
    },
    beforeCreate () {
      if (!this.$store.getters.apikey) {
        this.$router.replace('/');
      }
    },
    mounted () {
      const defaultLoc = this.$store.state.defaultLocation;
      this.currLoc.lat = defaultLoc.center.lat;
      this.currLoc.lng = defaultLoc.center.lng;

      this.mapInstance = new google.maps.Map(document.getElementById('google-map'), {
        center: defaultLoc.center,
        zoom: defaultLoc.zoom,
        disableDefaultUI: true,
        gestureHandling: 'cooperative'
      });

      this.$http.get('/api/locs').then(res => {
        const result = JSON.parse(res.bodyText);
        this.$store.commit('loadLocs', result);

        const heatmapData = [];
        const totalLocations = result.length;

        let totalVisitors = 0;

        for (let row of result) {
          totalVisitors += row.visitors;

          let markerWeight = row.visitors;
          if (row.id === 1) {
            markerWeight = 0;
          }

          heatmapData.push({
            location: new google.maps.LatLng(row.lat, row.lng),
            weight: markerWeight
          });
        }

        this.heatMapLayer = new google.maps.visualization.HeatmapLayer({
          data: heatmapData,
          dissipating: true,
          radius: 45,
          maxIntensity: totalVisitors / totalLocations,
          opacity: 0.7,
          map: this.mapInstance
        });
      }, err => {
        this.alerttext = `Error: ${err.bodyText}`;
        this.openDialog('alerterr');
      });
    }
  };
</script>
<style scoped>
  #google-map {
    height: 70vh;
    width: 100vw;
    margin: auto;
  }

  #appear-btn {
    float: right;
  }

  #place-card {
    min-width: 500px;
    max-width: 650px;
  }
</style>
