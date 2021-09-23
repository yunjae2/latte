import * as React from 'react';
import ReactDOM from 'react-dom';
import History from './history';
import Run from './run';
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link
} from "react-router-dom";

export default function App() {
  return (
    <Router>
      <div>
        <nav>
          <ul>
            <li>
              <Link to="/">History</Link>
            </li>
            <li>
              <Link to="/run">Run</Link>
            </li>
          </ul>
        </nav>

        {/* A <Switch> looks through its children <Route>s and
            renders the first one that matches the current URL. */}
        <Switch>
          <Route path="/run">
            <Run />
          </Route>
          <Route path="/">
            <History />
          </Route>
        </Switch>
      </div>
    </Router>
  );
}

function Home() {
  return <h2>Home</h2>;
}

ReactDOM.render(<App />, document.querySelector('#app'));