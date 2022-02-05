import React from 'react';
import { Button, Container, TextField, Grid, Typography } from '@mui/material';
import { Box } from '@mui/system';

function SettingsField(props) {
    return (
        <TextField label={props.label} value={props.value || ''} onChange={props.onChange} fullWidth variant="outlined" />
    );
}

export default function Register(props) {
    const { setRegistered } = props;
    const [workerUrl, setWorkerUrl] = React.useState("");
    const [username, setUsername] = React.useState("");
    const [password, setPassword] = React.useState("");

    const updateSetting = (setSetting) => {
        return event => setSetting(event.target.value);
    }

    const requestRegistration = (event) => {
        fetch("/api/settings/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                settings: {
                    workerUrl,
                    username,
                    password,
                }
            }),
        })
            .then(res => res.json())
            .then(res => res.registered)
            .then(registered => {
                setRegistered((registered === "true" || registered === true));
                sessionStorage.setItem("registered", registered);
            })
            .catch(error => alert("Registration failed"));
    };

    return (
        <React.Fragment>
            <Container>
                <Typography variant="h2" align="center">First setup</Typography>
                <Box sx={{ height: 30 }} />
                <Grid container spacing={4} alignItems="center">
                    <Grid item xs={12}>
                        <SettingsField label="Worker URL" value={workerUrl} onChange={updateSetting(setWorkerUrl)} />
                    </Grid>
                    <Grid item xs={12}>
                        <SettingsField label="Username" value={username} onChange={updateSetting(setUsername)} />
                    </Grid>
                    <Grid item xs={12}>
                        <SettingsField label="Password" value={password} onChange={updateSetting(setPassword)} />
                    </Grid>
                    <Grid item xs={12}>
                        <Button style={{ float: "right" }} onClick={requestRegistration} variant="contained" size="medium">Register</Button>
                    </Grid>
                </Grid>
            </Container>
        </React.Fragment>
    );
}
