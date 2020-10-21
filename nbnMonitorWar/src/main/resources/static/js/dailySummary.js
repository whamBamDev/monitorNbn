
var chartContext;
var dailySummaryChart;
var dailySummaryTable;
var picker;


function checkDateRange() {
  var today = moment().format(UI_DATE_FORMAT);
  var savedToday = sessionStorage.getItem("todayDate");
  var fromDate = $("#outageDateFrom").val();
  var toDate = $("#outageDateTo").val();

  if(today != savedToday) {
    sessionStorage.setItem("todayDate", today);
    if(savedToday && savedToday != "" && savedToday === toDate) {
      sessionStorage.setItem("todayDate", today);
      var daysDiff = 31;
      if(fromDate && fromDate != "") {
        daysDiff = moment(toDate, UI_DATE_FORMAT).diff(moment(fromDate, UI_DATE_FORMAT), "days");
      }
      
      toDate = today;
      fromDate = moment().subtract(daysDiff,"days").format(UI_DATE_FORMAT);

      $("#outageDateFrom").val(fromDate);
      $("#outageDateTo").val(toDate);

      getDailySummaryData( moment(fromDate, UI_DATE_FORMAT), moment(toDate, UI_DATE_FORMAT));
    } else {
      // TODO: unsubscribe from topic?
    }
  }

}


function resetDateRangeTo( dateRange) {
  var toDate = new moment();
  var fromDate;
  
  if(dateRange === "week") {
    fromDate = new moment().subtract(1, "weeks");
  } else if (dateRange === "twoWeeks") {
    fromDate = new moment().subtract(2, "weeks");
  } else {
    fromDate = new moment().subtract(1, "months");
  }
  
  getDailySummaryData(fromDate, toDate);
}

function submitsearch() {
  var outageDateTo = new moment();
  var outageDateFrom = new moment().subtract(1, "months");

  var outageDateFromStr = $("#outageDateFrom").val();
  var outageDateToStr = $("#outageDateTo").val();

  if(outageDateFromStr && outageDateFromStr != "") {
    var date = moment(outageDateFromStr,UI_DATE_FORMAT);
    if( date.isValid()) {
      outageDateFrom = date;
    }
  }
  if(outageDateToStr && outageDateToStr != "") {
    var date = moment(outageDateToStr,UI_DATE_FORMAT);
    if( date.isValid()) {
      outageDateTo = date;
    }
  }

  getDailySummaryData( outageDateFrom, outageDateTo);
}


function getDailySummaryData( outageDateFrom, outageDateTo) {
  $.getJSON( API_URL, { startDate: outageDateFrom.format(API_DATE_FORMAT), endDate: outageDateTo.format(API_DATE_FORMAT) } )
    .done(function( json ) {
      $("#outageDateFrom").val(outageDateFrom.format(UI_DATE_FORMAT));
      $("#outageDateTo").val(outageDateTo.format(UI_DATE_FORMAT));
      displayDailySummaryChart(json);
      displayDailySummaryList(json);
    })
    .fail(function( jqxhr, textStatus, error ) {
      // TODO: Screen message
      var err = textStatus + ", " + error;
      console.log( "Request Failed: " + err );
    });
}


function outageBarColour(outageCount) {
  var barColour = 'rgba(246, 39, 39, 0.71)';
  if (outageCount < 2) {
    barColour = 'rgba(18, 211, 18, 0.71)';
  } else if (outageCount < 6) {
    barColour = 'rgba(237, 240, 34, 0.84)';
  }
  return barColour;
}
    

function displayDailySummaryList(dailySummaries) {

  if(dailySummaryTable) {
    dailySummaryTable.clear();
    dailySummaryTable.rows.add(dailySummaries).draw();
  } else {
    dailySummaryTable = $("#dailySummaryTable").DataTable({
      paging: false,
      searching: false,
      order: [],          
      data: dailySummaries,
      columns: [
        { data: 'uiDate' },
        { data: 'outageCount' },
        { data: 'failedTestCount' },
        { data: 'testCount' }
      ],
      columnDefs: [{
        targets: [1, 2, 3],
        className: 'text-right'
      }]
    });

    $('#dailySummaryTable tbody').on('click', 'tr', function () {
      var data = dailySummaryTable.row( this ).data();
      openOutageDetailPage(data.date);
    });
  }
}    


function createChartContext() {
  chartContext = document.getElementById("dailySummaryChart").getContext('2d');
}

function displayDailySummaryChart(dailySummaries) {
  var dates = [];
  var outages = [];
  var testCounts = [];
  var barColors = [];
  dailySummaries.forEach(function (dailySummary, index) {
    uiDate = convertAPIDateToUIDate(dailySummary.date);
    dailySummary.uiDate = uiDate;
    dates.unshift(uiDate);
    outages.unshift(dailySummary.outageCount);
    testCounts.unshift(dailySummary.testCount);
    barColors.unshift(outageBarColour(dailySummary.outageCount));
  });

  if(dailySummaryChart) {
    dailySummaryChart.data = {
      labels: dates,
      datasets: [{
        label: 'Outages',
        data: outages,
        backgroundColor: barColors,
        yAxisID: 'y-axis-outages'
      },{
        label: 'Tests Performed',
        type: 'line',
        borderColor: 'rgb(153, 102, 255)',
        fill: false,
        data: testCounts,
        yAxisID: 'y-axis-testCount'
      }]
    };
    dailySummaryChart.update();
  } else {
    dailySummaryChart = new Chart(chartContext, {
      type: 'bar',
      data: {
        labels: dates,
        datasets: [{
          label: 'Outages',
          data: outages,
          backgroundColor: barColors,
          yAxisID: 'y-axis-outages'
        },{
          label: 'Tests Performed',
          type: 'line',
          borderColor: 'rgb(153, 102, 255)',
          fill: false,
          data: testCounts,
          yAxisID: 'y-axis-testCount'
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          yAxes: [{
            type: 'linear', // only linear but allow scale type registration. This allows extensions to exist solely for log scale for instance
            display: true,
            position: 'left',
            id: 'y-axis-outages',
            ticks: {
              beginAtZero: true
            }
          },{
            type: 'linear', // only linear but allow scale type registration. This allows extensions to exist solely for log scale for instance
            display: true,
            position: 'right',
            id: 'y-axis-testCount',
            // grid line settings
            gridLines: {
              drawOnChartArea: false, // only want the grid lines for one axis to show up
            },
            includeZero: true,
            ticks: {
              beginAtZero: true
            }
          }],
        }
      }
    })
      
    document.getElementById("dailySummaryChart").onclick = function(evt) {   
      var activePoints = dailySummaryChart.getElementsAtEvent(evt);

      if(activePoints.length > 0) {
        //get the internal index of slice in pie chart
        var clickedElementindex = activePoints[0]["_index"];

        //get specific label by index 
        var label = dailySummaryChart.data.labels[clickedElementindex];
        openOutageDetailPage(convertUIDateToAPIDate(label));
      }
    }
  }
}



function updateChartAndTable(dailySummary) {

  uiDate = convertAPIDateToUIDate(dailySummary.date);
  dailySummary.uiDate = uiDate;

  if(dailySummaryChart) {
    var index = -1;
    
    labels = dailySummaryChart.data.labels;
    for (var i = labels.length - 1; i >= 0; i--) {
      if(uiDate === labels[i]) {
        index = i;
        break;
      }
    }

    if(index != -1) {
      dailySummaryChart.data.datasets[0].data[index] = dailySummary.outageCount;
      dailySummaryChart.data.datasets[0].backgroundColor[index] = outageBarColour(dailySummary.outageCount);
      dailySummaryChart.data.datasets[1].data[index] = dailySummary.testCount;
      
      dailySummaryChart.update();
    }
  }

  if(dailySummaryTable) {
    dailySummaryTable.rows().every(function() {
      if(uiDate === this.data().uiDate) {
        this.data(dailySummary).draw();
      }
    });
  }
}


function createDateRangePicker( outageDateFrom, outageDateTo) {
  picker = new Litepicker({
    element: document.getElementById('outageDateFrom'),
    elementEnd: document.getElementById('outageDateTo'),
    startDate: outageDateFrom,
    endDate: outageDateTo,
    format: UI_DATE_FORMAT,
    numberOfMonths: 2,
    numberOfColumns: 2,
    singleMode: false,
    splitView: true,
    onSelect: function(selectedFromDate, selectedToDate) {
            getDailySummaryData( new moment(selectedFromDate), new moment(selectedToDate));
    }
  });
}

