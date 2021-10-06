import * as React from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import { TablePagination, TableSortLabel } from '@mui/material';
import PropTypes from 'prop-types';
import _ from 'lodash';
import LatencyChart from './latency_chart';

const grayCell = {
    backgroundColor: "F0F0F0"
}

function descendingComparator(a, b, orderBy) {
    if (_.get(a, orderBy) > _.get(b, orderBy)) {
        return -1;
    }
    if (_.get(a, orderBy) < _.get(b, orderBy)) {
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
            <br />
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
                <EnhancedTableCell id="rps" align="right" label="TPS" rowSpan={2} orderBy={orderBy} order={order} createSortHandler={createSortHandler} />
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

function Row(props) {
    const { row, isActive, setActiveRows } = props;
    const [open, setOpen] = React.useState(false);

    const handleRowClick = () => {
        setOpen((prevOpen) => !prevOpen);
        setActiveRows((prevActiveRows) => {
            if (prevActiveRows.includes(row)) {
                return prevActiveRows.filter(activeRow => (activeRow !== row));
            } else {
                return [...prevActiveRows, row];
            }
        });
    }

    const displayDuration = (duration) => {
        return Math.floor(duration / 60) + ":" + String(duration % 60).padStart(2, '0');
    }

    return (
        <React.Fragment>
            <TableRow sx={{ '& > *': { borderBottom: 'unset' } }} onClick={handleRowClick} selected={isActive}>
                <TableCell align="right" />
                <TableCell colSpan={2}>
                    {row.name}
                </TableCell>
                <TableCell align="center">{row.date}</TableCell>
                <TableCell align="right">{row.rps.toFixed(1)}</TableCell>
                <TableCell align="right">{displayDuration((row.duration / 1000).toFixed(0))}</TableCell>
                <TableCell align="right" style={grayCell}>{row.latency.min.toFixed(0)}</TableCell>
                <TableCell align="right" style={grayCell}>{row.latency.avg.toFixed(0)}</TableCell>
                <TableCell align="right" style={grayCell}>{row.latency.max.toFixed(0)}</TableCell>
                <TableCell align="right">{row.latency.p50.toFixed(0)}</TableCell>
                <TableCell align="right">{row.latency.p99.toFixed(0)}</TableCell>
                <TableCell align="right">{row.latency.p99_9.toFixed(0)}</TableCell>
                <TableCell align="right">{row.latency.p99_99.toFixed(0)}</TableCell>
            </TableRow>
        </React.Fragment>
    );
}

export default function HistoryTable(props) {
    const { rows } = props;
    const [order, setOrder] = React.useState('desc');
    const [orderBy, setOrderBy] = React.useState('date');
    const [page, setPage] = React.useState(0);
    const [rowsPerPage, setRowsPerPage] = React.useState(10);
    const [activeRows, setActiveRows] = React.useState([]);

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
            <LatencyChart rows={activeRows} />
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
                                <Row key={row.id} row={row} isActive={activeRows.includes(row)} setActiveRows={setActiveRows} />
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
