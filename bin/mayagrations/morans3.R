# TODO: Add comment
# 
# Author: Alex
###############################################################################

library(parallel)

params <- read.csv('params.csv')
threads <- makeCluster(7)
library(vegan)

morans.die <- function(run) {
	print(run)
	library(gstat)
	library(spdep)
	library(moments)

	dates <- read.csv('out.txt')
	dates <- data.frame(data.matrix(dates))
	dates <- dates[dates$run == run,]
#	dates <- dates[dates$run == 4,]
	dates <- unique(dates)
	pop <- sum(dates$labor)
	dates <- dates[dates$Stelae > 0,]
	summary(dates)
	dim(dates)

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
		summary(dates)
		term.mtest <- moran.test(dates$TermD, dates.listw)
		term.mI <- term.mtest$estimate[1]
		term.k <- kurtosis(dates$TermD)
		term.s <- skewness(dates$TermD)
		count.mtest <- moran.test(dates$Stelae, dates.listw)
		count.mI <- term.mtest$estimate[1]
		count.k <- kurtosis(dates$Stelae)
		count.s <- skewness(dates$Stelae)
		count <- sum(dates$Stelae)
		collapse <- (1+count)/(1+pop)
		
		return(list(run, pop, count, collapse,term.mI, term.k, term.s, count.mI, count.k, count.s, count, collapse, row.names=''))
	}
}


runs <- seq(1,336)
results <- parLapply(threads, runs, morans.die)
#results <- lapply(runs, morans.die)
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







