-- :name sql-get-pizzas :? :*
SELECT *
FROM example.pizza;

-- :name sql-get-pizza :? :1
SELECT *
FROM example.pizza
WHERE id = :id;

-- :name sql-insert-pizza :<! :1
INSERT INTO example.pizza (name, size, price, origin)
VALUES (:name, :size, :price, :origin)
RETURNING *;

-- :name sql-remove-pizzas :!
TRUNCATE example.pizza
