<template>
  <div>
    <md-layout md-align="center">
      <md-card md-with-hover id="additional-card">
        <md-card-header>
          <div class="md-title">Welcome!</div>
          <div class="md-subhead">
            Please provide some basic information to get started.
          </div>
        </md-card-header>
        <md-card-content>
          <form @submit.stop.prevent>

            <md-input-container>
              <label>Name</label>
              <md-input v-model="name" maxlength="50"></md-input>
            </md-input-container>

            <label>Gender</label>
            <div>
              <md-radio v-model="gender" name="gender-radio" md-value="f">Female</md-radio>
              <md-radio v-model="gender" name="gender-radio" md-value="m">Male</md-radio>
              <md-radio v-model="gender" name="gender-radio" md-value="o">Other</md-radio>
            </div>

            <!-- major selection injection point -->
            <md-input-container><label>Major</label>
              <md-select name="major-selection" v-model="major">
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

            <md-input-container>
              <label>Bio</label>
              <md-input v-model="bio" maxlength="255"></md-input>
            </md-input-container>

            <label>Traits</label>
            <div class="md-subhead">Please check all the traits below that you think you match or can relate to!</div>

            <md-checkbox v-model="tagboxes[0]">Fueled by music</md-checkbox>
            <md-checkbox v-model="tagboxes[1]">Feeds on books</md-checkbox>
            <md-checkbox v-model="tagboxes[2]">Feeds on Netflix (or similar)</md-checkbox>
            <md-checkbox v-model="tagboxes[3]">PC master race</md-checkbox>
            <md-checkbox v-model="tagboxes[4]">Console gamer</md-checkbox>
            <md-checkbox v-model="tagboxes[5]">No waifu no laifu</md-checkbox>
            <md-checkbox v-model="tagboxes[6]">Seriouly social 🎉</md-checkbox>
            <md-checkbox v-model="tagboxes[7]">एÖl丫glДヒ (polyglot)</md-checkbox>
            <md-checkbox v-model="tagboxes[8]">Gym = ❤️</md-checkbox>
            <md-checkbox v-model="tagboxes[9]">Runner</md-checkbox>
            <md-checkbox v-model="tagboxes[10]">🍔🥓🥓🥓🌮🌯🍖🍖🍖🍔</md-checkbox>
            <md-checkbox v-model="tagboxes[11]">🍎🍊🍋🍌🍉🍇🍓🥑🥗</md-checkbox>
            <md-checkbox v-model="tagboxes[12]">🎺🎸🎻🥁🎷🎹🎼</md-checkbox>
            <md-checkbox v-model="tagboxes[13]">🏀⚽️🏈⚾️🎾🏐🏓🏸🏒</md-checkbox>
            <md-checkbox v-model="tagboxes[14]">🐶🐱🐕🐈🐢🕊🐇🦎🦄 (pets)</md-checkbox>
            <md-checkbox v-model="tagboxes[15]">Would rather go sleep</md-checkbox>
            <md-checkbox v-model="tagboxes[16]">Other</md-checkbox>

          </form>
          <md-button class="md-raised md-primary" @click.native="submit">Done</md-button>
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
  export default {
    cred: '',
    name: 'signup',
    data() {
      return {
        alerttext: 'Error',
        name: '',
        gender: '',
        major: '',
        bio: '',
        tagboxes: Array(17).fill(false)
      };
    },
    methods: {
      submit() {
        const hobbyIds = [];
        for (let i = 0; i < this.tagboxes.length; i++) {
          if (this.tagboxes[i]) {
            hobbyIds.push(i + 1);
          }
        }

        this.$http.post('/api/usersregv', {
          cred: this.cred,
          name: this.name,
          genderCode: this.gender,
          majorCode: parseInt(this.major),
          bio: this.bio,
          hobbyIds
        }).then((res) => {
          const resObj = JSON.parse(res.bodyText);

          this.$store.commit('loadApiKey', resObj.newToken);

          this.$http.get(`/api/users/${resObj.username}`).then((res) => {
            this.$store.commit('loadUser', JSON.parse(res.bodyText));
            this.$router.push('/home');
          }, (err) => {
            this.alert.content = `Error: ${err.bodyText}`;
            this.openDialog('alerterr');
          });

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
      }
    },
    created () {
      this.cred = this.$route.params.cred;
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
