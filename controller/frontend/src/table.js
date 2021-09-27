import * as React from 'react';
import Box from '@mui/material/Box';
import Collapse from '@mui/material/Collapse';
import IconButton from '@mui/material/IconButton';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Typography from '@mui/material/Typography';
import Paper from '@mui/material/Paper';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';
import { Label, LineChart, Line, CartesianGrid, XAxis, YAxis, Tooltip } from 'recharts';

const grayCell = {
    backgroundColor: "F0F0F0"
}

/* TODO: show VU stats */
function convertToChartData(latency) {
    let data = Object.keys(latency).filter(key => key.startsWith("p"))
        .map(key => ({
            percentile: key.replace("_", "."),
            value: latency[key],
        }));
    return data;
}

function LatencyLabel(props) {
    const { x, y, stroke, value } = props;

    return (
        <text x={x} y={y} dy={-10} fill={stroke} textAnchor="middle">
            {value.toFixed(0)}
        </text>
    );
}

function Row(props) {
    const { row } = props;
    const [open, setOpen] = React.useState(false);

    return (
        <React.Fragment>
            <TableRow sx={{ '& > *': { borderBottom: 'unset' } }}>
                <TableCell>
                    <IconButton
                        aria-label="expand row"
                        size="small"
                        onClick={() => setOpen(!open)}
                    >
                        {open ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
                    </IconButton>
                </TableCell>
                <TableCell component="th" scope="row">
                    {row.name}
                </TableCell>
                <TableCell align="center">{row.date}</TableCell>
                <TableCell align="center">{row.rps.toFixed(1)}</TableCell>
                <TableCell align="center">{(row.duration / 1000).toFixed(0)}</TableCell>
                <TableCell align="right" style={grayCell}>{row.latency.min.toPrecision(3)}</TableCell>
                <TableCell align="right" style={grayCell}>{row.latency.avg.toPrecision(3)}</TableCell>
                <TableCell align="right" style={grayCell}>{row.latency.max.toPrecision(3)}</TableCell>
                <TableCell align="right">{row.latency.p50.toPrecision(3)}</TableCell>
                <TableCell align="right">{row.latency.p99.toPrecision(3)}</TableCell>
                <TableCell align="right">{row.latency.p99_9.toPrecision(3)}</TableCell>
                <TableCell align="right">{row.latency.p99_99.toPrecision(3)}</TableCell>
            </TableRow>
            <TableRow>
                <TableCell style={{ paddingBottom: 0, paddingTop: 0 }} colSpan={12}>
                    <Collapse in={open} timeout="auto" unmountOnExit>
                        <Box sx={{ margin: 1 }}>
                            <Typography variant="h6" gutterBottom component="div">
                                Latency distribution
                            </Typography>
                            <LineChart width={600} height={300} data={convertToChartData(row.latency)} margin={{ top: 5, right: 20, bottom: 5, left: 15 }}>
                                <Tooltip />
                                <XAxis dataKey="percentile">
                                    <Label value="Percentile" position="insideBottom" offset={0} />
                                </XAxis>
                                <YAxis tick={{ dx: -10 }}>
                                    <Label dx={-10} value="Latency (ms)" angle={-90} position="insideLeft" style={{ textAnchor: 'middle' }} />
                                </YAxis>
                                <CartesianGrid stroke="#ccc" strokeDasharray="5 5" />
                                <Line dataKey="value" stroke="#8884d8" label={<LatencyLabel />} />
                            </LineChart>
                        </Box>
                    </Collapse>
                </TableCell>
            </TableRow>
        </React.Fragment>
    );
}

/* TODO: paging */
export default function CollapsibleTable(props) {
    const { rows } = props;

    return (
        <React.Fragment>
            <TableContainer component={Paper}>
                <Table aria-label="collapsible table" size="small">
                    <TableHead>
                        <TableRow>
                            <TableCell rowSpan={2} />
                            <TableCell rowSpan={2} align="center">Name</TableCell>
                            <TableCell rowSpan={2} align="center">Date</TableCell>
                            <TableCell rowSpan={2} align="center">RPS</TableCell>
                            <TableCell rowSpan={2} align="center">Duration&nbsp;(s)</TableCell>
                            <TableCell colSpan={7} align="center" size="small">Latency&nbsp;(ms)</TableCell>
                        </TableRow>
                        <TableRow>
                            <TableCell align="right" size="small" style={grayCell}>min</TableCell>
                            <TableCell align="right" size="small" style={grayCell}>avg</TableCell>
                            <TableCell align="right" size="small" style={grayCell}>max</TableCell>
                            <TableCell align="right" size="small">p50</TableCell>
                            <TableCell align="right" size="small">p99</TableCell>
                            <TableCell align="right" size="small">p99.9</TableCell>
                            <TableCell align="right" size="small">p99.99</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {rows.map((row) => (
                            <Row key={row.name} row={row} />
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </React.Fragment>
    );
}
