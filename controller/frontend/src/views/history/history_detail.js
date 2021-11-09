import { Grid, Skeleton, Typography } from "@mui/material";
import { Box } from "@mui/system";
import React from "react";

export default function HistoryDetail(props) {
    const { id } = props;
    const [detail, setDetail] = React.useState({});
    const [loading, setLoading] = React.useState(true);

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
                <Typography variant="h6">
                    {detail.name}
                </Typography>
                <Typography variant="subtitle1">
                    {detail.date} (Duration: {displayDuration((detail.duration / 1000).toFixed(0))})
                </Typography>
                <Grid container>
                    <Grid item xs={3}>
                        <Grid container>
                            <Typography variant="subtitle1" sx={{ mt: 2 }} style={{ textAlign: "left", fontWeight: "300"}}>
                                <Grid item xs={12}>TPS (requested)</Grid>
                                <Grid item xs={12}>TPS (actual)</Grid>
                                <Grid item xs={12}>Total</Grid>
                                <Grid item xs={12}>Success</Grid>
                                <Grid item xs={12}>Fail</Grid>
                            </Typography>
                        </Grid>
                    </Grid>
                    <Grid item xs={3}>
                        <Grid container>
                            <Typography variant="subtitle1" sx={{ mt: 2 }} style={{ textAlign: "right", fontWeight: "400"}}>
                                <Grid item xs={12}>{detail.requestedTps?.toFixed(1) ?? "-"}</Grid>
                                <Grid item xs={12}>{detail.actualTps.toFixed(1)}</Grid>
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