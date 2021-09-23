import * as React from 'react';
import PropTypes from 'prop-types';
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

/* TODO: show VU stats */
function createData(name, date, rps, duration, min, max, p50, p99, p99_9, p99_99) {
    return {
        name,
        date,
        rps,
        duration,
        min,
        max,
        latency: [
            {
                percentile: "p50",
                value: p50
            },
            {
                percentile: "p99",
                value: p99
            },
            {
                percentile: "p99.9",
                value: p99_9
            },
            {
                percentile: "p99.99",
                value: p99_99
            },

        ]
    };
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
                <TableCell align="center">{row.rps}</TableCell>
                <TableCell align="center">{row.duration}</TableCell>
                <TableCell align="right">{row.min}</TableCell>
                <TableCell align="right">{row.max}</TableCell>
                <TableCell align="right">{row.latency[0].value}</TableCell>
                <TableCell align="right">{row.latency[1].value}</TableCell>
                <TableCell align="right">{row.latency[2].value}</TableCell>
                <TableCell align="right">{row.latency[3].value}</TableCell>
            </TableRow>
            <TableRow>
                <TableCell style={{ paddingBottom: 0, paddingTop: 0 }} colSpan={11}>
                    <Collapse in={open} timeout="auto" unmountOnExit>
                        <Box sx={{ margin: 1 }}>
                            <Typography variant="h6" gutterBottom component="div">
                                Latency distribution
                            </Typography>
                            <LineChart width={600} height={300} data={row.latency} margin={{ top: 5, right: 20, bottom: 5, left: 5 }}>
                                <Tooltip />
                                <XAxis dataKey="percentile">
                                    <Label value="Percentile" position="insideBottom" offset={0}/>
                                </XAxis>
                                <YAxis>
                                    <Label value="Latency (ms)" angle={-90} position="insideLeft" style={{ textAnchor: 'middle' }} />
                                </YAxis>
                                <CartesianGrid stroke="#ccc" strokeDasharray="5 5" />
                                <Line dataKey="value" barSize={50} stroke="#8884d8" />
                            </LineChart>
                        </Box>
                    </Collapse>
                </TableCell>
            </TableRow>
        </React.Fragment>
    );
}

Row.propTypes = {
    row: PropTypes.shape({
        calories: PropTypes.number.isRequired,
        carbs: PropTypes.number.isRequired,
        fat: PropTypes.number.isRequired,
        history: PropTypes.arrayOf(
            PropTypes.shape({
                amount: PropTypes.number.isRequired,
                customerId: PropTypes.string.isRequired,
                date: PropTypes.string.isRequired,
            }),
        ).isRequired,
        name: PropTypes.string.isRequired,
        price: PropTypes.number.isRequired,
        protein: PropTypes.number.isRequired,
    }).isRequired,
};

const rows = [
    createData('Test 1', '2021-09-10', 1000.0, 300, 90, 753, 162, 382, 486, 576),
    createData('Test 2', '2021-09-05', 1000.0, 300, 88, 690, 170, 260, 430, 570),
    createData('Test 3', '2021-09-03', 500.0, 300, 62, 540, 133, 310, 393, 452),
    createData('Test 4', '2021-09-02', 500.0, 300, 64, 592, 152, 355, 438, 513),
];

export default function CollapsibleTable() {
    return (
        <TableContainer component={Paper}>
            <Table aria-label="collapsible table" size="small">
                <TableHead>
                    <TableRow>
                        <TableCell rowSpan={2} />
                        <TableCell rowSpan={2} align="center">Name</TableCell>
                        <TableCell rowSpan={2} align="center">Date</TableCell>
                        <TableCell rowSpan={2} align="center">RPS</TableCell>
                        <TableCell rowSpan={2} align="center">Duration&nbsp;(s)</TableCell>
                        <TableCell colSpan={6} align="center" size="small">Latency&nbsp;(ms)</TableCell>
                    </TableRow>
                    <TableRow>
                        <TableCell align="right" size="small">min</TableCell>
                        <TableCell align="right" size="small">max</TableCell>
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
    );
}
