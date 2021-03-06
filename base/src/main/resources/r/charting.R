
require(quantmod)
convertTimeSeries <- function(d, startCol = 3){

        parsedTime <- lapply(d[1], function(x) as.POSIXct(x/1000000000, tz="GMT", origin="1970-01-01"))
        ts <- as.data.frame(do.call(rbind, lapply(parsedTime, as.double)))
        data <- xts(d[,startCol:length(d[1,])], as.POSIXct(t(ts), tz="GMT",origin="1970-01-01"))

}


o <- read.csv("http://localhost:44444/?SERIESID=CNX.MDI.GBP/AUD&FREQ=HOURS_1&FIELD=OPEN&STARTDATE=20120524&ENDDATE=20130301");
h <- read.csv("http://localhost:44444/?SERIESID=CNX.MDI.GBP/AUD&FREQ=HOURS_1&FIELD=HIGH&STARTDATE=20120524&ENDDATE=20130301");
l <- read.csv("http://localhost:44444/?SERIESID=CNX.MDI.GBP/AUD&FREQ=HOURS_1&FIELD=LOW&STARTDATE=20120524&ENDDATE=20130301");
c <- read.csv("http://localhost:44444/?SERIESID=CNX.MDI.GBP/AUD&FREQ=HOURS_1&FIELD=CLOSE&STARTDATE=20120524&ENDDATE=20130301");

# converting to timeseries. 
o <- convertTimeSeries(o)
h <- convertTimeSeries(h)
l <- convertTimeSeries(l)
c <- convertTimeSeries(c)

ohlc = cbind(o,h,l,c)

colnames(ohlc) <- c("OPEN", "HIGH", "LOW", "CLOSE")
# png("../pricecharts/gbpaud.png",w=1000,h=600)
candleChart(ohlc)
# dev.off()

