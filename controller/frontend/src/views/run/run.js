import * as React from 'react';
import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import Grid from '@mui/material/Grid'
import TextField from '@mui/material/TextField';
import LoadingButton from '@mui/lab/LoadingButton';
import EventSource from 'eventsource';
import { CssBaseline, Button, Autocomplete } from '@mui/material';
import RunChart from './run_chart';
import _ from "lodash";

export default function Run() {
    const name = React.useRef();
    const [branch, setBranch] = React.useState("");
    const [script, setScript] = React.useState("");
    const tps = React.useRef(50);
    const duration = React.useRef();
    const [stats, setStats] = React.useState([]);
    const [chartStats, setChartStats] = React.useState([]);
    const [loading, setLoading] = React.useState(false);
    const [branches, setBranches] = React.useState([]);
    const [scripts, setScripts] = React.useState([]);

    const updateStats = (stat) => {
        let metrics = _.get(stat, 'stat.data', []);
        let iters = _.find(metrics, (metric) => metric.id === "iterations")
            ?.attributes
            ?.sample
            ?.count
            ?? 0;

        setStats(prevStats => [...prevStats, stat]);
        setChartStats(prevChartStats => {
            let chartStat = {};

            chartStat.duration = stat.time;
            chartStat.totalIters = iters;
            if (prevChartStats === undefined || prevChartStats.length == 0) {
                chartStat.tps = iters;
            } else {
                chartStat.tps = iters - prevChartStats[prevChartStats.length - 1].totalIters;
            }

            return [...prevChartStats, chartStat];
        })
    }

    const requestReset = () => {
        fetch("/api/run/reset", {
            method: "PUT",
        })
            .then(res => res.text())
            .then(res => setLoading(false))
            .catch(error => alert("Failed to reset"))
            .finally(() => requestReplay());
    }

    const requestStop = () => {
        fetch("/api/run/stop", {
            method: "PUT",
        })
            .then(res => res.text())
            .then(res => setLoading(false))
            .catch(error => alert("Failed to stop the test"));
    }

    const requestRun = () => {
        setStats([]);
        setChartStats([]);
        setLoading(true);
        let url = "/api/run"
        url += "?testName=" + name.current.value;
        url += "&repositoryUrl=" + "http://" + window.location.hostname + ":8082/repository";
        url += "&branchName=" + branch;
        url += "&scriptFilePath=" + script;
        url += "&tps=" + tps.current.value;
        url += "&duration=" + duration.current.value;
        url += "&estimatedLatency=" + 200;
        url += "&estimatedPeakLatency=" + 1000 * 60 * 5;

        let eventSource = new EventSource(url)

        let running = false;
        eventSource.onmessage = e => {
            updateStats(JSON.parse(e.data));
            running = true;
        };

        eventSource.onerror = e => {
            if (!running) {
                if (e.status === 400) {
                    alert("Invalid arguments");
                } else if (e.status === 500) {
                    alert("Another test is running currently");
                }
            }
            eventSource.close();
            setLoading(false);
        }
    }

    const requestReplay = () => {
        setStats([]);
        setChartStats([]);
        setLoading(true);
        let url = "/api/run/replay"
        let eventSource = new EventSource(url)

        let running = false;
        eventSource.onmessage = e => {
            updateStats(JSON.parse(e.data));
            running = true;
        };

        eventSource.onerror = e => {
            if (!running) {
                if (e.status === 400) {
                    alert("Invalid arguments");
                } else if (e.status === 500) {
                    alert("Another test is running currently");
                }
            }
            eventSource.close();
            setLoading(false);
        }
    };

    const loadBranches = () => {
        fetch("/api/script/branch/all")
            .then(res => res.json())
            .then(res => res.branches)
            .then(branches => {
                setBranches(branches);
                if (branches.includes("master")) {
                    setBranch("master");
                } else if (branches.includes("main")) {
                    setBranch("main");
                } else {
                    setBranch(branches[0]);
                }
            })
            .catch(error => alert("Failed to load branches"));
    }

    const loadScripts = () => {
        fetch("/api/script/all")
            .then(res => res.json())
            .then(res => res.fileInfos)
            .then(fileInfos => fileInfos.map(fileInfo => fileInfo.name))
            .then(scripts => setScripts(scripts))
            .catch(error => alert("Failed to load scripts"));
    }

    const onBranchChange = (event, value) => setBranch(value);
    const onScriptChange = (event, value) => setScript(value);

    React.useEffect(() => {
        loadBranches();
        loadScripts();
        requestReplay();
    }, []);

    return (
        <React.Fragment>
            <CssBaseline />
            <Container maxWidth="false">
                <Box sx={{ height: 20 }} />
                <Box sx={{ height: 200 }}>
                    <Grid container spacing={4} alignItems="center">
                        <Grid item sm={3} md={3}>
                            <TextField fullWidth inputRef={name} label="Name" variant="outlined" />
                        </Grid>
                        <Grid item sm={3} md={3}>
                            <Autocomplete fullWidth disablePortal value={branch} onChange={onBranchChange} options={branches} renderInput={(params) => <TextField {...params} label="Branch" />} />
                        </Grid>
                        <Grid item sm={4} md={5}>
                            <Autocomplete fullWidth disablePortal value={script} onChange={onScriptChange} options={scripts} renderInput={(params) => <TextField {...params} label="Script" />} />
                        </Grid>
                        <Grid item sm={2} md={1}>
                            {loading
                                ? <LoadingButton onClick={requestStop} variant="contained" size="medium" color="error">Stop</LoadingButton>
                                : <LoadingButton onClick={requestRun} variant="contained" size="medium">Run</LoadingButton>
                            }
                        </Grid>

                        <Grid item sm={2} md={1.5}>
                            <TextField fullWidth inputRef={tps} label="TPS" variant="outlined" defaultValue={50} />
                        </Grid>
                        <Grid item sm={2} md={1.5}>
                            <TextField fullWidth inputRef={duration} label="Duration" variant="outlined" defaultValue="10s" />
                        </Grid>
                    </Grid>
                </Box>
                <Box height="40%">
                    <RunChart data={chartStats} />
                </Box>
                <Box>
                    <Grid container justifyContent="flex-end">
                        <Button onClick={requestReset} variant="outlined" color="error">Reset</Button>
                    </Grid>
                </Box>
            </Container>
        </React.Fragment>
    );
}