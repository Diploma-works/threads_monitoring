
window.threadComponent = function threadComponentFunction() {
    return {
        threadId: 0,
        init() {
            const urlParams = new URLSearchParams(window.location.search);
            this.threadId = urlParams.get("id");

            console.log(this.threadId + "  " + urlParams.get("id"));
        }
    }
}
