import * as React from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import { Modal, TablePagination, TableSortLabel} from '@mui/material';
import PropTypes from 'prop-types';
import _ from 'lodash';
import LatencyChart from './latency_chart';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined';
import HistoryDetail from './history_detail';

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
                <EnhancedTableCell id="tps" align="right" label="TPS" rowSpan={2} orderBy={orderBy} order={order} createSortHandler={createSortHandler} />
                <EnhancedTableCell id="duration" align="right" label="Duration" rowSpan={2} orderBy={orderBy} order={order} createSortHandler={createSortHandler} />
                <TableCell colSpan={8} align="center" size="small">Latency&nbsp;(ms)</TableCell>
            </TableRow>
            <TableRow>
                <EnhancedTableCell id="latency.min" align="right" label="min" rowSpan={1} orderBy={orderBy} order={order} createSortHandler={createSortHandler} style={grayCell} />
                <EnhancedTableCell id="latency.avg" align="right" label="avg" rowSpan={1} orderBy={orderBy} order={order} createSortHandler={createSortHandler} style={grayCell} />
                <EnhancedTableCell id="latency.max" align="right" label="max" rowSpan={1} orderBy={orderBy} order={order} createSortHandler={createSortHandler} style={grayCell} />
                <EnhancedTableCell id="latency.p50" align="right" label="p50" rowSpan={1} orderBy={orderBy} order={order} createSortHandler={createSortHandler} />
                <EnhancedTableCell id="latency.p99" align="right" label="p99" rowSpan={1} orderBy={orderBy} order={order} createSortHandler={createSortHandler} />
                <EnhancedTableCell id="latency.p99_9" align="right" label="p99.9" rowSpan={1} orderBy={orderBy} order={order} createSortHandler={createSortHandler} />
                <EnhancedTableCell id="latency.p99_99" align="right" label="p99.99" rowSpan={1} orderBy={orderBy} order={order} createSortHandler={createSortHandler} />
                <TableCell />
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
    const { row, isActive, setActiveRowIds, setIsDetailOpen, setDetailId, refresh } = props;
    const [open, setOpen] = React.useState(false);

    const handleRowClick = () => {
        setOpen((prevOpen) => !prevOpen);
        setActiveRowIds((prevActiveRowIds) => {
            if (prevActiveRowIds.includes(row.id)) {
                return prevActiveRowIds.filter(id => (id !== row.id));
            } else {
                return [...prevActiveRowIds, row.id];
            }
        });
    }

    const displayDuration = (duration) => {
        return Math.floor(duration / 60) + ":" + String(duration % 60).padStart(2, '0');
    }

    const openDetail = (event) => {
        event.stopPropagation();
        setDetailId(row.id);
        setIsDetailOpen(true);
    }

    const deleteRow = (event) => {
        event.stopPropagation();
        fetch("/api/history?" + new URLSearchParams({ id: row.id }),
            { method: "DELETE" }
        )
            .then(res => res.json())
            .then(res => res.success)
            .then(success => {
                if (success === false || success === "false") {
                    throw new Error();
                }
            })
            .catch(() => alert("Failed to delete history"))
            .finally(() => {
                refresh();
                if (isActive) {
                    setActiveRowIds((prevActiveRowIds) => prevActiveRowIds.filter(id => (id !== row.id)));
                }
            })
    }

    return (
        <React.Fragment>
            <TableRow sx={{ '& > *': { borderBottom: 'unset' } }} onClick={handleRowClick} selected={isActive}>
                <TableCell align="right"><InfoOutlinedIcon fontSize='small' color='action' onClick={openDetail} style={{ cursor: 'pointer' }}/></TableCell>
                <TableCell colSpan={2}>
                    {row.name}
                </TableCell>
                <TableCell align="center">{row.date}</TableCell>
                <TableCell align="right">{row.actualTps.toFixed(1)}</TableCell>
                <TableCell align="right">{displayDuration((row.duration / 1000).toFixed(0))}</TableCell>
                <TableCell align="right" style={grayCell}>{row.latency.min.toFixed(0)}</TableCell>
                <TableCell align="right" style={grayCell}>{row.latency.avg.toFixed(0)}</TableCell>
                <TableCell align="right" style={grayCell}>{row.latency.max.toFixed(0)}</TableCell>
                <TableCell align="right">{row.latency.p50.toFixed(0)}</TableCell>
                <TableCell align="right">{row.latency.p99.toFixed(0)}</TableCell>
                <TableCell align="right">{row.latency.p99_9.toFixed(0)}</TableCell>
                <TableCell align="right">{row.latency.p99_99.toFixed(0)}</TableCell>
                <TableCell align="right"><DeleteOutlineIcon fontSize='small' onClick={deleteRow} style={{ cursor: 'pointer' }} /></TableCell>
            </TableRow>
        </React.Fragment>
    );
}

export default function HistoryTable(props) {
    const { rows, reloadRows } = props;
    const [order, setOrder] = React.useState('desc');
    const [orderBy, setOrderBy] = React.useState('date');
    const [page, setPage] = React.useState(0);
    const [rowsPerPage, setRowsPerPage] = React.useState(10);
    const [activeRowIds, setActiveRowIds] = React.useState([]);
    const [isDetailOpen, setIsDetailOpen] = React.useState(false);
    const [detailId, setDetailId] = React.useState(-1);

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
            <LatencyChart rows={rows.filter(row => activeRowIds.includes(row.id))} />
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
                                <Row
                                    key={row.id}
                                    row={row}
                                    isActive={activeRowIds.includes(row.id)}
                                    setActiveRowIds={setActiveRowIds}
                                    setIsDetailOpen={setIsDetailOpen}
                                    setDetailId={setDetailId}
                                    refresh={reloadRows}
                                />
                            ))}
                    </TableBody>
                    <Modal
                        open={isDetailOpen}
                        onClose={() => setIsDetailOpen(false)}
                        aria-labelledby="History detail"
                        aria-describedby="History detail"
                    >
                        <HistoryDetail id={detailId} />
                    </Modal>
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
