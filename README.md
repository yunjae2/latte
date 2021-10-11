# latte
Latte is a simple latency testing framework that embraces [closed system model](https://www.usenix.org/legacy/event/nsdi06/tech/full_papers/schroeder/schroeder.pdf).
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
When the server is up, register the worker URL (`http://<worker ip>:8081`) and user info.


## Architecture
Latte is composed of two nodes: controller and worker.
<br>
Controller includes GUI server and API server that forwards run requests to the worker.
The system state is managed in the controller.
<br>
Worker is where the actual test is run. The standard output of the test is streamed back to the controller node.
