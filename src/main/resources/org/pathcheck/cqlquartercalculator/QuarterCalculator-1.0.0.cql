library QuarterCalculator version '1.0.0'

// Calculates the previous Quarter
// Quarter is represented as 1, 2, 3 or 4 (starts with 1)
define function "Previous Quarter"(quarter Integer):
  (quarter - 1 + 3) mod 4 + 1

// Calculates the next Quarter
// Quarter is represented as 1, 2, 3 or 4 (starts with 1)
define function "Next Quarter"(quarter Integer):
  (quarter - 1 + 1) mod 4 + 1

// Calculates the quarter based on the month
// Month is represented as 1..12 for Jan..Dec (starts with 1)
define function "Quarter from Month"(mo Integer): ((mo-1) div 3) + 1

// Calculates the starting day of each quarter for the year provided.
// Quarter is represented as 1, 2, 3 or 4 (starts with 1)
define function "Starting Date of Quarter"(yr Integer, quarter Integer):
  DateTime(yr, (quarter - 1) * 3 + 1, 1, 0, 0, 0)

// Calculates the end day of each quarter for the year provided.
// Quarter is represented as 1, 2, 3 or 4 (starts with 1)
define function "Ending Date of Quarter"(yr Integer, quarter Integer):
  case quarter
    when 4 then "Starting Date of Quarter"(yr + 1, "Next Quarter"(quarter)) - 1 second
    else "Starting Date of Quarter"(yr, "Next Quarter"(quarter)) - 1 second
  end

// Builds a Period for a given quarter in the year.
define function "Quarter Period"(yr Integer, quarter Integer):
    Interval[
      "Starting Date of Quarter"(yr, quarter),
      "Ending Date of Quarter"(yr, quarter)
    ]

// Calculate the previous Quarter to a date.
define function "Previous Quarter To"(dt DateTime):
  case "Quarter from Month"(month from dt)
    when 1 then "Quarter Period"(year from dt - 1, "Previous Quarter"(1))
    when 2 then "Quarter Period"(year from dt, "Previous Quarter"(2))
    when 3 then "Quarter Period"(year from dt, "Previous Quarter"(3))
    when 4 then "Quarter Period"(year from dt, "Previous Quarter"(4))
    else null
   end

define "Last Quarter": "Previous Quarter To"(Now())


// USAGE:

// using FHIR version '4.0.1'
//
// include FHIRHelpers version '4.0.1'

// define "Observations in the Last Quarter":
//  [Observation] O where O.effective starts during "Last Quarter"




// TEST Cases

define "Test Previous Quarter To 1": "Previous Quarter"(1) = 4
define "Test Previous Quarter To 2": "Previous Quarter"(2) = 1
define "Test Previous Quarter To 3": "Previous Quarter"(3) = 2
define "Test Previous Quarter To 4": "Previous Quarter"(4) = 3

define "Test Next Quarter To 1": "Next Quarter"(1) = 2
define "Test Next Quarter To 2": "Next Quarter"(2) = 3
define "Test Next Quarter To 3": "Next Quarter"(3) = 4
define "Test Next Quarter To 4": "Next Quarter"(4) = 1

define "Test Quarter from Month To Jan": "Quarter from Month"(1) = 1
define "Test Quarter from Month To Feb": "Quarter from Month"(2) = 1
define "Test Quarter from Month To Mar": "Quarter from Month"(3) = 1
define "Test Quarter from Month To Apr": "Quarter from Month"(4) = 2
define "Test Quarter from Month To May": "Quarter from Month"(5) = 2
define "Test Quarter from Month To Jun": "Quarter from Month"(6) = 2
define "Test Quarter from Month To Jul": "Quarter from Month"(7) = 3
define "Test Quarter from Month To Aug": "Quarter from Month"(8) = 3
define "Test Quarter from Month To Sep": "Quarter from Month"(9) = 3
define "Test Quarter from Month To Oct": "Quarter from Month"(10) = 4
define "Test Quarter from Month To Nov": "Quarter from Month"(11) = 4
define "Test Quarter from Month To Dec": "Quarter from Month"(12) = 4

define "Test Starting Date of Quarter 1": "Starting Date of Quarter"(2022, 1) = @2022-01-01T00:00:00
define "Test Starting Date of Quarter 2": "Starting Date of Quarter"(2022, 2) = @2022-04-01T00:00:00
define "Test Starting Date of Quarter 3": "Starting Date of Quarter"(2022, 3) = @2022-07-01T00:00:00
define "Test Starting Date of Quarter 4": "Starting Date of Quarter"(2022, 4) = @2022-10-01T00:00:00

define "Test Ending Date of Quarter 1": "Ending Date of Quarter"(2022, 1) = @2022-03-31T23:59:59
define "Test Ending Date of Quarter 2": "Ending Date of Quarter"(2022, 2) = @2022-06-30T23:59:59
define "Test Ending Date of Quarter 3": "Ending Date of Quarter"(2022, 3) = @2022-09-30T23:59:59
define "Test Ending Date of Quarter 4": "Ending Date of Quarter"(2022, 4) = @2022-12-31T23:59:59

define "Test Quarter Period 1": "Quarter Period"(2022, 1) = Interval[@2022-01-01T00:00:00,@2022-03-31T23:59:59]
define "Test Quarter Period 2": "Quarter Period"(2022, 2) = Interval[@2022-04-01T00:00:00,@2022-06-30T23:59:59]
define "Test Quarter Period 3": "Quarter Period"(2022, 3) = Interval[@2022-07-01T00:00:00,@2022-09-30T23:59:59]
define "Test Quarter Period 4": "Quarter Period"(2022, 4) = Interval[@2022-10-01T00:00:00,@2022-12-31T23:59:59]

define "Test Previous Quarter To Apr 1 2022": "Previous Quarter To"(@2022-04-01T00:00:00) = Interval[@2022-01-01T00:00:00,@2022-03-31T23:59:59]
define "Test Previous Quarter To Jul 1 2022": "Previous Quarter To"(@2022-07-01T00:00:00) = Interval[@2022-04-01T00:00:00,@2022-06-30T23:59:59]
define "Test Previous Quarter To Nov 1 2022": "Previous Quarter To"(@2022-11-01T00:00:00) = Interval[@2022-07-01T00:00:00,@2022-09-30T23:59:59]
define "Test Previous Quarter To Jan 1 2023": "Previous Quarter To"(@2023-01-01T00:00:00) = Interval[@2022-10-01T00:00:00,@2022-12-31T23:59:59]
define "Test Previous Quarter To Jan 2 2022": "Previous Quarter To"(@2022-01-02T00:00:00) = Interval[@2021-10-01T00:00:00,@2021-12-31T23:59:59]