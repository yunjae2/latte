import * as React from 'react';
import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import Grid from '@mui/material/Grid'
import TextField from '@mui/material/TextField';
import SyntaxHighlighter from 'react-syntax-highlighter';
import { atomOneDark } from 'react-syntax-highlighter/dist/esm/styles/hljs';
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
                        <Grid item sm={2} sm={1}>
                            <LoadingButton onClick={requestRun} loading={loading} variant="contained" size="medium">Run</LoadingButton>
                        </Grid>

                        <Grid item sm={2} md={1.5}>
                            <TextField fullWidth inputRef={rps} label="RPS" variant="outlined" defaultValue={50} />
                        </Grid>
                        <Grid item sm={2} md={1.5}>
                            <TextField fullWidth inputRef={duration} label="Duration" variant="outlined" defaultValue="10s" />
                        </Grid>
                    </Grid>
                </Box>
                <SyntaxHighlighter language="shell" style={atomOneDark} codeTagProps={{ style: { fontSize: 16 } }}>
                    {output}
                </SyntaxHighlighter>
            </Container>
        </React.Fragment>
    );
}