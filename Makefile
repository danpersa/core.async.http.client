acceptance-tests:
	lein cucumber --plugin pretty

watch-cucumber:
	lein test-refresh

watch-midje:
	lein midje :autotest
