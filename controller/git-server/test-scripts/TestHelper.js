import { Counter } from "k6/metrics"

export let handleSummary = (data) => {
	return {
		"summary.json": JSON.stringify(data, null, '\t')
	}
}

const failCounter = new Counter('fail_counter');

export let abortTest = () => {
	failCounter.add(1);
}

export let options = {
	discardResponseBodies: true,
	thresholds: {
		http_req_failed: [
			{
				threshold: 'rate == 0',
				abortOnFail: true
			}
		],
		fail_counter: [
			{
				threshold: 'count == 0',
				abortOnFail: true,
				delayAbortEval: '0s'
			}
		]
	},
	scenarios: {
		contacts: {
			executor: 'constant-arrival-rate',
			rate: 1,
			duration: '10s',
			preAllocatedVUs: 1,
			maxVUs: 1,
		},
	},
	summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(50)', 'p(99)', 'p(99.9)', 'p(99.99)', 'p(100)', 'count'],
}
