import * as React from 'react';
import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import Grid from '@mui/material/Grid'
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import SyntaxHighlighter from 'react-syntax-highlighter';
import { atomOneDark } from 'react-syntax-highlighter/dist/esm/styles/hljs';
import { CssBaseline } from '@mui/material';

export default function Run() {
	const name = React.useRef();
	const branch = React.useRef();
	const script = React.useRef();
	const [ output, setOutput ] = React.useState("");

	const requestRun = () => {
		setOutput("")
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
		}
	}

    return (
        <React.Fragment>
            <Container maxWidth="false">
				<Box sx={{ height: 30 }} />
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
                            <Button onClick={requestRun} variant="contained" size="medium">Run</Button>
                        </Grid>
                    </Grid>
                </Box>
                <SyntaxHighlighter language="shell" style={atomOneDark} codeTagProps={{style: {fontSize: 16}}}>
					{output}
                </SyntaxHighlighter>
            </Container>
        </React.Fragment>
    );
}