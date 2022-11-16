<template>
  <div>Redirecting!</div>
</template>
<script>
export default {
  mounted() {
    const params = new URLSearchParams(window.location.search);
    const code = params.get("code");
    const error = params.get("error");
    const error_description = params.get("error_description");
    if (error != null) {
      alert(error_description);
      this.$router.push("/login");
      return;
    }
    fetch(
      import.meta.env.VITE_API_URL + "/sso/jwt/" +
        code,
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
      }
    )
      .then((response) => {
        response.text().then((data) => {
          sessionStorage.setItem("token", data);

          fetch(import.meta.env.VITE_API_URL + "/sso/profile", {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: "Bearer " + sessionStorage.getItem("token"),
            },
          })
            .then((response) => response.json())
            .then((data) => {
              console.log(data);
              sessionStorage.setItem("user", JSON.stringify(data));
              sessionStorage.setItem("sso", true);
              if (
                data.email == "russel.stephan@kihn.name" ||
                data.email == "admin@example.com"
              ) {
                // super admin
                this.$router.push("/superadmin");
              } else if (data.email == "pagac_vince@yost.io") {
                // read only admin
                this.$router.push("/admin");
              } else {
                // normal user
                this.$router.push("/home");
              }
            });
        })
      })
      .catch((error) => {
        console.log(error.message);
        this.$router.push("/error")
      });

    //     const queryString = window.location.search;
    //     const urlParams = new URLSearchParams(queryString);
    //     if (urlParams.has("access_token")) {
    //       const access_token = urlParams.get("access_token");
    //       const created_at = urlParams.get("created_at");
    //       const expires_in = urlParams.get("expires_in");
    //       const scope = urlParams.get("scope");
    //       // 21600 * 1000 = 6 hours
    //       const expiryTime = new Date(created_at * 1000 + expires_in * 1000);
    //       document.cookie =
    //         "access_token=" +
    //         access_token +
    //         "; expires=" +
    //         expiryTime +
    //         "; path=/;";

    //       fetch("http://localhost:8080/sso/profile", {
    //         method: "GET",
    //         headers: {
    //           "Content-Type": "application/json",
    //           Authorization: "Bearer " + access_token,
    //         },
    //       })
    //         .then((response) => response.json())
    //         .then((data) => {
    //           console.log(data);
    //           sessionStorage.setItem("user", JSON.stringify(data));
    //           sessionStorage.setItem("sso", true);
    //           if (
    //             data.email == "russel.stephan@kihn.name" ||
    //             data.email == "admin@example.com"
    //           ) {
    //             // super admin
    //             window.location.replace("http://localhost:5173/superadmin");
    //           } else if (data.email == "pagac_vince@yost.io") {
    //             // read only admin
    //             window.location.replace("http://localhost:5173/admin");
    //           } else {
    //             // normal user
    //             window.location.replace("http://localhost:5173/home");
    //           }
    //         });
    //     } else {
    //       window.location.replace("http://localhost:5173/error");
    //     }
  },
};
</script>
