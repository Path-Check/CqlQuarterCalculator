library StockCalculator version '1.0.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

parameter "Measurement Period" Interval<Date>

// Returns all flags that overlap with the period.
define function "Flags that Intersect with"(period Interval<DateTime>):
    [Flag] F where F.period overlaps period
             return F.period intersect period

define "Flags that Intersect with Measurement Period": "Flags that Intersect with"("Measurement Period")

// 1. Determines the list of unique intervals to remove duplicates
// 2. Sum the days of the remaining non-overlapping intervals.
// In case of half days are found, this procedure rounds up to the closest full day:
// - < 12 hours in the last day -> truncate
// - > 12 hours in the last day -> round up
define function "Sum Of Collapsed Intervals"(intervals List<Interval<DateTime>>):
    Sum((collapse intervals) CollapsedInterval
        return all duration in days of CollapsedInterval
    ) + 1

/**
    There is a bug in hours. This does not work.
	Truncate(Round(
	    Sum((collapse intervals) CollapsedInterval
		    return all duration in hours of CollapsedInterval
	    ) / 24
	))
*/

define "Days Out Of Stock":
    "Sum Of Collapsed Intervals"("Flags that Intersect with Measurement Period")

