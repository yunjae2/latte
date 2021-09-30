import * as React from 'react';
import Box from '@mui/material/Box';
import Collapse from '@mui/material/Collapse';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Typography from '@mui/material/Typography';
import Paper from '@mui/material/Paper';
import { Label, LineChart, Line, CartesianGrid, XAxis, YAxis, Tooltip } from 'recharts';
import { TablePagination, Checkbox, TableSortLabel } from '@mui/material';
import PropTypes from 'prop-types';
import _ from 'lodash';

const grayCell = {
    backgroundColor: "F0F0F0"
}

function descendingComparator(a, b, orderBy) {
    if (_.get(a, orderBy) < _.get(b, orderBy)) {
        return -1;
    }
    if (_.get(a, orderBy) > _.get(b, orderBy)) {
        return 1;
    }
    return 0;
}

function getComparator(order, orderBy) {
    return order === 'desc'
        ? (a, b) => descendingComparator(a, b, orderBy)
        : (a, b) => -descendingComparator(a, b, orderBy);
}


function EnhancedTableCell(props) {
    const { id, align, rowSpan, colSpan, orderBy, order, label, createSortHandler, style } = props;
    return (
        <TableCell
            key={id}
            align={align}
            rowSpan={rowSpan}
            colSpan={colSpan}
            sortDirection={orderBy === id ? order : false}
            style={style}
        >
            {label}
            <br/>
            <TableSortLabel
                active={orderBy === id}
                direction={orderBy === id ? order : 'asc'}
                onClick={createSortHandler(id)}
            >
            </TableSortLabel>
        </TableCell>);
}

function EnhancedTableHead(props) {
    const { order, orderBy, onRequestSort } =
        props;

    const createSortHandler = (property) => (event) => {
        onRequestSort(event, property);
    };

    return (
        <TableHead>
            <TableRow>
                <TableCell rowSpan={2} />
                <EnhancedTableCell id="name" align="left" label="Name" rowSpan={2} colSpan={2} orderBy={orderBy} order={order} createSortHandler={createSortHandler} />
                <EnhancedTableCell id="date" align="center" label="Date" rowSpan={2} orderBy={orderBy} order={order} createSortHandler={createSortHandler} />
                <EnhancedTableCell id="rps" align="right" label="RPS" rowSpan={2} orderBy={orderBy} order={order} createSortHandler={createSortHandler} />
                <EnhancedTableCell id="duration" align="right" label="Duration" rowSpan={2} orderBy={orderBy} order={order} createSortHandler={createSortHandler} />
                <TableCell colSpan={7} align="center" size="small">Latency&nbsp;(ms)</TableCell>
            </TableRow>
            <TableRow>
                <EnhancedTableCell id="latency.min" align="right" label="min" rowSpan={1} orderBy={orderBy} order={order} createSortHandler={createSortHandler} style={grayCell} />
                <EnhancedTableCell id="latency.avg" align="right" label="avg" rowSpan={1} orderBy={orderBy} order={order} createSortHandler={createSortHandler} style={grayCell} />
                <EnhancedTableCell id="latency.max" align="right" label="max" rowSpan={1} orderBy={orderBy} order={order} createSortHandler={createSortHandler} style={grayCell} />
                <EnhancedTableCell id="latency.p50" align="right" label="p50" rowSpan={1} orderBy={orderBy} order={order} createSortHandler={createSortHandler} />
                <EnhancedTableCell id="latency.p99" align="right" label="p99" rowSpan={1} orderBy={orderBy} order={order} createSortHandler={createSortHandler} />
                <EnhancedTableCell id="latency.p99_9" align="right" label="p99.9" rowSpan={1} orderBy={orderBy} order={order} createSortHandler={createSortHandler} />
                <EnhancedTableCell id="latency.p99_99" align="right" label="p99.99" rowSpan={1} orderBy={orderBy} order={order} createSortHandler={createSortHandler} />
            </TableRow>
        </TableHead>
    );
}

EnhancedTableHead.propTypes = {
    numSelected: PropTypes.number.isRequired,
    onRequestSort: PropTypes.func.isRequired,
    onSelectAllClick: PropTypes.func.isRequired,
    order: PropTypes.oneOf(['asc', 'desc']).isRequired,
    orderBy: PropTypes.string.isRequired,
    rowCount: PropTypes.number.isRequired,
};

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

    const handleRowClick = () => {
        setOpen((prevOpen) => !prevOpen);
    }

    return (
        <React.Fragment>
            <TableRow sx={{ '& > *': { borderBottom: 'unset' } }} onClick={handleRowClick}>
                <TableCell align="right" />
                <TableCell colSpan={2}>
                    {row.name}
                </TableCell>
                <TableCell align="center">{row.date}</TableCell>
                <TableCell align="right">{row.rps.toFixed(1)}</TableCell>
                <TableCell align="right">{(row.duration / 1000).toFixed(0)}</TableCell>
                <TableCell align="right" style={grayCell}>{row.latency.min.toFixed(0)}</TableCell>
                <TableCell align="right" style={grayCell}>{row.latency.avg.toFixed(0)}</TableCell>
                <TableCell align="right" style={grayCell}>{row.latency.max.toFixed(0)}</TableCell>
                <TableCell align="right">{row.latency.p50.toFixed(0)}</TableCell>
                <TableCell align="right">{row.latency.p99.toFixed(0)}</TableCell>
                <TableCell align="right">{row.latency.p99_9.toFixed(0)}</TableCell>
                <TableCell align="right">{row.latency.p99_99.toFixed(0)}</TableCell>
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

export default function CollapsibleTable(props) {
    const { rows } = props;
    const [order, setOrder] = React.useState('asc');
    const [orderBy, setOrderBy] = React.useState('calories');
    const [page, setPage] = React.useState(0);
    const [rowsPerPage, setRowsPerPage] = React.useState(10);

    const handleRequestSort = (event, property) => {
        const isAsc = orderBy === property && order === 'asc';
        setOrder(isAsc ? 'desc' : 'asc');
        setOrderBy(property);
        setPage(0);
    };

    const handlePageChange = (event, newPage) => {
        setPage(newPage);
    }

    const handleRowsPerPageChange = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    }

    return (
        <React.Fragment>
            <TableContainer component={Paper}>
                <Table aria-label="collapsible table" size="small">
                    <EnhancedTableHead
                        order={order}
                        orderBy={orderBy}
                        onRequestSort={handleRequestSort}
                        rowCount={rows.length}
                    />
                    <TableBody>
                        {rows.sort(getComparator(order, orderBy))
                            .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                            .map((row) => (
                        <Row key={row.id} row={row} />
                            ))}
                    </TableBody>
                    <TablePagination
                        rowsPerPage={rowsPerPage}
                        rowsPerPageOptions={[10, 20, 50, { label: 'All', value: -1 }]}
                        onRowsPerPageChange={handleRowsPerPageChange}
                        count={rows.length}
                        page={page} onPageChange={handlePageChange}
                    />
                </Table>
            </TableContainer>
        </React.Fragment>
    );
}
