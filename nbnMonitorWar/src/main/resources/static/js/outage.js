
var chartContext;
var outageChart;
var outageTable;

var outageDate;
var outageInProgress = false;
var previousCounts;

function createChartContext() {
  chartContext = document.getElementById("outagesChart").getContext('2d');
}

function setOutageDate(date) {
  outdateDate = date;
}

function checkForNewOutage(outageUpdates) {
  if( ! previousCounts) {
    previousCounts = outageUpdates;
  }

  if( !outageInProgress && outageUpdates.outage && outageUpdates.outage.inProgress) {
    outageInProgress = true;
  }

  if(outageUpdates.outageCount) {
    $("#dailySummaryOutageCount").text(outageUpdates.outageCount);
  }
  if(outageUpdates.failedTestCount) {
    $("#dailySummaryFailedTestCount").text(outageUpdates.failedTestCount);
  }
  if(outageUpdates.testCount) {
    $("#dailySummaryTestCount").text(outageUpdates.testCount);
  }

  // if displaying outage data for today then check to see if there is new outage in progress, if yes then
  // update the chart and list with the latest outage data.
  if(outageDate && outageUpdates.date === outageDate) {
    if(outageInProgress
        || (outageUpdates.outageCount && outageUpdates.outageCount > previousCounts.outageCount)) {
      $.getJSON( OUTAGE_DATA_URL, { date: outageDate } )
        .done(function(json) {
          if(json.outageData) {
            displayOutagesChart(json.outageData);
          }
          if(json.outages) {
            displayOutagesList(json.outages);
          }
        })
        .fail(function( jqxhr, textStatus, error ) {
          // TODO: Screen message
          var err = textStatus + ", " + error;
          console.log( "Request Failed: " + err );
        });
    }
    
    outageInProgress = (outageUpdates.outage && outageUpdates.outage.inProgress) ? true : false;    
  }

  previousCounts = outageUpdates;
}

function displayOutagesChart(dayOutageData) {

  if(outageChart) {
    outageChart.data.datasets[0].data = dayOutageData; 
    outageChart.update();
  } else {
    outageChart = new Chart(chartContext, {
        type: 'bar',
        data: {
            datasets: [{
                label: "Outages",
                backgroundColor: "red",
                borderColor: "red",
                data: dayOutageData,
                type: 'bar',
                pointRadius: 0,
                fill: false,
                lineTension: 0,
                borderWidth: 2
            }]
        },
        options: {
            maintainAspectRatio: false,
            animation: {
                duration: 0
            },
            scales: {
                xAxes: [{
                    type: 'time',
                    time: {
                        unit: 'hour'
                    },
                    distribution: 'linear',
                    offset: true,
                    ticks: {
                        source: 'data',
                        autoSkip: true,
                        autoSkipPadding: 56,
                        stepSize: 60,
                    },
                }],
                yAxes: [{
                    gridLines: {
                        drawBorder: false
                    },
                    scaleLabel: {
                        display: true,
                        labelString: 'Outage'
                    },

                    ticks: {
                        display: false,
                        autoSkip: true,
                        min: 0,
                        max: 1,
                        stepSize: 1
                    },

                }]
            },
            tooltips: {
                intersect: false,
                mode: 'index',
                callbacks: {
                    label: function(tooltipItem, myData) {
                        var label = 'Outage started at ';
                        var tooltipData = myData.datasets[tooltipItem.datasetIndex].data[tooltipItem.index].tooltipData;

                        if(tooltipData && tooltipData.startTime) {
                            var startTime = moment(tooltipData.startTime).format("LTS");
                            label += startTime + " and lasted for " + humanizeDuration(tooltipData.duration);
                        }
                        return label;
                    }
                }
            }
        }
    });

    document.getElementById("outagesChart").onclick = function(evt) {   
        var activePoints = outageChart.getElementsAtEvent(evt);

        if(activePoints.length > 0)
        {
        
        
          //get the internal index of slice in pie chart
          var clickedElementindex = activePoints[0]["_index"];

          //get value by index      
          var value = outageChart.data.datasets[0].data[clickedElementindex];

          if(value && value.tooltipData) {
              /* other stuff that requires slice's label and value */
              var startTime = moment(value.tooltipData.startTime).toISOString(true);

              viewTestConsoleLog(startTime);
          }
       }
    }
  }    
}

function displayOutagesList(outages) {

  outages.forEach(function(outage, index) {
    outage.index = index + 1;
  });
  

  if(outageTable) {
    outageTable.clear();
    outageTable.rows.add(outages).draw();
  } else {
 
 /*
         columnDefs: [ {
            sortable: false,
            "class": "index",
            targets: 0
        } ],
        order: [[ 1, 'asc' ]],
        fixedColumns: true
    } );
    table.on( 'order.dt search.dt', function () {
        table.column(0, {search:'applied', order:'applied'}).nodes().each( function (cell, i) {
            cell.innerHTML = i+1;
        } );
    } ).draw();
  */

    outageTable = $("#outagesTable").DataTable({
      paging: false,
      searching: false,
      order: [],          
      data: outages,
      columns: [
        { data: "index"},
        { data: "outage.startTime", "render": function (data) {
                           return moment(data).format("HH:mm:ss");
                       } },
        { data: "outage", "render": function (data) {
                           if(data.endTime && ! data.inProgress) {
                             return moment(data.endTime).format("HH:mm:ss");
                           } else {
                             return "ongoing...";
                           }
                       }  },
        { data: "duration", "render": function (data) {
                           return humanizeDuration(data * 1000);
                       }   },
        {
                       "data": "outage.startTime",
                       "render": function (data) {
                           return '<img src="images/log.png" />';
                       }
                   },
      ],
      columnDefs: [
       { targets: [1, 2, 3], className: 'text-right'},
       { targets: [4], className: 'text-center'}
      ]
    });


    $('#outagesTable tbody').on('click', 'tr', function () {
      var data = outageTable.row( this ).data();
      viewTestConsoleLog(data.outage.startTime);
    });
  }
}    

function viewTestConsoleLog(startDateTime) {
  $.ajax({
    url: "api/outage/" + startDateTime + "/log",
    success: function(result){
      $("#consoleLogOutput").empty();
      if(result) {
        var logOutput = "";
        for (i = 0; i < result.length; i++) {
          logOutput += result[i] + "\n";
        }
        $("#consoleLogOutput").text(logOutput);
      }
      $('#consoleLogOutputModal').modal('show');
    }
  });
}

