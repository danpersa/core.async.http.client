acceptance-tests:
	lein cucumber --plugin pretty

run-spec:
	lein spec spec -f d

auto-spec:
	lein spec spec --autotest -f d

watch-cucumber:
	lein test-refresh
