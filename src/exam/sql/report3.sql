/*Report 3 - New customers this month*/
SELECT COUNT(*) as New_Customers FROM client_schedule.customers WHERE Create_Date BETWEEN DATE_FORMAT(NOW() - INTERVAL 1 MONTH, '%Y-%m-01 00:00:00')
AND DATE_FORMAT(LAST_DAY(NOW() - INTERVAL 1 MONTH), '%Y-%m-%d 23:59:59');