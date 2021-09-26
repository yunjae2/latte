import * as React from 'react';
import CssBaseline from '@mui/material/CssBaseline';
import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import Grid from '@mui/material/Grid'
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import SyntaxHighlighter from 'react-syntax-highlighter';
import { atomOneDark } from 'react-syntax-highlighter/dist/esm/styles/hljs';

const demoScript = `import http from 'k6/http';
import { URLSearchParams } from 'https://jslib.k6.io/url/1.0.0/index.js';
import { check } from 'k6';

const andKeywords = open("/home/k6/resources/and_keywords.txt").split("\n").filter(k => k)
const orKeywords = open("/home/k6/resources/or_keywords.txt").split("\n").filter(k => k)

/*
 * Utility methods
 */
function getRandomInt(max) {
	return Math.floor(Math.random() * max)
}

function getRandomItem(items) {
	return items[getRandomInt(items.length)]
}

/*
 * Helper methods
 */
function getKeyword() {
	const AND_THRESHOLD = 92

	if (getRandomInt(100) < AND_THRESHOLD)
		return getRandomItem(andKeywords)
	else
		return getRandomItem(orKeywords)
}

function buildAddress() {
	const url = "http://internal-sp-baemin-search-testing-6d42-459688424.ap-northeast-2.elb.amazonaws.com/shops/search"
	const params = {
		sortSet: "DEFAULT",
		latitude: "37.51690682",
		longitude: "127.1127602",
		subRegionId4User: "11710562",
		keyword: getKeyword(),
		filters: "DEFAULT",
	}

	const searchParams = new URLSearchParams(params)

	return url + "?" + searchParams
}

/*
 * k6 option and callback
 */
export let options = {
	discardResponseBodies: true,
	scenarios: {
		contacts: {
			executor: 'constant-arrival-rate',
			// TODO: use argument for the constant rate
			rate: 1000,
			// TODO: use argument for the duration
			duration: '3m',
			// TODO: use argument for the VUs
			preAllocatedVUs: 200,
			maxVUs: 1000,
		},
	},
	summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(50)', 'p(99)', 'p(99.9)', 'p(99.99)', 'p(100)', 'count'],
	thresholds: {
		http_req_failed: ['rate < 0.01'],
		// TODO: use argument for the latency threshold
		http_req_duration: ['p(99.99) < 700']
	}
}

export default function() {
	let res = http.get(buildAddress())
	check(res, { 'status was 200': (r) => r.status == 200});
}
`;

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

		var openOnce = false;
		eventSource.onopen = e => {
			if (openOnce) {
				eventSource.close();
			}
			openOnce = true;
		}
	}

    return (
        <React.Fragment>
            <CssBaseline />
            <Container maxWidth="false">
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
				<div style={{ whiteSpace: "pre", fontFamily: "monospace", fontSize: 14 }}>
					{output}
				</div>
                <SyntaxHighlighter showLineNumbers language="javascript" style={atomOneDark}>
                    {demoScript}
                </SyntaxHighlighter>
            </Container>
        </React.Fragment>
    );
}