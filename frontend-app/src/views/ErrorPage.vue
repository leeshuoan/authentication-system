<template>
    <div>
        <h1 id="error_description"></h1>
    </div>
</template>
<script>
window.onload = function() {
    showError();
    function showError() {
        const queryString = window.location.search;
        const urlParams = new URLSearchParams(queryString);
        console.log(urlParams.get("error_description"))
        if (urlParams.has("error_description")) {
            document.getElementById("error_description").innerText = urlParams.get("error_description");            
            setTimeout(function(){window.location.href = "/";}, 3000);
        }
    };
}

export default{
}
</script>