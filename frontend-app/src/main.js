import { createApp } from 'vue'
import App from './App.vue'
import router from './plugins/router'
import store from './plugins/store'
import { library } from '@fortawesome/fontawesome-svg-core'
import { faMagnifyingGlassPlus, faPen, faRightFromBracket, faFilePen, faUsers, faUser } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import './style.css'

library.add(faMagnifyingGlassPlus, faPen, faRightFromBracket, faFilePen, faUsers)

createApp(App).use(store).use(router).component("font-awesome-icon", FontAwesomeIcon).mount('#app')
