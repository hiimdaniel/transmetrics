CREATE TABLE holidays
(

    uid        text   NOT NULL,
    id         BIGINT NOT NULL,
    url_id     text   NOT NULL,
    url        text   NOT NULL,
    country_id text   NOT NULL,
    name       text   NOT NULL,
    one_liner  text   NOT NULL,
    types      jsonb  NOT NULL,
    subtype    jsonb  NOT NULL,
    date       date   NOT NULL,
    PRIMARY KEY (uid)
);
