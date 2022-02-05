import React, { useEffect } from "react";
import { Tabs, Tab, AppBar, CssBaseline, Toolbar } from "@mui/material";
import { withRouter } from "react-router-dom";

const MenuBar = props => {
    const { history, location } = props;
    const [value, setValue] = React.useState(location.pathname);

    const handleChange = (event, newValue) => {
        history.push(newValue);
        setValue(newValue);
    }
    return (
        <React.Fragment>
            <CssBaseline />
            <AppBar position="relative" color="transparent">
                <Toolbar variant="dense">
                <Tabs value={value} onChange={handleChange}>
                    <Tab label='History' value="/" />
                    <Tab label='Run' value="/run" />
                    <Tab label='Scripts' value="/scripts" />
                    <Tab label='Settings' value="/settings" />
                </Tabs>
                </Toolbar>
            </AppBar>
        </React.Fragment>
    );
}

export default withRouter(MenuBar)