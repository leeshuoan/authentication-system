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
              <router-link class="nav-link active" to="/profile"
                >Profile</router-link
              >
            </li>
            <form
              id="logoutForm"
              class="button_to"
              method="post"
              action="https://smurnauth-production.fly.dev/users/sign_out"
              target="_blank"
            >
              <input
                type="hidden"
                name="_method"
                value="delete"
                autocomplete="off"
              />
              <input
                type="hidden"
                name="authenticity_token"
                value="HNDUd76lpwIsv6EoOe5XhNpIwZy1r7TBPXX4NHQgJA66iFlomo6_4M-gHj_Hh6gJW83Uto5b4FNi7VGNMgasAg"
                autocomplete="off"
              />
              <input
                type="submit"
                id="logout"
                value="Sign out"
                class="btn btn-outline-danger"
                data-disable-with="Sign out"
                hidden
              />
            </form>
            <li class="nav-item">
              <a class="nav-link" @click="logout()">Log out</a>
            </li>
          </ul>
        </div>
      </div>
    </nav>
    <div class="container w-80 mt-5">
      <div class="row">
        <div class="col-12">
          <h1 class="text-center">{{ name }}</h1>
        </div>
      </div>
      <table class="table table-striped table-borderless mt-3">
        <tbody>
          <tr>
            <th scope="row">Given Name</th>
            <td>{{ given_name }}</td>
          </tr>
          <tr>
            <th scope="row">Family Name</th>
            <td>{{ family_name }}</td>
          </tr>
          <tr>
            <th scope="row">Gender</th>
            <td>{{ gender }}</td>
          </tr>
          <tr>
            <th scope="row">Date of Birth</th>
            <td>{{ birthdate }}</td>
          </tr>
          <tr>
            <th scope="row">Email</th>
            <td>{{ email }}</td>
          </tr>
          <tr>
            <th scope="row">Phone Number</th>
            <td>{{ phone_number }}</td>
          </tr>
        </tbody>
      </table>
      <button class="btn btn-primary" @click="deleteAccount()">
        Delete Account
      </button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      given_name: "",
      family_name: "",
      gender: "",
      birthdate: "",
      email: "",
      phone_number: "",
      error: null,
      loading: true,
    };
  },
  mounted() {
    // In actual implementation, backend would call the bank resource to get userDetails before
    // returning the user object to the frontend client. As the endpoint does not exist and certain fields
    // are missing from users.csv, we are hardcoding the values here.
    let userData = sessionStorage.getItem("user");
    userData = JSON.parse(userData);
    this.given_name = userData["given_name"];
    this.family_name = userData["family_name"];
    this.gender = userData["gender"];
    if (userData["birthdate"].includes("T")) {
      this.birthdate = userData["birthdate"].split("T")[0];
    } else {
      this.birthdate = userData["birthdate"];
    }
    this.email = userData["email"];
    this.phone_number = userData["phone_number"];
  },
  methods: {
    logout() {
      // logout from flyauth
      let isSsoObj = sessionStorage.getItem("sso");
      if (isSsoObj) {
        document.getElementById("logoutForm").submit();
      }
      // destroy the sessions
      window.sessionStorage.removeItem("sso");
      window.sessionStorage.removeItem("token");
      window.sessionStorage.removeItem("user");
      // destroy the cookie
      if (document.cookie) {
        document.cookie =
          "access_token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
      } else {
        fetch(import.meta.env.VITE_API_URL + "/api/logout", {
          method: "GET",
          credentials: "include",
          Authorization: "Bearer " + window.sessionStorage.getItem("token"),
        }).then((res) => {
          console.log(res.status);
          if (res.status === 200) {
            this.$router.push("/");
          }
        });
      } // end destroy cookie
      // redirect
      this.$router.push("/");
    },
    deleteAccount() {
      let confirmDelete = confirm(
        "Are you sure you want to delete your account?"
      );
      if (confirmDelete) {
        fetch(
          import.meta.env.VITE_API_URL + "/api/users/delete/" +
            this.email,
          {
            method: "DELETE",
            headers: {
              "Content-Type": "application/json",
              Authorization: "Bearer " + sessionStorage.getItem("token"),
            },
          }
        )
          .then((response) => {
            alert("Account deleted successfully");
            this.logout();
          })
          .catch((error) => {
            alert("Failed to delete account");
          });
      }
    },
    async created() {
      try {
        // read cookie
        var decoded_cookie = decodeURIComponent(document.cookie);
        var access_token = decoded_cookie
          .split("access_token=")[1]
          .split(";")[0];
        console.log(access_token);
        const response = await fetch(
          import.meta.env.VITE_API_URL + "/sso/profile",
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: "Bearer " + access_token,
            },
          }
        )
          .then((response) => response.json())
          .then((data) => {
            console.log(data);
            this.given_name = data.given_name;
            this.family_name = data.family_name;
            this.gender = data.gender;
            this.birthdate = data.birthdate;
            this.email = data.email;
            this.phone_number = data.phone_number;
            this.name = data.name;
          });
      } catch (error) {
        this.error = error;
        console.log(this.error);
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>
