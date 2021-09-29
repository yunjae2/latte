const { createProxyMiddleware } = require('http-proxy-middleware');

const rewriteFn = function(path) {
    return path.split('/api')[1];
}

module.exports = function (app) {
    app.use(
        '/api',
        createProxyMiddleware({
            target: 'http://localhost:8080',
            pathRewrite: rewriteFn,
            changeOrigin: true,
        })
    );
};
