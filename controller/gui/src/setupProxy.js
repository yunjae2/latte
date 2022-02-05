const { createProxyMiddleware } = require('http-proxy-middleware');

const api = process.env.API_URI;

const rewriteFn = function(path) {
    return path.split('/api')[1];
}

module.exports = function (app) {
    app.use(
        '/api',
        createProxyMiddleware({
            target: api,
            pathRewrite: rewriteFn,
            changeOrigin: true,
            headers: {
                origin: 'http://localhost',
            }
        })
    );
};
