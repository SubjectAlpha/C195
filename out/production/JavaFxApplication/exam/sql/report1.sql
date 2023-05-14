/*Report 1*/
SELECT YEAR(Start), MONTH(Start), Type, COUNT(Appointment_ID) AS TOTALCOUNT
FROM client_schedule.appointments
GROUP BY YEAR(Start), MONTH(Start)
ORDER BY YEAR(Start), MONTH(Start), Type
