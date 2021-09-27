import * as React from 'react';
import ReactDOM from 'react-dom';
import History from './history';
import Run from './run';
import Settings from './settings';
import MenuBar from './menubar';
import {
  BrowserRouter as Router,
  Switch,
  Route
} from "react-router-dom";

export default function App() {
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
        <Route path="/">
          <History />
        </Route>
      </Switch>
    </Router >
  );
}

ReactDOM.render(<App />, document.querySelector('#app'));