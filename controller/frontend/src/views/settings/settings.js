import { Button, Container, TextField, Grid } from '@mui/material';
import React, { useEffect, useReducer } from 'react';
import { set, get } from 'lodash';
import { Box } from '@mui/system';

const emptySettings = {
    controller: {
        worker: {
            url: "1234",
        },
        git: {
            username: "",
            password: "",
        },
    }
}

function reduceSettings(settings, action) {
    return set({ ...settings }, action.key, action.value);
}

function SettingsField(props) {
    return (
        <TextField label={props.label} value={props.value || ''} onChange={props.onChange} fullWidth margin="normal" variant="outlined" />
    );
}

export default function Settings() {
    const [settings, dispatchSettings] = useReducer(reduceSettings, emptySettings);

    const updateWorkerUrl = event => {
        dispatchSettings({ key: "controller.worker.url", value: event.target.value });
    };

    const updateGitUrl = event => {
        dispatchSettings({ key: "controller.git.url", value: event.target.value });
    };

    const updateGitUsername = event => {
        dispatchSettings({ key: "controller.git.username", value: event.target.value });
    };

    const updateGitPassword = event => {
        dispatchSettings({ key: "controller.git.password", value: event.target.value });
    };

    const requestWorkerUpdate = () => {
        fetch("/api/settings/update/worker", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ worker: get(settings, "controller.worker") }),
        })
            .then(res => res.json())
            .then(res => res.controller)
            .then(controller => dispatchSettings({ key: "controller", value: controller }))
            .catch(error => alert("Failed to update worker setting"));
    };

    const requestGitUpdate = () => {
        fetch("/api/settings/update/git", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ git: get(settings, "controller.git") }),
        })
            .then(res => res.json())
            .then(res => res.controller)
            .then(controller => dispatchSettings({ key: "controller", value: controller }))
            .catch(error => alert("Failed to update user setting"));
    };

    useEffect(() => {
        fetch("/api/settings")
            .then(res => res.json())
            .then(res => res.controller)
            .then(controller => dispatchSettings({ key: "controller", value: controller }))
            .catch(error => alert("Failed to load the current settings"));
    }, []);

    return (
        <React.Fragment>
            <Container>
                <Box sx={{ height: 10 }} />
                <Grid container spacing={4} alignItems="center">
                    <Grid item sm={10}>
                        <SettingsField label="Worker URL" value={settings.controller.worker.url} onChange={updateWorkerUrl} />
                    </Grid>
                    <Grid item sm={2}>
                        <Button onClick={requestWorkerUpdate} variant="contained" size="medium">Update</Button>
                    </Grid>
                    <Grid item sm={5}>
                        <SettingsField label="username" value={settings.controller.git.username} onChange={updateGitUsername} />
                    </Grid>
                    <Grid item sm={5}>
                        <SettingsField label="password" value={settings.controller.git.password} onChange={updateGitPassword} />
                    </Grid>
                    <Grid item sm={2}>
                        <Button onClick={requestGitUpdate} variant="contained" size="medium">Update</Button>
                    </Grid>
                </Grid>
            </Container>
        </React.Fragment>
    );
}