
window.pauseButtonComponent = function pauseButtonComponentFunction() {
    return {
        description: "#{GUI_PAUSE_BUTTON_DESCRIPTION_ON}",
        handleClick() {
            if (this.description == "#{GUI_PAUSE_BUTTON_DESCRIPTION_ON}") {
                this.description = "#{GUI_PAUSE_BUTTON_DESCRIPTION_OFF}";
            } else {
                this.description = "#{GUI_PAUSE_BUTTON_DESCRIPTION_ON}"
            }
            document.getElementById('pause_button').value = this.description;
            window.isMainPagePaused = 1 - window.isMainPagePaused;
        },
        init() {
            document.getElementById('pause_button').value = this.description;
            window.isMainPagePaused = 0;
        }
    }
}
