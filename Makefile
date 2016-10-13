acceptance-tests:
	lein cucumber --plugin pretty

run-tests:
	lein test

watch-tests:
	lein test-refresh

watch-changed-tests:
	lein test-refresh :changes-only

watch-cucumber:
	lein test-refresh :only :acceptance

check-outdated-libs:
	lein ancient

# clojurescript

run-phantom-tests:
	lein doo phantom test once

watch-phantom-tests:
	lein doo phantom test auto

run-browser-tests:
	lein figwheel devcards-test

