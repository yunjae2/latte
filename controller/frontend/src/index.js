import * as React from 'react';
import ReactDOM from 'react-dom';
import History from './views/history/history';
import Run from './views/run/run';
import Settings from './views/settings/settings';
import MenuBar from './menubar';
import Register from './views/register/register';
import {
  BrowserRouter as Router,
  Switch,
  Route
} from "react-router-dom";
import { LinearProgress } from '@mui/material';
import Scripts from './views/scripts/scripts';

export default function App() {
  const [loading, setLoading] = React.useState(true);
  const [registered, setRegistered] = React.useState(false);

  const checkRegistered = () => {
    if (sessionStorage.getItem("registered") === "true") {
      setRegistered(true);
      setLoading(false);
    } else {
      fetch("/api/settings/registered")
        .then(res => res.text())
        .then(registered => {
          setRegistered((registered === true || registered === "true"));
          sessionStorage.setItem("registered", registered);
        })
        .catch(error => alert("Failed to check previous registration"))
        .finally(() => setLoading(false));
    }
  }

  React.useEffect(() => {
    checkRegistered();
  }, []);

  if (loading) {
    return (
      <LinearProgress />
    )
  } else {
    if (registered !== true) {
      return <Register setRegistered={setRegistered} />
    } else {
      return (
        <Router>
          <MenuBar />
          <Switch>
            <Route path="/settings">
              <Settings />
            </Route>
            <Route path="/run">
              <Run />
            </Route>
            <Route path="/scripts">
              <Scripts />
            </Route>
            <Route path="/">
              <History />
            </Route>
          </Switch>
        </Router>
      );
    }
  }
}

ReactDOM.render(<App />, document.querySelector('#app'));