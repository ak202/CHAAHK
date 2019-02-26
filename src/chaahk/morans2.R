# TODO: Add comment
# 
# Author: Alex
###############################################################################

library(parallel)

setwd('C:/Users/Alex/workspace/Chaahk/output/wtf')
params <- read.csv('params.csv')
threads <- makeCluster(7)
library(vegan)
"hello"

morans.die <- function(run) {
	
	library(RPostgreSQL)
	library(gstat)
	library(spdep)
	library(moments)
	
	drv <- dbDriver("PostgreSQL")
	con <- dbConnect(drv, dbname="postgres",host="localhost",port=5433,user="postgres",password="m36jackson")
	dates <- dbGetQuery(con, paste("SELECT * FROM output WHERE run =", run))
	dbDisconnect(con)
	dates <- dates[1:120,]
	pop <- sum(dates$labor)
	dates <- dates[dates$stela > 0,]
	
	if (nrow(dates) > 29) {
		dates <- SpatialPointsDataFrame(dates[,4:5], dates)
		
		dates.knb <- knn2nb(knearneigh(dates, k=1, longlat=F))
		dates.dists <- unlist(nbdists(dates.knb, dates))
		dates.dists <- subset(dates.dists, dates.dists < 3.5*median(dates.dists))
		dates.dnb <- dnearneigh(dates, 0, max(dates.dists))
		nbCnt <- card(dates.dnb)
		dates <- SpatialPointsDataFrame(dates, data.frame(dates, nbCnt))
		dates <- dates[dates$nbCnt>0,]
		
		dates.knb <- knn2nb(knearneigh(dates, k=1, longlat=F))
		dates.dists <- unlist(nbdists(dates.knb, dates))
		dates.dnb <- dnearneigh(dates, 0, max(dates.dists))
		dates.listw <- nb2listw(dates.dnb)
		
		term.mtest <- moran.test(dates$termd, dates.listw)
		term.mI <- term.mtest$estimate[1]
		term.k <- kurtosis(dates$termd)
		term.s <- skewness(dates$termd)
		count.mtest <- moran.test(dates$stela, dates.listw)
		count.mI <- term.mtest$estimate[1]
		count.k <- kurtosis(dates$stela)
		count.s <- skewness(dates$stela)
		count <- sum(dates$stela)
		collapse <- (1+count)/(1+pop)
		
		return(list(run, pop, count, collapse,term.mI, term.k, term.s, count.mI, count.k, count.s, count, collapse, row.names=''))
	}
}

offset <- function(x) x + sample(0:9, 1)
runs <- unique(lapply(as.integer(seq(1,82935, length.out = 15000)), offset))
results <- parLapply(threads, runs, morans.die)
data <- as.data.frame(do.call(rbind, results))
colnames(data) <- c('run','pop', 'count','collapse', 'term.mI', 'term.k', 'term.s', 'count.mI', 'count.k', 'count.s', 'count2', 'collapse2', 'rownames')
data2 <- data[,c(1:7,9,10)]
data2[,1:9] <- as.numeric(as.matrix(data2[,1:9]))
write.csv(data2, 'data.csv')

data.join <- merge(data2, params, by='run')
sumstats <- list(data.join[,5], data.join[,6], data.join[,7], data.join[,8], data.join[,9])
sumstats.mean <- lapply(sumstats, mean)
sumstats.sd <- lapply(sumstats, sd)
dev.fun <- function(x, m, s) (x - m) / s
sumstats.dev <- data.frame(mapply(dev.fun, sumstats, sumstats.mean, sumstats.sd))
colnames(sumstats.dev) <- c('term.mI.dev', 'term.k.dev', 'term.s.dev', 'count.k.dev', 'count.s.dev')
sumstats.dev.net <- abs(rowMeans(sumstats.dev))
data.final <- data.frame(data.join, sumstats.dev, sumstats.dev.net)
write.csv(data.final,'finaldata.csv')

data.final[order(sumstats.dev.net),][0:5,]







