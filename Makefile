run-acceptance-tests:
	lein test :acceptance

all-tests:
	lein test :all

run-tests:
	lein test

watch-tests:
	lein test-refresh

watch-changed-tests:
	lein test-refresh :changes-only

watch-acceptance-tests:
	lein test-refresh :only :acceptance

check-outdated-libs:
	lein ancient

start-endpoints:
	lein run

start-endpoints-bg:
	lein run&

# clojurescript

run-phantom-tests:
	lein doo phantom test once

watch-phantom-tests:
	lein doo phantom test auto

# to run the tests: http://0.0.0.0:3449/tests.html
run-browser-tests:
	lein figwheel devcards-test

# node
run-node-tests:
	lein doo node node-test once

watch-node-tests:
	lein doo node node-test auto
