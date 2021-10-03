<template>
  <div class="auth">
    <h1>{{access}}</h1>
    <div>
      Регистрация:
      <form @submit="submitRegistration">
        <input type="text" placeholder="Name" v-model="registrationForm.name"/>
        <input type="text" placeholder="Email" v-model="registrationForm.email"/>
        <input type="password" placeholder="Password" v-model="registrationForm.password"/>
        <input type="submit" value="Register"/>
      </form>
    </div>
    <div>
      Вход:
      <form @submit="submitLogin">
        <input type="text" placeholder="Email" v-model="loginForm.email"/>
        <input type="password" placeholder="Password" v-model="loginForm.password"/>
        <input type="submit" value="Enter"/>
      </form>
    </div>
    <div>
      <input type="button" @click="submitLogout" value="Выход"/>
    </div>
  </div>
</template>

<script>
import {mapGetters} from "vuex"
import "@/assets/main.css"

export default {
  computed: mapGetters({
    access: "user/getAccess"
  }),
  fetch(){
    this.$store.dispatch("user/fetchAccess")
  },
  data() {
    return {
      registrationForm: {
        name: null,
        email: '',
        password: ''
      },
      loginForm: {
        email: '',
        password: ''
      }
    }
  },
  methods: {
    submitLogout() {
      this.$store.dispatch("user/logout")
    },
    submitLogin(event) {
      event.preventDefault()
      this.$store.dispatch("user/login", this.loginForm)
    },
    submitRegistration(event) {
      event.preventDefault()
      this.$store.dispatch("user/registration", this.registrationForm)
    }
  }
}
</script>
