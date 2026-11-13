// Stub mínimo compatible con la API de vuex que usaba la versión anterior:
//   this.$store.dispatch('security/revisaError', err)
// Muestra el error en consola; aquí se puede conectar un manejador global.
export const store = {
  dispatch(action, payload) {
    if (action === 'security/revisaError') {
      // eslint-disable-next-line no-console
      console.error('[security/revisaError]', payload && payload.response ? payload.response.data : payload)
      return
    }
    // eslint-disable-next-line no-console
    console.warn(`[store] acción no implementada: ${action}`, payload)
  },
}
