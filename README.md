# Electricity Cost Simulator

[![jdk11](https://img.shields.io/badge/java-11-blue.svg)](http://jdk.java.net/11)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/cd4fcd368f3f4818bbb351dc4e1a34c8)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=matteodri/electricity-cost-simulator&amp;utm_campaign=Badge_Grade)
[![CircleCI](https://circleci.com/gh/matteodri/electricity-cost-simulator.svg?style=svg&circle-token=4ce3a7aae23a86df5e7a4f53810706d5c1f1450a)](https://circleci.com/gh/matteodri/electricity-cost-simulator)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Purpose
Small command line application to calculate electricity cost taking as input a tariff and a CSV file produced by an OWL Intuition device. The application also simulates the cost savings that could have been achieved in the analysed time frame if system was equipped with an electricity storage.
Refunds for energy pushed to the network are not being considered, the application only calculates costs on the energy being drawn from the network.

## OWL Intuition
[OWL Intuition](http://www.theowl.com/index.php/owl-intuition/) is an electricity monitoring system that can track electricity consumption and, in the PV version, record electricity produced from photovoltaic panels. Their software provides a way to monitor system electricity flows, keeping historical data that the user can download as CSV file.

## CSV format
CSV file can be downloaded from https://www.owlintuition.com/ once you have correctly configured the electricity monitor. It lists electricity measurements sampled with a 1-minute frequency. Fields being considered by the application are: _timestamp_, _curr_property_ and _curr_solar_generating_.

## Tariff based on time of use
Application calculates electricity cost (without considering taxes and fixed costs) based on Italian electricity time-based rates. These are defined as:
<ul>
  <li>F1 (peak times): Monday to Friday from 8.00 till 19.00. Excluding national holidays.</li>
  <li>F2 (intermediate times):<ul>
                                <li> Monday to Friday from 7.00 till 8.00 and from 19.00 till 23.00;</li>
                                <li>Saturday from 7.00 till 23.00. Excluding national holidays.</li>
                              </ul></li>
  <li>F3 (off peak): Monday to Saturday from 23.00 till 7.00. All day on Sunday and national holidays.</li>
</ul>

## Usage
`java -jar electricity-cost-simulator-0.1.0.jar <csv file> <f1 cost> <f2 cost> <f3 cost>`

or:

`java -jar electricity-cost-simulator-0.1.0.jar <csv file> <f1 cost> <f2 cost> <f3 cost> <warning threshold> <solar multiplier> <clipping threshold>`

Parameters:

| Name                 | Description                                | Unit      |
|----------------------|--------------------------------------------|-----------|
| `csv file`           | path to CSV file                           |           |
| `f1 cost`            | cost of F1 rate                            | money/kWh |
| `f2 cost`            | cost of F2 rate                            | money/kWh |
| `f3 cost`            | cost of F3 rate                            | money/kWh |
| `warning threshold`  | virtual power threshold                    | W         |
| `solar multiplier`   | multiplier to simulate different PV sytem  |           |
| `clipping threshold` | virtual clipping threshold of the inverter | W         |


The application, among other stats, will return the amount of time during which consumption from the network exceeded the `warning threshold`. This value helps determine if it would be possible to move to a cheaper contract with a lower allowed peak consumption.
Cost values are currency agnostic, thus results are not tied to any specific currency or convertion rate.

Sample output:
    
    $java -jar electricity-cost-simulator-0.1.0-SNAPSHOT.jar "./readings.csv" 0.129 0.0299 0.0299 3500 2.0 6000
    
    RESULTS
    Dataset starts at 2020-11-01T00:00:35
    Dataset ends at 2021-10-31T23:59:47
    Overall cost = 191.6102501086235
    Cost F1 = 47.68291102250538 F2 = 108.15386190583524 F3 = 35.773477180282875
    Cost if had a battery F1 = 15.513070153333672 F2 = 22.06679206583325 F3 = 7.497453363472788
    Total solar production = 9520kWh
    Peak consumption from grid = 5288W on 2021-03-12T06:57:11
    Peak production = 7244W on 2021-05-23T11:49:57
    Time over warning threshold = 0d 2h 15m 24s
    Time drawing energy from grid if had a battery = 45d 12h 8m 44s
    Days with consumption greater than solar production = 89
    Solar production lost due to clipping = 7kWh
    Time when production exceeds clipping threshold = 0.4754589779344939%
    Days processed 364
    Lines processed = 1537228

## Source

Assemble JAR:

    mvn clean package

## License

>MIT License
>
>Copyright (c) 2019 Matteo Dri
>
>Permission is hereby granted, free of charge, to any person obtaining a copy
>of this software and associated documentation files (the "Software"), to deal
>in the Software without restriction, including without limitation the rights
>to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
>copies of the Software, and to permit persons to whom the Software is
>furnished to do so, subject to the following conditions:
>
>The above copyright notice and this permission notice shall be included in all
>copies or substantial portions of the Software.
>
>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
>IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
>FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
>AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
>LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
>OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
>SOFTWARE.
