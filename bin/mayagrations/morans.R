# TODO: Add comment
# 
# Author: Alex
###############################################################################
library(gstat)
library(maptools)
library(spdep)

setwd('C:/Users/Alex/Documents/School/Spring 2015/ABM')
dates <- readShapeSpatial('projected_terminal_dates')

dates.nb <- knn2nb(knearneigh(dates, k=8, longlat=F))
dates.idw <- lapply(nbdists(dates.nb, dates), function(x) 1/x)
dates.listw <- nb2listw(dates.nb, glist=dates.idw, style='B')
moran.test(dates$Terminal_D, dates.listw)

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
moran.test(dates$Terminal_D, dates.listw)

setwd('C:/Users/Alex/Documents/School/Spring 2015/ABM')
dates <- readShapeSpatial('utm_dates')

dates.nb <- knn2nb(knearneigh(dates, k=8, longlat=F))
dates.idw <- lapply(nbdists(dates.nb, dates), function(x) 1/x)
dates.listw <- nb2listw(dates.nb, glist=dates.idw, style='B')
moran.test(dates$Terminal_D, dates.listw)

dates.knb <- knn2nb(knearneigh(dates, k=1, longlat=F))
dates.maxDist <- max(unlist(nbdists(dates.knb, dates)))
dates.dnb <- dnearneigh(dates, 0, dates.maxDist)
dates.listw <- nb2listw(dates.dnb)
moran.test(dates$Terminal_D, dates.listw)

dates.dnb <- dnearneigh(dates, 0, 40000)
dates.listw <- nb2listw(dates.dnb, zero.policy = TRUE)
moran.test(dates$Terminal_D, dates.listw, zero.policy = TRUE)

setwd('C:/Users/Alex/Documents/School/Spring 2015/ABM')
dates <- readShapeSpatial('clipped_dates')

dates.nb <- knn2nb(knearneigh(dates, k=8, longlat=F))
dates.idw <- lapply(nbdists(dates.nb, dates), function(x) 1/x)
dates.listw <- nb2listw(dates.nb, glist=dates.idw, style='B')
moran.test(dates$Terminal_D, dates.listw)

dates.knb <- knn2nb(knearneigh(dates, k=1, longlat=F))
dates.maxDist <- max(unlist(nbdists(dates.knb, dates)))
dates.dnb <- dnearneigh(dates, 0, dates.maxDist)
dates.listw <- nb2listw(dates.dnb)
moran.test(dates$Terminal_D, dates.listw)

dates.dnb <- dnearneigh(dates, 0, 40000)
dates.listw <- nb2listw(dates.dnb, zero.policy = TRUE)
moran.test(dates$Terminal_D, dates.listw, zero.policy = TRUE)

finally <- variogram(Terminal_D~1, dates)
fin.fit <- fit.variogram(finally, model = vgm(1, "Exp", 100000, 1))
plot(finally, fin.fit)

max(dates$Terminal_D)
hist(dates.dists)
