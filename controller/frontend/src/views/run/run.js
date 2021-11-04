import * as React from 'react';
import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import Grid from '@mui/material/Grid'
import TextField from '@mui/material/TextField';
import LoadingButton from '@mui/lab/LoadingButton';
import EventSource from 'eventsource';
import { CssBaseline } from '@mui/material';

export default function Run() {
    const name = React.useRef();
    const branch = React.useRef();
    const script = React.useRef();
    const rps = React.useRef(50);
    const duration = React.useRef();
    const [output, setOutput] = React.useState("");
    const [loading, setLoading] = React.useState(false);
    const bottomRef = React.useRef();

    const scrollToBottom = () => {
        bottomRef.current?.scrollIntoView({ behavior: "smooth" });
    }

    const requestStop = () => {
        fetch("/api/run/stop", {
            method: "PUT",
        })
            .then(res => res.text())
            .then(res => setLoading(false))
            .catch(error => alert("Failed to update worker setting"));
    }

    const requestRun = () => {
        setOutput("");
        setLoading(true);
        let url = "/api/run"
        url += "?testName=" + name.current.value;
        url += "&repositoryUrl=" + "http://" + window.location.hostname + ":8082/repository";
        url += "&branchName=" + branch.current.value;
        url += "&scriptFilePath=" + script.current.value;
        url += "&rps=" + rps.current.value;
        url += "&duration=" + duration.current.value;
        url += "&estimatedLatency=" + 200;
        url += "&estimatedPeakLatency=" + 1000;

        let eventSource = new EventSource(url)

        let running = false;
        eventSource.onmessage = e => {
            setOutput(output => output + e.data);
            scrollToBottom();
            running = true;
        };

        eventSource.onerror = e => {
            if (!running) {
                if (e.status === 400) {
                    alert("Invalid arguments");
                } else {
                    alert("Another test is running currently");
                }
            }
            eventSource.close();
            setLoading(false);
        }
    }

    const requestReplay = () => {
        setOutput("");
        setLoading(true);
        let url = "/api/run/replay"
        let eventSource = new EventSource(url)

        let running = false;
        eventSource.onmessage = e => {
            setOutput(output => output + e.data);
            scrollToBottom();
            running = true;
        };

        eventSource.onerror = e => {
            if (!running) {
                if (e.status === 400) {
                    alert("Invalid arguments");
                } else {
                    alert("Another test is running currently");
                }
            }
            eventSource.close();
            setLoading(false);
        }
    };

    React.useEffect(() => {
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
                            <TextField fullWidth inputRef={branch} label="Branch" variant="outlined" defaultValue="master"/>
                        </Grid>
                        <Grid item sm={4} md={5}>
                            <TextField fullWidth inputRef={script} id="script" label="Script file" variant="outlined" defaultValue="scripts/DemoTest.js" />
                        </Grid>
                        <Grid item sm={2} md={1}>
                            {loading
                                ? <LoadingButton onClick={requestStop} variant="contained" size="medium" color="error">Stop</LoadingButton>
                                : <LoadingButton onClick={requestRun} variant="contained" size="medium">Run</LoadingButton>
                            }
                        </Grid>

                        <Grid item sm={2} md={1.5}>
                            <TextField fullWidth inputRef={rps} label="RPS" variant="outlined" defaultValue={50} />
                        </Grid>
                        <Grid item sm={2} md={1.5}>
                            <TextField fullWidth inputRef={duration} label="Duration" variant="outlined" defaultValue="10s" />
                        </Grid>
                    </Grid>
                </Box>
                <Box height="70%" paddingLeft="20" paddingBottom="10" style={{ backgroundColor: "#282c34", color: "#D4D4D4", fontFamily: "Courier", whiteSpace: "pre", overflowY: "scroll" }}>
                    {output}
                    <div ref={bottomRef} />
                </Box>
            </Container>
        </React.Fragment>
    );
}