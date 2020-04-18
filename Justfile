@list:
   just --list

# start backend repl
@backend:
    clj -A:dev:test:common:backend -m nrepl.cmdline -i -C

# test
@test +args='':
   clojure -A:dev:test:common:backend:frontend -m kaocha.runner {{args}}

# lint
@lint:
   clj -A:dev -m clj-kondo.main --lint src

# update dependencies
@outdated:
    clj -A:dev:test:common:backend:frontend:outdated --update

# Create and start devlopment containers
@up:
   docker-compose -f dev/docker-compose.yml up

# Stop and remove development containers, networks, images, and volumes
@down:
   docker-compose -f dev/docker-compose.yml down
