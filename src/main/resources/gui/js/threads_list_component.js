
window.threadsListComponent = function threadsListComponentFunction() {
    return {
        threads: [],
        init() {
            setInterval(() => {
                if (window.isMainPagePaused == 0) {
                    fetch("http://#{GUI_URL_PREFIX}/api/threads_names_list")
                        .then(response => response.json())
                        .then(threadsWrapper => {
                            this.threads = [];
                            for (thread of threadsWrapper.threadNameWrappers) {
                                this.threads.push(thread);
                            }
                        });
                }
            }, 500);
        },
        redirect(id) {
            window.location = "http://#{GUI_URL_PREFIX}/thread.html?id=" + id;
        }
    }
}
