<template>
  <div>
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
      <div class="container-fluid">
        <img class="logo mx-3" src="/Ascenda.png" />
        <button
          class="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarSupportedContent"
          aria-controls="navbarSupportedContent"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
          <ul class="navbar-nav me-auto mb-2 mb-lg-0">
            <li class="nav-item">
              <router-link class="nav-link" to="/">Login</router-link>
            </li>
          </ul>
        </div>
      </div>
    </nav>
    <div class="container position-relative">
      <div class="register shadow-lg p-3 bg-body rounded">
        <h1 class="heading">New Account</h1>

        <form class="m-5" @submit.prevent="">
          <div class="mb-4">
            <input
              type="text"
              class="form-control"
              id="firstname"
              placeholder="First Name"
              v-model="firstName"
            />
          </div>

          <div class="mb-4">
            <input
              type="text"
              class="form-control"
              id="lastname"
              placeholder="Last Name"
              v-model="lastName"
            />
          </div>

          <div class="mb-4">
            <input
              type="email"
              class="form-control"
              id="email"
              placeholder="Email"
              v-model="email"
            />
          </div>

          <div class="mb-4">
            <input
              type="date"
              class="form-control"
              id="dob"
              placeholder="Birthdate"
              v-model="dob"
            />
          </div>

          <div class="mb-4">
            <input
              type="password"
              class="form-control"
              id="password"
              placeholder="Password"
              v-model="pw"
            />
          </div>

          <div class="d-grid gap-2 mb-3">
            <button type="submit" class="btn btn-primary" @click="sendEmail()">
              Register
            </button>
          </div>

          <div class="mb-3 text-center">
            <router-link class="nav-link" to="/">Login</router-link>
          </div>
        </form>
      </div>

      <!-- Modal -->
      <div
        class="modal fade"
        id="successModal"
        tabindex="-1"
        aria-labelledby="exampleModalLabel"
        aria-hidden="true"
      >
        <div class="modal-dialog" id="modal">
          <div class="modal-content">
            <div class="modal-header">
              <h1 class="modal-title fs-5" id="exampleModalLabel">
                Multi-factor Authentication (MFA)
              </h1>
              <button
                type="button"
                class="btn-close"
                data-bs-dismiss="modal"
                aria-label="Close"
              ></button>
            </div>

            <div class="modal-body">
              Email has been successfully sent, please check your inbox.
            </div>

            <div class="modal-footer" id="button">
              <div data-bs-dismiss="modal">
                <router-link class="nav-link" to="/" tag="button">
                  Login
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div
        class="modal fade"
        id="errorModal"
        tabindex="-1"
        aria-labelledby="exampleModalLabel"
        aria-hidden="true"
      >
        <div class="modal-dialog" id="modal">
          <div class="modal-content">
            <div class="modal-header">
              <h1 class="modal-title fs-5" id="exampleModalLabel">
                Multi-factor Authentication (MFA)
              </h1>
              <button
                type="button"
                class="btn-close"
                data-bs-dismiss="modal"
                aria-label="Close"
              ></button>
            </div>

            <div class="modal-body">
              Unable to send an email. Please check all fields are filled correctly.
            </div>

            <div class="modal-footer" id="button">
              <button
                class="btn btn-primary"
                data-bs-dismiss="modal"
                aria-label="Close"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      </div>

      <div
        class="modal fade"
        id="enrolledErrorModal"
        tabindex="-1"
        aria-labelledby="exampleModalLabel"
        aria-hidden="true"
      >
        <div class="modal-dialog" id="modal">
          <div class="modal-content">
            <div class="modal-header">
              <h1 class="modal-title fs-5" id="exampleModalLabel">
                Multi-factor Authentication (MFA)
              </h1>
              <button
                type="button"
                class="btn-close"
                data-bs-dismiss="modal"
                aria-label="Close"
              ></button>
            </div>

            <div class="modal-body">
              Unable to send an email. User is already enrolled.
            </div>

            <div class="modal-footer" id="button">
              <button
                class="btn btn-primary"
                data-bs-dismiss="modal"
                aria-label="Close"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      firstName: "",
      lastName: "",
      email: "",
      dob: "",
      pw: "",
      modalContent: "",
    };
  },
  mounted() {
    $("#successModal").modal({ show: false });
    $("#errorModal").modal({ show: false });
  },
  methods: {
    sendEmail() {
      console.log(this.$refs);
      let url = "https://mot636ih9g.execute-api.ap-southeast-1.amazonaws.com/verify";
      console.log(this.firstName);
      console.log(this.lastName);
      console.log(this.email);
      console.log(this.dob);
      console.log(this.pw);
      axios
        .post(url, {
          firstName: this.firstName,
          lastName: this.lastName,
          username: this.email,
          dob: this.dob,
          password: this.pw,
        })
        .then((response) => {
          this.showSuccessModal = true;
          $("#successModal").modal("show");
          if (response.status == 409){
            $("#enrolledErrorModal").modal("show");
          }
        })
        .catch((error) => {
          console.log(error);
          console.log(error.response.status);
          if (error.response.status == 500) {
            $("#enrolledErrorModal").modal("show");
            return;
          } else {
            this.showErrorModal = true;
            $("#errorModal").modal("show");
          }
        });
    },
  },
};
</script>

<style>
</style>