<template>
  <div>
    <md-layout md-align="center" id="wrapper">
      <md-card id="profile-card">
        <md-card-header v-if="disableEdit">
          <md-card-header-text>
            <div class="md-title">{{ profile.name }}</div>
            <div class="md-subhead">{{ profile.major }}</div>
          </md-card-header-text>

          <md-button class="md-raised md-accent" @click.native="follow" v-if="disableEdit && !alreadyLiked">
            <md-icon>favorite</md-icon>
            <md-tooltip>Like</md-tooltip>
          </md-button>

          <md-button class="md-raised md-primary" @click.native="unfollow" v-if="alreadyLiked">
            <md-icon>cancel</md-icon>
            <md-tooltip>Un-like</md-tooltip>
          </md-button>
        </md-card-header>

        <md-card-media>
          <img :src="profile.avatarUrl" alt="avatarImage">
        </md-card-media>

        <md-card-content>
          <form @submit.prevent novalidate>

            <md-input-container v-if="!disableEdit">
              <label>Name</label>
              <md-input v-model="profile.name" maxlength="50"></md-input>
            </md-input-container>

            <label>Gender</label>
            <div>
              <md-radio v-model="profile.genderCode" name="gender-radio" md-value="f" :disabled="disableEdit">Female
              </md-radio>
              <md-radio v-model="profile.genderCode" name="gender-radio" md-value="m" :disabled="disableEdit">Male
              </md-radio>
              <md-radio v-model="profile.genderCode" name="gender-radio" md-value="o" :disabled="disableEdit">Other
              </md-radio>
            </div>

            <div v-if="disableEdit">
              <label>Bio</label>
              <p>{{ profile.bio }}</p>
            </div>

            <!-- major selection injection point -->
            <md-input-container v-if="!disableEdit">
              <label>Major</label>
              <md-select name="major-selection" v-model="profile.majorCode" :disabled="disableEdit">
                <md-option value="0">Accountancy - BUS</md-option>
                <md-option value="1">Acting - FAA</md-option>
                <md-option value="2">Actuarial Science - LAS</md-option>
                <md-option value="3">Advertising - COM</md-option>
                <md-option value="4">Aerospace Engineering - ENG</md-option>
                <md-option value="5">African American Studies - LAS</md-option>
                <md-option value="6">Agri-Accounting - ACES</md-option>
                <md-option value="7">Agribusiness, Markets &amp; Management - ACES</md-option>
                <md-option value="8">Agricultural &amp; Biological Engineering - ACES</md-option>
                <md-option value="9">Agricultural &amp; Biological Engineering - ENG</md-option>
                <md-option value="10">Agricultural &amp; Consumer Economics - ACES</md-option>
                <md-option value="11">Agricultural Communications - ACES</md-option>
                <md-option value="12">Agricultural Leadership Education - ACES</md-option>
                <md-option value="13">Agricultural Science Education - ACES</md-option>
                <md-option value="14">Agroecology - ACES</md-option>
                <md-option value="15">Animal Sciences - ACES</md-option>
                <md-option value="16">Anthropology - LAS</md-option>
                <md-option value="17">Architectural Studies - FAA</md-option>
                <md-option value="18">Art Education (K-12) - FAA</md-option>
                <md-option value="19">Art History - FAA</md-option>
                <md-option value="20">Asian American Studies - LAS</md-option>
                <md-option value="21">Astronomy - LAS</md-option>
                <md-option value="22">Atmospheric Sciences - LAS</md-option>
                <md-option value="23">Audiology - AHS</md-option>
                <md-option value="24">Biochemistry - LAS</md-option>
                <md-option value="25">Bioengineering - ENG</md-option>
                <md-option value="26">Biological Sciences (Crop Sciences) - ACES</md-option>
                <md-option value="27">Biology - LAS</md-option>
                <md-option value="28">Business Process Management - BUS</md-option>
                <md-option value="29">Chemical &amp; Biomolecular Engineering - LAS</md-option>
                <md-option value="30">Chemistry - LAS</md-option>
                <md-option value="31">Child &amp; Adolescent Development - ACES</md-option>
                <md-option value="32">Civil Engineering - ENG</md-option>
                <md-option value="33">Classical Archaeology^ - LAS</md-option>
                <md-option value="34">Classical Civilization^ - LAS</md-option>
                <md-option value="35">Classics - LAS</md-option>
                <md-option value="36">Communication - LAS</md-option>
                <md-option value="37">Community Health - AHS</md-option>
                <md-option value="38">Companion Animal &amp; Equine Science - ACES</md-option>
                <md-option value="39">Comparative &amp; World Literature - LAS</md-option>
                <md-option value="40">Computer Engineering - ENG</md-option>
                <md-option value="41">Computer Science - ENG</md-option>
                <md-option value="42">Computer Science &amp; Anthropology - LAS</md-option>
                <md-option value="43">Computer Science &amp; Astronomy - LAS</md-option>
                <md-option value="44">Computer Science &amp; Chemistry - LAS</md-option>
                <md-option value="45">Computer Science &amp; Linguistics - LAS</md-option>
                <md-option value="46">Consumer Economics &amp; Finance - ACES</md-option>
                <md-option value="47">Costume Design &amp; Technology - FAA</md-option>
                <md-option value="48">Crafts: Ceramics - FAA</md-option>
                <md-option value="49">Crafts: Metals - FAA</md-option>
                <md-option value="50">Creative Writing - LAS</md-option>
                <md-option value="51">Crop Agribusiness - ACES</md-option>
                <md-option value="52">Crop Sciences - ACES</md-option>
                <md-option value="53">Crops - ACES</md-option>
                <md-option value="54">Cultural Linguistic Diversity - AHS</md-option>
                <md-option value="55">Dance - FAA</md-option>
                <md-option value="56">Dietetics - ACES</md-option>
                <md-option value="57">Early Childhood Education (Birth-Grade 2) - EDU</md-option>
                <md-option value="58">Earth, Society &amp; Environmental Sustainability - LAS</md-option>
                <md-option value="59">East Asian Languages &amp; Cultures - LAS</md-option>
                <md-option value="60">Economics - LAS</md-option>
                <md-option value="61">Electrical Engineering - ENG</md-option>
                <md-option value="62">Elementary Education (Grades 1-6) - EDU</md-option>
                <md-option value="63">Engineering Mechanics - ENG</md-option>
                <md-option value="64">Engineering Physics - ENG</md-option>
                <md-option value="65">English - LAS</md-option>
                <md-option value="66">Entrepreneurship - BUS</md-option>
                <md-option value="67">Environmental Economics &amp; Policy - ACES</md-option>
                <md-option value="68">Family Studies - ACES</md-option>
                <md-option value="69">Farm Management - ACES</md-option>
                <md-option value="70">Finance - BUS</md-option>
                <md-option value="71">Finance in Agri-Business - ACES</md-option>
                <md-option value="72">Financial Planning - ACES</md-option>
                <md-option value="73">Fish &amp; Wildlife Conservation - ACES</md-option>
                <md-option value="74">Food Science - ACES</md-option>
                <md-option value="75">Food Science &amp; Human Nutrition - ACES</md-option>
                <md-option value="76">French - LAS</md-option>
                <md-option value="77">Gender &amp; Women&#039;s Studies - LAS</md-option>
                <md-option value="78">Geography &amp; Geographic Information Science - LAS</md-option>
                <md-option value="79">Geology - LAS</md-option>
                <md-option value="80">Germanic Languages &amp; Literature - LAS</md-option>
                <md-option value="81">Global Change &amp; Landscape Dynamics - ACES</md-option>
                <md-option value="82">Global Studies - LAS</md-option>
                <md-option value="83">Graphic Design - FAA</md-option>
                <md-option value="84">Greek^ - LAS</md-option>
                <md-option value="85">Health Education &amp; Promotion - AHS</md-option>
                <md-option value="86">Health Planning &amp; Administration - AHS</md-option>
                <md-option value="87">Health Sciences, Interdisciplinary  - AHS</md-option>
                <md-option value="88">History - LAS</md-option>
                <md-option value="89">History of Art - LAS</md-option>
                <md-option value="90">Horticultural Food Systems - ACES</md-option>
                <md-option value="91">Hospitality Management - ACES</md-option>
                <md-option value="92">Human Development &amp; Family Studies - ACES</md-option>
                <md-option value="93">Human Dimensions of the Environment - ACES</md-option>
                <md-option value="94">Human Nutrition - ACES</md-option>
                <md-option value="95">Industrial Design - FAA</md-option>
                <md-option value="96">Industrial Engineering - ENG</md-option>
                <md-option value="97">Information Systems &amp; Information Technology - BUS</md-option>
                <md-option value="98">Integrative Biology - LAS</md-option>
                <md-option value="99">Interdisciplinary Studies - LAS</md-option>
                <md-option value="100">International Business - BUS</md-option>
                <md-option value="101">Italian - LAS</md-option>
                <md-option value="102">Jazz Studies - FAA</md-option>
                <md-option value="103">Jewish Studies - LAS</md-option>
                <md-option value="104">Journalism - COM</md-option>
                <md-option value="105">Kinesiology - AHS</md-option>
                <md-option value="106">Kinesiology - Physical Education (K-12) - AHS</md-option>
                <md-option value="107">Landscape Architecture  - FAA</md-option>
                <md-option value="108">Latin^ - LAS</md-option>
                <md-option value="109">Latin American Studies - LAS</md-option>
                <md-option value="110">Latina/Latino Studies - LAS</md-option>
                <md-option value="111">Learning &amp; Education Studies - EDU</md-option>
                <md-option value="112">Lighting Design - FAA</md-option>
                <md-option value="113">Linguistics - LAS</md-option>
                <md-option value="114">Management - BUS</md-option>
                <md-option value="115">Marketing - BUS</md-option>
                <md-option value="116">Materials Science &amp; Engineering - ENG</md-option>
                <md-option value="117">Mathematics - LAS</md-option>
                <md-option value="118">Mathematics &amp; Computer Science - LAS</md-option>
                <md-option value="119">Mechanical Engineering - ENG</md-option>
                <md-option value="120">Media &amp; Cinema Studies - COM</md-option>
                <md-option value="121">Medieval Civilization - LAS</md-option>
                <md-option value="122">Middle Grades Education (Grades 5-8) - EDU</md-option>
                <md-option value="123">Molecular &amp; Cellular Biology - LAS</md-option>
                <md-option value="124">Music - FAA</md-option>
                <md-option value="125">Music Composition Theory - FAA</md-option>
                <md-option value="126">Music Education (K-12) - FAA</md-option>
                <md-option value="127">Music Instrumental Performance - FAA</md-option>
                <md-option value="128">Music Open Studies - FAA</md-option>
                <md-option value="129">Music Voice Performance - FAA</md-option>
                <md-option value="130">Musicology - FAA</md-option>
                <md-option value="131">Natural Resources &amp; Environmental Sciences - ACES</md-option>
                <md-option value="132">Neuroscience of Communication - AHS</md-option>
                <md-option value="133">New Media - FAA</md-option>
                <md-option value="134">Nuclear, Plasma &amp; Radiological Engineering - ENG</md-option>
                <md-option value="135">Nursing^^ - NUR</md-option>
                <md-option value="136">Painting - FAA</md-option>
                <md-option value="137">Philosophy - LAS</md-option>
                <md-option value="138">Photography - FAA</md-option>
                <md-option value="139">Physics - LAS</md-option>
                <md-option value="140">Plant Biotechnology &amp; Molecular Biology - ACES</md-option>
                <md-option value="141">Plant Protection  - ACES</md-option>
                <md-option value="142">Policy, International Trade &amp; Development - ACES</md-option>
                <md-option value="143">Political Science - LAS</md-option>
                <md-option value="144">Portuguese - LAS</md-option>
                <md-option value="145">Psychology - LAS</md-option>
                <md-option value="146">Public Policy &amp; Law - ACES</md-option>
                <md-option value="147">Recreation Management - AHS</md-option>
                <md-option value="148">Recreation, Sport &amp; Tourism - AHS</md-option>
                <md-option value="149">Rehabilitation Studies - AHS</md-option>
                <md-option value="150">Religion - LAS</md-option>
                <md-option value="151">Resource Conservation &amp; Restoration Ecology - ACES</md-option>
                <md-option value="152">Russian, East European &amp; Eurasian Studies - LAS</md-option>
                <md-option value="153">Scenic Design - FAA</md-option>
                <md-option value="154">Scenic Technology - FAA</md-option>
                <md-option value="155">Science, Pre-Veterinary &amp; Medical - ACES</md-option>
                <md-option value="156">Sculpture - FAA</md-option>
                <md-option value="157">Secondary Education - LAS</md-option>
                <md-option value="158">Secondary Education: Agricultural - ACES</md-option>
                <md-option value="159">Secondary Education: Biology - LAS</md-option>
                <md-option value="160">Secondary Education: Chemistry - LAS</md-option>
                <md-option value="161">Secondary Education: Earth Science - LAS</md-option>
                <md-option value="162">Secondary Education: English  - LAS</md-option>
                <md-option value="163">Secondary Education: Mathematics - LAS</md-option>
                <md-option value="164">Secondary Education: Physics - LAS</md-option>
                <md-option value="165">Secondary Education: Social Studies - LAS</md-option>
                <md-option value="166">Slavic Studies - LAS</md-option>
                <md-option value="167">Social Work - SSW</md-option>
                <md-option value="168">Sociology - LAS</md-option>
                <md-option value="169">Sound Design &amp; Technology - FAA</md-option>
                <md-option value="170">Spanish - LAS</md-option>
                <md-option value="171">Special Education - EDU</md-option>
                <md-option value="172">Speech &amp; Hearing Science - AHS</md-option>
                <md-option value="173">Speech Language Pathology - AHS</md-option>
                <md-option value="174">Sport Management - AHS</md-option>
                <md-option value="175">Stage Management - FAA</md-option>
                <md-option value="176">Statistics - LAS</md-option>
                <md-option value="177">Statistics &amp; Computer Science - LAS</md-option>
                <md-option value="178">Supply Chain Management - BUS</md-option>
                <md-option value="179">Systems Engineering and Design (formerly General Engineering) - ENG</md-option>
                <md-option value="180">Teacher Education: Art (K-12) - FAA</md-option>
                <md-option value="181">Teacher Education: French (K-12) - LAS</md-option>
                <md-option value="182">Teacher Education: German (K-12) - LAS</md-option>
                <md-option value="183">Teacher Education: Japanese (K-12) - LAS</md-option>
                <md-option value="184">Teacher Education: Kinesiology - Physical Education (K-12) - AHS</md-option>
                <md-option value="185">Teacher Education: Latin (K-12) - LAS</md-option>
                <md-option value="186">Teacher Education: Mandarin Chinese (K-12) - LAS</md-option>
                <md-option value="187">Teacher Education: Spanish (K-12) - LAS</md-option>
                <md-option value="188">Technical Systems Management - ACES</md-option>
                <md-option value="189">Technology &amp; Management (Animal Sciences) - ACES</md-option>
                <md-option value="190">Theatre - FAA</md-option>
                <md-option value="191">Theatre Studies - FAA</md-option>
                <md-option value="192">Tourism Management - AHS</md-option>
                <md-option value="193">Undeclared - DGS</md-option>
                <md-option value="194">Urban Studies &amp; Planning - FAA</md-option>
              </md-select>
            </md-input-container>

            <md-input-container v-if="!disableEdit">
              <label>Bio</label>
              <md-input v-model="profile.bio" maxlength="255" :disabled="disableEdit"></md-input>
            </md-input-container>

            <p>Traits</p>

            <md-checkbox v-model="profile.hobbyTags[0]" :disabled="disableEdit">Fueled by music</md-checkbox>
            <md-checkbox v-model="profile.hobbyTags[1]" :disabled="disableEdit">Feeds on books</md-checkbox>
            <md-checkbox v-model="profile.hobbyTags[2]" :disabled="disableEdit">Feeds on Netflix (or similar)
            </md-checkbox>
            <md-checkbox v-model="profile.hobbyTags[3]" :disabled="disableEdit">PC master race</md-checkbox>
            <md-checkbox v-model="profile.hobbyTags[4]" :disabled="disableEdit">Console gamer</md-checkbox>
            <md-checkbox v-model="profile.hobbyTags[5]" :disabled="disableEdit">No waifu no laifu</md-checkbox>
            <md-checkbox v-model="profile.hobbyTags[6]" :disabled="disableEdit">Seriouly social 🎉</md-checkbox>
            <md-checkbox v-model="profile.hobbyTags[7]" :disabled="disableEdit">एÖl丫glДヒ (polyglot)</md-checkbox>
            <md-checkbox v-model="profile.hobbyTags[8]" :disabled="disableEdit">Gym = ❤️</md-checkbox>
            <md-checkbox v-model="profile.hobbyTags[9]" :disabled="disableEdit">Runner</md-checkbox>
            <md-checkbox v-model="profile.hobbyTags[10]" :disabled="disableEdit">🍔🥓🥓🥓🌮🌯🍖🍖🍖🍔</md-checkbox>
            <md-checkbox v-model="profile.hobbyTags[11]" :disabled="disableEdit">🍎🍊🍋🍌🍉🍇🍓🥑🥗</md-checkbox>
            <md-checkbox v-model="profile.hobbyTags[12]" :disabled="disableEdit">🎺🎸🎻🥁🎷🎹🎼</md-checkbox>
            <md-checkbox v-model="profile.hobbyTags[13]" :disabled="disableEdit">🏀⚽️🏈⚾️🎾🏐🏓🏸🏒</md-checkbox>
            <md-checkbox v-model="profile.hobbyTags[14]" :disabled="disableEdit">🐶🐱🐕🐈🐢🕊🐇🦎🦄 (pets)</md-checkbox>
            <md-checkbox v-model="profile.hobbyTags[15]" :disabled="disableEdit">Would rather go sleep</md-checkbox>
            <md-checkbox v-model="profile.hobbyTags[16]" :disabled="disableEdit">Other</md-checkbox>

          </form>

          <div v-if="!disableEdit">
            <md-card-actions>
              <md-button class="md-raised md-primary" @click.native="doEdit">Edit Profile</md-button>
            </md-card-actions>
          </div>
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
    name: 'userprofile',
    data() {
      return {
        profile: {
          id: '',
          username: '',
          name: '',
          genderCode: '',
          majorCode: '',
          major: '',
          bio: '',
          hobbyTags: new Array(17).fill(false),
          avatarUrl: ''
        },
        disableEdit: true,
        alreadyLiked: false,
        alerttext: 'Error'
      };
    },
    methods: {
      follow() {
        const fromId = parseInt(this.$store.state.user.id);
        const toId = parseInt(this.profile.id);

        this.$http.post('/api/usersfollow', {fromId, toId}).then(res => {
          this.$store.commit('appendLikes', {
            name: this.profile.name,
            username: this.profile.username
          });
          this.alerttext = 'Like success!';
          this.openDialog('alerterr');
          this.alreadyLiked = true;
        }, err => {
          this.alerttext = `Error: ${err.bodyText}`;
          this.openDialog('alerterr');
        });
      },
      unfollow() {
        const fromId = parseInt(this.$store.state.user.id);
        const toId = parseInt(this.profile.id);
        const rmUsername = this.profile.username;

        this.$http.delete(`/api/usersfollow/${fromId}-${toId}`).then(res => {
          this.$store.commit('rmLikes', rmUsername);
          this.alerttext = 'Un-like success!';
          this.openDialog('alerterr');
          this.alreadyLiked = false;
        }, err => {
          this.alerttext = `Error: ${err.bodyText}`;
          this.openDialog('alerterr');
        });
      },
      doEdit () {
        const hobbyIds = this.hobbyBoolsToIds(this.profile.hobbyTags);

        this.$http.put(`/api/users/${this.profile.username}`, {
          id: this.profile.id,
          name: this.profile.name,
          genderCode: this.profile.genderCode,
          majorCode: parseInt(this.profile.majorCode),
          bio: this.profile.bio,
          hobbyIds
        }).then((res) => {
          this.alerttext = 'Edit success!';
          this.$store.commit('loadUser', this.profile);
          this.openDialog('alertsuc');
        }, (err) => {
          this.alerttext = `Error: ${err.bodyText}`;
          this.openDialog('alerterr');
        });
      },
      hobbyBoolsToIds (boolArr) {
        const res = [];
        for (let i = 0; i < boolArr.length; i++) {
          if (boolArr[i]) {
            res.push(i + 1);
          }
        }
        return res;
      },
      mapProfile(newProfile) {
        this.profile.id = newProfile.id;
        this.profile.username = newProfile.username;
        this.profile.name = newProfile.name;
        this.profile.bio = newProfile.bio;
        this.profile.hobbyTags = newProfile.hobbyTags;
        this.profile.avatarUrl = newProfile.avatarUrl;

        this.profile.major = newProfile.major;
        this.profile.majorCode = this.$store.getters.majorCodeFromName(newProfile.major);

        this.profile.genderCode = newProfile.genderCode;
        switch (newProfile.gender) {
          case 'male':
            this.profile.genderCode = 'm';
            break;
          case 'female':
            this.profile.genderCode = 'f';
            break;
          case 'other':
            this.profile.genderCode = 'o';
            break;
          default:
            break;
        }

        for (let i = 0; i < newProfile.hobbyTags.length; i++) {
          this.profile.hobbyTags[i] = newProfile.hobbyTags[i];
        }
      },
      openDialog(ref) {
        this.$refs[ref].open();
      },
      closeDialog(ref) {
        this.$refs[ref].close();
      },
      onRegSucClose() {
        this.$router.push({name: 'home'});
      }
    },
    beforeCreate () {
      if (!this.$store.getters.apikey) {  // not logged in
        this.$router.replace('/');
      }
    },
    mounted () {
      const paramUsername = this.$route.params.username;

      if (paramUsername === this.$store.state.user.username) {
        this.mapProfile(this.$store.getters.userProfile);
        this.disableEdit = false;
        console.log(`reusing profile from store: ${JSON.stringify(this.profile)}`);
      } else {
        this.$http.get(`/api/users/${paramUsername}`).then((res) => {
          this.mapProfile(JSON.parse(res.bodyText));
          const userAlreadyLikes = this.$store.getters.likes;
          for (let row of userAlreadyLikes) {
            if (row.username === this.profile.username) {
              this.alreadyLiked = true;
              break;
            }
          }
        }, (err) => {
          alert(err.bodyText);
        });
      }
    }
  };
</script>

<style scoped>
  #wrapper {
    padding-top: 120px;
    overflow-y: scroll;
  }

  #profile-card {
    margin-top: 100px;
    margin-bottom: 100px;
    max-width: 650px;
    width: 100%;
    cursor: default
  }
</style>
