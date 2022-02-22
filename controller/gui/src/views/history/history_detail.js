import {Button, Grid, Skeleton, TextField, Typography} from "@mui/material";
import { Box } from "@mui/system";
import React from "react";
import FileDownloadOutlinedIcon from '@mui/icons-material/FileDownloadOutlined';
import { saveAs } from 'file-saver';
import EditIcon from '@mui/icons-material/Edit';
import SaveIcon from '@mui/icons-material/Save';
import ClearIcon from '@mui/icons-material/Clear';

export default function HistoryDetail(props) {
    const { id, updateTable } = props;
    const [detail, setDetail] = React.useState({});
    const [loading, setLoading] = React.useState(true);
    const [editing, setEditing] = React.useState(false);
    const newName = React.useRef();

    const style = {
        position: 'absolute',
        top: '50%',
        left: '50%',
        transform: 'translate(-50%, -50%)',
        width: '80%',
        bgcolor: 'white',
        boxShadow: 24,
        borderRadius: 1,
        p: 4,
    };

    const displayDuration = (duration) => {
        return Math.floor(duration / 60) + ":" + String(duration % 60).padStart(2, '0');
    }

    const loadHistoryDetail = () => {
        setLoading(true);
        fetch("/api/history/" + id + "/detail")   
            .then(res => res.json())
            .then(res => res.detail)
            .then(detail => setDetail(detail))
            .then(() => setLoading(false))
            .catch(() => alert("Failed to load the history"));
    }

    const cancelEdit = (event) => {
        event.stopPropagation();
        setEditing(false);
    }

    const saveName = (event) => {
        event.stopPropagation();
        fetch(`/api/history/${id}/detail`, {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json",
            },
            body: newName.current.value,
        })
            .then(res => {
                if (!res.ok) {
                    throw new Error();
                }
                return res.json()
            })
            .then(res => res.detail)
            .then(detail => setDetail(detail))
            .catch(() => alert("Failed to update the test name"))
            .finally(() => {
                setEditing(false)
                updateTable();
            });
    }

    const editName = (event) => {
        event.stopPropagation();
        setEditing(true);
    }

    const downloadLog = () => {
        saveAs(`/api/history/${id}/log`, `${detail.name}-${detail.date}.log`);
    }

    React.useEffect(() => {
        loadHistoryDetail();
    }, []);

    if (loading) {
        return (
            <Box sx={style}>
                <Skeleton variant="text" height="80" />
                <Skeleton variant="rectangular" height="600"/>
            </Box>
        );
    } else {
        return (
            <Box sx={style}>
                <Grid container>
                    <Grid item xs={6}>
                        <Grid container>
                            <Grid item xs={12}>
                                {editing ?
                                    <Typography variant="h6">
                                        <TextField inputRef={newName} defaultValue={detail.name} variant="standard" />
                                        <ClearIcon fontSize="small" onClick={cancelEdit} style={{ cursor: 'pointer' }} />
                                        <span>&nbsp;&nbsp;&nbsp;&nbsp;</span>
                                        <SaveIcon onClick={saveName} style={{ cursor: 'pointer' }} />
                                    </Typography>
                                    :
                                    <Typography variant="h6">
                                        {detail.name}
                                        <span>&nbsp;&nbsp;</span>
                                        <EditIcon fontSize="small" onClick={editName} style={{ cursor: 'pointer' }}/>
                                    </Typography>
                                }
                            </Grid>
                            <Grid item xs={12}>
                                <Typography variant="subtitle1">
                                    {detail.date} (Duration: {displayDuration((detail.duration / 1000).toFixed(0))})
                                </Typography>
                            </Grid>
                        </Grid>
                    </Grid>
                    <Grid item xs={6} style={{ textAlign: "right", fontWeight: "400"}}>
                        <Button size="large" variant="text" onClick={downloadLog} endIcon={<FileDownloadOutlinedIcon />}>
                            Log
                        </Button>
                    </Grid>
                </Grid>
                <Grid container>
                    <Grid item xs={1.5}>
                        <Grid container>
                            <Typography variant="subtitle1" sx={{ mt: 2 }} style={{ textAlign: "left", fontWeight: "300"}}>
                                <Grid item xs={12}>TPS</Grid>
                                <Grid item xs={12}><br /></Grid>
                                <Grid item xs={12}>Iterations</Grid>
                                <Grid item xs={12}><br /></Grid>
                                <Grid item xs={12}><br /></Grid>
                                <Grid item xs={12}>Requests</Grid>
                                <Grid item xs={12}><br /></Grid>
                                <Grid item xs={12}><br /></Grid>
                            </Typography>
                        </Grid>
                    </Grid>
                    <Grid item xs={1.5}>
                        <Grid container>
                            <Typography variant="subtitle1" sx={{ mt: 2 }} style={{ textAlign: "left", fontWeight: "300"}}>
                                <Grid item xs={12}>requested</Grid>
                                <Grid item xs={12}>actual</Grid>
                                <Grid item xs={12}>total</Grid>
                                <Grid item xs={12}>success</Grid>
                                <Grid item xs={12}>fail</Grid>
                                <Grid item xs={12}>total</Grid>
                                <Grid item xs={12}>success</Grid>
                                <Grid item xs={12}>fail</Grid>
                            </Typography>
                        </Grid>
                    </Grid>
                    <Grid item xs={3}>
                        <Grid container>
                            <Typography variant="subtitle1" sx={{ mt: 2 }} style={{ textAlign: "right", fontWeight: "400"}}>
                                <Grid item xs={12}>{detail.requestedTps?.toFixed(1) ?? "-"}</Grid>
                                <Grid item xs={12}>{detail.actualTps.toFixed(1)}</Grid>
                                <Grid item xs={12}>{detail.iterationTotal}</Grid>
                                <Grid item xs={12}>{detail.iterationSuccess}</Grid>
                                <Grid item xs={12}>{detail.iterationFail}</Grid>
                                <Grid item xs={12}>{detail.requestCount}</Grid>
                                <Grid item xs={12}>{detail.successCount}</Grid>
                                <Grid item xs={12}>{detail.failCount}</Grid>
                            </Typography>
                        </Grid>
                    </Grid>
                    <Grid item xs={3}>
                        <Grid container>
                            <Typography variant="subtitle1" sx={{ mt: 2 }} style={{ textAlign: "left", fontWeight: "300"}}>
                                <Grid item xs={12}>Branch</Grid>
                                <Grid item xs={12}>Script</Grid>
                            </Typography>
                        </Grid>
                    </Grid>
                    <Grid item xs={3}>
                        <Grid container>
                            <Typography variant="subtitle1" sx={{ mt: 2 }} style={{ textAlign: "left", fontWeight: "400"}}>
                                <Grid item xs={12}>{detail.branchName}</Grid>
                                <Grid item xs={12}>{detail.scriptFilePath}</Grid>
                            </Typography>
                        </Grid>
                    </Grid>
                </Grid>
            </Box>
        );
    }
}