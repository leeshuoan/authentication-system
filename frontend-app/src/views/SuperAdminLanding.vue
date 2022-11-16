<template>
  <div>
    <nav class="navbar bg-light shadow-lg">
      <div class="container-fluid">
        <button
          class="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarToggleExternalContent"
          aria-controls="navbarToggleExternalContent"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span class="navbar-toggler-icon"></span>
          <img class="logo" src="/Bank.png" />
          <span class="bank">My Bank</span>
        </button>
        <form id="logoutForm" class="button_to" method="post" action="https://smurnauth-production.fly.dev/users/sign_out" target="_blank">
          <input type="hidden" name="_method" value="delete" autocomplete="off" />
          <input type="hidden" name="authenticity_token" value="HNDUd76lpwIsv6EoOe5XhNpIwZy1r7TBPXX4NHQgJA66iFlomo6_4M-gHj_Hh6gJW83Uto5b4FNi7VGNMgasAg" autocomplete="off" />
          <input type="submit" id="logout" value="Sign out" class="btn btn-outline-danger" data-disable-with="Sign out" hidden/>
        </form>
        <div class="headerRight">
          <h2 class="username h4 px-5 m-0">{{ username }}</h2>
          <font-awesome-icon icon="right-from-bracket" />
          <a class="text-dark h4 px-1 m-0" @click="logout()">Logout</a>
        </div>
      </div>

      <div class="collapse" id="navbarToggleExternalContent">
        <ul class="navbar-nav me-auto px-4">
          <li class="nav-item">
            <router-link class="nav-link active" to="/profile">Profile</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link active" to="/superadmin">Admin Dashboard</router-link>
          </li>
        </ul>
      </div>
    </nav>

    <div class="m-0 p-0">
      <div class="tab">
        <button
          class="tablinks"
          @click="openTab('Users')"
          id="defaultOpen"
          ref="userTab"
        >
          <font-awesome-icon icon="users" />
          Users
        </button>
        <button
          class="tablinks"
          @click="openTab('Enrollment')"
          ref="enrollmentTab"
        >
          <font-awesome-icon icon="file-pen" />
          Enrollment
        </button>
      </div>

      <div id="Users" class="tabcontent">
        <h3>Customers</h3>
        <table class="table mt-3">
          <thead class="table-light">
            <tr>
              <th scope="col">Email</th>
              <th scope="col">Name</th>
              <th scope="col">User ID</th>
              <th scope="col">Status</th>
              <th scope="col">Created At</th>
              <th scope="col">Updated At</th>
              <th scope="col">Actions</th>
            </tr>
          </thead>

          <tbody v-for="(user, i) in allUsers" :key="i">
            <th scope="col">{{ user.username }}</th>
            <td>{{ user.firstName }} {{ user.lastName }}</td>
            <td>{{ user.id }}</td>
            <td>{{ user.status == true ? "enrolled" : "not enrolled" }}</td>
            <td>{{ user.created }}</td>
            <td>{{ user.updated }}</td>
            <td>
              <a href="#">
                <font-awesome-icon icon="magnifying-glass-plus" />
              </a>
              <a>
                <font-awesome-icon icon="pen" />
              </a>
            </td>
          </tbody>
        </table>
      </div>

      <div id="Enrollment" class="tabcontent" style="display: none">
        <h3>Enrollment</h3>
        <p></p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      allUsers: [],
    };
  },
  mounted() {
    this.getAllUsers();
  },
  methods: {
    openTab(tabName) {
      var i, tabcontent, tablinks;
      tabcontent = document.getElementsByClassName("tabcontent");
      for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
      }
      tablinks = document.getElementsByClassName("tablinks");
      for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
      }
      document.getElementById(tabName).style.display = "block";
      if (tabName === "Users") {
        this.$refs.userTab.className += " active";
      } else {
        this.$refs.enrollmentTab.className += " active";
      }
    },
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

    getAllUsers() {
      // should have a bank end point for SSO Admins hence here is a mockup
      if (window.sessionStorage.getItem("sso")) {
        this.allUsers = [
          {
            id: 1,
            username: "johndoe@gmail.com",
            firstName: "John",
            lastName: "Doe",
            status: true,
            created: "2021-01-01",
            updated: "2021-01-01",
          },
          {
            id: 2,
            username: "janedoe@gmail.com",
            firstName: "Jane",
            lastName: "Doe",
            status: false,
            created: "2021-01-01",
            updated: "2021-01-01",
          },
        ];
      } else {
        fetch(
          import.meta.env.VITE_API_URL + "/api/users",
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: "Bearer " + window.sessionStorage.getItem("token"),
            },
          }
        )
          .then((response) => response.json())
          .then((data) => {
            this.allUsers = data;
            console.log(data[0]);
          });
      }
    },
  },
};
</script>

<style scoped>
* {
  font-family: "Montserrat", sans-serif;
  font-size: 15px;
}

main {
  overflow-x: hidden;
}

.headerRight {
  text-align: center;
  align-items: center;
  display: flex;
  float: right;
}

.logo {
  height: 900%;
}

.bank {
  font-size: 20px;
}

/* Style the tab */
.tab {
  float: left;
  border: 1px solid #ccc;
  border-top: none;
  width: 20%;
  height: 100vh;
}

.tab button {
  display: block;
  background-color: inherit;
  color: black;
  padding: 22px 16px;
  width: 100%;
  border: none;
  outline: none;
  text-align: left;
  cursor: pointer;
  transition: 0.3s;
  font-size: 18px;
}

/* Change background color of buttons on hover */
.tab button:hover {
  background-color: #ddd;
}

/* Create an active/current "tab button" class */
.tab button.active {
  background-color: #ccc;
}

/* Style the tab content */
.tabcontent {
  float: left;
  padding: 40px 5%;
  border: 1px solid #ccc;
  width: 80%;
  border-left: none;
  border-top: none;
  height: 100vh;
}
</style>