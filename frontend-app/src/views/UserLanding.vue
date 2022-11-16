<template>
  <div>
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
      <div class="container-fluid">
        <img class="logo mx-3" src="/Ascenda.png" />
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
          <ul class="navbar-nav me-auto mb-2 mb-lg-0">
            <li class="nav-item">
              <router-link class="nav-link" to="/profile">Profile</router-link>
            </li>
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
          <h1 class="text-center">Welcome to Ascendas Loyalty!</h1>
        </div>
      </div>
    </div>    
    <form id="logoutForm" class="button_to" method="post" action="https://smurnauth-production.fly.dev/users/sign_out" target="_blank">
      <input type="hidden" name="_method" value="delete" autocomplete="off" />
      <input type="hidden" name="authenticity_token" value="HNDUd76lpwIsv6EoOe5XhNpIwZy1r7TBPXX4NHQgJA66iFlomo6_4M-gHj_Hh6gJW83Uto5b4FNi7VGNMgasAg" autocomplete="off" />
      <input type="submit" id="logout" value="Sign out" class="btn btn-outline-danger" data-disable-with="Sign out" hidden/>
    </form>
  </div>
</template>

<script>

export default {
  methods :{
    logout() {
      // logout from flyauth
      let isSsoObj = sessionStorage.getItem("sso");
      if (isSsoObj) {
        document.getElementById("logoutForm").submit();
      }
      // destroy the sessions
      window.sessionStorage.removeItem('sso');
      window.sessionStorage.removeItem('token');
      window.sessionStorage.removeItem('user');
      // destroy the cookie
      if (document.cookie){
        document.cookie =
          "access_token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
      }else{
        fetch(import.meta.env.VITE_API_URL + "/api/logout", {
          method: "GET",
          credentials: "include",
          Authorization: "Bearer " + window.sessionStorage.getItem("token"),
        })
        .then((res) => {
          console.log(res.status);
          if (res.status === 200) {
            this.$router.push("/");
          }
        });
      } // end destroy cookie
      // redirect
      this.$router.push("/");
    },
  }

};
</script>