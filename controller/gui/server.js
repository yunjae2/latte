const express = require('express');
const path = require('path');
const { createProxyMiddleware } = require('http-proxy-middleware');
const app = express();

let port = 80;
if (process.env.PORT) {
    port = process.env.PORT;
}

let api = 'http://localhost:8080';
if (process.env.API_URI) {
    api = process.env.API_URI;
}

/**
 * Proxy
 */
const rewriteFn = function(path) {
    return path.split('/api')[1];
}

app.use('/api',
    createProxyMiddleware({
        target: api,
        pathRewrite: rewriteFn,
        changeOrigin: true,
        headers: {
            origin: 'http://localhost',
        }
    })
);

/**
 * SPA
 */
app.use(express.static(path.join(__dirname, 'build')));
app.get('/*', function (req, res) {
    res.sendFile(path.join(__dirname, 'build', 'index.html'));
});


app.listen(port, () => {
    console.log(`latte-controller-gui started at port ${port}, redirecting requests to ${api}`)
});
