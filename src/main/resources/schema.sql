-- LOGS
CREATE or replace VIEW V_LOGS AS
SELECT CNAME,
       ACTIVITIES.USR_ID,
       PRT_ID,
       DOC_ID
FROM ACTIVITIES,
     USERS
WHERE USERS.ID = ACTIVITIES.USR_ID;

CREATE UNIQUE INDEX IDX_DOC_NAME ON DOCUMENTS (NAME);