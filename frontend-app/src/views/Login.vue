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
              <router-link class="nav-link" to="/register"
                >Register</router-link
              >
            </li>
          </ul>
        </div>
      </div>
    </nav>

    <div class="container position-relative">
      <div class="login shadow-lg p-3 bg-body rounded">
        <h1 class="heading">Login</h1>

        <form class="m-5" @submit.prevent="">
          <div class="mb-3" id="emailinput">
            <input
              type="email"
              class="form-control"
              id="email"
              aria-describedby="emailHelp"
              placeholder="Enter email"
              v-model="email"
            />
            <div id="emailHelp" class="form-text">
              We'll never share your email with anyone else.
            </div>
          </div>

          <div class="mb-3" id="pwinput">
            <input
              type="password"
              class="form-control"
              id="password"
              placeholder="Password"
              v-model="password"
            />
          </div>

          <div class="mb-3">
            <a href="#" class="formstyles">Forgot password?</a>
          </div>

          <div class="d-grid gap-2 mb-3">
            <button type="submit" class="btn btn-primary" @click="checkLogin()">
              Login
            </button>
          </div>

          <div class="mb-3 text-center">
            <a
              class="formstyles"
              href="https://mot636ih9g.execute-api.ap-southeast-1.amazonaws.com/sso/login"
              >Or Login via Bank SSO</a
            >
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      email: "",
      password: "",
    };
  },
  methods: {
    checkLogin() {
      const params = new URLSearchParams();
      params.append("username", this.email);
      params.append("password", this.password);
      fetch(
        import.meta.env.VITE_API_URL +  `/api/login`,
        {
          method: "POST",
          headers: {
            Credentials: "include",
          },
          body: params,
        }
      ).then((response) => {
        if (response.status == 403) {
          alert("Invalid credentials");
          return;
        }
        response.json().then((data) => {
          sessionStorage.setItem("token", data.access_token);
          fetch(
            import.meta.env.VITE_API_URL + "/api/getUserData/" +
              this.email,
            {
              method: "GET",
              headers: {
                Authorization: "Bearer " + sessionStorage.getItem("token"),
              },
            }
          )
            .then((response) => response.json())
            .then((data) => {
              console.log(data);
              console.log(data["approved"]);
              if (data.status == 409) {
                alert("User is not enrolled");
                return;
              }

              // In the actual implementation, upon successful login, the frontend client should make a GET request to
              // the bank's resource server with the access token in the Authorization header to get the user's information.
              // As the bank resource api endpoint is not implemented, we are using the data from  users.csv and adding
              // additional hardcoded data to simulate the response from the bank resource server.

              if (data["error+message"] == undefined) {
                delete Object.assign(data, {
                  ["given_name"]: data["firstName"],
                })["firstName"];
                delete Object.assign(data, {
                  ["family_name"]: data["lastName"],
                })["lastName"];
                delete Object.assign(data, {
                  ["birthdate"]: data["birthDate"],
                })["birthDate"];
                delete Object.assign(data, { ["email"]: data["username"] })[
                  "username"
                ];
                data["gender"] = "male";
                data["phone_number"] = "+1-784 (718) 184-7385";

                console.log("APPROVED HERE");
                console.log(data["approved"]);
                if (!data["approved"]) {
                  let approve = confirm(
                    "To login, Ascenda will need to fetch your personal information. Do you approve?"
                  );
                  if (!approve) {
                    return;
                  }
                  fetch(
                    import.meta.env.VITE_API_URL + "/api/users/approve/" +
                      data["email"],
                    {
                      method: "PUT",
                      headers: {
                        Authorization:
                          "Bearer " + sessionStorage.getItem("token"),
                      },
                    }
                  )
                    .then((response) => {
                      data["approved"] = true;
                      console.log(response)
                    })
                    .catch((error) => {
                      console.log(error);
                    });
                }

                sessionStorage.setItem("user", JSON.stringify(data));
                let roles = data.roles;
                let superadmin = roles.find(
                  (o) => o.name === "ROLE_SUPER_ADMIN"
                );
                let admin = roles.find((o) => o.name === "ROLE_ADMIN");
                let user = roles.find((o) => o.name === "ROLE_USER");
                if (superadmin) {
                  this.$router.push("/superadmin");
                  return;
                }
                if (admin) {
                  this.$router.push("/admin"); 
                  return;        
                }
                if (user) {
                  this.$router.push("/home");
                  return;
                }
                if(!superadmin && !admin && !user){
                  alert("An unexpected error has occurred. Please try again later.");
                }
              }
            })
            .catch((error) => {
              console.log(error);
            });
        });
      });
    },
  },
};

// fetch("/api/users", {
//             headers: {
//               "Content-Type": "application/json",
//             },
//             method: "GET",
//           })
//             .then((response) => response.json())
//             .then((data) => {
//               console.log(data);
//             })
//             .catch((error) => {
//               console.log(error);
//             });
//   fetch('/api/users', {
//     headers: {
//       'Content-Type': 'application/json',
//     },
//     method: "GET"
//   }).then((response) => response.json())
//     .then((data) => {
//       console.log(data)
//     }).catch((error) => {
//       console.log(error)
//     });
// })
//   .catch((error) => {
//     console.log(error.message);
</script>
