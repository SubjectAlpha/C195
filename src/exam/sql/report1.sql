/*Report 1*/
SELECT Count(Appointment_ID), Month(Start) as startMonth FROM client_schedule.appointments GROUP BY startMonth ORDER BY Type, startMonth;
