export const state = () => ({
  access: null
})

export const getters = {
  getAccess(state){
    return state.access
  }
}

export const actions = {
  fetchAccess(context){
    this.$axios.get("/api/v1/client/access").then(res => {
      context.commit("setAccess", res.data)
    })
  },
  login(context, data){
    this.$axios.post("/api/v1/auth/login", data).then(res => {
      context.dispatch("fetchAccess")
    })
  },
  registration(context, data){
    this.$axios.post("/api/v1/auth/registration", data).then(res => {
      context.dispatch("fetchAccess")
    })
  },
  logout(context){
    this.$axios.get("/api/v1/auth/logout").then(res => {
      context.dispatch("fetchAccess")
    })
  }
}

export const mutations = {
  setAccess(state, data){
    state.access = data
  }
}
