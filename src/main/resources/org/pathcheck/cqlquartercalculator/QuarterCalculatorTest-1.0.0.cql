library QuarterCalculatorTest version '1.0.0'

include QuarterCalculator version '1.0.0'

define "Test Previous Quarter To 1": QuarterCalculator."Previous Quarter"(1) = 4
define "Test Previous Quarter To 2": QuarterCalculator."Previous Quarter"(2) = 1
define "Test Previous Quarter To 3": QuarterCalculator."Previous Quarter"(3) = 2
define "Test Previous Quarter To 4": QuarterCalculator."Previous Quarter"(4) = 3

define "Test Next Quarter To 1": QuarterCalculator."Next Quarter"(1) = 2
define "Test Next Quarter To 2": QuarterCalculator."Next Quarter"(2) = 3
define "Test Next Quarter To 3": QuarterCalculator."Next Quarter"(3) = 4
define "Test Next Quarter To 4": QuarterCalculator."Next Quarter"(4) = 1

define "Test Quarter from Month To Jan": QuarterCalculator."Quarter from Month"(1) = 1
define "Test Quarter from Month To Feb": QuarterCalculator."Quarter from Month"(2) = 1
define "Test Quarter from Month To Mar": QuarterCalculator."Quarter from Month"(3) = 1
define "Test Quarter from Month To Apr": QuarterCalculator."Quarter from Month"(4) = 2
define "Test Quarter from Month To May": QuarterCalculator."Quarter from Month"(5) = 2
define "Test Quarter from Month To Jun": QuarterCalculator."Quarter from Month"(6) = 2
define "Test Quarter from Month To Jul": QuarterCalculator."Quarter from Month"(7) = 3
define "Test Quarter from Month To Aug": QuarterCalculator."Quarter from Month"(8) = 3
define "Test Quarter from Month To Sep": QuarterCalculator."Quarter from Month"(9) = 3
define "Test Quarter from Month To Oct": QuarterCalculator."Quarter from Month"(10) = 4
define "Test Quarter from Month To Nov": QuarterCalculator."Quarter from Month"(11) = 4
define "Test Quarter from Month To Dec": QuarterCalculator."Quarter from Month"(12) = 4

define "Test Starting Date of Quarter 1": QuarterCalculator."Starting Date of Quarter"(2022, 1) = @2022-01-01T00:00:00
define "Test Starting Date of Quarter 2": QuarterCalculator."Starting Date of Quarter"(2022, 2) = @2022-04-01T00:00:00
define "Test Starting Date of Quarter 3": QuarterCalculator."Starting Date of Quarter"(2022, 3) = @2022-07-01T00:00:00
define "Test Starting Date of Quarter 4": QuarterCalculator."Starting Date of Quarter"(2022, 4) = @2022-10-01T00:00:00

define "Test Ending Date of Quarter 1": QuarterCalculator."Ending Date of Quarter"(2022, 1) = @2022-03-31T23:59:59
define "Test Ending Date of Quarter 2": QuarterCalculator."Ending Date of Quarter"(2022, 2) = @2022-06-30T23:59:59
define "Test Ending Date of Quarter 3": QuarterCalculator."Ending Date of Quarter"(2022, 3) = @2022-09-30T23:59:59
define "Test Ending Date of Quarter 4": QuarterCalculator."Ending Date of Quarter"(2022, 4) = @2022-12-31T23:59:59

define "Test Quarter Period 1": QuarterCalculator."Quarter Period"(2022, 1) = Interval[@2022-01-01T00:00:00,@2022-03-31T23:59:59]
define "Test Quarter Period 2": QuarterCalculator."Quarter Period"(2022, 2) = Interval[@2022-04-01T00:00:00,@2022-06-30T23:59:59]
define "Test Quarter Period 3": QuarterCalculator."Quarter Period"(2022, 3) = Interval[@2022-07-01T00:00:00,@2022-09-30T23:59:59]
define "Test Quarter Period 4": QuarterCalculator."Quarter Period"(2022, 4) = Interval[@2022-10-01T00:00:00,@2022-12-31T23:59:59]

define "Test Previous Quarter To Apr 1 2022": QuarterCalculator."Previous Quarter To"(@2022-04-01T00:00:00) = Interval[@2022-01-01T00:00:00,@2022-03-31T23:59:59]
define "Test Previous Quarter To Jul 1 2022": QuarterCalculator."Previous Quarter To"(@2022-07-01T00:00:00) = Interval[@2022-04-01T00:00:00,@2022-06-30T23:59:59]
define "Test Previous Quarter To Nov 1 2022": QuarterCalculator."Previous Quarter To"(@2022-11-01T00:00:00) = Interval[@2022-07-01T00:00:00,@2022-09-30T23:59:59]
define "Test Previous Quarter To Jan 1 2023": QuarterCalculator."Previous Quarter To"(@2023-01-01T00:00:00) = Interval[@2022-10-01T00:00:00,@2022-12-31T23:59:59]
define "Test Previous Quarter To Jan 2 2022": QuarterCalculator."Previous Quarter To"(@2022-01-02T00:00:00) = Interval[@2021-10-01T00:00:00,@2021-12-31T23:59:59]