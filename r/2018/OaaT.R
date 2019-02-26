library(ggplot2)
setwd("/home/akara/workspace/Chaahk/output/")
setwd("/media/nvme/workspace2/Chaahk/output/")

#read output
Frs.params <- read.csv("FrsParams.csv")
Frs.out <- read.csv("Frs.csv")
#calculate secondary indices
Frs.out$collapse1 <- Frs.out$MinPop/Frs.out$MaxPop
Frs.out$collapse2 <- Frs.out$countPop/Frs.out$MaxPop
#merge to single data frame
data <- data.frame(Frs.params, Frs.out)
#view dot plot
qplot(fecundityResil, collapse1, data=data)
qplot(fecundityResil, collapse2, data=data)

#same idea for different dependant variable
Fu.params <- read.csv("FuParams.csv")
Fu.out <- read.csv("Fu.csv")
Fu.out$collapse1 <- Fu.out$MinPop/Fu.out$MaxPop
Fu.out$collapse2 <- Fu.out$countPop/Fu.out$MaxPop
data <- data.frame(Fu.params, Fu.out)
qplot(fecundityDisturbance, collapse1, data=data)
qplot(fecundityDisturbance, collapse2, data=data)

#etc
Frg.params <- read.csv("FrgParams.csv")
Frg.out <- read.csv("Frg.csv")
Frg.out$collapse1 <- Frg.out$MinPop/Frg.out$MaxPop
Frg.out$collapse2 <- Frg.out$countPop/Frg.out$MaxPop
data <- data.frame(Frg.params, Frg.out)
qplot(fecundityRegen, collapse1, data=data)
qplot(fecundityRegen, collapse2, data=data)

#etc
Frk.params <- read.csv("FrkParams.csv")
Frk.out <- read.csv("Frk.csv")
Frk.out$collapse1 <- Frk.out$MinPop/Frk.out$MaxPop
Frk.out$collapse2 <- Frk.out$countPop/Frk.out$MaxPop
data <- data.frame(Frk.params, Frk.out)
qplot(fecundityReck, MaxPop, data=data)
qplot(fecundityReck, MinPop, data=data)
qplot(fecundityReck, collapse1, data=data)
qplot(fecundityReck, collapse2, data=data)

Crs.params <- read.csv("CrsParams.csv")
Crs.out <- read.csv("Crs.csv")
Crs.out$collapse1 <- Crs.out$MinPop/Crs.out$MaxPop
Crs.out$collapse2 <- Crs.out$count/Crs.out$MaxPop
data <- data.frame(Crs.params, Crs.out)
qplot(costResil, collapse1, data=data)
qplot(costResil, collapse2, data=data)

Cu.params <- read.csv("CuParams.csv")
Cu.out <- read.csv("Cu.csv")
Cu.out$collapse1 <- Cu.out$MinPop/Cu.out$MaxPop
Cu.out$collapse2 <- Cu.out$countPop/Cu.out$MaxPop
data <- data.frame(Cu.params, Cu.out)
qplot(costDisturbance, collapse1, data=data)
qplot(costDisturbance, collapse2, data=data)

Crg.params <- read.csv("CrgParams.csv")
Crg.out <- read.csv("Crg.csv")
Crg.out$collapse1 <- Crg.out$MinPop/Crg.out$MaxPop
Crg.out$collapse2 <- Crg.out$countPop/Crg.out$MaxPop
data <- data.frame(Crg.params, Crg.out)
qplot(costRegen, collapse1, data=data)
qplot(costRegen, collapse2, data=data)



UA.params <- read.csv("UaParams.csv")
UA.out <- read.csv("Ua.csv")
UA.out$collapse1 <- UA.out$MinPop/UA.out$MaxPop
UA.out$collapse2 <- UA.out$countPop/UA.out$MaxPop
data <- data.frame(UA.params, UA.out)
qplot(uplandAmount, MaxPop, data=data)
qplot(uplandAmount, MinPop, data=data)
qplot(uplandAmount, countPop, data=data)
qplot(uplandAmount, collapse1, data=data)
qplot(uplandAmount, collapse2, data=data)


Cpr.params <- read.csv("CprParams.csv")
Cpr.out <- read.csv("Cpr.csv")
Cpr.out$collapse1 <- Cpr.out$MinPop/Cpr.out$MaxPop
Cpr.out$collapse2 <- Cpr.out$countPop/Cpr.out$MaxPop
data <- data.frame(Cpr.params, Cpr.out)
qplot(costPromotiveRes, MaxPop, data=data)
qplot(costPromotiveRes, MinPop, data=data)
qplot(costPromotiveRes, countPop, data=data)
qplot(costPromotiveRes, collapse1, data=data)
qplot(costPromotiveRes, collapse2, data=data)

Cpmx.params <- read.csv("CpmxParams.csv")
Cpmx.out <- read.csv("Cpmx.csv")
Cpmx.out$collapse1 <- Cpmx.out$MinPop/Cpmx.out$MaxPop
Cpmx.out$collapse2 <- Cpmx.out$countPop/Cpmx.out$MaxPop
data <- data.frame(Cpmx.params, Cpmx.out)
qplot(costPromotiveMax, MaxPop, data=data)
qplot(costPromotiveMax, MinPop, data=data)
qplot(costPromotiveMax, countPop, data=data)
qplot(costPromotiveMax, collapse1, data=data)
qplot(costPromotiveMax, collapse2, data=data)

d.params <- read.csv("dParams.csv")
d.out <- read.csv("d.csv")
d.out$collapse1 <- d.out$MinPop/d.out$MaxPop
d.out$collapse2 <- d.out$countPop/d.out$MaxPop
data <- data.frame(d.params, d.out)
qplot(disturbance, MaxPop, data=data)
qplot(disturbance, MinPop, data=data)
qplot(disturbance, countPop, data=data)
qplot(disturbance, collapse1, data=data)
qplot(disturbance, collapse2, data=data)
