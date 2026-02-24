Component({
  properties: {
    show: {
      type: Boolean,
      value: true
    }
  },

  methods: {
    onClose() {
      this.triggerEvent('close')
    },

    onConfirm() {
      this.triggerEvent('confirm')
    }
  }
})
