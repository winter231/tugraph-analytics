CREATE GRAPH modern (
  Vertex person (
    id bigint ID,
    name varchar,
    age int
  ),
  Vertex software (
    id bigint ID,
    name varchar,
    lang varchar
  ),
  Edge knows (
    srcId bigint SOURCE ID,
    targetId bigint DESTINATION ID,
    weight double
  ),
  Edge created (
    srcId bigint SOURCE ID,
    targetId bigint DESTINATION ID,
    weight double
  )
) WITH (
  storeType='rocksdb',
  shardCount = 2
);

INSERT INTO modern.person(id, name, age)
VALUES(1, 'jim', 20)
     ,(2, 'kate', 22)
     ,(3, 'tom', 24)
     ,(4, 'lily', 26);

INSERT INTO modern.software(id, name, lang)
VALUES(5, 'geaflow', 'java')
     ,(6, 'geaflow', 'java');

INSERT INTO modern.knows
VALUES(1, 2, 0.5)
     ,(1, 3, 0.4)
     ,(1, 4, 0.3)
     ,(2, 4, 0.6)
     ,(3, 4, 0.7);

INSERT INTO modern.created
VALUES(1, 5, 0.8)
     ,(2, 5, 0.7)
     ,(3, 6, 0.6)
     ,(4, 6, 0.9);