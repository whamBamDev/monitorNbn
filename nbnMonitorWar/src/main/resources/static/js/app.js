
const API_DATE_FORMAT = "YYYY-MM-DD";
const UI_DATE_FORMAT = "DD-MM-YYYY";
const API_URL = "api/dailySummary";
const OUTAGE_DATA_URL = "outage/data";


const TOPIC_DAILY_SUMMARY = "/topic/dailySummary";
const TOPIC_LAST_OUTAGE = "/topic/lastOutage";

function openTodaysOutageDetailPage() {
  openOutageDetailPage(moment().format(API_DATE_FORMAT));
}

function openOutageDetailPage( date) {
  window.location.href = "outage?date=" + date;
}

function convertAPIDateToUIDate(apiDate) {
  return convertDateFormat(apiDate,API_DATE_FORMAT,UI_DATE_FORMAT);
}

function convertUIDateToAPIDate(uiDate) {
  return convertDateFormat(uiDate,UI_DATE_FORMAT,API_DATE_FORMAT);
}

function convertDateFormat(date, fromFormat, toFormat) {
  var returnDate = "";
  if(date && date != "") {
    cnvDate = moment(date,fromFormat);
    if(cnvDate.isValid()) {
      returnDate = cnvDate.format(toFormat);
    }
  }
  return returnDate;
}


function checkIfOutageInProgress(outage) {
  var isHidden = $("#outageInProgressAlert").is(':hidden');
   
  if(outage && outage.inProgress) {
    var message = "An outage is currently in progress"
    if(outage.startTime) {
      var startTime = moment(outage.startTime);
      message += ", started at "
        + startTime.format("HH:mm:ss")
        + " and has been running for "
        + humanizeDuration(moment().diff(startTime), { round: true });
    }
    message += ". Click <a href='#' class='alert-link' onclick='openTodaysOutageDetailPage();return false;'>here</a> to view today&#39;s outages."
    $("#outageInProgressMessage").html(message);

    if(isHidden) {
      $("#outageInProgressAlert").removeClass("d-none");
    }
  } else {
    if(! isHidden) {
      $("#outageInProgressAlert").addClass("d-none");
    }
  } 
}

function createStompClient(callback) {
  var stompClient = Stomp.over(function(){
    return new SockJS('/portfolio');
  });
  stompClient.reconnect_delay = 60000;
  stompClient.connect({}, function (frame) {
    if(callback) {
      callback(stompClient);
    }
  });

  return stompClient;
}

function handleStompJSONMessage(message, callback) {
  if(message && message.body) {
    callback(JSON.parse(message.body));
  }
}





var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
}

function connect() {
    stompClient = Stomp.over(function(){
      return new SockJS('/portfolio');
    });
    stompClient.reconnect_delay = 60000;
    stompClient.connect({}, function (frame) {
        setConnected(true);
        stompClient.subscribe('/topic/dailySummary', function(topicMessage) {
            var dailySummary = JSON.parse(topicMessage.body);
            for (i = 0; i < outageChart.data.labels.length; i++) {
              if (outageChart.data.labels[i] == dailySummary.date) {
                
                outageChart.data.datasets[0].data[i] = dailySummary.outageCount;
                outageChart.data.datasets[1].data[i] = dailySummary.testCount;
                outageChart.update();

                break;
              }
            }
// TODO: If no data found in the table then unsubscribe from topic.
            
            var tableData = outageTable.getData();            
            outageTable.updateData([dailySummary]);
            tableData = outageTable.getData();            
            
            var rows = outageTable.getRows();
            for (i = 0; i < rows.length; i++) {
               var rowData = rows[i].getData();
               if( rowData.date == dailySummary.date) {
                 rows[i].update(dailySummary);
                 break;
               }
            }
        });
    });
}


function disconnect() {
  if (stompClient !== null) {
    stompClient.disconnect();
  }
  setConnected(false);
  console.log("Disconnected");
}

/*
$(function () {
  $("form").on('submit', function (e) {
      e.preventDefault();
  });
  $( "#connect" ).click(function() { connect(); });
  $( "#disconnect" ).click(function() { disconnect(); });
});
*/
