import { Button, Container, TextField, Grid } from '@mui/material';
import React, { useEffect, useState } from 'react';
import { get } from 'lodash';
import { Box } from '@mui/system';

const emptySettings = {
    workerUrl: "",
    username: "",
    password: "",
}

function SettingsField(props) {
    return (
        <TextField label={props.label} value={props.value || ''} onChange={props.onChange} fullWidth margin="normal" variant="outlined" />
    );
}

export default function Settings() {
    const [settings, setSettings] = useState(emptySettings);

    const buildUpdater = (key) => {
        return event => {
            setSettings(prevSettings => ({
                ...prevSettings,
                [key]: event.target.value,
            }))
        }
    }

    const requestWorkerUpdate = () => {
        fetch("/api/settings/update/worker", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ workerUrl: get(settings, "workerUrl") }),
        })
            .then(res => res.json())
            .then(res => res.settings)
            .then(settings => setSettings(settings))
            .catch(error => alert("Failed to update worker setting"));
    };

    const requestAuthUpdate = () => {
        fetch("/api/settings/update/auth", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                username: get(settings, "username"),
                password: get(settings, "password"),
            }),
        })
            .then(res => res.json())
            .then(res => res.settings)
            .then(settings => setSettings(settings))
            .catch(error => alert("Failed to update user setting"));
    };

    useEffect(() => {
        fetch("/api/settings")
            .then(res => res.json())
            .then(res => res.settings)
            .then(settings => setSettings(settings))
            .catch(error => alert("Failed to load the current settings"));
    }, []);

    return (
        <React.Fragment>
            <Container>
                <Box sx={{ height: 10 }} />
                <Grid container spacing={4} alignItems="center">
                    <Grid item sm={10}>
                        <SettingsField label="Worker URL" value={settings.workerUrl} onChange={buildUpdater("workerUrl")} />
                    </Grid>
                    <Grid item sm={2}>
                        <Button onClick={requestWorkerUpdate} variant="contained" size="medium">Update</Button>
                    </Grid>
                    <Grid item sm={5}>
                        <SettingsField label="username" value={settings.username} onChange={buildUpdater("username")} />
                    </Grid>
                    <Grid item sm={5}>
                        <SettingsField label="password" value={settings.password} onChange={buildUpdater("password")} />
                    </Grid>
                    <Grid item sm={2}>
                        <Button onClick={requestAuthUpdate} variant="contained" size="medium">Update</Button>
                    </Grid>
                </Grid>
            </Container>
        </React.Fragment>
    );
}