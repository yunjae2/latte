import * as React from 'react';
import ReactDOM from 'react-dom';
import History from './history';
import Run from './run';
import Settings from './settings';
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link
} from "react-router-dom";
import { Tabs } from '@mui/material';
import { Tab } from '@mui/material';

/* TODO: fix bug that wrong tab is highlighted after refresh */
export default function App() {
  const [value, setValue] = React.useState("1");

  const handleChange = (event, newValue) => {
    setValue(newValue);
  }

  return (
    <Router>
      <div>
        <Tabs value={value} onChange={handleChange}>
          <Tab label='History' component={Link} to={"/"} value="1" />
          <Tab label='Run' component={Link} to={"/run"} value="2" />
          <Tab label='Settings' component={Link} to={"/settings"} value="3" />
        </Tabs>

        {/* A <Switch> looks through its children <Route>s and
            renders the first one that matches the current URL. */}
        <Switch>
          <Route path="/settings">
            <Settings />
          </Route>
          <Route path="/run">
            <Run />
          </Route>
          <Route path="/">
            <History />
          </Route>
        </Switch>
      </div>
    </Router >
  );
}

ReactDOM.render(<App />, document.querySelector('#app'));