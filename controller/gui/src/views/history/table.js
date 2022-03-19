import * as React from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Grid, Modal, TablePagination, TableSortLabel, Typography} from '@mui/material';
import PropTypes from 'prop-types';
import _ from 'lodash';
import LatencyChart from './latency_chart';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined';
import HistoryDetail from './history_detail';
import {useEffect, useState} from "react";

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
    return order === 'DESC'
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
            sortDirection={(orderBy === id ? order : 'DESC').toLowerCase()}
            style={style}
        >
            {label}
            <br />
            <TableSortLabel
                active={orderBy === id}
                direction={(orderBy === id ? order : 'ASC').toLowerCase()}
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
                <EnhancedTableCell id="actualTps" align="right" label="TPS" rowSpan={2} orderBy={orderBy} order={order} createSortHandler={createSortHandler} />
                <EnhancedTableCell id="duration" align="right" label="Duration" rowSpan={2} orderBy={orderBy} order={order} createSortHandler={createSortHandler} />
                <TableCell colSpan={8} align="center" size="small">Latency&nbsp;(ms)</TableCell>
            </TableRow>
            <TableRow>
                <EnhancedTableCell id="latency.min" align="right" label="min" rowSpan={1} orderBy={orderBy} order={order} createSortHandler={createSortHandler} style={grayCell} />
                <EnhancedTableCell id="latency.avg" align="right" label="avg" rowSpan={1} orderBy={orderBy} order={order} createSortHandler={createSortHandler} style={grayCell} />
                <EnhancedTableCell id="latency.max" align="right" label="max" rowSpan={1} orderBy={orderBy} order={order} createSortHandler={createSortHandler} style={grayCell} />
                <EnhancedTableCell id="latency.p50" align="right" label="p50" rowSpan={1} orderBy={orderBy} order={order} createSortHandler={createSortHandler} />
                <EnhancedTableCell id="latency.p99" align="right" label="p99" rowSpan={1} orderBy={orderBy} order={order} createSortHandler={createSortHandler} />
                <EnhancedTableCell id="latency.p9c3" align="right" label="p99.9" rowSpan={1} orderBy={orderBy} order={order} createSortHandler={createSortHandler} />
                <EnhancedTableCell id="latency.p9c4" align="right" label="p99.99" rowSpan={1} orderBy={orderBy} order={order} createSortHandler={createSortHandler} />
                <TableCell />
            </TableRow>
        </TableHead>
    );
}

EnhancedTableHead.propTypes = {
    numSelected: PropTypes.number.isRequired,
    onRequestSort: PropTypes.func.isRequired,
    onSelectAllClick: PropTypes.func.isRequired,
    order: PropTypes.oneOf(['ASC', 'DESC']).isRequired,
    orderBy: PropTypes.string.isRequired,
    rowCount: PropTypes.number.isRequired,
};

function Row(props) {
    const { row, isActive, setActiveRowIds, setIsDetailOpen, setDetailId, refresh } = props;
    const [open, setOpen] = React.useState(false);
    const [deleteConfirmOpen, setDeleteConfirmOpen] = React.useState(false);

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

    const tryDelete = (event) => {
        event.stopPropagation();
        setDeleteConfirmOpen(true);
    }

    const deleteRow = (event) => {
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
                <TableCell align="right">{row.latency.p9c3?.toFixed(0) ?? '-'}</TableCell>
                <TableCell align="right">{row.latency.p9c4?.toFixed(0) ?? '-'}</TableCell>
                <TableCell align="right"><DeleteOutlineIcon fontSize='small' onClick={tryDelete} style={{ cursor: 'pointer' }} /></TableCell>
            </TableRow>
            <Dialog open={deleteConfirmOpen} fullWidth maxWidth="xs">
                <DialogTitle>Delete confirmation</DialogTitle>
                <DialogContent>
                    <Grid container>
                        <Grid xs={7}>
                            <Grid container>
                                <Typography style={{ lineHeight: '2', textAlign: "left", fontWeight: "300" }}>
                                    <Grid xs={12}>Name</Grid>
                                    <Grid xs={12}>Date</Grid>
                                </Typography>
                            </Grid>
                        </Grid>
                        <Grid xs={5}>
                            <Grid container>
                                <Typography style={{ lineHeight: '2', textAlign: "left", fontWeight: "400", flex: 1 }}>
                                    <Grid xs={12}>{row.name}</Grid>
                                    <Grid xs={12}>{row.date}</Grid>
                                </Typography>
                            </Grid>
                        </Grid>
                    </Grid>
                </DialogContent>
                <DialogActions>
                    <Button variant="contained" color="error" onClick={deleteRow}>Delete</Button>
                    <Button onClick={() => setDeleteConfirmOpen(false)}>Cancel</Button>
                </DialogActions>
            </Dialog>
        </React.Fragment>
    );
}

export default function HistoryTable() {
    const [order, setOrder] = React.useState('DESC');
    const [orderBy, setOrderBy] = React.useState('date');
    const [page, setPage] = React.useState(0);
    const [rowsPerPage, setRowsPerPage] = React.useState(10);
    const [activeRowIds, setActiveRowIds] = React.useState([]);
    const [isDetailOpen, setIsDetailOpen] = React.useState(false);
    const [detailId, setDetailId] = React.useState(-1);
    const [count, setCount] = useState(0);
    const [rows, setRows] = useState([]);

    const fetchCount = () => {
        fetch("/api/history/count")
            .then(res => res.text())
            .then(count => setCount(parseInt(count)))
            .catch(error => alert("Failed to load the number of past records"));
    }

    const fetchRows = (page, size, orderBy, order) => {
        let params = {
            page,
            size,
            orderBy,
            order
        };

        return fetch("/api/history?" + new URLSearchParams(params))
            .then(res => res.json())
            .then(res => res.records)
            .catch(error => alert("Failed to load history"))
    }

    const handleRequestSort = async (event, property) => {
        const isAsc = orderBy === property && order === 'ASC';
        let newOrder = isAsc ? 'DESC' : 'ASC';
        await updateRows(0, rowsPerPage, property, newOrder);
        setOrder(newOrder);
        setOrderBy(property);
        setPage(0);
    };

    const handlePageChange = async (event, newPage) => {
        await fetchCount();
        await updateRows(newPage, rowsPerPage, orderBy, order);
        await setPage(newPage);
    }

    const handleRowsPerPageChange = (event) => {
        let newRowsPerPage = parseInt(event.target.value, 10);
        updateRows(page, newRowsPerPage);
        setRowsPerPage(newRowsPerPage);
        setPage(0);
    }

    const updateRows = async (newPage = page, _size = rowsPerPage, _orderBy = orderBy, _order = order) => {
        let newRows = await fetchRows(newPage, _size, _orderBy, _order);
        setRows(newRows);
    }

    useEffect(async () => {
        await fetchCount();
        await updateRows(page);
    }, []);

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
                            .map((row) => (
                                <Row
                                    key={row.id}
                                    row={row}
                                    isActive={activeRowIds.includes(row.id)}
                                    setActiveRowIds={setActiveRowIds}
                                    setIsDetailOpen={setIsDetailOpen}
                                    setDetailId={setDetailId}
                                    refresh={updateRows}
                                />
                            ))}
                    </TableBody>
                    <Modal
                        open={isDetailOpen}
                        onClose={() => setIsDetailOpen(false)}
                        aria-labelledby="History detail"
                        aria-DESCribedby="History detail"
                    >
                        <HistoryDetail id={detailId} updateTable={updateRows} />
                    </Modal>
                    <TablePagination
                        rowsPerPage={rowsPerPage}
                        rowsPerPageOptions={[10, 20, 50, { label: 'All', value: -1 }]}
                        onRowsPerPageChange={handleRowsPerPageChange}
                        count={count}
                        page={page}
                        onPageChange={handlePageChange}
                    />
                </Table>
            </TableContainer>
        </React.Fragment>
    );
}
