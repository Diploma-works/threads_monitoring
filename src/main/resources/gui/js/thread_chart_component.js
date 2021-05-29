
window.threadChartComponent = function threadChartComponentFunction() {
    return {
        threadInfos: [],
        threadInfo: {},
        timestamp: 0,
        chartObject: {},
        data: {
            labels: [],
            datasets: [
                {label: "event", data: [], pointRadius: 10, pointHoverRadius: 15, fill: false}
            ]
        },
        config: {
            type: 'line',
            data: this.data,
            options: {
                onClick: function (element, dataAtClick) {
                    if (dataAtClick.length > 0) {
                        const pointIndex = dataAtClick[0]._index;
                        window.should_update = 1;
                        window.pointIndex = pointIndex;
                        console.log(pointIndex);
                    }
                },
                legend: {
                    display: false
                },
                scales: {
                    xAxes: [{
                        gridLines: {
                            display:false
                        }
                    }],
                    yAxes: [{
                        gridLines: {
                            display:false
                        },
                        ticks: {
                            display: false
                        }
                    }]
                }
            }
        },
        init() {
            window.should_update = 0;

            this.chartObject = new Chart (
                document.getElementById('thread_info_chart'),
                this.config
            );

            setInterval( () => {
                if (window.should_update == 1) {
                    this.threadInfo = this.threadInfos[window.pointIndex];
                    window.should_update = 0;
                }
            }, 100);

            setInterval(() => {
                if (window.isMainPagePaused == 0) {
                    fetch("http://#{GUI_URL_PREFIX}/api/threads_infos_by_thread_id?timestamp=" + this.timestamp + "&id=" + new URLSearchParams(window.location.search).get("id"))
                        .then(response => response.json())
                        .then(threads => {
                            console.log(threads);
                            for (thread of threads.reverse()) {

                                const date = new Date(thread.timestamp);
                                const time = date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();

                                if (this.data.labels.length > 50) {
                                    this.data.labels.shift();
                                }
                                this.data.labels.push(time);

                                if (this.threadInfos.length > 50) {
                                    this.threadInfos.shift();
                                }
                                this.threadInfos.push(thread);

                                for (dataset of this.data.datasets) {
                                    if (dataset.data.length > 50) {
                                        dataset.data.shift();
                                    }
                                    dataset.data.push(1);
                                }

                                if (thread.timestamp > this.timestamp) {
                                    this.timestamp = thread.timestamp;
                                }
                            }

                            this.config.data = this.data;
                            this.chartObject.update();
                    });
                }
            }, 1000);
        }
    }
}
