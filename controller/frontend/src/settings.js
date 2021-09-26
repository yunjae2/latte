import { Button, TextField } from '@mui/material';
import React, { useEffect, useReducer } from 'react';
import { set } from 'lodash';

const emptySettings = {
    controller: {
        worker: {
            url: "1234",
        },
        git: {
            url: "",
            token: {
                name: "",
                value: "",
            },
        },
    }
}

function reduceSettings(settings, action) {
    return set({...settings}, action.key, action.value);
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

    const updateGitTokenName = event => {
        dispatchSettings({ key: "controller.git.token.name", value: event.target.value });
    };

    const updateGitTokenValue = event => {
        dispatchSettings({ key: "controller.git.token.value", value: event.target.value });
    };

    const requestUpdate = () => {
        fetch("http://localhost:8080/settings/update", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(settings)
        })
            .then(res => res.json())
            .then(res => res.controller)
            .then(controller => dispatchSettings({ key: "controller", value: controller }))
            .catch(error => console.error(error));
    };

    useEffect(() => {
        fetch("http://localhost:8080/settings")
            .then(res => res.json())
            .then(res => res.controller)
            .then(controller => dispatchSettings({ key: "controller", value: controller }))
    }, []);

    return (
        <React.Fragment>
            <SettingsField label="Worker URL" value={settings.controller.worker.url} onChange={updateWorkerUrl} />
            <SettingsField label="Script repo URL" value={settings.controller.git.url} onChange={updateGitUrl} />
            <SettingsField label="Script repo token name" value={settings.controller.git.token.name} onChange={updateGitTokenName} />
            <SettingsField label="Script repo token value" value={settings.controller.git.token.value} onChange={updateGitTokenValue} />
            <Button onClick={requestUpdate} variant="contained" size="medium">Update</Button>
        </React.Fragment>
    );
}