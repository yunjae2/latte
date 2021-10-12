# latte
Latte is a simple fixed-throughput latency testing framework that embraces [closed system model](https://www.usenix.org/legacy/event/nsdi06/tech/full_papers/schroeder/schroeder.pdf).
It is based on [k6](https://github.com/grafana/k6).


## Demo
demo.gif


## Installation
Currently only RHEL-based OSes (including Amazon Linux) are supported.
### Worker
```
$ git clone https://github.com/yunjae2/latte
$ cd latte/worker/api/run/
$ ./install.sh
$ ./run.sh
```


### Controller
1. API server
```
$ git clone https://github.com/yunjae2/latte
$ cd latte/controller/backend/run/
$ ./install.sh
$ ./install_git.sh
$ ./run.sh
```

2. GUI server
```
$ git clone https://github.com/yunjae2/latte
$ cd latte/controller/frontend/run/
$ ./install.sh 
$ ./run.sh
```
When the server is up, register the worker URL (`http://<worker ip addr>:8081`) and user info.


## Usage
1. Configuration
    
    A worker should be configured to run a test.
    Register or update the worker in the `SETTINGS` tab (skip if configured properly during installation).

2. Writing a test script

    Latte uses k6 internally, so you need to write k6 scripts for testing.
    The scripts are maintained in the controller and are accessible through git (please check `SCRIPTS` tab on the web console).
    You can check `scripts/DemoTest.js` from the initial git repository to see how the script is written.
    Please refer to the [k6 scripting guide](https://k6.io/docs/getting-started/running-k6/) for more usage.

3. Running a test

    After writing a test script, you can run the test in the `RUN` tab.
    The output of the test, which is the k6 standard output, is displayed at the `RUN` tab.
    When the testing is done, the latency distribution of the test is recorded and provided through the `HISTORY` tab.
    For performance reasons, only a single test can be run at a time.
    

## Architecture
Latte is composed of two nodes: controller and worker.
<br>
Controller includes GUI server and API server that forwards run requests to the worker.
The system state is managed in the controller.
<br>
Worker is where the actual test is run. The standard output of the test is streamed back to the controller node.
