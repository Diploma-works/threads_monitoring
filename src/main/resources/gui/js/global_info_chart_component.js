
window.chartComponent = function chartComponentFunction () {
    return {
        chartObject: {},
        data: {
            labels: [],
            datasets: [
                {label: "total", data: [], fill: false},
                {label: "new", data: [], fill: false},
                {label: "runnable", data: [], fill: false},
                {label: "blocked", data: [], fill: false},
                {label: "waiting", data: [], fill: false},
                {label: "timedWaiting", data: [], fill: false},
                {label: "terminated", data: [], fill: false}
            ]
        },
        config: {
          type: 'line',
          data: this.data,
          options: {}
        },
        init() {
            this.chartObject = new Chart (
                document.getElementById('global_info_chart'),
                this.config
            );

            setInterval( () => {
                if (window.isMainPagePaused == 0) {
                    fetch("http://#{GUI_URL_PREFIX}/api/threads_total_amount")
                        .then(response => response.json())
                        .then(threads => {
                            const date = new Date();
                            const time = date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();

                            if (this.data.labels.length > 30) {
                                this.data.labels.shift();
                            }
                            this.data.labels.push(time);

                            for (dataset of this.data.datasets) {

                                if (dataset.data.length > 30) {
                                    dataset.data.shift();
                                }

                                if (dataset.label == "terminated") {
                                    dataset.data.push(threads.terminatedAmount);
                                } else if (dataset.label == "timedWaiting") {
                                    dataset.data.push(threads.timedWaitingAmount);
                                } else if (dataset.label == "waiting") {
                                    dataset.data.push(threads.waitingAmount);
                                } else if (dataset.label == "blocked") {
                                    dataset.data.push(threads.blockedAmount);
                                } else if (dataset.label == "runnable") {
                                    dataset.data.push(threads.runnableAmount);
                                } else if (dataset.label == "new") {
                                    dataset.data.push(threads.newAmount);
                                } else if (dataset.label == "total") {
                                    dataset.data.push(threads.totalAmount);
                                }

                            }

                            this.config.data = this.data;

                            this.chartObject.update();
                        });
                }
            },
                500
            );
        }
    }
}
