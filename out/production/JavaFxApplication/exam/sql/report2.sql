/*Stored procedure to be created first*/
CREATE DEFINER=`sqlUser`@`%` PROCEDURE `report2`()
BEGIN
	DECLARE fin INT DEFAULT FALSE;
	DECLARE CustomerId INT;
	DECLARE CURS CURSOR
	FOR
		SELECT Customer_ID
		FROM client_schedule.customers;
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET fin=TRUE;
    DROP TABLE IF EXISTS report2_table;
    CREATE TEMPORARY TABLE report2_table (ID INT primary key, Title VARCHAR(255), Description VARCHAR(255), Type VARCHAR(255), Start TIMESTAMP, End TIMESTAMP, Customer_ID INT) ENGINE=MEMORY;
	OPEN CURS;
    read_loop: LOOP
		FETCH NEXT FROM CURS INTO CustomerId;
        IF fin THEN
			LEAVE read_loop;
		END IF;
        INSERT INTO report2_table (ID, Title, Type, Description, Start, End, Customer_ID)
			SELECT Appointment_ID, Title, Type, Description, Start, End, Customer_ID FROM client_schedule.appointments WHERE Customer_ID=CustomerId;
	END LOOP;
    CLOSE CURS;
END

/*Get Report 2*/
CALL report2;
SELECT * FROM client_schedule.report2_table;