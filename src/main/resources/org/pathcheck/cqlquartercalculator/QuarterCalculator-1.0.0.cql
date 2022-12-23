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