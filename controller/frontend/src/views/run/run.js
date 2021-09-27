import * as React from 'react';
import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import Grid from '@mui/material/Grid'
import TextField from '@mui/material/TextField';
import SyntaxHighlighter from 'react-syntax-highlighter';
import { atomOneDark } from 'react-syntax-highlighter/dist/esm/styles/hljs';
import LoadingButton from '@mui/lab/LoadingButton';

export default function Run() {
    const name = React.useRef();
    const branch = React.useRef();
    const script = React.useRef();
    const [output, setOutput] = React.useState("");
    const [loading, setLoading] = React.useState(false);

    const requestRun = () => {
        setOutput("");
        setLoading(true);
        let url = new URL("http://localhost:8080/run");
        url.searchParams.set("testName", name.current.value);
        url.searchParams.set("branchName", branch.current.value);
        url.searchParams.set("scriptFilePath", script.current.value);

        let eventSource = new EventSource(url.toString())

        eventSource.onmessage = e => {
            setOutput(output => output + e.data);
        };

        eventSource.onerror = e => {
            eventSource.close();
            setLoading(false);
        }
    }

    return (
        <React.Fragment>
            <Container maxWidth="false">
                <Box sx={{ height: 20 }} />
                <Box sx={{ height: 100 }}>
                    <Grid container spacing={4} alignItems="center">
                        <Grid item sm={3} md={3}>
                            <TextField fullWidth inputRef={name} label="Name" variant="outlined" />
                        </Grid>
                        <Grid item sm={3} md={3}>
                            <TextField fullWidth inputRef={branch} label="Branch" variant="outlined" />
                        </Grid>
                        <Grid item sm={4} md={5}>
                            <TextField fullWidth inputRef={script} id="script" label="Script file" variant="outlined" />
                        </Grid>
                        <Grid item sm={2} sm={1}>
                            <LoadingButton onClick={requestRun} loading={loading} variant="contained" size="medium">Run</LoadingButton>
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