library(ggplot2)
setwd("/home/akara/workspace/Mayagrations/output/")
setwd("/media/nvme/workspace2/Mayagrations/output/")

Frs.params <- read.csv("FrsParams.csv")
Frs.out <- read.csv("Frs.csv")
Frs.out$collapse1 <- log(Frs.out$MaxPop/Frs.out$MinPop)
Frs.out$collapse2 <- log(Frs.out$MaxPop/Frs.out$countPop)
data <- data.frame(Frs.params, Frs.out)
qplot(fecundityResil, collapse1, data=data)
qplot(fecundityResil, collapse2, data=data)

Fu.params <- read.csv("FuParams.csv")
Fu.out <- read.csv("Fu.csv")
Fu.out$collapse1 <- log(Fu.out$MaxPop/Fu.out$MinPop)
Fu.out$collapse2 <- log(Fu.out$MaxPop/Fu.out$countPop)
data <- data.frame(Fu.params, Fu.out)
qplot(fecundityDisturbance, collapse1, data=data)
qplot(fecundityDisturbance, collapse2, data=data)

Frg.params <- read.csv("FrgParams.csv")
Frg.out <- read.csv("Frg.csv")
Frg.out$collapse1 <- log(Frg.out$MaxPop/Frg.out$MinPop)
Frg.out$collapse2 <- log(Frg.out$MaxPop/Frg.out$countPop)
data <- data.frame(Frg.params, Frg.out)
qplot(fecundityRegen, collapse1, data=data)
qplot(fecundityRegen, collapse2, data=data)

Frk.params <- read.csv("FrkParams.csv")
Frk.out <- read.csv("Frk.csv")
Frk.out$collapse1 <- log(Frk.out$MaxPop/Frk.out$MinPop)
Frk.out$collapse2 <- log(Frk.out$MaxPop/Frk.out$countPop)
data <- data.frame(Frk.params, Frk.out)
qplot(fecundityReck, MaxPop, data=data)
qplot(fecundityReck, MinPop, data=data)
qplot(fecundityReck, collapse1, data=data)
qplot(fecundityReck, collapse2, data=data)

Crs.params <- read.csv("CrsParams.csv")
Crs.out <- read.csv("Crs.csv")
Crs.out$collapse1 <- log(Crs.out$MaxPop/Crs.out$MinPop)
Crs.out$collapse2 <- log(Crs.out$MaxPop/Crs.out$countPop)
data <- data.frame(Crs.params, Crs.out)
qplot(costResil, collapse1, data=data)
qplot(costResil, collapse2, data=data)

Cu.params <- read.csv("CuParams.csv")
Cu.out <- read.csv("Cu.csv")
Cu.out$collapse1 <- Cu.out$MaxPop/Cu.out$MinPop
Cu.out$collapse2 <- Cu.out$MaxPop/Cu.out$countPop
data <- data.frame(Cu.params, Cu.out)
qplot(costDisturbance, collapse1, data=data)
qplot(costDisturbance, collapse2, data=data)

Crg.params <- read.csv("CrgParams.csv")
Crg.out <- read.csv("Crg.csv")
Crg.out$collapse1 <- Crg.out$MaxPop/Crg.out$MinPop
Crg.out$collapse2 <- Crg.out$MaxPop/Crg.out$countPop
data <- data.frame(Crg.params, Crg.out)
qplot(costRegen, collapse1, data=data)
qplot(costRegen, collapse2, data=data)

UA.params <- read.csv("UAParams.csv")
UA.out <- read.csv("UA.csv")
UA.out$collapse1 <- log(UA.out$MaxPop/UA.out$MinPop)
UA.out$collapse2 <- log(UA.out$MaxPop/UA.out$countPop)
data <- data.frame(UA.params, UA.out)
qplot(uplandAmount, MaxPop, data=data)
qplot(uplandAmount, MinPop, data=data)
qplot(uplandAmount, collapse1, data=data)
qplot(uplandAmount, collapse2, data=data)












asumma