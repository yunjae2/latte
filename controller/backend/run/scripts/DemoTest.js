import http from 'k6/http';
import { check } from 'k6';
export { handleSummary, options } from './TestHelper.js';

export default function() {
	let res = http.get("http://httpbin.org/get")
	check(res, { 'status was 200': (r) => r.status == 200});
}
