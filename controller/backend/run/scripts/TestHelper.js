export let handleSummary = (data) => {
	return {
		"summary.json": JSON.stringify(data, null, '\t')
	}
}

export let options = {
	discardResponseBodies: true,
	scenarios: {
		contacts: {
			executor: 'constant-arrival-rate',
			rate: 50,
			duration: '10s',
			preAllocatedVUs: 10,
			maxVUs: 50,
		},
	},
	summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(50)', 'p(99)', 'p(99.9)', 'p(99.99)', 'p(100)', 'count'],
}
