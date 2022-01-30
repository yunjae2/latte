const { createProxyMiddleware } = require('http-proxy-middleware');

const backend = process.env.BACKEND_URI;

const rewriteFn = function(path) {
    return path.split('/api')[1];
}

module.exports = function (app) {
    app.use(
        '/api',
        createProxyMiddleware({
            target: backend,
            pathRewrite: rewriteFn,
            changeOrigin: true,
            headers: {
                origin: 'http://localhost',
            }
        })
    );
};
