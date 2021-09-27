import React, { useEffect } from "react";
import { Tabs, Tab } from "@mui/material";
import { withRouter } from "react-router-dom";

const MenuBar = props => {
    const {history, location} = props;
    const [value, setValue] = React.useState(location.pathname);

    const handleChange = (event, newValue) => {
        history.push(newValue);
        setValue(newValue);
    }
    return (
        <Tabs value={value} onChange={handleChange}>
            <Tab label='History' value="/" />
            <Tab label='Run' value="/run" />
            <Tab label='Settings' value="/settings" />
        </Tabs>
    );
}

export default withRouter(MenuBar)